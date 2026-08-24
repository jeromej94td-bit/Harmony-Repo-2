package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal enum class HarmonyImageChoiceKind {
    EGG,
    STEAK,
    TRAVEL,
    PANDA_INTRO
}

/** Session-scoped gate for the Panda intro. Once the clip finishes or is skipped,
 * the normal first question is rendered by QuizRunnerScreen without changing quiz state.
 */
internal object UnpopularOpinionsPandaIntroState {
    var played by mutableStateOf(false)
}

internal fun harmonyImageChoiceKind(packId: String, questionIndex: Int): HarmonyImageChoiceKind? =
    when {
        packId == "unbeliebt" && questionIndex == 0 && !UnpopularOpinionsPandaIntroState.played -> HarmonyImageChoiceKind.PANDA_INTRO
        packId == "essenreden" && questionIndex == 3 -> HarmonyImageChoiceKind.EGG
        packId == "essenreden" && questionIndex == 4 -> HarmonyImageChoiceKind.STEAK
        packId == "reisevor" && questionIndex == 4 -> HarmonyImageChoiceKind.TRAVEL
        else -> null
    }

internal fun harmonyImageChoiceRevealDelayMillis(index: Int): Long {
    val row = index / 3
    val column = index % 3
    return row * 420L + column * 110L
}
