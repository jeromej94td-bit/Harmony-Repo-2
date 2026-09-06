package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.util.Locale

/** Original, compact panda-kid scenes for Paarlabor questions. */
internal enum class PandaQuestionScene { PEACE_BETWEEN_SIBLINGS, BEDTIME_ROUTINE, WARM_CHILDREN_MOMENT }

internal object PandaQuestionScenePolicy {
    fun forQuestion(question: String): PandaQuestionScene {
        val normalized = question.lowercase(Locale.ROOT)
        return when {
            normalized.contains("geschwister") || normalized.contains("frieden") -> PandaQuestionScene.PEACE_BETWEEN_SIBLINGS
            normalized.contains("schlafenszeit") || normalized.contains("bettzeit") || normalized.contains("routine") -> PandaQuestionScene.BEDTIME_ROUTINE
            else -> PandaQuestionScene.WARM_CHILDREN_MOMENT
        }
    }
}

@Composable
internal fun PandaQuestionSceneBanner(question: String, modifier: Modifier = Modifier) {
    val scene = PandaQuestionScenePolicy.forQuestion(question)
    val transition = rememberInfiniteTransition(label = "panda_question_scene")
    val glow by transition.animateFloat(0.38f, 0.72f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing)), label = "panda_question_scene_glow")
    Canvas(modifier.fillMaxWidth().height(64.dp).testTag("panda_question_scene_${scene.name.lowercase()}")) {
        val pink = Color(0xFFFF5FA7)
        val violet = Color(0xFFAF75FF)
        drawRoundRect(Color(0xFF120D20).copy(alpha = 0.52f), size = size, cornerRadius = androidx.compose.ui.geometry.CornerRadius(26.dp.toPx()))
        drawCircle(violet.copy(alpha = glow * 0.30f), size.minDimension * 0.42f, Offset(size.width * 0.22f, size.height * 0.36f))
        drawCircle(pink.copy(alpha = glow * 0.24f), size.minDimension * 0.48f, Offset(size.width * 0.78f, size.height * 0.60f))
        when (scene) {
            PandaQuestionScene.PEACE_BETWEEN_SIBLINGS -> drawPeaceScene(pink, violet)
            PandaQuestionScene.BEDTIME_ROUTINE -> drawBedtimeScene(pink, violet)
            PandaQuestionScene.WARM_CHILDREN_MOMENT -> drawWarmScene(pink, violet)
        }
    }
}

private fun DrawScope.drawPeaceScene(pink: Color, violet: Color) {
    val radius = size.height * 0.29f
    drawPandaKid(Offset(size.width * 0.28f, size.height * 0.59f), radius, pink, false)
    drawPandaKid(Offset(size.width * 0.72f, size.height * 0.59f), radius, violet, true)
    drawCircle(Color.White.copy(alpha = 0.80f), radius * 0.22f, Offset(size.width * 0.50f, size.height * 0.26f))
    drawCircle(pink.copy(alpha = 0.92f), radius * 0.12f, Offset(size.width * 0.45f, size.height * 0.34f))
    drawCircle(violet.copy(alpha = 0.92f), radius * 0.12f, Offset(size.width * 0.55f, size.height * 0.34f))
    drawLine(pink.copy(alpha = 0.88f), Offset(size.width * 0.38f, size.height * 0.66f), Offset(size.width * 0.48f, size.height * 0.57f), 4.dp.toPx())
    drawLine(violet.copy(alpha = 0.88f), Offset(size.width * 0.62f, size.height * 0.66f), Offset(size.width * 0.52f, size.height * 0.57f), 4.dp.toPx())
}

private fun DrawScope.drawBedtimeScene(pink: Color, violet: Color) {
    val bedTop = size.height * 0.58f
    drawRoundRect(violet.copy(alpha = 0.72f), Offset(size.width * 0.20f, bedTop), Size(size.width * 0.60f, size.height * 0.25f), androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()))
    drawRoundRect(pink.copy(alpha = 0.64f), Offset(size.width * 0.26f, bedTop + size.height * 0.08f), Size(size.width * 0.48f, size.height * 0.17f), androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()))
    drawPandaKid(Offset(size.width * 0.42f, size.height * 0.56f), size.height * 0.24f, pink, false)
    drawPandaKid(Offset(size.width * 0.60f, size.height * 0.56f), size.height * 0.24f, violet, true)
    drawCircle(Color(0xFFFFE8A3), size.height * 0.11f, Offset(size.width * 0.82f, size.height * 0.25f))
    drawCircle(Color.White.copy(alpha = 0.72f), size.height * 0.025f, Offset(size.width * 0.72f, size.height * 0.20f))
}

private fun DrawScope.drawWarmScene(pink: Color, violet: Color) {
    val radius = size.height * 0.29f
    drawPandaKid(Offset(size.width * 0.37f, size.height * 0.59f), radius, pink, false)
    drawPandaKid(Offset(size.width * 0.63f, size.height * 0.59f), radius, violet, true)
    drawHeart(Offset(size.width * 0.50f, size.height * 0.24f), size.height * 0.16f, pink)
}

private fun DrawScope.drawPandaKid(center: Offset, radius: Float, accent: Color, bow: Boolean) {
    val ink = Color(0xFF1A1422)
    val cream = Color(0xFFFFF8FB)
    drawCircle(ink, radius * 0.43f, Offset(center.x - radius * 0.63f, center.y - radius * 0.58f)); drawCircle(ink, radius * 0.43f, Offset(center.x + radius * 0.63f, center.y - radius * 0.58f)); drawCircle(cream, radius, center)
    drawOval(ink, Offset(center.x - radius * 0.56f, center.y - radius * 0.30f), Size(radius * 0.42f, radius * 0.58f)); drawOval(ink, Offset(center.x + radius * 0.14f, center.y - radius * 0.30f), Size(radius * 0.42f, radius * 0.58f))
    drawCircle(Color.White, radius * 0.10f, Offset(center.x - radius * 0.35f, center.y - radius * 0.06f)); drawCircle(Color.White, radius * 0.10f, Offset(center.x + radius * 0.35f, center.y - radius * 0.06f)); drawCircle(ink, radius * 0.11f, Offset(center.x, center.y + radius * 0.18f))
    drawCircle(accent.copy(alpha = 0.48f), radius * 0.20f, Offset(center.x - radius * 0.59f, center.y + radius * 0.28f)); drawCircle(accent.copy(alpha = 0.48f), radius * 0.20f, Offset(center.x + radius * 0.59f, center.y + radius * 0.28f))
    if (bow) { drawCircle(accent, radius * 0.23f, Offset(center.x + radius * 0.72f, center.y - radius * 0.73f)); drawCircle(Color.White.copy(alpha = 0.62f), radius * 0.06f, Offset(center.x + radius * 0.66f, center.y - radius * 0.80f)) }
}

private fun DrawScope.drawHeart(center: Offset, radius: Float, color: Color) {
    drawCircle(color, radius * 0.62f, Offset(center.x - radius * 0.43f, center.y - radius * 0.18f)); drawCircle(color, radius * 0.62f, Offset(center.x + radius * 0.43f, center.y - radius * 0.18f))
    drawPath(androidx.compose.ui.graphics.Path().apply { moveTo(center.x - radius, center.y); lineTo(center.x + radius, center.y); lineTo(center.x, center.y + radius); close() }, color)
}
