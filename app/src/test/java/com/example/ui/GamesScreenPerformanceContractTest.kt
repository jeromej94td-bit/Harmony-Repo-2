package com.example.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesScreenPerformanceContractTest {

    @Test
    fun `scrollable game cards do not run continuous per-item animations`() {
        val source = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")

        assertFalse(
            "Category cards must not create their own infinite transition while users horizontally swipe the category rail.",
            source.contains("label = \"category_power_")
        )
        assertFalse(
            "Category cards must not continuously pulse every visible category during a swipe.",
            source.contains("label = \"category_glow_")
        )
        assertFalse(
            "Category cards must not continuously rescale while the category rail is being swiped.",
            source.contains("label = \"category_breathe_")
        )
        assertFalse(
            "Topic cards must not create their own infinite transition while the Games screen uses a non-lazy vertical scroll container.",
            source.contains("label = \"topic_power_")
        )
        assertFalse(
            "Topic cards must not run a continuous travelling-energy animation for every topic at once.",
            source.contains("label = \"topic_energy_")
        )
        assertFalse(
            "Topic cards must not continuously rescale every topic card while scrolling.",
            source.contains("label = \"topic_breathe_")
        )
    }

    @Test
    fun `pack list virtualizes pack cards`() {
        val source = source("app/src/main/java/com/example/ui/screens/PackListScreen.kt")

        assertTrue(
            "PackListScreen must use LazyColumn so off-screen pack cards are not composed and animated.",
            source.contains("LazyColumn(")
        )
        assertTrue(
            "Pack cards must be emitted through lazy items.",
            source.contains("items(items = list") || source.contains("items(list")
        )
        assertFalse(
            "PackListScreen must not compose the whole pack list inside a verticalScroll Column.",
            source.contains(".verticalScroll(scrollState)")
        )
        assertFalse(
            "PackListScreen must not eagerly compose every pack with list.forEach.",
            source.contains("list.forEach")
        )
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
