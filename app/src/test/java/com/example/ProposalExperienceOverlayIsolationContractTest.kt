package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalExperienceOverlayIsolationContractTest {

    @Test
    fun `proposal experience paints an opaque base before translucent aurora layers`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")

        assertTrue(
            "Proposal overlay needs an opaque base so a stale QuizRunner cannot ghost through it",
            source.contains(
                ".fillMaxSize()\n            .background(Color.Black)\n            .background(\n                Brush.radialGradient("
            )
        )
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path), File(path.removePrefix("app/")))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
