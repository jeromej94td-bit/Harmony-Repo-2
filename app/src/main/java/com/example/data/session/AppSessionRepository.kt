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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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
        return resolveAvatarUrls(rows.getJSONObject(0).toAppSession())
    }

    suspend fun updateProfile(displayName: String): AppSession {
        postRpc(
            "update_harmony_profile",
            JSONObject().put("p_display_name", displayName)
        )
        return refresh()
    }

    suspend fun updateAvatar(
        userId: String,
        bytes: ByteArray,
        contentType: String
    ): AppSession {
        if (bytes.isEmpty()) throw HarmonySessionException("avatar_empty")
        if (contentType !in ALLOWED_AVATAR_TYPES) {
            throw HarmonySessionException("avatar_invalid_type")
        }

        val storagePath = "$userId/avatar"
        uploadAvatar(storagePath, bytes, contentType)
        postRpc(
            "update_harmony_avatar",
            JSONObject().put("p_avatar_ref", "$AVATAR_REF_PREFIX$storagePath")
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

    private suspend fun uploadAvatar(
        storagePath: String,
        bytes: ByteArray,
        contentType: String
    ) = withContext(Dispatchers.IO) {
        val accessToken = accessTokenProvider()
        val request = Request.Builder()
            .url("${SupabaseConfig.SUPABASE_URL}/storage/v1/object/harmony-avatars/${encodeStoragePath(storagePath)}")
            .post(bytes.toRequestBody(contentType.toMediaType()))
            .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", contentType)
            .header("x-upsert", "true")
            .build()

        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw HarmonySessionException(storageErrorCode(responseBody, response.code))
            }
        }
    }

    private suspend fun resolveAvatarUrls(session: AppSession): AppSession {
        val userProfile = session.profile.copy(
            avatarUrl = resolveAvatarUrl(session.profile.avatarUrl)
        )
        val partnerProfile = session.partner?.let { partner ->
            partner.copy(avatarUrl = resolveAvatarUrl(partner.avatarUrl))
        }
        return session.copy(profile = userProfile, partner = partnerProfile)
    }

    private suspend fun resolveAvatarUrl(value: String?): String? {
        val avatar = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (!avatar.startsWith(AVATAR_REF_PREFIX)) return avatar

        val storagePath = avatar.removePrefix(AVATAR_REF_PREFIX)
        if (!AVATAR_STORAGE_PATH.matches(storagePath)) return null
        return runCatching { createSignedAvatarUrl(storagePath) }.getOrNull()
    }

    private suspend fun createSignedAvatarUrl(storagePath: String): String =
        withContext(Dispatchers.IO) {
            val accessToken = accessTokenProvider()
            val request = Request.Builder()
                .url("${SupabaseConfig.SUPABASE_URL}/storage/v1/object/sign/harmony-avatars/${encodeStoragePath(storagePath)}")
                .post(
                    JSONObject()
                        .put("expiresIn", SIGNED_AVATAR_TTL_SECONDS)
                        .toString()
                        .toRequestBody(jsonMediaType)
                )
                .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build()

            httpClient.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw HarmonySessionException(storageErrorCode(responseBody, response.code))
                }
                val signedUrl = JSONObject(responseBody)
                    .optString("signedURL")
                    .takeIf { it.isNotBlank() }
                    ?: throw HarmonySessionException("avatar_sign_failed")

                when {
                    signedUrl.startsWith("http://") || signedUrl.startsWith("https://") -> signedUrl
                    signedUrl.startsWith("/storage/v1/") -> "${SupabaseConfig.SUPABASE_URL}$signedUrl"
                    signedUrl.startsWith("/") -> "${SupabaseConfig.SUPABASE_URL}/storage/v1$signedUrl"
                    else -> "${SupabaseConfig.SUPABASE_URL}/storage/v1/$signedUrl"
                }
            }
        }

    private fun storageErrorCode(responseBody: String, statusCode: Int): String {
        val message = runCatching {
            val json = JSONObject(responseBody)
            json.optString("message").ifBlank { json.optString("error") }
        }.getOrNull().orEmpty()
        return message.ifBlank { "avatar_storage_$statusCode" }
    }

    private fun encodeStoragePath(path: String): String =
        path.split('/').joinToString("/") { segment ->
            URLEncoder.encode(segment, StandardCharsets.UTF_8.toString()).replace("+", "%20")
        }

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

    private companion object {
        const val AVATAR_REF_PREFIX = "harmony-avatar:"
        const val SIGNED_AVATAR_TTL_SECONDS = 3600
        val ALLOWED_AVATAR_TYPES = setOf("image/jpeg", "image/png", "image/webp")
        val AVATAR_STORAGE_PATH = Regex("^[0-9a-fA-F-]{36}/avatar$")
    }
}

class HarmonySessionException(
    val reason: String
) : IOException(reason)
