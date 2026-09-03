package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrentAppStateContractTest {

    @Test
    fun `signed in email remains visible from real AppSession profile`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        val bridge = source("app/src/main/java/com/example/ui/screens/ProfileSheetSessionBridge.kt")

        assertTrue(profile.contains("session.email"))
        assertTrue(profile.contains("Angemeldet als"))
        assertTrue(profile.contains("account_email"))
        assertTrue(bridge.contains("val realSession = sessionState.session ?: fallbackSession"))
        assertFalse(profile.contains("currentSessionOrNull()?.user?.email"))
    }

    @Test
    fun `partner invite code flow remains directly reachable from profile`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        val partnerConnection = source("app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt")

        assertTrue(profile.contains("onOpenPartnerConnection"))
        assertTrue(profile.contains("partner_connection_button"))
        assertTrue(profile.contains("Code erstellen oder eingeben"))
        assertTrue(partnerConnection.contains("Code erstellen"))
        assertTrue(partnerConnection.contains("Code eingeben"))
        assertTrue(partnerConnection.contains("Code teilen"))
        assertTrue(partnerConnection.contains("24 Stunden gültig"))
    }

    @Test
    fun `Brain chat bridge is removed and private couple chat remains wired`() {
        val activeChat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertFalse(activeChat.contains("Harmony Brain"))
        assertFalse(activeChat.contains("BrainMessage"))
        assertFalse(exists("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt"))
        assertFalse(main.contains("onSendBrainMessage ="))
        assertFalse(main.contains("onToggleBrainChatMode ="))
        assertTrue(main.contains("onSendMessage ="))
        assertTrue(main.contains("onSendImage ="))
        assertTrue(main.contains("onSendVoiceMessage ="))
    }

    @Test
    fun `archived Brain sections remain fail closed while productive wiring is absent`() {
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(home.contains("brainEnabled: Boolean = false"))
        assertTrue(games.contains("brainEnabled: Boolean = false"))
        assertTrue(games.contains("if (brainEnabled && generatedGames.isNotEmpty())"))
        assertFalse(main.contains("brainInterests ="))
        assertFalse(main.contains("generatedGames ="))
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

    private fun exists(path: String): Boolean =
        listOf(File(path.removePrefix("app/")), File(path)).any(File::exists)
}
