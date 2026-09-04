package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.ExperiencePartnerPredictionRound
import com.example.data.model.ExperiencePartnerPredictionSelection
import com.example.data.model.ExperiencePartnerPredictionSelectionCodec
import com.example.data.model.ProfileEntity

/**
 * Stable-ID adapter for experience partner predictions.
 *
 * Experience callers receive a typed selection while the board owns the private prediction,
 * handoff and partner choice. Hit/miss feedback is intentionally deferred to the experience's
 * final reveal instead of interrupting every question.
 */
@Composable
internal fun ExperiencePartnerPredictionBoard(
    round: ExperiencePartnerPredictionRound,
    selectedSelection: ExperiencePartnerPredictionSelection?,
    profile: ProfileEntity,
    onPick: (ExperiencePartnerPredictionSelection) -> Unit,
    modifier: Modifier = Modifier
) {
    PartnerPredictionCollectionBoard(
        question = round.prompt,
        options = round.options,
        selectedAnswer = selectedSelection?.let(ExperiencePartnerPredictionSelectionCodec::encode),
        profile = profile,
        onPick = { encoded ->
            ExperiencePartnerPredictionSelectionCodec.decode(encoded)?.let(onPick)
        },
        modifier = modifier
    )
}
