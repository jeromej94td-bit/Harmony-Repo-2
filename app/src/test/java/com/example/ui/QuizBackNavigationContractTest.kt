package com.example.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizBackNavigationContractTest {

    @Test
    fun `quiz toolbar back always goes to the previous question`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        val quizRunnerBlock = source.substringAfter("QuizRunnerScreen(").substringBefore("resultsPackId?.let")

        assertTrue(
            "The in-app quiz back button must always delegate to previousStep().",
            quizRunnerBlock.contains("onAskExit = { viewModel.previousStep() }")
        )
        assertFalse(
            "The in-app quiz back button must not turn into an exit action when a run is finished.",
            quizRunnerBlock.contains("activeRun.isFinished") && quizRunnerBlock.contains("viewModel.askExitRun()")
        )
    }

    @Test
    fun `android system back keeps the quiz exit confirmation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        val backHandlerBlock = source.substringAfter("BackHandler(enabled = canHandleBack").substringBefore("AmbientBackground")

        assertTrue(
            "Android system back must still ask to leave an active quiz.",
            backHandlerBlock.contains("isQuizActive ->") && backHandlerBlock.contains("viewModel.askExitRun()")
        )
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
