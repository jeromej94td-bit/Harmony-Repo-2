package com.example.data.session

import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
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

class AppSessionRepository(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build(),
    private val accessTokenProvider: suspend () -> String = {
        SupabaseConfig.client.auth.currentSessionOrNull()?.accessToken
            ?: throw HarmonySessionException("not_authenticated")
    }
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun refresh(): AppSession {
        val response = postRpc("get_app_session", JSONObject())
        val rows = JSONArray(response)
        if (rows.length() != 1) throw HarmonySessionException("session_not_available")
        return rows.getJSONObject(0).toAppSession()
    }

    suspend fun updateProfile(displayName: String): AppSession {
        postRpc(
            "update_harmony_profile",
            JSONObject().put("p_display_name", displayName)
        )
        return refresh()
    }

    suspend fun createPartnerInvite(): PartnerInvite {
        val response = postRpc("create_partner_invite", JSONObject())
        val rows = JSONArray(response)
        if (rows.length() != 1) throw HarmonySessionException("invite_not_available")
        val row = rows.getJSONObject(0)
        return PartnerInvite(
            code = row.getString("code"),
            expiresAt = row.getString("expires_at")
        )
    }

    suspend fun joinPartnerInvite(code: String): AppSession {
        val normalized = normalizeInviteCode(code)
        if (normalized.length != 6) throw HarmonySessionException("invalid_invite_code")
        postRpc("join_partner_invite", JSONObject().put("p_code", normalized))
        return refresh()
    }

    suspend fun leaveCurrentCouple(): AppSession {
        postRpc("leave_current_couple", JSONObject())
        return refresh()
    }

    suspend fun resetHarmony(): AppSession {
        postRpc("reset_harmony", JSONObject())
        return refresh()
    }

    internal fun normalizeInviteCode(code: String): String =
        code.uppercase().filter { it in 'A'..'Z' || it in '0'..'9' }

    private suspend fun postRpc(functionName: String, body: JSONObject): String = withContext(Dispatchers.IO) {
        val accessToken = accessTokenProvider()
        val request = Request.Builder()
            .url("${SupabaseConfig.SUPABASE_URL}/rest/v1/rpc/$functionName")
            .post(body.toString().toRequestBody(jsonMediaType))
            .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()

        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val serverCode = runCatching {
                    JSONObject(responseBody).optString("message")
                }.getOrNull().orEmpty().ifBlank { "rpc_${response.code}" }
                throw HarmonySessionException(serverCode)
            }
            responseBody
        }
    }

    private fun JSONObject.toAppSession(): AppSession {
        val userId = getString("user_id")
        val profile = UserProfile(
            userId = userId,
            displayName = optString("display_name").ifBlank { "Harmony User" },
            avatarUrl = nullableString("avatar_url")
        )
        val coupleId = nullableString("couple_id")
        val partnerUserId = nullableString("partner_user_id")
        val partner = partnerUserId?.let {
            UserProfile(
                userId = it,
                displayName = optString("partner_display_name").ifBlank { "Partner" },
                avatarUrl = nullableString("partner_avatar_url")
            )
        }

        return AppSession(
            userId = userId,
            email = nullableString("email"),
            profile = profile,
            coupleId = coupleId,
            partner = partner
        )
    }

    private fun JSONObject.nullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return optString(key).takeIf { it.isNotBlank() && it != "null" }
    }
}

class HarmonySessionException(
    val reason: String
) : IOException(reason)
