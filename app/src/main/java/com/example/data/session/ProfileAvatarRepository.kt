package com.example.data.session

import android.content.Context
import android.net.Uri
import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

const val HARMONY_AVATAR_BUCKET = "harmony-avatars"
const val HARMONY_AVATAR_PREFIX = "harmony-avatar:"
private const val MAX_AVATAR_BYTES = 5 * 1024 * 1024

fun harmonyAvatarRef(userId: String): String = "$HARMONY_AVATAR_PREFIX$userId/avatar"

fun harmonyAvatarObjectPath(avatarRef: String?): String? = avatarRef
    ?.takeIf { it.startsWith(HARMONY_AVATAR_PREFIX) }
    ?.removePrefix(HARMONY_AVATAR_PREFIX)
    ?.takeIf { it.matches(Regex("^[0-9a-fA-F-]{36}/avatar$")) }

class ProfileAvatarRepository(
    private val context: Context,
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    suspend fun uploadOwnAvatar(uri: Uri): String = withContext(Dispatchers.IO) {
        val session = SupabaseConfig.client.auth.currentSessionOrNull()
            ?: throw HarmonySessionException("not_authenticated")
        val userId = session.user?.id
            ?: throw HarmonySessionException("not_authenticated")
        val accessToken = session.accessToken

        val mimeType = context.contentResolver.getType(uri)
            ?.lowercase()
            ?.takeIf { it in ALLOWED_MIME_TYPES }
            ?: throw HarmonySessionException("invalid_avatar_type")

        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
            val declaredLength = descriptor.length
            if (declaredLength > MAX_AVATAR_BYTES) {
                throw HarmonySessionException("avatar_too_large")
            }
        }

        val bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes()
        } ?: throw IOException("avatar_read_failed")

        if (bytes.isEmpty()) throw HarmonySessionException("avatar_empty")
        if (bytes.size > MAX_AVATAR_BYTES) throw HarmonySessionException("avatar_too_large")

        val objectPath = "$userId/avatar"
        val request = Request.Builder()
            .url("${SupabaseConfig.SUPABASE_URL}/storage/v1/object/$HARMONY_AVATAR_BUCKET/$objectPath")
            .post(bytes.toRequestBody(mimeType.toMediaType()))
            .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", mimeType)
            .header("x-upsert", "true")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw HarmonySessionException("avatar_upload_${response.code}")
            }
        }

        harmonyAvatarRef(userId)
    }

    companion object {
        private val ALLOWED_MIME_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}
