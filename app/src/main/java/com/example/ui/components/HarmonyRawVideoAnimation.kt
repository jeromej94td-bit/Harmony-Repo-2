package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun HarmonyRawVideoAnimation(
    rawResId: Int,
    modifier: Modifier = Modifier
) {
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
                        player.isLooping = true
                        player.setVolume(0f, 0f)
                        start()
                    }
                    setOnCompletionListener {
                        seekTo(0)
                        start()
                    }
                }
            },
            update = { videoView ->
                if (!videoView.isPlaying) videoView.start()
            }
        )
    }
}
