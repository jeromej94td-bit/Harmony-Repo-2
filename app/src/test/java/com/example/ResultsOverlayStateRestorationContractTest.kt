package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultsOverlayStateRestorationContractTest {

    @Test
    fun `pack results overlay survives activity recreation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(source.contains("var resultsPackId by rememberSaveable { mutableStateOf<String?>(null) }"))
        assertFalse(source.contains("var resultsPackId by remember { mutableStateOf<String?>(null) }"))
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
