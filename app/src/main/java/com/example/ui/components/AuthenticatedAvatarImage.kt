package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.data.SupabaseConfig
import com.example.data.session.HARMONY_AVATAR_BUCKET
import com.example.data.session.harmonyAvatarObjectPath
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

private val avatarHttpClient = OkHttpClient.Builder()
    .connectTimeout(12, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

/**
 * Loads provider avatars directly, but resolves Harmony custom avatars through
 * the authenticated Storage endpoint. Private avatar bytes are cached only in
 * the app cache directory and are never turned into a public URL.
 */
@Composable
fun AuthenticatedAvatarImage(
    avatarRef: String?,
    displayName: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val objectPath = harmonyAvatarObjectPath(avatarRef)
    var privateFile by remember(avatarRef) { mutableStateOf<File?>(null) }

    LaunchedEffect(avatarRef, objectPath) {
        privateFile = null
        if (objectPath == null) return@LaunchedEffect

        val accessToken = SupabaseConfig.client.auth.currentSessionOrNull()?.accessToken
            ?: return@LaunchedEffect

        privateFile = withContext(Dispatchers.IO) {
            runCatching {
                val cacheDir = File(context.cacheDir, "harmony_private_avatars").apply { mkdirs() }
                val cacheFile = File(cacheDir, objectPath.substringBefore('/').lowercase())
                val request = Request.Builder()
                    .url("${SupabaseConfig.SUPABASE_URL}/storage/v1/object/authenticated/$HARMONY_AVATAR_BUCKET/$objectPath")
                    .get()
                    .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
                    .header("Authorization", "Bearer $accessToken")
                    .build()

                avatarHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@runCatching null
                    val bytes = response.body?.bytes() ?: return@runCatching null
                    if (bytes.isEmpty()) return@runCatching null
                    cacheFile.writeBytes(bytes)
                    cacheFile
                }
            }.getOrNull()
        }
    }

    val directProviderUrl = avatarRef?.takeIf {
        objectPath == null && (it.startsWith("https://") || it.startsWith("http://"))
    }
    val model: Any? = privateFile ?: directProviderUrl

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))),
        contentAlignment = Alignment.Center
    ) {
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Text(
                text = displayName.trim().take(1).ifBlank { "?" }.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black
            )
        }
    }
}
