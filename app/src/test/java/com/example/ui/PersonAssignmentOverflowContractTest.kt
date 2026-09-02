package com.example.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonAssignmentOverflowContractTest {

    @Test
    fun `role duel keeps long role lists reachable inside fixed stage`() {
        val source = source("app/src/main/java/com/example/ui/screens/QuestionInteractionBoard.kt")

        assertTrue(
            "The unassigned-card pool needs its own bounded scroll viewport.",
            source.contains("assignment_unassigned_roles")
        )
        assertTrue(
            "Each person target needs a bounded scroll viewport for assigned roles.",
            source.contains("${'$'}{targetTag}_roles")
        )
        assertTrue(
            "PersonAssignment role viewports must use vertical scrolling instead of clipping overflow.",
            source.contains("verticalScroll(rememberScrollState())")
        )
    }

    @Test
    fun `submit action stays outside role scroll areas`() {
        val source = source("app/src/main/java/com/example/ui/screens/QuestionInteractionBoard.kt")
        val rolePool = source.indexOf("assignment_unassigned_roles")
        val submit = source.indexOf("testTag = \"assignment_submit\"")

        assertTrue("Role pool must exist before the fixed submit action.", rolePool >= 0 && submit > rolePool)
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
