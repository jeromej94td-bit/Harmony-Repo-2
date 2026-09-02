package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrentAppStateContractTest {

    @Test
    fun `profile email is sourced from the active app session`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")

        assertTrue(main.contains("accountEmail = appSession.email"))
        assertTrue(profile.contains("accountEmail: String?"))
        assertFalse(profile.contains("currentSessionOrNull()?.user?.email"))
    }

    @Test
    fun `archived Harmony Brain is not wired into active screens`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertFalse(main.contains("brainInterests = uiState.brainInterests"))
        assertFalse(main.contains("brainSuggestions = uiState.brainSuggestions"))
        assertFalse(main.contains("brainQuestions = uiState.brainQuestions"))
        assertFalse(main.contains("generatedGames = uiState.generatedGames"))
        assertFalse(main.contains("isBrainChatMode = uiState.isBrainChatMode"))
        assertFalse(main.contains("brainMessages = uiState.brainMessages"))
        assertFalse(main.contains("onSendBrainMessage"))
    }

    @Test
    fun `removed Mischung content is filtered at every dynamic content boundary`() {
        val content = source("app/src/main/java/com/example/data/HarmonyContentRepository.kt")
        val devData = source("app/src/main/java/com/example/data/DeveloperDataManager.kt")

        assertTrue(content.contains("RemovedGameCatalogPolicy.allowsCategory"))
        assertTrue(content.contains("RemovedGameCatalogPolicy.allowsPack"))
        assertTrue(devData.contains("purgeRemovedCatalogEntries"))
        assertTrue(devData.contains("RemovedGameCatalogPolicy.allowsCategory"))
        assertTrue(devData.contains("RemovedGameCatalogPolicy.allowsPack"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
