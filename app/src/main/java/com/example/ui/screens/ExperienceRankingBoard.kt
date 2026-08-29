package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.ExperienceRankingRound
import com.example.data.model.ExperienceRankingSelectionCodec
import com.example.data.model.ProfileEntity

/**
 * Stable-ID adapter around the shipped drag/drop ranking board.
 *
 * The visual layout and gesture behavior remain in [RankingSlotBoard]. Experience callers own the
 * selected item-id order and never need to know the legacy encoded label payload.
 */
@Composable
internal fun ExperienceRankingBoard(
    round: ExperienceRankingRound,
    selectedItemIds: List<String>,
    profile: ProfileEntity,
    onPick: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = round.items.map { it.label }
    val selectedAnswer = ExperienceRankingSelectionCodec.encodeOrNull(round, selectedItemIds)

    RankingSlotBoard(
        question = round.prompt,
        options = labels,
        selectedAnswer = selectedAnswer,
        profile = profile,
        onPick = { encoded ->
            ExperienceRankingSelectionCodec.decode(round, encoded)?.let(onPick)
        },
        modifier = modifier.testTag("experience_ranking_board")
    )
}
