package com.example.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesScrollVirtualizationContractTest {

    @Test
    fun `pack list virtualizes cards instead of eagerly composing every pack`() {
        val source = source("app/src/main/java/com/example/ui/screens/PackListScreen.kt")

        assertTrue(
            "PackListScreen must use LazyColumn so off-screen pack cards are not composed.",
            source.contains("LazyColumn(")
        )
        assertTrue(
            "Pack cards must be emitted through lazy items with stable ids.",
            source.contains("items(items = list, key = { it.id })") ||
                source.contains("items(list, key = { it.id })")
        )
        assertFalse(
            "PackListScreen must not keep the eager verticalScroll container.",
            source.contains(".verticalScroll(scrollState)")
        )
        assertFalse(
            "PackListScreen must not eagerly compose every pack with list.forEach.",
            source.contains("list.forEach")
        )
    }

    @Test
    fun `existing category and topic animations remain intact`() {
        val source = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertTrue(source.contains("label = \"category_power_"))
        assertTrue(source.contains("label = \"category_glow_"))
        assertTrue(source.contains("label = \"category_breathe_"))
        assertTrue(source.contains("label = \"topic_power_"))
        assertTrue(source.contains("label = \"topic_energy_"))
        assertTrue(source.contains("label = \"topic_breathe_"))
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path)
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
