package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalExperienceBackNavigationContractTest {

    @Test
    fun `proposal screen handles system back only when an earlier experience position exists`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")

        assertTrue(source.contains("ProposalExperienceRunnerPolicy.previous(position)"))
        assertTrue(source.contains("BackHandler(enabled = started && previousPosition != null)"))
        assertTrue(source.contains("position = previousPosition"))
    }

    @Test
    fun `proposal header exposes a dedicated previous control without removing close`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")

        assertTrue(source.contains("testTag(\"proposal_previous_button\")"))
        assertTrue(source.contains("Text(\"Zurück\""))
        assertTrue(source.contains("Text(\"Schließen\""))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
