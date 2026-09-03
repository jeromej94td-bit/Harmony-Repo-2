package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HarmonyBrainRemovalContractTest {

    @Test
    fun `productive navigation and chat expose no Harmony Brain UI`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val devStudio = source("app/src/main/java/com/example/ui/screens/DevStudioScreen.kt")

        assertFalse(main.contains("brainInterests ="))
        assertFalse(main.contains("brainSuggestions ="))
        assertFalse(main.contains("brainQuestions ="))
        assertFalse(main.contains("isBrainChatMode ="))
        assertFalse(main.contains("onSendBrainMessage ="))
        assertFalse(main.contains("onSendVoiceBrainMessage ="))
        assertFalse(main.contains("generatedGames ="))
        assertFalse(main.contains("onStartGeneratedGame ="))

        assertFalse(devStudio.contains("🧠 Brain"))
        assertFalse(devStudio.contains("DevBrainTab("))
        assertFalse(exists("app/src/main/java/com/example/ui/screens/DevBrainTab.kt"))
        assertFalse(exists("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt"))

        assertTrue(exists("app/src/main/java/com/example/ui/screens/ChatScreen.kt"))
        assertTrue(main.contains("onSendMessage ="))
        assertTrue(main.contains("onSendImage ="))
        assertTrue(main.contains("onSendVoiceMessage ="))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()

    private fun exists(path: String): Boolean =
        listOf(File(path.removePrefix("app/")), File(path)).any(File::exists)
}
