package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.ExperiencePartnerPredictionRound
import com.example.data.model.ExperiencePartnerPredictionSelection
import com.example.data.model.ExperiencePartnerPredictionSelectionCodec
import com.example.data.model.ProfileEntity

/**
 * Stable-ID adapter around the shipped partner-prediction board.
 *
 * The visual A → B → Reveal interaction remains in the existing board while experience callers
 * receive a typed selection instead of the legacy encoded payload.
 */
@Composable
internal fun ExperiencePartnerPredictionBoard(
    round: ExperiencePartnerPredictionRound,
    selectedSelection: ExperiencePartnerPredictionSelection?,
    profile: ProfileEntity,
    onPick: (ExperiencePartnerPredictionSelection) -> Unit,
    modifier: Modifier = Modifier
) {
    PartnerPredictionBoard(
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
