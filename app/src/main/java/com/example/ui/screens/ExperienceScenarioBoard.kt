package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.ExperienceScenarioRound
import com.example.data.model.ProfileEntity

/**
 * Thin reusable adapter around the shipped scenario mechanic.
 *
 * The scenario journey/result choreography remains in the existing board while callers use the
 * generic round contract.
 */
@Composable
internal fun ExperienceScenarioBoard(
    round: ExperienceScenarioRound,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ScenarioBoard(
        question = round.prompt,
        options = round.options,
        selectedAnswer = selectedAnswer,
        profile = profile,
        onPick = onPick,
        modifier = modifier
    )
}
