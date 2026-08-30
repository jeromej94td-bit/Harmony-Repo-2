package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalExperienceOverlayIsolationContractTest {

    @Test
    fun `proposal experience suppresses the legacy quiz runner behind it`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(
            "QuizRunnerScreen must not stay rendered behind ProposalExperienceScreen",
            source.contains(
                "if (!isProposalExperienceOpen) {\n                    uiState.activeRun?.let { activeRun ->\n                        QuizRunnerScreen("
            )
        )
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path), File(path.removePrefix("app/")))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
