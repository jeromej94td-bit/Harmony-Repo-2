package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.ProposalImageDuelOption
import com.example.data.model.ProposalImageDuelRound
import com.example.data.model.ProposalLocationDuels
import com.example.data.model.toExperienceImageDuelRound

/** Legacy-compatible proposal wrapper around the reusable Stage-03 image-duel board. */
@Composable
internal fun ProposalLocationDuelBoard(
    round: ProposalImageDuelRound,
    selectedOptionId: String?,
    onPick: (ProposalImageDuelOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    ExperienceImageDuelBoard(
        round = round.toExperienceImageDuelRound(),
        selectedOptionId = selectedOptionId,
        imageResolver = { imageKey ->
            context.resources.getIdentifier(imageKey, "drawable", context.packageName)
        },
        onPick = { option -> onPick(round.proposalOption(option.id)) },
        kicker = "✦  Euer Ort",
        instruction = "Wählt den Ort, der sich für euren Moment am meisten nach euch anfühlt.",
        testTagPrefix = "proposal_location",
        rootTestTag = "proposal_location_duel",
        modifier = modifier
    )
}

/**
 * Plays the complete local Stage 02.3 location sequence without registering navigation.
 * Kept as a compatibility surface while the visual mechanic is now reusable.
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

/** Legacy-compatible animated wrapper preserving the shipped location choreography and tags. */
@Composable
internal fun AnimatedProposalLocationDuelBoard(
    round: ProposalImageDuelRound,
    selectedOptionId: String?,
    onPick: (ProposalImageDuelOption) -> Unit,
    onTransitionFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AnimatedExperienceImageDuelBoard(
        round = round.toExperienceImageDuelRound(),
        selectedOptionId = selectedOptionId,
        imageResolver = { imageKey ->
            context.resources.getIdentifier(imageKey, "drawable", context.packageName)
        },
        onPick = { option -> onPick(round.proposalOption(option.id)) },
        onTransitionFinished = onTransitionFinished,
        kicker = "✦  Euer Ort",
        instruction = "Wählt den Ort, der sich für euren Moment am meisten nach euch anfühlt.",
        testTagPrefix = "proposal_location",
        rootTestTag = "proposal_location_duel",
        modifier = modifier
    )
}

private fun ProposalImageDuelRound.proposalOption(optionId: String): ProposalImageDuelOption =
    when (optionId) {
        firstOption.id -> firstOption
        secondOption.id -> secondOption
        else -> error("Unknown proposal image-duel option: $optionId")
    }
