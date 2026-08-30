package com.example.ui

/**
 * Guards callbacks emitted by a question that is no longer the active question.
 *
 * Compose can deliver a second tap before the screen has recomposed after the first action.
 * Without the expected index, that stale callback would act on the newly advanced question.
 */
fun HarmonyViewModel.pickAnswer(optionText: String, expectedIndex: Int) {
    val run = uiState.value.activeRun ?: return
    if (run.isFinished || run.currentIndex != expectedIndex) return
    pickAnswer(optionText)
}

fun HarmonyViewModel.nextStep(expectedIndex: Int) {
    val run = uiState.value.activeRun ?: return
    if (run.isFinished || run.currentIndex != expectedIndex) return
    nextStep()
}

fun HarmonyViewModel.skipCurrentQuestion(expectedIndex: Int) {
    val run = uiState.value.activeRun ?: return
    if (run.isFinished || run.currentIndex != expectedIndex) return
    skipCurrentQuestion()
}
