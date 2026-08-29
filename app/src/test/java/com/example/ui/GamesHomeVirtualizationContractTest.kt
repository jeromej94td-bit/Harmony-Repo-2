package com.example.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesHomeVirtualizationContractTest {

    @Test
    fun `games home uses lazy vertical composition for search and topic cards`() {
        val source = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertFalse(
            "GamesScreen must not keep an eager outer verticalScroll container.",
            source.contains(".verticalScroll(scrollState)")
        )
        assertFalse(
            "Topic cards must not be eagerly composed with TOPICS.forEach.",
            source.contains("HarmonyPacksData.TOPICS.forEach")
        )
        assertFalse(
            "Search result cards must not be eagerly composed with searchResults.forEach.",
            source.contains("searchResults.forEach")
        )
        assertTrue(
            "The main screen must use lazy keyed topic items.",
            source.contains("items(items = HarmonyPacksData.TOPICS, key = { it.id })") ||
                source.contains("items(HarmonyPacksData.TOPICS, key = { it.id })")
        )
        assertTrue(
            "Search results must be emitted lazily with stable pack ids.",
            source.contains("items(items = searchResults, key = { it.id })") ||
                source.contains("items(searchResults, key = { it.id })")
        )
    }

    @Test
    fun `virtualization keeps the existing card animations`() {
        val source = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertTrue(source.contains("label = \"category_power_"))
        assertTrue(source.contains("label = \"category_glow_"))
        assertTrue(source.contains("label = \"category_breathe_"))
        assertTrue(source.contains("label = \"topic_power_"))
        assertTrue(source.contains("label = \"topic_glow_"))
        assertTrue(source.contains("label = \"topic_energy_"))
        assertTrue(source.contains("label = \"topic_breathe_"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
