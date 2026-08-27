package com.example.ui.components

import android.app.Activity
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

@Composable
fun HarmonyRawVideoAnimation(
    rawResId: Int,
    modifier: Modifier = Modifier,
    immersive: Boolean = false,
    onCompleted: () -> Unit = {}
) {
    val view = LocalView.current
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

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color.Transparent)
    ) {
        val context = androidx.compose.ui.platform.LocalContext.current
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
                    setOnCompletionListener {
                        onCompleted()
                    }
                }
            },
            update = { videoView ->
                if (!videoView.isPlaying && videoView.currentPosition == 0) {
                    videoView.start()
                }
            }
        )
    }
}
