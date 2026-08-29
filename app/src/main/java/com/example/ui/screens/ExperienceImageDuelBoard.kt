package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExperienceImageDuelOption
import com.example.data.model.ExperienceImageDuelRound
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import kotlinx.coroutines.delay

private enum class ExperienceImageDuelPhase {
    IntroCards,
    RevealQuestion,
    AwaitingSelection,
    SelectionLocked,
    TransitionOut
}

/** Stateless reusable two-card image choice for Harmony experiences. */
@Composable
internal fun ExperienceImageDuelBoard(
    round: ExperienceImageDuelRound,
    selectedOptionId: String?,
    imageResolver: (String) -> Int,
    onPick: (ExperienceImageDuelOption) -> Unit,
    modifier: Modifier = Modifier,
    kicker: String = "✦  BILD-DUELL",
    instruction: String = "Wählt das Bild, das sich für euch am stimmigsten anfühlt.",
    testTagPrefix: String = "experience_image_duel",
    rootTestTag: String = testTagPrefix
) {
    ExperienceImageDuelBoardContent(
        round = round,
        selectedOptionId = selectedOptionId,
        imageResolver = imageResolver,
        onPick = onPick,
        phase = ExperienceImageDuelPhase.AwaitingSelection,
        pickingEnabled = true,
        animateCardIntro = false,
        kicker = kicker,
        instruction = instruction,
        testTagPrefix = testTagPrefix,
        rootTestTag = rootTestTag,
        modifier = modifier
    )
}

/**
 * Reusable staged choreography extracted from the shipped proposal-location duels:
 * cards first, question second, locked selection, then coordinated fade + 3D card flip.
 */
@Composable
internal fun AnimatedExperienceImageDuelBoard(
    round: ExperienceImageDuelRound,
    selectedOptionId: String?,
    imageResolver: (String) -> Int,
    onPick: (ExperienceImageDuelOption) -> Unit,
    onTransitionFinished: () -> Unit,
    modifier: Modifier = Modifier,
    kicker: String = "✦  BILD-DUELL",
    instruction: String = "Wählt das Bild, das sich für euch am stimmigsten anfühlt.",
    testTagPrefix: String = "experience_image_duel",
    rootTestTag: String = testTagPrefix
) {
    var phase by remember(round.id) { mutableStateOf(ExperienceImageDuelPhase.IntroCards) }
    var lockedSelectionId by remember(round.id) { mutableStateOf<String?>(null) }
    val latestOnPick by rememberUpdatedState(onPick)
    val latestOnTransitionFinished by rememberUpdatedState(onTransitionFinished)

    LaunchedEffect(round.id) {
        lockedSelectionId = null
        phase = ExperienceImageDuelPhase.IntroCards
        delay(620)
        phase = ExperienceImageDuelPhase.RevealQuestion
        delay(120)
        phase = ExperienceImageDuelPhase.AwaitingSelection
    }

    LaunchedEffect(lockedSelectionId) {
        if (lockedSelectionId == null) return@LaunchedEffect
        delay(420)
        phase = ExperienceImageDuelPhase.TransitionOut
        delay(760)
        latestOnTransitionFinished()
    }

    val effectiveSelectionId = lockedSelectionId ?: selectedOptionId

    ExperienceImageDuelBoardContent(
        round = round,
        selectedOptionId = effectiveSelectionId,
        imageResolver = imageResolver,
        onPick = { option ->
            if (phase == ExperienceImageDuelPhase.AwaitingSelection && lockedSelectionId == null) {
                lockedSelectionId = option.id
                phase = ExperienceImageDuelPhase.SelectionLocked
                latestOnPick(option)
            }
        },
        phase = phase,
        pickingEnabled = phase == ExperienceImageDuelPhase.AwaitingSelection && lockedSelectionId == null,
        animateCardIntro = true,
        kicker = kicker,
        instruction = instruction,
        testTagPrefix = testTagPrefix,
        rootTestTag = rootTestTag,
        modifier = modifier
    )
}

