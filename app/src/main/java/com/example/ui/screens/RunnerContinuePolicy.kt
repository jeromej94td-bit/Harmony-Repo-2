package com.example.ui.screens

/**
 * Shows a neutral forward action only when the current question already has a saved answer.
 * This covers back-navigation/resume states without turning "Weiter" into an implicit skip.
 */
internal object RunnerContinuePolicy {
    fun shouldShow(
        isFinished: Boolean,
        packType: String,
        currentIndex: Int,
        answeredIndexes: Set<Int>
    ): Boolean =
        !isFinished &&
            packType != "disc" &&
            currentIndex >= 0 &&
            currentIndex in answeredIndexes
}
