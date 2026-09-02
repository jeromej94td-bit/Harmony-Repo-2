package com.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FreeTextImeSafetyContractTest {

    @Test
    fun `main activity lets the window resize for the software keyboard`() {
        val manifest = File("src/main/AndroidManifest.xml").readText()

        val mainActivityBlock = manifest.substringAfter("android:name=\".MainActivity\"")
            .substringBefore("</activity>")

        assertTrue(mainActivityBlock.contains("android:windowSoftInputMode=\"adjustResize\""))
    }

    @Test
    fun `proposal free text keeps the continue action reachable above the ime`() {
        val source = File("src/main/java/com/example/ui/screens/ExperienceOpenPromptBoard.kt").readText()

        assertTrue(source.contains(".imePadding()"))
        assertTrue(source.contains(".verticalScroll(rememberScrollState())"))
    }

    @Test
    fun `deep talk keeps its weighted answer pane and submit action ime safe`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenDeepTalk.kt").readText()
        val answerPane = source.substringAfter("private fun DeepTalkAnswerPane(")
            .substringBefore("private fun DeepTalkRevealCard(")

        assertTrue(answerPane.contains(".imePadding()"))
        assertFalse(answerPane.contains(".verticalScroll("))
        assertTrue(answerPane.contains(".weight(1f)"))
    }
}
