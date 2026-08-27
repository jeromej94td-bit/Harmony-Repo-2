package com.example.data.brain.gateway

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SupabaseBrainAuthSession(
    private val projectId: String = "rspgnonlpkxdudbjxnrl",
    private val anonKey: String = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1",
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
    private val tokenMutex = Mutex()
    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    suspend fun getOrFetchToken(forceRefresh: Boolean = false): String = tokenMutex.withLock {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            if (!forceRefresh && cachedToken != null && now < tokenExpiryTime) {
                return@withContext cachedToken!!
            }

            Log.d("BrainAuthSession", "Fetching anonymous Supabase token...")
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
                    Log.e("BrainAuthSession", "Auth failed: Code ${response.code}, Body: $errorBody")
                    throw IOException("Supabase Auth failed with code ${response.code}: $errorBody")
                }

                val responseBody = response.body?.string() ?: throw IOException("Empty auth response")
                val json = JSONObject(responseBody)
                val accessToken = json.getString("access_token")
                val expiresIn = json.optLong("expires_in", 3600L)

                cachedToken = accessToken
                tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000) - (300 * 1000)
                Log.d("BrainAuthSession", "Anonymous auth successful, token cached.")
                accessToken
            }
        }
    }
}
