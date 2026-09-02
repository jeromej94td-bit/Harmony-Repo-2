package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrentAppStateContractTest {

    @Test
    fun `signed in email remains visible in profile`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")

        assertTrue(profile.contains("currentSessionOrNull()?.user?.email"))
        assertTrue(profile.contains("Angemeldet als"))
        assertTrue(profile.contains("account_email"))
    }

    @Test
    fun `stale Brain chat callsite resolves only to private couple chat`() {
        val activeChat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        val bridge = source("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt")

        assertFalse(activeChat.contains("Harmony Brain"))
        assertFalse(activeChat.contains("BrainMessage"))
        assertTrue(bridge.contains("intentionally ignores every archived Brain argument"))
        assertTrue(bridge.contains("onSendVoiceMessage = onSendVoiceMessage"))
        assertFalse(bridge.contains("onSendBrainMessage("))
        assertFalse(bridge.contains("onToggleBrainChatMode("))
    }

    @Test
    fun `archived Brain sections remain disabled in home and games`() {
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertTrue(home.contains("brainEnabled: Boolean = false"))
        assertTrue(games.contains("brainEnabled: Boolean = false"))
        assertTrue(games.contains("if (brainEnabled && generatedGames.isNotEmpty())"))
    }

    @Test
    fun `removed Mischung category has a permanent runtime tombstone`() {
        val policy = source("app/src/main/java/com/example/data/model/RemovedGameCatalogPolicy.kt")
        val models = source("app/src/main/java/com/example/data/model/Models.kt")

        assertTrue(policy.contains("MISCHUNG_CATEGORY_ID = \"mischung\""))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsCategoryId"))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsPackCategoryId"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
