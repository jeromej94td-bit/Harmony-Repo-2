package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LoveBalanceQuestionPolicy
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.ui.tr
import kotlinx.coroutines.delay

private val happyCoupleImages = listOf(
    R.drawable.happy_couple_01,
    R.drawable.happy_couple_02,
    R.drawable.happy_couple_03,
    R.drawable.happy_couple_04
)

private val happyCoupleFallbackOptions = listOf("1", "2", "3", "4")

@Composable
internal fun HarmonyHappyCoupleQuestion(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val answerOptions = options.takeIf {
        it.size >= 4 && it.take(4) == happyCoupleFallbackOptions
    }?.take(4) ?: happyCoupleFallbackOptions
    val displayQuestion = question.takeIf { it == LoveBalanceQuestionPolicy.QUESTION_TEXT }
        ?: LoveBalanceQuestionPolicy.QUESTION_TEXT
    val containerShape = RoundedCornerShape(28.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.32f),
                        HarmonySurface2.copy(alpha = 0.96f),
                        HarmonyBg.copy(alpha = 0.98f)
                    )
                )
            )
            .border(1.2.dp, HarmonyPink.copy(alpha = 0.42f), containerShape)
            .padding(horizontal = 10.dp, vertical = 18.dp)
            .testTag("harmony_happy_couple_question"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayQuestion,
            color = HarmonyText,
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 31.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tr(
                "Wähle das Paar, das für dich am glücklichsten wirkt.",
                "Choose the couple that looks happiest to you."
            ),
            color = Color(0xFFFFB8DB),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        answerOptions.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowOptions.forEachIndexed { columnIndex, option ->
                    val index = rowIndex * 2 + columnIndex
                    HarmonyHappyCoupleCard(
                        animationKey = "happy_couple_${displayQuestion}_$index",
                        index = index,
                        option = option,
                        imageRes = happyCoupleImages[index],
                        selected = selectedAnswer == option,
                        onClick = { onPick(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (rowIndex == 0) Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HarmonyHappyCoupleCard(
    animationKey: Any,
    index: Int,
    option: String,
    @DrawableRes imageRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reveal = remember(animationKey) { Animatable(0f) }
    val density = LocalDensity.current.density
    val shape = RoundedCornerShape(24.dp)

    LaunchedEffect(animationKey) {
        reveal.snapTo(0f)
        delay(happyCoupleRevealDelayMillis(index))
        reveal.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = HAPPY_COUPLE_REVEAL_DURATION_MILLIS,
                easing = FastOutSlowInEasing
            )
        )
    }

    val progress = reveal.value.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .graphicsLayer {
                alpha = progress
                rotationY = -82f * (1f - progress)
                translationX = -18f * (1f - progress)
                scaleX = 0.91f + progress * 0.09f
                scaleY = 0.94f + progress * 0.06f
                transformOrigin = TransformOrigin(0f, 0.5f)
                cameraDistance = 28f * density
                shadowElevation = if (selected) 18f else 7f * progress
            }
            .clip(shape)
            .background(HarmonyBg)
            .border(
                width = if (selected) 2.4.dp else 0.8.dp,
                brush = Brush.linearGradient(
                    if (selected) {
                        listOf(Color.White, HarmonyPink, HarmonyPurpleLight, HarmonyPink)
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            HarmonyPink.copy(alpha = 0.30f),
                            HarmonyPurpleLight.copy(alpha = 0.24f)
                        )
                    }
                ),
                shape = shape
            )
            .clickable(enabled = progress > 0.86f, onClick = onClick)
            .testTag(
                if (selected) "happy_couple_option_${index}_selected"
                else "happy_couple_option_$index"
            )
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = tr("Paar $option", "Couple $option"),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                HarmonyPink.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}
