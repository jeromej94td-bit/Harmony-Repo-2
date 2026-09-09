package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
internal fun IslandHoppingPageTurn(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val turn = progress.coerceIn(0f, 1f)
    val pageShape = RoundedCornerShape(28.dp)

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(pageShape)
                .background(IslandBookPaper)
                .border(1.dp, IslandBookGold.copy(alpha = 0.42f), pageShape)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0.5f)
                    rotationY = -176f * turn
                    cameraDistance = density * 34f
                    shadowElevation = 18f * (1f - turn)
                    alpha = 1f - ((turn - 0.60f) / 0.40f).coerceIn(0f, 1f) * 0.82f
                }
                .clip(pageShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            IslandBookPaperDeep,
                            IslandBookPaper,
                            Color(0xFFFFF9E9)
                        )
                    )
                )
                .border(1.2.dp, IslandBookGold.copy(alpha = 0.74f), pageShape),
            content = content
        )

        Canvas(Modifier.fillMaxSize()) {
            val edgeX = size.width * (1f - turn * 0.97f)
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, IslandBookGold.copy(alpha = 0.28f * turn)),
                    startX = edgeX - 36.dp.toPx(),
                    endX = edgeX
                ),
                topLeft = Offset(edgeX - 36.dp.toPx(), 0f),
                size = androidx.compose.ui.geometry.Size(36.dp.toPx(), size.height)
            )
        }
    }
}
