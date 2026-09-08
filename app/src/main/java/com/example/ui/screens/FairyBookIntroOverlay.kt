package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val FairyInk = Color(0xFF160A2A)
private val FairyPurple = Color(0xFF6F35D8)
private val FairyPink = Color(0xFFFF5BA7)
private val FairyGold = Color(0xFFFFD978)
private val FairyBlue = Color(0xFF78D8FF)

private data class FairyDustParticle(    val phaseOffset: Float,
    val orbit: Float,
    val speed: Float,
    val size: Float,
    val warm: Boolean
)

@Composable
fun FairyBookIntroOverlay(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var useFallback by remember { mutableStateOf(false) }
    if (useFallback) {
        FairyBookCanvasFallback(onFinished = onFinished, modifier = modifier)
    } else {
        FairyBookFilamentIntro(
            onFinished = onFinished,
            onUnavailable = { useFallback = true },
            modifier = modifier
        )
    }
}

@Composable
fun FairyBookCanvasFallback(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val infinite = rememberInfiniteTransition(label = "fairy_book_ambient")
    val particlePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fairy_dust_phase"
    )

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(3100, easing = LinearEasing)
        )
        delay(60)
        onFinished()
    }

    val fadeOut = segment(progress.value, 0.95f, 1f)
    val overlayAlpha = 1f - fadeOut

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = overlayAlpha }
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4D1B79),
                        Color(0xFF1B0C31),
                        Color(0xFF08050F)
                    ),
                    radius = 1500f
                )
            )
            .testTag("fairy_book_intro")
            .semantics {
                contentDescription = "Harmony"
            }
    ) {
        AuroraPortalGlow(progress.value)
        FairyDustField(
            revealProgress = segment(progress.value, 0.24f, 0.90f),
            phase = particlePhase
        )
        FairyBurstRays(
            revealProgress = smooth(segment(progress.value, 0.50f, 0.90f))
        )
        MagicalHarmonyBook(
            progress = progress.value,
            modifier = Modifier.align(Alignment.Center)
        )
        IntroCaption(
            progress = progress.value,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AuroraPortalGlow(progress: Float) {
    val pulse = 0.74f + 0.26f * sin(progress * PI.toFloat() * 5f)
    Canvas(Modifier.fillMaxSize()) {
        val center = Offset(size.width * 0.5f, size.height * 0.44f)
        val radius = size.minDimension * (0.34f + segment(progress, 0f, 0.5f) * 0.12f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    FairyGold.copy(alpha = 0.16f * pulse),
                    FairyPink.copy(alpha = 0.18f * pulse),
                    FairyPurple.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
        drawCircle(
            color = FairyBlue.copy(alpha = 0.12f * pulse),
            radius = radius * 0.77f,
            center = center,
            style = Stroke(width = 2.2.dp.toPx())
        )
        drawCircle(
            color = FairyGold.copy(alpha = 0.18f * pulse),
            radius = radius * 0.93f,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

@Composable
private fun FairyDustField(
    revealProgress: Float,
    phase: Float
) {
    val particles = remember {
        List(72) { index ->
            FairyDustParticle(
                phaseOffset = ((index * 37) % 101) / 101f,
                orbit = 0.28f + ((index * 19) % 70) / 100f,
                speed = 0.55f + ((index * 13) % 60) / 100f,
                size = 0.55f + ((index * 23) % 80) / 100f,
                warm = index % 3 != 0
            )
        }
    }

    Canvas(Modifier.fillMaxSize()) {
        val origin = Offset(size.width * 0.5f, size.height * 0.45f)
        val baseRadius = size.minDimension * 0.37f
        particles.forEachIndexed { index, particle ->
            val local = (phase * particle.speed + particle.phaseOffset) % 1f
            val spiral = local * PI.toFloat() * (2.6f + particle.orbit)
            val radius = baseRadius * (0.18f + local * particle.orbit)
            val x = origin.x + cos(spiral) * radius
            val y = origin.y + sin(spiral) * radius * 0.58f - local * size.height * 0.10f
            val life = sin(local * PI.toFloat()).coerceAtLeast(0f)
            val burst = revealProgress * life
            val particleColor = if (particle.warm) FairyGold else FairyBlue
            drawCircle(
                color = particleColor.copy(alpha = burst * 0.82f),
                radius = (1.4.dp.toPx() + particle.size * 1.9.dp.toPx()),
                center = Offset(x, y)
            )
            if (index % 7 == 0) {
                drawLine(
                    color = Color.White.copy(alpha = burst * 0.52f),
                    start = Offset(x - 4.dp.toPx(), y),
                    end = Offset(x + 4.dp.toPx(), y),
                    strokeWidth = 0.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun FairyBurstRays(revealProgress: Float) {
    Canvas(Modifier.fillMaxSize()) {
        if (revealProgress <= 0f) return@Canvas
        val origin = Offset(size.width * 0.5f, size.height * 0.455f)
        val baseLength = size.minDimension * (0.17f + revealProgress * 0.16f)
        repeat(18) { index ->
            val angle = (-PI.toFloat() * 0.88f) + index * (PI.toFloat() * 1.76f / 17f)
            val inner = 24.dp.toPx() + (index % 3) * 6.dp.toPx()
            val length = baseLength * (0.72f + (index % 4) * 0.09f)
            val start = Offset(origin.x + cos(angle) * inner, origin.y + sin(angle) * inner)
            val end = Offset(origin.x + cos(angle) * length, origin.y + sin(angle) * length)
            val rayColor = if (index % 2 == 0) FairyGold else FairyPink
            drawLine(
                color = rayColor.copy(alpha = revealProgress * 0.24f),
                start = start,
                end = end,
                strokeWidth = (0.8f + (index % 3) * 0.45f).dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        drawCircle(
            brush = Brush.radialGradient(
                listOf(
                    Color.White.copy(alpha = revealProgress * 0.38f),
                    FairyGold.copy(alpha = revealProgress * 0.25f),
                    FairyPink.copy(alpha = revealProgress * 0.14f),
                    Color.Transparent
                ),
                center = origin,
                radius = size.minDimension * 0.26f
            ),
            radius = size.minDimension * 0.26f,
            center = origin
        )
    }
}

@Composable
private fun MagicalHarmonyBook(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val arrive = smooth(segment(progress, 0f, 0.22f))
    val open = smooth(segment(progress, 0.18f, 0.72f))
    val reveal = smooth(segment(progress, 0.50f, 0.90f))
    val hover = sin(progress * PI.toFloat() * 2.1f) * 7f
    val cameraPullback = 1f - open * 0.10f

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val bookWidth = minOf(maxWidth * 0.82f, 340.dp)
        val bookHeight = bookWidth * 0.62f

        Box(
            modifier = Modifier
                .size(bookWidth, bookHeight)
                .graphicsLayer {
                    val arrivalScale = 0.68f + arrive * 0.32f
                    scaleX = arrivalScale * cameraPullback
                    scaleY = arrivalScale * cameraPullback
                    translationY = (1f - arrive) * 120f + hover
                    rotationZ = -8f * (1f - arrive)
                    cameraDistance = density * 24f
                }
        ) {
            BookAura(reveal = reveal)
            OpenPageSpread(open = open, reveal = reveal)
            TurningPage(progress = segment(progress, 0.35f, 0.60f), delayBias = 0f)
            TurningPage(progress = segment(progress, 0.41f, 0.68f), delayBias = 0.35f)
            TurningPage(progress = segment(progress, 0.47f, 0.75f), delayBias = 0.70f)
            MagicFrontCover(openProgress = open)
            BookSpine(openProgress = open)
        }
    }
}

@Composable
private fun BookAura(reveal: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val center = Offset(size.width * 0.5f, size.height * 0.54f)
        val radius = size.width * (0.44f + reveal * 0.15f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    FairyGold.copy(alpha = 0.22f + reveal * 0.22f),
                    FairyPink.copy(alpha = 0.18f + reveal * 0.18f),
                    FairyPurple.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}

@Composable
private fun OpenPageSpread(open: Float, reveal: Float) {
    val pageShape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 9.dp, vertical = 12.dp)
            .clip(pageShape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFF3D8),
                        Color(0xFFFFFBEE),
                        Color(0xFFFFF0D0)
                    )
                )
            )
            .border(1.dp, FairyGold.copy(alpha = 0.44f), pageShape)
    ) {
        MagicPageInk(open = open, reveal = reveal)
    }
}

@Composable
private fun MagicPageInk(open: Float, reveal: Float) {
    val inkAlpha = open * (0.32f + reveal * 0.68f)
    Canvas(Modifier.fillMaxSize()) {
        val centerX = size.width * 0.5f
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(
                    Color.Transparent,
                    FairyPurple.copy(alpha = 0.16f * open),
                    Color.Transparent
                )
            ),
            topLeft = Offset(centerX - 12.dp.toPx(), 0f),
            size = Size(24.dp.toPx(), size.height)
        )
        repeat(5) { line ->
            val y = size.height * (0.30f + line * 0.09f)
            drawLine(
                color = FairyInk.copy(alpha = inkAlpha * 0.34f),
                start = Offset(size.width * 0.09f, y),
                end = Offset(size.width * 0.39f, y),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = FairyInk.copy(alpha = inkAlpha * 0.34f),
                start = Offset(size.width * 0.61f, y),
                end = Offset(size.width * 0.91f, y),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        val starCenter = Offset(size.width * 0.5f, size.height * 0.53f)
        drawCircle(FairyGold.copy(alpha = reveal * 0.40f), 18.dp.toPx(), starCenter)
        drawCircle(Color.White.copy(alpha = reveal * 0.76f), 5.dp.toPx(), starCenter)
    }

    if (reveal > 0.08f) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .graphicsLayer { alpha = reveal },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HARMONY",
                color = FairyPurple.copy(alpha = 0.86f),
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun BoxScope.TurningPage(progress: Float, delayBias: Float) {
    val density = LocalDensity.current.density
    val pageTint = if (delayBias < 0.5f) Color(0xFFFFF7E8) else Color(0xFFFFEDDA)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.49f)
            .fillMaxHeight(0.86f)
            .offset(x = (-6).dp)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0.5f)
                rotationY = -164f * smooth(progress)
                cameraDistance = density * 26f
                alpha = 0.92f - segment(progress, 0.94f, 1f) * 0.42f
            }
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color.White, pageTint, FairyGold.copy(alpha = 0.24f))
                )
            )
            .border(
                0.7.dp,
                FairyGold.copy(alpha = 0.36f + delayBias * 0.16f),
                RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
            )
            .align(Alignment.CenterEnd)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            repeat(4) { line ->
                val y = size.height * (0.34f + line * 0.11f)
                drawLine(
                    color = FairyPurple.copy(alpha = 0.10f),
                    start = Offset(size.width * 0.17f, y),
                    end = Offset(size.width * 0.78f, y),
                    strokeWidth = 0.8.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun MagicFrontCover(openProgress: Float) {
    val density = LocalDensity.current.density
    val coverShape = RoundedCornerShape(22.dp)
    val frontFaceAlpha = 1f - smooth(segment(openProgress, 0.76f, 0.96f))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0.5f)
                rotationY = -168f * openProgress
                cameraDistance = density * 30f
                shadowElevation = 24f * (1f - openProgress * 0.55f)
                alpha = frontFaceAlpha
            }
            .clip(coverShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2C0E4D),
                        Color(0xFF7D2EA8),
                        Color(0xFF38115C),
                        Color(0xFF12051F)
                    )
                )
            )
            .border(
                2.dp,
                Brush.sweepGradient(
                    listOf(FairyGold, FairyPink, FairyBlue, FairyGold)
                ),
                coverShape
            )
    ) {
        CoverEngraving(openProgress)
        Text(
            text = "HARMONY",
            color = FairyGold.copy(alpha = 0.96f),
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            modifier = Modifier.align(Alignment.Center)
        )
        Text(
            text = "✦",
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-44).dp)
        )
    }
}

