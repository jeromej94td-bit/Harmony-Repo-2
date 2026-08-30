package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalStateRestorationContractTest {

    @Test
    fun `proposal overlay remains open across activity recreation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(source.contains("import androidx.compose.runtime.saveable.rememberSaveable"))
        assertTrue(source.contains("var isProposalExperienceOpen by rememberSaveable { mutableStateOf(false) }"))
        assertFalse(source.contains("var isProposalExperienceOpen by remember { mutableStateOf(false) }"))
    }

    @Test
    fun `proposal session state survives activity recreation`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")

        assertTrue(source.contains("import androidx.compose.runtime.saveable.rememberSaveable"))
        assertTrue(source.contains("var started by rememberSaveable"))
        assertTrue(source.contains("var positionStepIndex by rememberSaveable"))
        assertTrue(source.contains("var positionItemIndex by rememberSaveable"))
        assertTrue(source.contains("var eitherOrSelections by rememberSaveable"))
        assertTrue(source.contains("var locationSelections by rememberSaveable"))
        assertTrue(source.contains("var ringSelections by rememberSaveable"))
        assertTrue(source.contains("var rankedPriorityIds by rememberSaveable"))
        assertTrue(source.contains("var predictionAnswersEncoded by rememberSaveable"))
        assertTrue(source.contains("var scenarioSelections by rememberSaveable"))
        assertTrue(source.contains("var personalWishAnswers by rememberSaveable"))

        assertFalse(source.contains("var started by remember { mutableStateOf(false) }"))
        assertFalse(source.contains("var position by remember { mutableStateOf(ProposalRunnerPosition(0, 0)) }"))
    }

    @Test
    fun `proposal open prompt draft is saveable`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")
        assertTrue(source.contains("var text by rememberSaveable(prompt) { mutableStateOf(initialValue) }"))
    }

    @Test
    fun `partner prediction state uses string codec for saveability`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")
        assertTrue(source.contains("ExperiencePartnerPredictionSelectionCodec"))
        assertTrue(source.contains("predictionAnswersEncoded[round.id]?.let(ExperiencePartnerPredictionSelectionCodec::decode)"))
        assertTrue(source.contains("ExperiencePartnerPredictionSelectionCodec.encode(selection)"))
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
