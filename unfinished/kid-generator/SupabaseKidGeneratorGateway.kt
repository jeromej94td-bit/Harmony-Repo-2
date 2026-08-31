package com.example.data

import android.util.Log
import com.example.data.model.KidGeneratorRequest
import com.example.data.model.KidGeneratorResponse
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

class SupabaseKidGeneratorGateway {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
    private val projectId = "rspgnonlpkxdudbjxnrl"
    private val anonKey = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1"

    suspend fun generateKid(request: KidGeneratorRequest): KidGeneratorResponse = withContext(Dispatchers.IO) {
        val functionName = "harmony-kid-generator-generate"
        val url = "https://$projectId.supabase.co/functions/v1/$functionName"

        try {
            // Get valid JWT Token from the main Supabase gateway
            val token = SupabaseBrainGateway.getInstance().getOrFetchToken()

            // Build request body
            val requestBodyJson = JSONObject().apply {
                put("userName", request.userName)
                put("partnerName", request.partnerName)
                put("userBase64", request.userBase64)
                put("partnerBase64", request.partnerBase64)

                put("additionalUserBase64", JSONArray().apply {
                    request.additionalUserBase64.forEach { put(it) }
                })
                put("additionalPartnerBase64", JSONArray().apply {
                    request.additionalPartnerBase64.forEach { put(it) }
                })

                put("scenario", request.scenario)
                put("style", request.style)
                put("childOption", request.childOption)
                put("wishes", request.wishes)
                put("locale", request.locale)
            }

            val requestBody = requestBodyJson.toString().toRequestBody(mediaTypeJson)

            val apiRequest = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()

            Log.d("KidGeneratorGateway", "Starting Edge Function call to $functionName...")
            client.newCall(apiRequest).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.e("KidGeneratorGateway", "Edge Function HTTP Error ${response.code}: $responseBody")
                    return@withContext parseHttpError(response.code, responseBody)
                }

                val json = JSONObject(responseBody)
                val ok = json.optBoolean("ok", true)
                val imageUrl = json.optString("imageUrl", null)
                val imageBase64 = json.optString("imageBase64", null)
                val promptSummary = json.optString("promptSummary", null)
                val scenario = json.optString("scenario", null)
                val style = json.optString("style", null)
                val childOption = json.optString("childOption", null)
                val generationId = json.optString("generationId", null)
                val errorMsg = json.optString("error", null)

                val warningsList = mutableListOf<String>()
                val warningsArr = json.optJSONArray("warnings")
                if (warningsArr != null) {
                    for (i in 0 until warningsArr.length()) {
                        warningsList.add(warningsArr.getString(i))
                    }
                }

                KidGeneratorResponse(
                    ok = ok,
                    imageUrl = imageUrl,
                    imageBase64 = imageBase64,
                    promptSummary = promptSummary,
                    scenario = scenario,
                    style = style,
                    childOption = childOption,
                    warnings = warningsList,
                    generationId = generationId,
                    error = errorMsg
                )
            }
        } catch (e: Exception) {
            Log.e("KidGeneratorGateway", "Network Error in Kid Generator: ${e.message}", e)
            KidGeneratorResponse(
                ok = false,
                error = "Verbindungsfehler: ${e.localizedMessage ?: "Keine Verbindung zur Edge-Function."}"
            )
        }
    }

    private fun parseHttpError(code: Int, body: String): KidGeneratorResponse {
        var errorMessage = "Server-Fehler: HTTP $code"
        try {
            val json = JSONObject(body)
            val msg = json.optString("message", "")
            if (msg.isNotBlank()) {
                errorMessage = msg
            }
        } catch (_: Exception) {}
        return KidGeneratorResponse(ok = false, error = errorMessage)
    }
}
