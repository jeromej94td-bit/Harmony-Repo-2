package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KidGeneratorSaveStateContractTest {

    @Test
    fun `saved moment state is scoped to the generated result`() {
        val source = source("app/src/main/java/com/example/ui/screens/KidGeneratorScreen.kt")
            .replace(Regex("\\s+"), " ")

        assertTrue(
            "Saved state must reset when the generated local path changes.",
            source.contains("var isMomentSaved by remember(uiState.generatedLocalPath) { mutableStateOf(false) }")
        )
        assertFalse(
            "A global remember would leak the saved state into the next generated result.",
            source.contains("var isMomentSaved by remember { mutableStateOf(false) }")
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
