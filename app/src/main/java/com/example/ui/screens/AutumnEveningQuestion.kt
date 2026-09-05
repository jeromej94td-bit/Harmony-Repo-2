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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.contentText
import kotlinx.coroutines.delay

private val AutumnAubergine = Color(0xFF251827)
private val AutumnBlackberry = Color(0xFF140F18)
private val AutumnCopper = Color(0xFFC88862)
private val AutumnRoseGold = Color(0xFFE2A27D)
private val AutumnIvory = Color(0xFFFFF4E6)
internal data class AutumnEveningVisuals(
    val subtitle: String,
    @param:DrawableRes val images: List<Int>
)

internal fun autumnEveningVisuals(kind: HarmonyImageChoiceKind): AutumnEveningVisuals = when (kind) {
    HarmonyImageChoiceKind.AUTUMN_STORY -> AutumnEveningVisuals(
        subtitle = "Wählt die Stimmung für eure Geschichte.",
        images = listOf(
            R.drawable.autumn_story_01,
            R.drawable.autumn_story_02,
            R.drawable.autumn_story_03,
            R.drawable.autumn_story_04
        )
    )
    HarmonyImageChoiceKind.AUTUMN_DRINK -> AutumnEveningVisuals(
        subtitle = "Etwas Warmes für kalte Hände.",
        images = listOf(
            R.drawable.autumn_drink_01,
            R.drawable.autumn_drink_02,
            R.drawable.autumn_drink_03,
            R.drawable.autumn_drink_04
        )
    )
    HarmonyImageChoiceKind.AUTUMN_SNACK -> AutumnEveningVisuals(
        subtitle = "Der süße Begleiter für euren Abend.",
        images = listOf(
            R.drawable.autumn_snack_01,
            R.drawable.autumn_snack_02,
            R.drawable.autumn_snack_03,
            R.drawable.autumn_snack_04
        )
    )
    HarmonyImageChoiceKind.AUTUMN_NOOK -> AutumnEveningVisuals(
        subtitle = "Findet euren gemütlichsten Rückzugsort.",
        images = listOf(
            R.drawable.autumn_nook_01,
            R.drawable.autumn_nook_02,
            R.drawable.autumn_nook_03,
            R.drawable.autumn_nook_04
        )
    )
    HarmonyImageChoiceKind.AUTUMN_SOUND -> AutumnEveningVisuals(
        subtitle = "Welcher Klang macht den Moment komplett?",
        images = listOf(
            R.drawable.autumn_sound_01,
            R.drawable.autumn_sound_02,
            R.drawable.autumn_sound_03,
            R.drawable.autumn_sound_04
        )
    )
    HarmonyImageChoiceKind.AUTUMN_SCENT -> AutumnEveningVisuals(
        subtitle = "Der Duft, der noch lange bleibt.",
        images = listOf(
            R.drawable.autumn_scent_01,
            R.drawable.autumn_scent_02,
            R.drawable.autumn_scent_03,
            R.drawable.autumn_scent_04
        )
    )
    else -> error("$kind is not an autumn evening image-choice kind")
}

@Composable
internal fun AutumnEveningQuestion(
    kind: HarmonyImageChoiceKind,
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (kind in HEART_OR_HEAD_KINDS) {
        HeartOrHeadQuestion(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            kind = kind,
            onPick = onPick,
            modifier = modifier
        )
        return
    }

    val visuals = autumnEveningVisuals(kind)
    check(options.size == visuals.images.size) {
        "Autumn evening rounds require exactly ${visuals.images.size} options, found ${options.size}"
    }
    val containerShape = RoundedCornerShape(30.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5A303D).copy(alpha = 0.58f),
                        AutumnAubergine.copy(alpha = 0.98f),
                        AutumnBlackberry
                    )
                )
            )
            .border(1.2.dp, AutumnCopper.copy(alpha = 0.54f), containerShape)
            .padding(horizontal = 12.dp, vertical = 17.dp)
            .testTag("autumn_evening_question"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(AutumnCopper.copy(alpha = 0.16f))
                .border(1.dp, AutumnCopper.copy(alpha = 0.48f), CircleShape)
                .padding(horizontal = 16.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🍂 ${contentText("Unser Herbstabend")}",
                color = AutumnIvory,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(11.dp))
        Text(
            text = contentText(question),
            color = AutumnIvory,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = contentText(visuals.subtitle),
            color = AutumnRoseGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(15.dp))

        options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowOptions.forEachIndexed { columnIndex, option ->
                    val index = rowIndex * 2 + columnIndex
                    AutumnEveningCard(
                        animationKey = "${kind.name}_${contentText(question)}_$index",
                        index = index,
                        question = contentText(question),
                        option = contentText(option),
                        imageRes = visuals.images[index],
                        selected = selectedAnswer == option,
                        onClick = { onPick(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (rowIndex == 0) Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun AutumnEveningCard(
    animationKey: String,
    index: Int,
    question: String,
    option: String,
    @DrawableRes imageRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reveal = remember(animationKey) { Animatable(0f) }
    val density = LocalDensity.current.density
    val haptics = LocalHapticFeedback.current
    val shape = RoundedCornerShape(22.dp)

    LaunchedEffect(animationKey) {
        reveal.snapTo(0f)
        delay(autumnEveningRevealDelayMillis(index))
        reveal.animateTo(1f, tween(durationMillis = 470, easing = FastOutSlowInEasing))
    }

    val progress = reveal.value.coerceIn(0f, 1f)
    val entranceDirection = if (index % 2 == 0) -1f else 1f
    Column(
        modifier = modifier
            .aspectRatio(0.78f)
            .graphicsLayer {
                alpha = progress
                rotationY = entranceDirection * 14f * (1f - progress)
                translationX = entranceDirection * 24f * density * (1f - progress)
                translationY = if (selected) -4f * density else 0f
                scaleX = 0.94f + progress * 0.06f
                scaleY = 0.94f + progress * 0.06f
                transformOrigin = TransformOrigin(0.5f, 0.5f)
                cameraDistance = 28f * density
                shadowElevation = if (selected) 14f else 5f * progress
            }
            .clip(shape)
            .background(AutumnAubergine.copy(alpha = 0.98f))
            .border(
                width = if (selected) 2.4.dp else 0.8.dp,
                color = if (selected) AutumnRoseGold else Color.White.copy(alpha = 0.16f),
                shape = shape
            )
            .semantics(mergeDescendants = true) {
                contentDescription = "$question: $option"
                this.selected = selected
            }
            .clickable(enabled = progress > 0.86f) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(bottom = 10.dp)
            .testTag(
                if (selected) "autumn_evening_option_${index}_selected"
                else "autumn_evening_option_$index"
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.03f)
                .clip(RoundedCornerShape(topStart = 21.dp, topEnd = 21.dp))
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, AutumnBlackberry.copy(alpha = 0.32f))
                        )
                    )
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = option,
            color = AutumnIvory,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 17.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 7.dp)
        )
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(25.dp)
                .clip(CircleShape)
                .background(if (selected) AutumnCopper else Color.White.copy(alpha = 0.08f))
                .border(
                    width = 1.dp,
                    color = if (selected) AutumnIvory else AutumnCopper.copy(alpha = 0.55f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = AutumnIvory,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}
