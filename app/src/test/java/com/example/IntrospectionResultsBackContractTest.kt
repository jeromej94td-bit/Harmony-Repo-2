package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class IntrospectionResultsBackContractTest {

    @Test
    fun `results visible back returns to the last question`() {
        val source = source("app/src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt")
        val normalized = source.replace(Regex("\\s+"), " ")

        assertTrue(
            normalized.contains(
                "ResultsScreen( progress = progress, appLang = appLang, isAnswerPlaying = isAnswerPlaying, activeStage = activeAnswerStage"
            )
        )
        assertTrue(normalized.contains("onBackRequest = { goBackOneQuestion() }"))
        assertTrue(source.contains("modifier = Modifier.testTag(\"results_back_button\")"))
        assertTrue(source.contains("IntrospectionStage.RESULTS -> IntrospectionStage.WATER"))
    }

    @Test
    fun `system back remains leave confirmation instead of question navigation`() {
        val source = source("app/src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt")
        val handler = source.substringAfter("// The Android system back action always asks before leaving the game.")
            .substringAfter("BackHandler {")
            .substringBefore("// Permission Launcher")
            .replace(Regex("\\s+"), " ")

        assertTrue(handler.contains("showLeaveDialog = true"))
        assertTrue(!handler.contains("goBackOneQuestion()"))
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
