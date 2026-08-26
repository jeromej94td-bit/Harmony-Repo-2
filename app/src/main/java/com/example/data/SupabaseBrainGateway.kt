package com.example.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class HarmonyBrainSource(
    val title: String,
    val url: String
)

data class HarmonyBrainSearchResponse(
    val ok: Boolean,
    val grounded: Boolean,
    val model: String?,
    val answer: String?,
    val sources: List<HarmonyBrainSource>,
    val searchQueries: List<String>,
    val latencyMs: Long?,
    val errorType: String? = null,
    val errorMessage: String? = null
)

class SupabaseBrainGateway private constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    private val projectId = "rspgnonlpkxdudbjxnrl"
    private val anonKey = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1"

    private val tokenMutex = Mutex()
    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    companion object {
        @Volatile
        private var instance: SupabaseBrainGateway? = null

        fun getInstance(): SupabaseBrainGateway {
            return instance ?: synchronized(this) {
                instance ?: SupabaseBrainGateway().also { instance = it }
            }
        }
    }

    /**
     * Besorgt ein gültiges JWT Token für Supabase über Anonymous Auth.
     * Nutzt ein gecachtes Token, falls dieses noch gültig ist.
     */
    suspend fun getOrFetchToken(forceRefresh: Boolean = false): String = tokenMutex.withLock {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            if (!forceRefresh && cachedToken != null && now < tokenExpiryTime) {
                return@withContext cachedToken!!
            }

            Log.d("SupabaseBrainGateway", "Starte anonyme Authentifizierung...")
            val url = "https://$projectId.supabase.co/auth/v1/signup"
            val requestBody = "{}".toRequestBody(mediaTypeJson)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("apikey", anonKey)
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    Log.e("SupabaseBrainGateway", "Auth fehlgeschlagen: Code ${response.code}, Body: $errorBody")
                    throw IOException("Auth fehlgeschlagen mit Status ${response.code}: $errorBody")
                }

                val responseBody = response.body?.string() ?: throw IOException("Leere Auth-Antwort")
                val json = JSONObject(responseBody)
                val accessToken = json.getString("access_token")
                val expiresIn = json.optLong("expires_in", 3600L)

                cachedToken = accessToken
                // Setze Expiry mit 5 Minuten Puffer
                tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000) - (300 * 1000)
                Log.d("SupabaseBrainGateway", "Anonyme Auth erfolgreich. Token erhalten.")
                accessToken
            }
        }
    }

    /**
     * Führt eine Suche über das Harmony Brain durch (ruft die Edge Function auf).
     * Handhabt Fehler und 401 Retries.
     */
    suspend fun search(query: String, functionName: String = "harmony-brain-search-test"): HarmonyBrainSearchResponse {
        if (query.isBlank()) {
            return HarmonyBrainSearchResponse(
                ok = false,
                grounded = false,
                model = null,
                answer = null,
                sources = emptyList(),
                searchQueries = emptyList(),
                latencyMs = 0L,
                errorType = "invalid_query",
                errorMessage = "Ungültige/leere Anfrage."
            )
        }

        try {
            val token = getOrFetchToken()
            return executeRequest(query, token, functionName)
        } catch (authException: Exception) {
            Log.e("SupabaseBrainGateway", "Auth Exception-Klasse: ${authException.javaClass.name}")
            Log.e("SupabaseBrainGateway", "Auth Exception: ${authException.toString()}")
            Log.e("SupabaseBrainGateway", "Auth Cause-Klasse: ${authException.cause?.javaClass?.name}")
            Log.e("SupabaseBrainGateway", "Auth Cause: ${authException.cause?.toString()}")

            return HarmonyBrainSearchResponse(
                ok = false,
                grounded = false,
                model = null,
                answer = null,
                sources = emptyList(),
                searchQueries = emptyList(),
                latencyMs = 0L,
                errorType = "auth_failed",
                errorMessage = "Supabase Auth fehlgeschlagen. Bitte Internetverbindung prüfen."
            )
        }
    }

    private suspend fun executeRequest(query: String, token: String, functionName: String, isRetry: Boolean = false): HarmonyBrainSearchResponse = withContext(Dispatchers.IO) {
        val url = "https://$projectId.supabase.co/functions/v1/$functionName"

        val bodyJson = JSONObject().apply {
            put("query", query)
        }
        val requestBody = bodyJson.toString().toRequestBody(mediaTypeJson)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("apikey", anonKey)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.code == 401) {
                    if (!isRetry) {
                        Log.w("SupabaseBrainGateway", "JWT ungültig (401). Versuche Token-Erneuerung...")
                        val newToken = getOrFetchToken(forceRefresh = true)
                        return@withContext executeRequest(query, newToken, functionName, isRetry = true)
                    } else {
                        return@withContext HarmonyBrainSearchResponse(
                            ok = false,
                            grounded = false,
                            model = null,
                            answer = null,
                            sources = emptyList(),
                            searchQueries = emptyList(),
                            latencyMs = 0,
                            errorType = "unauthorized",
                            errorMessage = "JWT wurde abgelehnt (401) trotz Erneuerung."
                        )
                    }
                }

                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.e("SupabaseBrainGateway", "Edge Function HTTP ${response.code}: $responseBody")
                    return@withContext parseHttpError(response.code, responseBody)
                }

                // Erfolgreicher Fall
                val json = JSONObject(responseBody)
                val ok = json.optBoolean("ok", true)
                val grounded = json.optBoolean("grounded", false)
                val model = json.optString("model", "gemini-2.5-flash")
                val answer = json.optString("answer", "")
                val latencyMs = if (json.has("latencyMs") && !json.isNull("latencyMs")) json.getLong("latencyMs") else null

                val sourcesList = mutableListOf<HarmonyBrainSource>()
                if (json.has("sources") && !json.isNull("sources")) {
                    val sourcesArr = json.getJSONArray("sources")
                    for (i in 0 until sourcesArr.length()) {
                        val srcObj = sourcesArr.getJSONObject(i)
                        sourcesList.add(
                            HarmonyBrainSource(
                                title = srcObj.optString("title", "Quelle"),
                                url = srcObj.optString("url", "")
                            )
                        )
                    }
                }

                val queriesList = mutableListOf<String>()
                if (json.has("searchQueries") && !json.isNull("searchQueries")) {
                    val queriesArr = json.getJSONArray("searchQueries")
                    for (i in 0 until queriesArr.length()) {
                        queriesList.add(queriesArr.getString(i))
                    }
                }

                return@withContext HarmonyBrainSearchResponse(
                    ok = ok,
                    grounded = grounded,
                    model = model,
                    answer = answer,
                    sources = sourcesList,
                    searchQueries = queriesList,
                    latencyMs = latencyMs
                )
            }
        } catch (e: Exception) {
            Log.e("SupabaseBrainGateway", "Netzwerkfehler: ${e.message}")
            return@withContext HarmonyBrainSearchResponse(
                ok = false,
                grounded = false,
                model = null,
                answer = null,
                sources = emptyList(),
                searchQueries = emptyList(),
                latencyMs = 0,
                errorType = "network_error",
                errorMessage = "Netzwerkfehler: ${e.localizedMessage ?: "Keine Verbindung zum Server."}"
            )
        }
    }

    private fun parseHttpError(code: Int, body: String): HarmonyBrainSearchResponse {
        var errorType = "http_error"
        var errorMessage = "Server-Fehler: HTTP $code"

        try {
            val json = JSONObject(body)
            val errCode = json.optString("code", "")
            val errMsg = json.optString("message", "")

            if (errCode == "gemini_not_configured" || errMsg.contains("gemini_not_configured") || body.contains("gemini_not_configured")) {
                errorType = "gemini_not_configured"
                errorMessage = "Gemini-Key ist serverseitig nicht konfiguriert"
            } else if (errCode == "gemini_request_failed" || errMsg.contains("gemini_request_failed") || body.contains("gemini_request_failed")) {
                errorType = "gemini_request_failed"
                errorMessage = "Supabase erreicht Gemini, aber Gemini hat die Anfrage abgelehnt."
            } else if (errCode == "gemini_unreachable" || errMsg.contains("gemini_unreachable") || body.contains("gemini_unreachable")) {
                errorType = "gemini_unreachable"
                errorMessage = "Gemini momentan nicht erreichbar."
            } else if (errCode == "invalid_query" || errMsg.contains("invalid_query") || body.contains("invalid_query")) {
                errorType = "invalid_query"
                errorMessage = "Ungültige/leere Anfrage."
            } else if (errMsg.isNotBlank()) {
                errorMessage = errMsg
            }
        } catch (e: Exception) {
            // Keine valide JSON-Fehlermeldung, wir nutzen den Body direkt, falls kurz
            if (body.isNotBlank() && body.length < 150) {
                errorMessage = body
            }
        }

        // Spezifische HTTP Codes abfangen, falls keine JSON-Details da waren
        if (code == 503 && errorType == "http_error") {
            errorType = "gemini_not_configured"
            errorMessage = "Gemini-Key ist serverseitig nicht konfiguriert"
        } else if (code == 502 && errorType == "http_error") {
            errorType = "gemini_request_failed"
            errorMessage = "Gemini momentan nicht erreichbar oder Anfrage abgelehnt."
        }

        return HarmonyBrainSearchResponse(
            ok = false,
            grounded = false,
            model = null,
            answer = null,
            sources = emptyList(),
            searchQueries = emptyList(),
            latencyMs = 0,
            errorType = errorType,
            errorMessage = errorMessage
        )
    }
}
