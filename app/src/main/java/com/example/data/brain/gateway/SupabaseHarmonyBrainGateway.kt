package com.example.data.brain.gateway

import android.util.Log
import com.example.data.HarmonyBrainSource
import com.example.data.brain.engine.HarmonyContextBuilder
import com.example.data.brain.model.BrainQuestionResult
import com.example.data.brain.model.BrainRecommendationResult
import com.example.data.brain.model.BrainResult
import com.example.data.brain.model.BrainSearchResult
import com.example.data.brain.model.GeneratedBrainQuestion
import com.example.data.brain.model.HarmonyBrainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SupabaseHarmonyBrainGateway(
    private val authSession: SupabaseBrainAuthSession = SupabaseBrainAuthSession(),
    private val projectId: String = "yepluyipizbbrgoffqdq",
    private val anonKey: String = "sb_publishable_lat183ycL-tC_3NDwzCHOw_GKmcNWqM",
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) : HarmonyBrainGateway {

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    companion object {
        @Volatile
        private var INSTANCE: SupabaseHarmonyBrainGateway? = null

        fun getInstance(): SupabaseHarmonyBrainGateway {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseHarmonyBrainGateway().also { INSTANCE = it }
            }
        }
    }

    override suspend fun chat(
        query: String,
        context: HarmonyBrainContext,
        useCurrentInfo: Boolean
    ): BrainResult = withContext(Dispatchers.IO) {
        val rawResult = executeEdgeCall(
            mode = "chat",
            query = query,
            context = context,
            useCurrentInfo = useCurrentInfo
        )

        val isOk = rawResult.optBoolean("ok", false)
        if (!isOk || rawResult.has("errorMessage") && rawResult.optString("errorMessage").isNotEmpty()) {
            val errType = rawResult.optString("errorType", "error")
            val errMsg = rawResult.optString("errorMessage", "Unbekannter Fehler")
            return@withContext BrainResult(
                ok = false,
                answer = null,
                model = rawResult.optString("model", null),
                latencyMs = rawResult.optLong("latencyMs", 0),
                errorType = errType,
                errorMessage = errMsg
            )
        }

        val dataObj = rawResult.optJSONObject("data")
        val answerText = rawResult.optString("answer", "").ifBlank {
            dataObj?.optString("answer", "").orEmpty()
        }.ifBlank {
            rawResult.optString("reply", "")
        }

        BrainResult(
            ok = answerText.isNotBlank(),
            answer = answerText,
            model = rawResult.optString("model", dataObj?.optString("model", "gemini-2.5-flash")),
            latencyMs = rawResult.optLong("latencyMs", 0)
        )
    }

    override suspend fun generateQuestions(
        query: String,
        context: HarmonyBrainContext
    ): BrainQuestionResult = withContext(Dispatchers.IO) {
        val rawResult = executeEdgeCall(
            mode = "questions",
            query = query,
            context = context,
            useCurrentInfo = false
        )

        val isOk = rawResult.optBoolean("ok", false)
        if (!isOk || rawResult.has("errorMessage") && rawResult.optString("errorMessage").isNotEmpty()) {
            return@withContext BrainQuestionResult(
                ok = false,
                questions = emptyList(),
                rawAnswer = null,
                model = rawResult.optString("model", null),
                latencyMs = rawResult.optLong("latencyMs", 0),
                errorType = rawResult.optString("errorType", "error"),
                errorMessage = rawResult.optString("errorMessage", "Unbekannter Fehler")
            )
        }

        val dataObj = rawResult.optJSONObject("data")
        val questions = mutableListOf<GeneratedBrainQuestion>()
        val qArr = dataObj?.optJSONArray("questions") ?: rawResult.optJSONArray("questions")

        if (qArr != null) {
            for (i in 0 until qArr.length()) {
                val item = qArr.opt(i)
                if (item is JSONObject) {
                    val text = item.optString("text", item.optString("q", item.optString("question", "")))
                    if (text.isNotBlank()) {
                        questions.add(
                            GeneratedBrainQuestion(
                                text = text,
                                category = item.optString("category", context.category ?: "Gemischt"),
                                difficulty = item.optString("difficulty", "medium"),
                                topic = item.optString("topic", null)
                            )
                        )
                    }
                } else if (item is String && item.isNotBlank()) {
                    questions.add(
                        GeneratedBrainQuestion(
                            text = item,
                            category = context.category ?: "Gemischt"
                        )
                    )
                }
            }
        } else {
            val answerStr = dataObj?.optString("answer", "") ?: rawResult.optString("answer", "")
            if (answerStr.isNotBlank()) {
                try {
                    val cleanJson = if (answerStr.startsWith("```json")) {
                        answerStr.substringAfter("```json").substringBeforeLast("```").trim()
                    } else if (answerStr.startsWith("```")) {
                        answerStr.substringAfter("```").substringBeforeLast("```").trim()
                    } else {
                        answerStr.trim()
                    }

                    if (cleanJson.startsWith("[")) {
                        val arr = JSONArray(cleanJson)
                        for (i in 0 until arr.length()) {
                            val item = arr.opt(i)
                            if (item is JSONObject) {
                                val text = item.optString("text", item.optString("q", item.optString("question", "")))
                                if (text.isNotBlank()) {
                                    questions.add(
                                        GeneratedBrainQuestion(
                                            text = text,
                                            category = item.optString("category", context.category ?: "Gemischt"),
                                            difficulty = item.optString("difficulty", "medium")
                                        )
                                    )
                                }
                            } else if (item is String && item.isNotBlank()) {
                                questions.add(
                                    GeneratedBrainQuestion(
                                        text = item,
                                        category = context.category ?: "Gemischt"
                                    )
                                )
                            }
                        }
                    } else {
                        val lines = answerStr.lines().filter { it.isNotBlank() && (it.startsWith("-") || it.first().isDigit()) }
                        for (line in lines) {
                            val cleanLine = line.replace(Regex("^[0-9.-]+\\s*"), "").trim()
                            if (cleanLine.isNotBlank()) {
                                questions.add(
                                    GeneratedBrainQuestion(
                                        text = cleanLine,
                                        category = context.category ?: "Gemischt"
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w("SupabaseBrainGateway", "Question parsing fallback: ${e.message}")
                }
            }
        }

        BrainQuestionResult(
            ok = questions.isNotEmpty(),
            questions = questions,
            rawAnswer = dataObj?.optString("answer", null) ?: rawResult.optString("answer", null),
            model = rawResult.optString("model", dataObj?.optString("model", "gemini-2.5-flash")),
            latencyMs = rawResult.optLong("latencyMs", 0)
        )
    }

    override suspend fun recommendations(
        query: String,
        context: HarmonyBrainContext
    ): BrainRecommendationResult = withContext(Dispatchers.IO) {
        val rawResult = executeEdgeCall(
            mode = "recommendations",
            query = query,
            context = context,
            useCurrentInfo = false
        )

        val isOk = rawResult.optBoolean("ok", false)
        if (!isOk || rawResult.has("errorMessage") && rawResult.optString("errorMessage").isNotEmpty()) {
            return@withContext BrainRecommendationResult(
                ok = false,
                recommendations = emptyList(),
                rawAnswer = null,
                model = rawResult.optString("model", null),
                latencyMs = rawResult.optLong("latencyMs", 0),
                errorType = rawResult.optString("errorType", "error"),
                errorMessage = rawResult.optString("errorMessage", "Unbekannter Fehler")
            )
        }

        val dataObj = rawResult.optJSONObject("data")
        val recs = mutableListOf<String>()
        val rArr = dataObj?.optJSONArray("recommendations") ?: rawResult.optJSONArray("recommendations")

        if (rArr != null) {
            for (i in 0 until rArr.length()) {
                val item = rArr.opt(i)
                if (item is String && item.isNotBlank()) {
                    recs.add(item)
                } else if (item is JSONObject) {
                    val text = item.optString("text", item.optString("title", item.optString("recommendation", "")))
                    if (text.isNotBlank()) recs.add(text)
                }
            }
        } else {
            val answerStr = dataObj?.optString("answer", "") ?: rawResult.optString("answer", "")
            val lines = answerStr.lines().filter { it.isNotBlank() && (it.startsWith("-") || it.first().isDigit() || it.startsWith("•")) }
            for (line in lines) {
                val clean = line.replace(Regex("^[0-9.•-]+\\s*"), "").trim()
                if (clean.isNotBlank()) recs.add(clean)
            }
            if (recs.isEmpty() && answerStr.isNotBlank()) {
                recs.add(answerStr)
            }
        }

        BrainRecommendationResult(
            ok = recs.isNotEmpty(),
            recommendations = recs,
            rawAnswer = dataObj?.optString("answer", null) ?: rawResult.optString("answer", null),
            model = rawResult.optString("model", dataObj?.optString("model", "gemini-2.5-flash")),
            latencyMs = rawResult.optLong("latencyMs", 0)
        )
    }

    override suspend fun search(
        query: String,
        context: HarmonyBrainContext
    ): BrainSearchResult = withContext(Dispatchers.IO) {
        val rawResult = executeEdgeCall(
            mode = "search",
            query = query,
            context = context,
            useCurrentInfo = true
        )

        val isOk = rawResult.optBoolean("ok", false)
        if (!isOk || rawResult.has("errorMessage") && rawResult.optString("errorMessage").isNotEmpty()) {
            return@withContext BrainSearchResult(
                ok = false,
                grounded = false,
                answer = null,
                sources = emptyList(),
                searchQueries = emptyList(),
                model = rawResult.optString("model", null),
                latencyMs = rawResult.optLong("latencyMs", 0),
                errorType = rawResult.optString("errorType", "error"),
                errorMessage = rawResult.optString("errorMessage", "Unbekannter Fehler")
            )
        }

        val dataObj = rawResult.optJSONObject("data")
        val sourcesList = mutableListOf<HarmonyBrainSource>()
        val srcArr = rawResult.optJSONArray("sources") ?: dataObj?.optJSONArray("sources")
        if (srcArr != null) {
            for (i in 0 until srcArr.length()) {
                val src = srcArr.optJSONObject(i)
                if (src != null) {
                    val title = src.optString("title", src.optString("name", "Quelle"))
                    val url = src.optString("url", src.optString("uri", ""))
                    if (title.isNotBlank() || url.isNotBlank()) {
                        sourcesList.add(
                            HarmonyBrainSource(
                                title = title.ifBlank { "Quelle" },
                                url = url
                            )
                        )
                    }
                }
            }
        }

        val queriesList = mutableListOf<String>()
        val qArr = rawResult.optJSONArray("searchQueries") ?: dataObj?.optJSONArray("searchQueries")
        if (qArr != null) {
            for (i in 0 until qArr.length()) {
                val q = qArr.optString(i, "")
                if (q.isNotBlank()) queriesList.add(q)
            }
        }

        val answerText = rawResult.optString("answer", "").ifBlank {
            dataObj?.optString("answer", "").orEmpty()
        }.ifBlank {
            rawResult.optString("reply", "")
        }

        BrainSearchResult(
            ok = true,
            grounded = rawResult.optBoolean("grounded", dataObj?.optBoolean("grounded", false) ?: false),
            answer = answerText,
            sources = sourcesList,
            searchQueries = queriesList,
            model = rawResult.optString("model", dataObj?.optString("model", "gemini-2.5-flash")),
            latencyMs = rawResult.optLong("latencyMs", 0)
        )
    }

    private suspend fun executeEdgeCall(
        mode: String,
        query: String,
        context: HarmonyBrainContext,
        useCurrentInfo: Boolean,
        isRetry: Boolean = false
    ): JSONObject = withContext(Dispatchers.IO) {
        val token = try {
            authSession.getOrFetchToken(forceRefresh = isRetry)
        } catch (e: Exception) {
            Log.e("SupabaseBrainGateway", "Token fetch failed: ${e.message}")
            return@withContext JSONObject().apply {
                put("ok", false)
                put("errorType", "auth_failed")
                put("errorMessage", "Authentifizierung bei Supabase fehlgeschlagen (${e.localizedMessage ?: "Offline"}).")
            }
        }

        val url = "https://$projectId.supabase.co/functions/v1/harmony-brain-generate"
        val contextJsonString = HarmonyContextBuilder.serializeCompact(context)
        val contextJsonObject = JSONObject(contextJsonString)

        val requestPayload = JSONObject().apply {
            put("mode", mode)
            put("query", query)
            put("context", contextJsonObject)
            put("useCurrentInfo", useCurrentInfo)
        }

        val requestBody = requestPayload.toString().toRequestBody(mediaTypeJson)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("apikey", anonKey)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("x-gemini-key", com.example.BuildConfig.GEMINI_API_KEY)
            .addHeader("Content-Type", "application/json")
            .build()

        val startTime = System.currentTimeMillis()
        try {
            client.newCall(request).execute().use { response ->
                val latency = System.currentTimeMillis() - startTime
                if (response.code == 401) {
                    if (!isRetry) {
                        Log.w("SupabaseBrainGateway", "Received 401 from Edge Function. Retrying with fresh token...")
                        return@withContext executeEdgeCall(mode, query, context, useCurrentInfo, isRetry = true)
                    } else {
                        return@withContext JSONObject().apply {
                            put("ok", false)
                            put("errorType", "unauthorized")
                            put("errorMessage", "JWT wurde trotz Erneuerung abgelehnt (401).")
                            put("latencyMs", latency)
                        }
                    }
                }

                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.e("SupabaseBrainGateway", "Edge HTTP ${response.code}: $responseBody")
                    return@withContext JSONObject().apply {
                        put("ok", false)
                        put("errorType", "http_error_${response.code}")
                        put("errorMessage", "Edge Function Fehler (HTTP ${response.code}): $responseBody")
                        put("latencyMs", latency)
                    }
                }

                val parsed = if (responseBody.isNotBlank()) JSONObject(responseBody) else JSONObject()
                if (!parsed.has("latencyMs")) {
                    parsed.put("latencyMs", latency)
                }
                return@withContext parsed
            }
        } catch (e: IOException) {
            Log.e("SupabaseBrainGateway", "Network IO failure: ${e.message}")
            return@withContext JSONObject().apply {
                put("ok", false)
                put("errorType", "network_error")
                put("errorMessage", "Netzwerkverbindung fehlgeschlagen (${e.localizedMessage ?: "Offline"}).")
                put("latencyMs", System.currentTimeMillis() - startTime)
            }
        } catch (e: Exception) {
            Log.e("SupabaseBrainGateway", "Edge call failed: ${e.message}")
            return@withContext JSONObject().apply {
                put("ok", false)
                put("errorType", "unknown_error")
                put("errorMessage", "Fehler: ${e.localizedMessage}")
                put("latencyMs", System.currentTimeMillis() - startTime)
            }
        }
    }
}
