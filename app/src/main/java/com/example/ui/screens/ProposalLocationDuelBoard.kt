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
import com.example.R
import com.example.data.model.ProposalImageDuelOption
import com.example.data.model.ProposalImageDuelRound
import com.example.data.model.ProposalLocationDuels
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import kotlinx.coroutines.delay

private val proposalLocationImages = mapOf(
    "location_home" to R.drawable.proposal_location_home,
    "location_lake" to R.drawable.proposal_location_lake,
    "location_garden" to R.drawable.proposal_location_garden,
    "location_view" to R.drawable.proposal_location_view,
    "location_city" to R.drawable.proposal_location_city,
    "location_coast" to R.drawable.proposal_location_coast
)

private enum class ProposalDuelPhase {
    IntroCards,
    RevealQuestion,
    AwaitingSelection,
    SelectionLocked,
    TransitionOut
}

/** Aurora-Glass presentation for one Stage 02.3 proposal-location duel. */
@Composable
internal fun ProposalLocationDuelBoard(
    round: ProposalImageDuelRound,
    selectedOptionId: String?,
    onPick: (ProposalImageDuelOption) -> Unit,
    modifier: Modifier = Modifier
) {
    ProposalLocationDuelBoardContent(
        round = round,
        selectedOptionId = selectedOptionId,
        onPick = onPick,
        phase = ProposalDuelPhase.AwaitingSelection,
        pickingEnabled = true,
        animateCardIntro = false,
        modifier = modifier
    )
}

/**
 * Plays the complete local Stage 02.3 location sequence without registering navigation.
 * Stage 02.11 can embed this composable into the end-to-end proposal runner later.
 */
