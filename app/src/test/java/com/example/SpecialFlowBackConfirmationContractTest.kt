package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpecialFlowBackConfirmationContractTest {

    @Test
    fun `system back requests confirmation instead of closing special flows`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        val backHandler = source.substringAfter("BackHandler(enabled = canHandleBack || isLiveChangeEditorOpen)")
            .substringBefore("AmbientBackground {")
            .replace(Regex("\\s+"), " ")

        assertTrue(source.contains("var isSpecialFlowExitConfirmOpen by remember"))
        assertTrue(backHandler.contains("isProposalExperienceOpen -> { isSpecialFlowExitConfirmOpen = true }"))
        assertTrue(backHandler.contains("isEureMischungOpen -> { isSpecialFlowExitConfirmOpen = true }"))
        assertTrue(backHandler.contains("isKidGeneratorOpen -> { isSpecialFlowExitConfirmOpen = true }"))

        assertFalse(backHandler.contains("isProposalExperienceOpen -> { isProposalExperienceOpen = false }"))
        assertFalse(backHandler.contains("isEureMischungOpen -> { isEureMischungOpen = false }"))
        assertFalse(backHandler.contains("isKidGeneratorOpen -> { isKidGeneratorOpen = false }"))
    }

    @Test
    fun `proposal visible back moves one step while system back stays global`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")
        val normalized = source.replace(Regex("\\s+"), " ")

        assertTrue(source.contains("val previousPosition = ProposalExperienceRunnerPolicy.previous(position)"))
        assertTrue(normalized.contains("onClick = { moveTo(previousPosition) }, modifier = Modifier.testTag(\"proposal_previous_button\")"))

        // Android/system back must bubble to MainActivity, where the existing special-flow
        // leave confirmation is owned. Only the visible in-app button may move backwards.
        assertFalse(source.contains("import androidx.activity.compose.BackHandler"))
        assertFalse(source.contains("BackHandler("))
    }

    @Test
    fun `special flow leave dialog keeps flow open until explicit confirmation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        val normalized = source.replace(Regex("\\s+"), " ")

        assertTrue(normalized.contains("if (isSpecialFlowExitConfirmOpen) { androidx.compose.material3.AlertDialog("))
        assertTrue(normalized.contains("onDismissRequest = { isSpecialFlowExitConfirmOpen = false }"))
        assertTrue(normalized.contains("isSpecialFlowExitConfirmOpen = false when { isProposalExperienceOpen -> isProposalExperienceOpen = false isEureMischungOpen -> isEureMischungOpen = false isKidGeneratorOpen -> isKidGeneratorOpen = false }"))
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
