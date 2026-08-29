package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RunnerFinishedVisibleBackContractTest {

    @Test
    fun `visible runner back always returns to previous question even from finished state`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        val runnerCall = source.substringAfter("QuizRunnerScreen(")
            .substringBefore("onCloseExitConfirm")
            .replace(Regex("\\s+"), " ")

        assertTrue(
            "Visible runner back must delegate to previousStep.",
            runnerCall.contains("onAskExit = { viewModel.previousStep() }")
        )
        assertFalse(
            "Finished runner state must not route the visible back button to the exit dialog.",
            runnerCall.contains("activeRun.isFinished") || runnerCall.contains("viewModel.askExitRun()")
        )
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path)
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
