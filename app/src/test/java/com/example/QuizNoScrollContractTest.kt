package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizNoScrollContractTest {

    @Test
    fun `standard text questions use adaptive non scrolling layout`() {
        val runner = source("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")

        assertTrue(runner.contains("StandardQuizLayoutPolicy.metrics("))
        assertTrue(runner.contains("layoutMetrics = standardLayoutMetrics"))
        assertTrue(runner.contains("if (imageChoiceKind != null) Modifier.verticalScroll(scrollState) else Modifier"))
        assertFalse(
            runner.contains(
                ".fillMaxSize()\n                                    .verticalScroll(scrollState)"
            )
        )
    }

    @Test
    fun `tot results header has no fake overflow button`() {
        val runner = source("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")

        assertFalse(runner.contains("text = \"•••\""))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
