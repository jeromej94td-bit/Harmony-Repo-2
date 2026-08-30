package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EureMischungStateRestorationContractTest {

    @Test
    fun `eure mischung overlay survives activity recreation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(source.contains("var isEureMischungOpen by rememberSaveable { mutableStateOf(false) }"))
        assertFalse(source.contains("var isEureMischungOpen by remember { mutableStateOf(false) }"))
    }

    @Test
    fun `eure mischung form selections and draft survive recreation`() {
        val source = source("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt")

        assertTrue(source.contains("var parent1CustomUriString by rememberSaveable"))
        assertTrue(source.contains("var parent2CustomUriString by rememberSaveable"))
        assertTrue(source.contains("var selectedScenarioName by rememberSaveable"))
        assertTrue(source.contains("var selectedStyleName by rememberSaveable"))
        assertTrue(source.contains("var selectedGenderName by rememberSaveable"))
        assertTrue(source.contains("var customNotes by rememberSaveable"))
        assertTrue(source.contains("var errorMessage by rememberSaveable"))
        assertTrue(source.contains("var technicalErrorDetails by rememberSaveable"))
        assertTrue(source.contains("var isTechDetailsExpanded by rememberSaveable"))
    }

    @Test
    fun `generated result and session history can be reconstructed from saved file metadata`() {
        val source = source("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt")

        assertTrue(source.contains("var currentResultPath by rememberSaveable"))
        assertTrue(source.contains("var historyPaths by rememberSaveable"))
        assertTrue(source.contains("var resultDescriptions by rememberSaveable"))
        assertTrue(source.contains("var resultSummaries by rememberSaveable"))
        assertTrue(source.contains("var resultTimestamps by rememberSaveable"))
        assertTrue(source.contains("restoreGeneratedImageResult("))
        assertTrue(source.contains("BitmapFactory.decodeFile"))
        assertTrue(source.contains("var isFullscreenImageOpen by rememberSaveable"))
        assertTrue(source.contains("var isMomentSaved by rememberSaveable"))
    }

    @Test
    fun `in flight generation is intentionally not restored as a stuck loading state`() {
        val source = source("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt")

        assertTrue(source.contains("var isGenerating by remember { mutableStateOf(false) }"))
        assertFalse(source.contains("var isGenerating by rememberSaveable"))
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
