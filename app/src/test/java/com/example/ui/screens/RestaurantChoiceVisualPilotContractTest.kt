package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantChoiceVisualPilotContractTest {

    @Test
    fun `restaurant revisit question becomes a visual four-card pilot`() {
        val visualPolicy = sourceOrEmpty("app/src/main/java/com/example/ui/screens/RestaurantChoiceVisualPilot.kt")
        val priorityBoard = source("app/src/main/java/com/example/ui/screens/DirectPriorityPokerBoard.kt")

        assertTrue(visualPolicy.contains("Was wäre für dich der größte Grund, ein Restaurant nicht wieder zu besuchen?"))
        assertTrue(visualPolicy.contains("Euer Date läuft – was würde dir den Abend am schnellsten verderben?"))
        assertTrue(visualPolicy.contains("Essen enttäuscht"))
        assertTrue(visualPolicy.contains("Schlechter Service"))
        assertTrue(visualPolicy.contains("Zu laut"))
        assertTrue(visualPolicy.contains("Preis passt nicht"))
        assertTrue(visualPolicy.contains("RestaurantChoiceVisualThumbnail"))
        assertTrue(visualPolicy.contains("R.drawable.restaurant_revisit_visual_atlas"))
        assertTrue(priorityBoard.contains("restaurantChoiceVisualSpec"))
        assertTrue(priorityBoard.contains("RestaurantChoiceVisualGrid"))
    }

    @Test
    fun `stored restaurant answer values stay stable`() {
        val curation = source("app/src/main/java/com/example/data/Harmony360FoodSectionCuration.kt")
        listOf("Essen enttäuscht", "Schlechter Service", "Zu laut", "Preis passt nicht").forEach { value ->
            assertTrue("Missing stable answer value: $value", curation.contains("\"$value\""))
        }
    }

    @Test
    fun `restaurant pilot artwork is a real webp atlas`() {
        val atlas = resolve("app/src/main/res/drawable/restaurant_revisit_visual_atlas.webp")
        assertTrue("Missing restaurant visual atlas", atlas.exists())
        val bytes = atlas.readBytes()
        assertTrue("Restaurant visual atlas is too small: ${bytes.size} bytes", bytes.size > 100_000)
        assertTrue("Restaurant visual atlas unexpectedly large: ${bytes.size} bytes", bytes.size < 800_000)
        assertTrue(bytes.size >= 12)
        assertEquals("RIFF", String(bytes.copyOfRange(0, 4), Charsets.US_ASCII))
        assertEquals("WEBP", String(bytes.copyOfRange(8, 12), Charsets.US_ASCII))
    }

    @Test
    fun `pilot stays scoped to one existing restaurant question`() {
        val visualPolicy = sourceOrEmpty("app/src/main/java/com/example/ui/screens/RestaurantChoiceVisualPilot.kt")
        assertFalse(visualPolicy.contains("Was entscheidet bei der Restaurantwahl zuerst?"))
        assertFalse(visualPolicy.contains("Was soll bei einem Date-Restaurant am stärksten sein?"))
    }

    private fun source(path: String): String = resolve(path).readText()
    private fun sourceOrEmpty(path: String): String = resolve(path).takeIf(File::exists)?.readText().orEmpty()

    private fun resolve(path: String): File =
        listOf(File(path.removePrefix("app/")), File(path)).firstOrNull(File::exists) ?: File(path)
}
