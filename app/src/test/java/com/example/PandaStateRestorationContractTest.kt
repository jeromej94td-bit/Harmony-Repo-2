package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PandaStateRestorationContractTest {

    @Test
    fun `panda overlay and exit dialog survive activity recreation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(source.contains("var isPandaEitherOrOpen by rememberSaveable { mutableStateOf(false) }"))
        assertTrue(source.contains("var isPandaExitConfirmOpen by rememberSaveable { mutableStateOf(false) }"))
        assertFalse(source.contains("var isPandaEitherOrOpen by remember { mutableStateOf(false) }"))
    }

    @Test
    fun `panda question order and secret selections survive recreation`() {
        val source = source("app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt")

        assertTrue(source.contains("import androidx.compose.runtime.saveable.rememberSaveable"))
        assertTrue(source.contains("completedBeforeStartEncoded by rememberSaveable"))
        assertTrue(source.contains("questionOrderEncoded by rememberSaveable"))
        assertTrue(source.contains("var orderPosition by rememberSaveable"))
        assertTrue(source.contains("var stepName by rememberSaveable"))
        assertTrue(source.contains("var userChoice by rememberSaveable"))
        assertTrue(source.contains("var partnerChoice by rememberSaveable"))
    }

    @Test
    fun `panda step uses stable encoded state`() {
        val source = source("app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt")

        assertTrue(source.contains("val step = CoupleGameStep.valueOf(stepName)"))
        assertTrue(source.contains("stepName = CoupleGameStep.HANDOVER.name"))
        assertTrue(source.contains("stepName = CoupleGameStep.PARTNER_CHOICE.name"))
        assertTrue(source.contains("stepName = CoupleGameStep.REVEAL.name"))
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
