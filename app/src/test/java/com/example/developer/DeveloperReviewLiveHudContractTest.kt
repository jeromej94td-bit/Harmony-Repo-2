package com.example.developer

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeveloperReviewLiveHudContractTest {

    @Test
    fun `live change hud exposes contextual developer quick note`() {
        val source = File("src/main/java/com/example/ui/screens/LiveChangeOverlay.kt").readText()
        val hud = source.substringAfter("fun LiveChangeHud(")
            .substringBefore("private enum class LiveEditorMode")

        assertTrue(hud.contains("DeveloperReviewQuickNote("))
        assertTrue(hud.contains("🛠 Notiz") || source.contains("Text(\"🛠 Notiz\""))
    }
}