@Composable
private fun ExperienceImageDuelBoardContent(
    round: ExperienceImageDuelRound,
    selectedOptionId: String?,
    imageResolver: (String) -> Int,
    onPick: (ExperienceImageDuelOption) -> Unit,
    phase: ExperienceImageDuelPhase,
    pickingEnabled: Boolean,
    animateCardIntro: Boolean,
    kicker: String,
    instruction: String,
    testTagPrefix: String,
    rootTestTag: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag(rootTestTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(154.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = phase != ExperienceImageDuelPhase.IntroCards && phase != ExperienceImageDuelPhase.TransitionOut,
                enter = fadeIn(tween(durationMillis = 320)) + slideInVertically(
                    animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
                    initialOffsetY = { it / 4 }
                ),
                exit = fadeOut(tween(durationMillis = 240)) + slideOutVertically(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    targetOffsetY = { -it / 5 }
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    HarmonyPurple.copy(alpha = 0.52f),
                                    HarmonySurface2.copy(alpha = 0.97f)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(30.dp))
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                        .testTag("${testTagPrefix}_question")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = kicker,
                            color = HarmonyPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = round.prompt,
                            color = Color.White,
                            fontSize = 23.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (instruction.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = instruction,
                                color = Color.White.copy(alpha = 0.72f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(320.dp).testTag("${testTagPrefix}_cards"),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExperienceImageDuelOptionCard(
                option = round.firstOption,
                imageResId = imageResolver(round.firstOption.imageKey),
                choiceNumber = 1,
                cardIndex = 0,
                selected = selectedOptionId == round.firstOption.id,
                anotherOptionSelected = selectedOptionId != null && selectedOptionId != round.firstOption.id,
                enabled = pickingEnabled,
                phase = phase,
                animateEntrance = animateCardIntro,
                onClick = { onPick(round.firstOption) },
                testTagPrefix = testTagPrefix,
                modifier = Modifier.weight(1f)
            )
            ExperienceImageDuelOptionCard(
                option = round.secondOption,
                imageResId = imageResolver(round.secondOption.imageKey),
                choiceNumber = 2,
                cardIndex = 1,
                selected = selectedOptionId == round.secondOption.id,
                anotherOptionSelected = selectedOptionId != null && selectedOptionId != round.secondOption.id,
                enabled = pickingEnabled,
                phase = phase,
                animateEntrance = animateCardIntro,
                onClick = { onPick(round.secondOption) },
                testTagPrefix = testTagPrefix,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ExperienceImageDuelOptionCard(
    option: ExperienceImageDuelOption,
    @DrawableRes imageResId: Int,
    choiceNumber: Int,
    cardIndex: Int,
    selected: Boolean,
    anotherOptionSelected: Boolean,
    enabled: Boolean,
    phase: ExperienceImageDuelPhase,
    animateEntrance: Boolean,
    onClick: () -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    var entered by remember(option.id, animateEntrance) { mutableStateOf(!animateEntrance) }

    LaunchedEffect(option.id, animateEntrance) {
        if (!animateEntrance) {
            entered = true
            return@LaunchedEffect
        }
        entered = false
        delay(if (cardIndex == 0) 30 else 170)
        entered = true
    }

    val isExiting = phase == ExperienceImageDuelPhase.TransitionOut
    val side = if (cardIndex == 0) -1f else 1f
    val horizontalOffset by animateDpAsState(
        targetValue = when {
            isExiting -> (16f * side).dp
            !entered -> (30f * side).dp
            else -> 0.dp
        },
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "experienceImageDuelOffset"
    )
    val rotationY by animateFloatAsState(
        targetValue = when {
            isExiting -> 82f * side
            !entered -> 18f * side
            else -> 0f
        },
        animationSpec = tween(durationMillis = 560, easing = FastOutSlowInEasing),
        label = "experienceImageDuelRotation"
    )
    val cardScale by animateFloatAsState(
        targetValue = when {
            isExiting -> 0.94f
            !entered -> 0.91f
            selected -> 1.025f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "experienceImageDuelScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = when {
            isExiting -> 0.08f
            !entered -> 0f
            anotherOptionSelected -> 0.72f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 360),
        label = "experienceImageDuelAlpha"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) HarmonyPink else Color.White.copy(alpha = 0.26f),
        animationSpec = tween(durationMillis = 240),
        label = "experienceImageDuelBorder"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = horizontalOffset.toPx()
                this.rotationY = rotationY
                scaleX = cardScale
                scaleY = cardScale
                alpha = cardAlpha
                cameraDistance = 14f * density
            }
            .shadow(if (selected) 18.dp else 7.dp, shape, clip = false)
            .clip(shape)
            .background(HarmonySurface2)
            .border(if (selected) 3.dp else 1.dp, borderColor, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .testTag("${testTagPrefix}_${option.id}")
    ) {
        ExperienceImageDuelImage(imageResId = imageResId, contentDescription = option.label)
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.04f),
                    0.52f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.82f)
                )
            )
        )
        if (selected) {
            Box(Modifier.fillMaxSize().background(HarmonyPink.copy(alpha = 0.08f)))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(HarmonyPink),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontWeight = FontWeight.Black)
            }
        }
        Text(
            text = option.label,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 19.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 14.dp, end = 14.dp, bottom = 62.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(HarmonySurface2.copy(alpha = 0.94f))
                .border(2.dp, HarmonyPink, CircleShape)
                .testTag("${testTagPrefix}_choice_$choiceNumber"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = choiceNumber.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun ExperienceImageDuelImage(
    @DrawableRes imageResId: Int,
    contentDescription: String
) {
    if (imageResId != 0) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(HarmonyPurple.copy(alpha = 0.30f)),
            contentAlignment = Alignment.Center
        ) {
            Text("✦", color = Color.White.copy(alpha = 0.82f), fontSize = 52.sp)
        }
    }
}
