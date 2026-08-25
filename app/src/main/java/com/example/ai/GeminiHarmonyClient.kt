package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiHarmonyClient(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(75, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        private const val ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/interactions"
        const val MODEL = "gemini-3.6-flash"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun ask(
        prompt: String,
        route: HarmonyAiRoute,
        location: CoachLocation?
    ): HarmonyCoachResponse = withContext(Dispatchers.IO) {
        requireConfiguredKey()

        val body = buildJsonObject {
            put("model", JsonPrimitive(MODEL))
            put("input", JsonPrimitive(prompt))
            val tools = toolsFor(route.grounding, location)
            if (tools.isNotEmpty()) put("tools", tools)
        }.toString().toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(ENDPOINT)
            .header("x-goog-api-key", apiKey.trim())
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            val raw = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val detail = extractError(raw)
                throw IOException("Gemini ${response.code}: $detail")
            }
            parseResponse(raw)
        }
    }

    private fun toolsFor(mode: HarmonyGroundingMode, location: CoachLocation?): JsonArray = buildJsonArray {
        if (mode == HarmonyGroundingMode.GOOGLE_SEARCH || mode == HarmonyGroundingMode.SEARCH_AND_MAPS) {
            add(buildJsonObject { put("type", JsonPrimitive("google_search")) })
        }
        if (mode == HarmonyGroundingMode.GOOGLE_MAPS || mode == HarmonyGroundingMode.SEARCH_AND_MAPS) {
            add(buildJsonObject {
                put("type", JsonPrimitive("google_maps"))
                if (location != null) {
                    put("latitude", JsonPrimitive(location.latitude))
                    put("longitude", JsonPrimitive(location.longitude))
                }
            })
        }
    }

    private fun parseResponse(raw: String): HarmonyCoachResponse {
        val root = json.parseToJsonElement(raw).jsonObject
        val steps = root["steps"] as? JsonArray ?: JsonArray(emptyList())
        val textParts = mutableListOf<String>()
        val sources = linkedMapOf<String, CoachSource>()
        var usedSearch = false
        var usedMaps = false

        steps.forEach { element ->
            val step = element as? JsonObject ?: return@forEach
            when (step.string("type")) {
                "google_search_call", "google_search_result" -> usedSearch = true
                "google_maps_call", "google_maps_result" -> usedMaps = true
                "model_output" -> {
                    val content = step["content"] as? JsonArray ?: JsonArray(emptyList())
                    content.forEach contentLoop@{ blockElement ->
                        val block = blockElement as? JsonObject ?: return@contentLoop
                        if (block.string("type") != "text") return@contentLoop
                        block.string("text")?.takeIf { it.isNotBlank() }?.let(textParts::add)
                        val annotations = block["annotations"] as? JsonArray ?: JsonArray(emptyList())
                        annotations.forEach annotationLoop@{ annotationElement ->
                            val annotation = annotationElement as? JsonObject ?: return@annotationLoop
                            val type = annotation.string("type") ?: return@annotationLoop
                            val url = annotation.string("url")?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
                                ?: return@annotationLoop
                            val title = annotation.string("name")
                                ?: annotation.string("title")
                                ?: if (type == "place_citation") "Google Maps" else "Quelle"
                            sources[url] = CoachSource(title = title, url = url, type = type)
                            if (type == "place_citation") usedMaps = true
                            if (type == "url_citation") usedSearch = true
                        }
                    }
                }
            }
        }

        if (textParts.isEmpty()) {
            root.string("output_text")?.takeIf { it.isNotBlank() }?.let(textParts::add)
        }

        val text = textParts.joinToString("\n\n").trim()
        if (text.isBlank()) throw IOException("Gemini returned no readable answer")

        return HarmonyCoachResponse(
            text = text,
            sources = sources.values.take(8),
            groundedBySearch = usedSearch,
            groundedByMaps = usedMaps
        )
    }

    private fun extractError(raw: String): String = runCatching {
        val root = json.parseToJsonElement(raw).jsonObject
        root["error"]?.jsonObject?.get("message")?.jsonPrimitive?.contentOrNull
    }.getOrNull().orEmpty().ifBlank { "request failed" }

    private fun requireConfiguredKey() {
        val key = apiKey.trim()
        if (key.isBlank() || key.startsWith("MY_") || key.contains("GEMINI_KEY")) {
            throw IllegalStateException("GEMINI_API_KEY fehlt. Trage deinen Gemini-Key in der lokalen .env-Datei ein.")
        }
    }

    private fun JsonObject.string(name: String): String? =
        (get(name) as? JsonPrimitive)?.contentOrNull
}
