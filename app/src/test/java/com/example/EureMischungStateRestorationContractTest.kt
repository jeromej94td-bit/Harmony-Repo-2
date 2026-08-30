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
    fun `eure mischung screen delegates user visible session state to saveable holder`() {
        val source = source("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt")

        assertTrue(source.contains("rememberSaveable(saver = EureMischungSessionState.Saver)"))
        assertTrue(source.contains("var parent1CustomUriString by sessionState.parent1CustomUriStringState"))
        assertTrue(source.contains("var parent2CustomUriString by sessionState.parent2CustomUriStringState"))
        assertTrue(source.contains("var selectedScenario by sessionState.selectedScenarioState"))
        assertTrue(source.contains("var selectedStyle by sessionState.selectedStyleState"))
        assertTrue(source.contains("var selectedGender by sessionState.selectedGenderState"))
        assertTrue(source.contains("var customNotes by sessionState.customNotesState"))
        assertTrue(source.contains("var errorMessage by sessionState.errorMessageState"))
        assertTrue(source.contains("var technicalErrorDetails by sessionState.technicalErrorDetailsState"))
        assertTrue(source.contains("val historyList = sessionState.historyList"))
    }

    @Test
    fun `generated result metadata is saved and bitmap is reconstructed from local file`() {
        val source = source("app/src/main/java/com/example/ui/screens/EureMischungSessionState.kt")

        assertTrue(source.contains("currentResult = state.currentResultState.value?.toSavedResult()"))
        assertTrue(source.contains("history = ArrayList(state.historyList.map(GeneratedImageResult::toSavedResult))"))
        assertTrue(source.contains("BitmapFactory.decodeFile(localFilePath)"))
        assertTrue(source.contains("val currentResultState: MutableState<GeneratedImageResult?>"))
        assertTrue(source.contains("val isFullscreenImageOpenState: MutableState<Boolean>"))
        assertTrue(source.contains("val isMomentSavedState: MutableState<Boolean>"))
    }

    @Test
    fun `in flight generation is intentionally not restored as a stuck loading state`() {
        val source = source("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt")

        assertTrue(source.contains("var isGenerating by remember { mutableStateOf(false) }"))
        assertFalse(source.contains("isGeneratingState"))
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