@Composable
private fun CoverEngraving(openProgress: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val inset = 18.dp.toPx()
        drawRoundRect(
            color = FairyGold.copy(alpha = 0.28f + (1f - openProgress) * 0.20f),
            topLeft = Offset(inset, inset),
            size = Size(size.width - inset * 2f, size.height - inset * 2f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )
        val c = Offset(size.width * 0.5f, size.height * 0.50f)
        val r = size.minDimension * 0.16f
        val gem = Path().apply {
            moveTo(c.x, c.y - r)
            lineTo(c.x + r * 0.72f, c.y)
            lineTo(c.x, c.y + r)
            lineTo(c.x - r * 0.72f, c.y)
            close()
        }
        drawPath(
            path = gem,
            brush = Brush.radialGradient(
                listOf(Color.White, FairyPink, FairyPurple),
                center = c,
                radius = r * 1.3f
            )
        )
        drawPath(
            path = gem,
            color = FairyGold.copy(alpha = 0.82f),
            style = Stroke(width = 1.4.dp.toPx())
        )
        drawCircle(Color.White.copy(alpha = 0.70f), 3.5.dp.toPx(), c - Offset(r * 0.18f, r * 0.18f))
    }
}

@Composable
private fun BookSpine(openProgress: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val spineWidth = 10.dp.toPx()
        val top = size.height * 0.09f
        val height = size.height * 0.82f
        val alpha = 0.42f + openProgress * 0.46f
        drawRoundRect(
            brush = Brush.horizontalGradient(
                listOf(
                    FairyPurple.copy(alpha = alpha * 0.32f),
                    FairyGold.copy(alpha = alpha),
                    FairyPink.copy(alpha = alpha * 0.42f)
                )
            ),
            topLeft = Offset(size.width * 0.5f - spineWidth / 2f, top),
            size = Size(spineWidth, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(spineWidth / 2f)
        )
        drawLine(
            color = Color.White.copy(alpha = openProgress * 0.44f),
            start = Offset(size.width * 0.5f, top + 8.dp.toPx()),
            end = Offset(size.width * 0.5f, top + height - 8.dp.toPx()),
            strokeWidth = 0.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun IntroCaption(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val reveal = smooth(segment(progress, 0.50f, 0.76f))
    val lift = (1f - reveal) * 24f
    Column(
        modifier = modifier
            .padding(horizontal = 28.dp, vertical = 78.dp)
            .graphicsLayer {
                alpha = reveal
                translationY = lift
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✦  HARMONY  ✦",
            color = FairyGold.copy(alpha = 0.94f),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
    }
}

private fun segment(value: Float, start: Float, end: Float): Float {
    if (end <= start) return if (value >= end) 1f else 0f
    return ((value - start) / (end - start)).coerceIn(0f, 1f)
}

private fun smooth(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}
