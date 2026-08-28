package com.example.ui.components

import android.app.Activity
import android.net.Uri
import android.util.Base64
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun HarmonyRawVideoAnimation(
    rawResId: Int = 0,
    modifier: Modifier = Modifier,
    immersive: Boolean = false,
    onCompleted: () -> Unit = {},
    roundedCorners: Boolean = true,
    assetPrefix: String? = null
) {
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(immersive, view) {
        val activity = view.context as? Activity
        val controller = activity?.window?.let { WindowCompat.getInsetsController(it, view) }
        if (immersive) {
            controller?.hide(WindowInsetsCompat.Type.systemBars())
            controller?.systemBarsBehavior =
                androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    var assetVideoPath by remember(assetPrefix, rawResId) { mutableStateOf<String?>(null) }
    var useRawFallback by remember(assetPrefix, rawResId) { mutableStateOf(false) }

    LaunchedEffect(assetPrefix, rawResId) {
        useRawFallback = false
        assetVideoPath = if (assetPrefix == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                val target = File(context.cacheDir, "harmony_${assetPrefix}intro.mp4")
                if (!target.exists() || target.length() != 7_297_407L) {
                    val chunkNames = context.assets
                        .list("introspection")
                        .orEmpty()
                        .filter { it.startsWith(assetPrefix) && it.endsWith(".b64") }
                        .sorted()
                    require(chunkNames.isNotEmpty()) { "No video chunks found for $assetPrefix" }

                    val encoded = buildString {
                        chunkNames.forEach { name ->
                            context.assets.open("introspection/$name").bufferedReader().use { append(it.readText()) }
                        }
                    }
                    val decoded = Base64.decode(encoded, Base64.DEFAULT)
                    require(decoded.size == 7_297_407) {
                        "Unexpected intro video size: ${decoded.size}"
                    }
                    target.writeBytes(decoded)
                }
                target.absolutePath
            }
        }
    }

    Box(
        modifier = modifier
            .then(if (roundedCorners) Modifier.clip(RoundedCornerShape(28.dp)) else Modifier)
            .background(Color.Black)
    ) {
        if (assetPrefix == null || useRawFallback) {
            val videoUri = remember(rawResId, context.packageName) {
                Uri.parse("android.resource://" + context.packageName + "/" + rawResId)
            }
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->
                    VideoView(viewContext).apply {
                        setVideoURI(videoUri)
                        setOnPreparedListener { player ->
                            player.setVolume(0f, 0f)
                            start()
                        }
                        setOnCompletionListener { onCompleted() }
                        setOnErrorListener { _, _, _ ->
                            if (assetPrefix != null && rawResId != 0 && !useRawFallback) {
                                useRawFallback = true
                                true
                            } else false
                        }
                    }
                },
                update = { videoView ->
                    if (!videoView.isPlaying && videoView.currentPosition == 0) videoView.start()
                }
            )
        } else {
            assetVideoPath?.let { path ->
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { viewContext ->
                        VideoView(viewContext).apply {
                            setVideoPath(path)
                            setOnPreparedListener { player ->
                                player.setVolume(0f, 0f)
                                start()
                            }
                            setOnCompletionListener { onCompleted() }
                        setOnErrorListener { _, _, _ ->
                            if (assetPrefix != null && rawResId != 0 && !useRawFallback) {
                                useRawFallback = true
                                true
                            } else false
                        }
                        }
                    },
                    update = { videoView ->
                        if (!videoView.isPlaying && videoView.currentPosition == 0) videoView.start()
                    }
                )
            }
        }
    }
}
