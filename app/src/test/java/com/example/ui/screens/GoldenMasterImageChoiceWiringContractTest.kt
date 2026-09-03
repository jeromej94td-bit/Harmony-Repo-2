package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoldenMasterImageChoiceWiringContractTest {
    @Test
    fun `only egg steak and travel delegate to the additive golden-master renderer`() {
        val root = sequenceOf(File("."), File(".."))
            .first { File(it, "src/main/java/com/example/ui/screens/HarmonyImageChoiceQuestion.kt").exists() }
        val currentRenderer = File(root, "src/main/java/com/example/ui/screens/HarmonyImageChoiceQuestion.kt").readText()
        val goldenRenderer = File(root, "src/main/java/com/example/ui/screens/GoldenMasterLegacyImageChoiceQuestion.kt")

        assertTrue(goldenRenderer.exists())
        assertTrue(currentRenderer.contains("GoldenMasterImageChoiceLayoutPolicy.forKindName(kind.name)"))
        assertTrue(currentRenderer.contains("GoldenMasterLegacyImageChoiceQuestion("))

        val goldenText = goldenRenderer.readText()
        assertTrue(goldenText.contains("options.take(12).chunked(layout.columns)"))
        assertTrue(goldenText.contains("harmonyImageChoiceRevealDelayMillis(index)"))
        assertTrue(goldenText.contains("HarmonyImageChoiceKind.EGG"))
        assertTrue(goldenText.contains("HarmonyImageChoiceKind.STEAK"))
        assertTrue(goldenText.contains("HarmonyImageChoiceKind.TRAVEL"))
        assertFalse(goldenText.contains("HarmonyImageChoiceKind.TRAUMHAUS"))
    }
}
