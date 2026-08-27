package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Native Compose recreation of the supplied Harmony video effect:
 * dark cosmic background, orbit lines, particles, glowing gradient heart
 * and a breathing organic energy halo.
 */
@Composable
fun HarmonyVideoHeartAnimation(
    modifier: Modifier = Modifier,
    contentScale: Float = 1f
) {
    val transition = rememberInfiniteTransition(label = "harmony_video_effect")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7200, easing = FastOutSlowInEasing)),
        label = "phase"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val stars = remember {
        List(34) { index ->
            val angle = index * 2.39996f
            val radius = 0.18f + ((index * 37) % 73) / 100f
            Triple(cos(angle) * radius, sin(angle) * radius, 0.45f + (index % 5) * 0.12f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height * 0.45f)
        val unit = minOf(size.width, size.height) * 0.43f * contentScale
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF321044), Color(0xFF120A22), Color(0xFF080510)),
                center = center,
                radius = size.maxDimension * 0.8f
            )
        )

        stars.forEachIndexed { index, (x, y, alpha) ->
            val drift = phase * (if (index % 2 == 0) 0.018f else -0.018f)
            val p = Offset(center.x + (x + drift) * size.width, center.y + y * size.height)
            drawCircle(Color(0xFFE9D7FF).copy(alpha = alpha), 1.2f + (index % 3), p)
        }

        repeat(4) { orbit ->
            drawOval(
                color = Color(0xFFB58BCE).copy(alpha = 0.13f),
                topLeft = Offset(center.x - unit * (1.05f + orbit * 0.12f), center.y - unit * (0.82f - orbit * 0.04f)),
                size = androidx.compose.ui.geometry.Size(unit * (2.1f + orbit * 0.18f), unit * (1.55f + orbit * 0.12f)),
                style = Stroke(width = 1.1f)
            )
        }

        val halo = Path().apply {
            val points = 96
            for (i in 0..points) {
                val a = i / points.toFloat() * 6.28318f
                val wave = 1f + 0.10f * sin(a * 7f + phase * 6.28318f) + 0.045f * sin(a * 13f - phase * 4f)
                val r = unit * 0.78f * wave
                val p = Offset(center.x + cos(a) * r, center.y + sin(a) * r * 0.86f)
                if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(
            halo,
            brush = Brush.sweepGradient(listOf(Color(0xFFFF65B5), Color(0xFF8C55FF), Color(0xFF38C7FF), Color(0xFFFF65B5))),
            style = Stroke(width = unit * 0.055f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            alpha = 0.25f
        )
        drawPath(
            halo,
            brush = Brush.sweepGradient(listOf(Color(0xFFFF8CCB), Color(0xFFB06CFF), Color(0xFF5EE7FF), Color(0xFFFF8CCB))),
            style = Stroke(width = unit * 0.018f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            alpha = 0.85f
        )

        val heart = Path().apply {
            moveTo(center.x, center.y + unit * 0.72f)
            cubicTo(center.x - unit * 0.28f, center.y + unit * 0.42f, center.x - unit * 0.78f, center.y + unit * 0.10f, center.x - unit * 0.76f, center.y - unit * 0.23f)
            cubicTo(center.x - unit * 0.74f, center.y - unit * 0.60f, center.x - unit * 0.22f, center.y - unit * 0.62f, center.x, center.y - unit * 0.25f)
            cubicTo(center.x + unit * 0.22f, center.y - unit * 0.62f, center.x + unit * 0.74f, center.y - unit * 0.60f, center.x + unit * 0.76f, center.y - unit * 0.23f)
            cubicTo(center.x + unit * 0.78f, center.y + unit * 0.10f, center.x + unit * 0.28f, center.y + unit * 0.42f, center.x, center.y + unit * 0.72f)
        }
        val heartBrush = Brush.linearGradient(
            0f to Color(0xFFFF55A9),
            0.48f to Color(0xFFD52EFF),
            1f to Color(0xFF35CFFF),
            start = Offset(center.x - unit, center.y - unit),
            end = Offset(center.x + unit, center.y + unit)
        )
        val heartStroke = Stroke(width = unit * 0.16f * pulse, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawPath(heart, heartBrush, style = heartStroke, alpha = 0.22f)
        drawPath(heart, heartBrush, style = Stroke(width = unit * 0.105f * pulse, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(Color.White.copy(alpha = 0.9f), unit * 0.055f, Offset(center.x, center.y + unit * 0.02f))
        drawCircle(Color(0xFFFFB6FF).copy(alpha = 0.7f), unit * 0.12f, Offset(center.x, center.y + unit * 0.02f), style = Stroke(width = unit * 0.018f))
    }
}