@Composable
internal fun ProposalLocationDuelSequence(
    rounds: List<ProposalImageDuelRound> = ProposalLocationDuels.rounds,
    initialSelections: Map<String, String> = emptyMap(),
    onRoundPicked: (roundId: String, option: ProposalImageDuelOption) -> Unit = { _, _ -> },
    onComplete: (Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    require(rounds.isNotEmpty()) { "Proposal location duel sequence needs at least one round." }

    val sequenceKey = rounds.joinToString(separator = "|") { it.id }
    var currentRoundIndex by remember(sequenceKey) { mutableStateOf(0) }
    var selections by remember(sequenceKey) {
        mutableStateOf(initialSelections.filterKeys { roundId -> rounds.any { it.id == roundId } })
    }
    val currentRound = rounds[currentRoundIndex]

    AnimatedProposalLocationDuelBoard(
        round = currentRound,
        selectedOptionId = selections[currentRound.id],
        onPick = { option ->
            selections = selections + (currentRound.id to option.id)
            onRoundPicked(currentRound.id, option)
        },
        onTransitionFinished = {
            if (currentRoundIndex == rounds.lastIndex) {
                onComplete(selections)
            } else {
                currentRoundIndex += 1
            }
        },
        modifier = modifier.testTag("proposal_location_round_${currentRound.id}")
    )
}

/**
 * Staged Harmony choreography: cards first, question second, locked selection, then a
 * coordinated question fade and 3D card flip before the next round.
 */
@Composable
internal fun AnimatedProposalLocationDuelBoard(
    round: ProposalImageDuelRound,
    selectedOptionId: String?,
    onPick: (ProposalImageDuelOption) -> Unit,
    onTransitionFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by remember(round.id) { mutableStateOf(ProposalDuelPhase.IntroCards) }
    var lockedSelectionId by remember(round.id) { mutableStateOf<String?>(null) }
    val latestOnPick by rememberUpdatedState(onPick)
    val latestOnTransitionFinished by rememberUpdatedState(onTransitionFinished)

    LaunchedEffect(round.id) {
        lockedSelectionId = null
        phase = ProposalDuelPhase.IntroCards
        delay(620)
        phase = ProposalDuelPhase.RevealQuestion
        delay(120)
        phase = ProposalDuelPhase.AwaitingSelection
    }

    LaunchedEffect(lockedSelectionId) {
        if (lockedSelectionId == null) return@LaunchedEffect
        delay(420)
        phase = ProposalDuelPhase.TransitionOut
        delay(760)
        latestOnTransitionFinished()
    }

    val effectiveSelectionId = lockedSelectionId ?: selectedOptionId

    ProposalLocationDuelBoardContent(
        round = round,
        selectedOptionId = effectiveSelectionId,
        onPick = { option ->
            if (phase == ProposalDuelPhase.AwaitingSelection && lockedSelectionId == null) {
                lockedSelectionId = option.id
                phase = ProposalDuelPhase.SelectionLocked
                latestOnPick(option)
            }
        },
        phase = phase,
        pickingEnabled = phase == ProposalDuelPhase.AwaitingSelection && lockedSelectionId == null,
        animateCardIntro = true,
        modifier = modifier
    )
}

@Composable
private fun ProposalLocationDuelBoardContent(
    round: ProposalImageDuelRound,
    selectedOptionId: String?,
    onPick: (ProposalImageDuelOption) -> Unit,
    phase: ProposalDuelPhase,
    pickingEnabled: Boolean,
    animateCardIntro: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().testTag("proposal_location_duel"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Keep a fixed question slot so the cards do not jump when the delayed prompt appears.
        Box(
            modifier = Modifier.fillMaxWidth().height(154.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = phase != ProposalDuelPhase.IntroCards && phase != ProposalDuelPhase.TransitionOut,
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
                        .testTag("proposal_location_question")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✦  Euer Ort",
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
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Wählt den Ort, der sich für euren Moment am meisten nach euch anfühlt.",
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

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(320.dp).testTag("proposal_location_cards"),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProposalLocationOptionCard(
                option = round.firstOption,
                choiceNumber = 1,
                cardIndex = 0,
                selected = selectedOptionId == round.firstOption.id,
                anotherOptionSelected = selectedOptionId != null && selectedOptionId != round.firstOption.id,
                enabled = pickingEnabled,
                phase = phase,
                animateEntrance = animateCardIntro,
                onClick = { onPick(round.firstOption) },
                modifier = Modifier.weight(1f)
            )
            ProposalLocationOptionCard(
                option = round.secondOption,
                choiceNumber = 2,
                cardIndex = 1,
                selected = selectedOptionId == round.secondOption.id,
                anotherOptionSelected = selectedOptionId != null && selectedOptionId != round.secondOption.id,
                enabled = pickingEnabled,
                phase = phase,
                animateEntrance = animateCardIntro,
                onClick = { onPick(round.secondOption) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProposalLocationOptionCard(
    option: ProposalImageDuelOption,
    choiceNumber: Int,
    cardIndex: Int,
    selected: Boolean,
    anotherOptionSelected: Boolean,
    enabled: Boolean,
    phase: ProposalDuelPhase,
    animateEntrance: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageResId = proposalLocationImages.getValue(option.id)
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

    val isExiting = phase == ProposalDuelPhase.TransitionOut
    val side = if (cardIndex == 0) -1f else 1f
    val horizontalOffset by animateDpAsState(
        targetValue = when {
            isExiting -> (16f * side).dp
            !entered -> (30f * side).dp
            else -> 0.dp
        },
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "proposalCardOffset"
    )
    val rotationY by animateFloatAsState(
        targetValue = when {
            isExiting -> 82f * side
            !entered -> 18f * side
            else -> 0f
        },
        animationSpec = tween(durationMillis = 560, easing = FastOutSlowInEasing),
        label = "proposalCardRotation"
    )
    val cardScale by animateFloatAsState(
        targetValue = when {
            isExiting -> 0.94f
            !entered -> 0.91f
            selected -> 1.025f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "proposalCardScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = when {
            isExiting -> 0.08f
            !entered -> 0f
            anotherOptionSelected -> 0.72f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 360),
        label = "proposalCardAlpha"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) HarmonyPink else Color.White.copy(alpha = 0.26f),
        animationSpec = tween(durationMillis = 240),
        label = "proposalCardBorder"
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
            .testTag("proposal_location_${option.id}")
    ) {
        ProposalLocationImage(imageResId = imageResId, contentDescription = option.label)
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
                .testTag("proposal_location_choice_$choiceNumber"),
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
private fun ProposalLocationImage(
    @DrawableRes imageResId: Int,
    contentDescription: String
) {
    Image(
        painter = painterResource(imageResId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
