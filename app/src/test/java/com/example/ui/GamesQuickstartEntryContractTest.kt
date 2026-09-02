package com.example.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesQuickstartEntryContractTest {

    @Test
    fun `games screen gates the existing browser behind one simple start choice`() {
        val source = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertTrue(source.contains("showStartChoice"))
        assertTrue(source.contains("GamesQuickstartEntryDialog("))
        assertTrue(source.contains("onChooseBrowse = { showStartChoice = false }"))
    }

    @Test
    fun `quickstart uses only the existing pack start callback`() {
        val source = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertTrue(source.contains("buildGamesQuickstartPool(HarmonyPacksData.CATALOG_PACKS, answers)"))
        assertTrue(source.contains("onStartPack(candidate.packId)"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
