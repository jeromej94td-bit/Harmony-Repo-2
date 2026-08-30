package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PandaBackButtonContractTest {

    @Test
    fun `first panda question does not show a dead back button`() {
        val source = source("app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt")

        assertTrue(source.contains("if (orderPosition > 0) {"))
        assertTrue(source.contains("Modifier.size(48.dp).testTag(\"panda_back_placeholder\")"))
        assertTrue(source.contains("Modifier.testTag(\"panda_previous_button\")"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
