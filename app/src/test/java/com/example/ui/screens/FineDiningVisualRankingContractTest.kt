package com.example.ui.screens

import java.io.File
import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FineDiningVisualRankingContractTest {

    @Test
    fun `fine dining ranking is visual for exactly six rounds`() {
        val visualPolicy = sourceOrEmpty("app/src/main/java/com/example/ui/screens/FineDiningVisualRanking.kt")
        val rankingBoard = source("app/src/main/java/com/example/ui/screens/RankingSlotBoard.kt")

        assertTrue(visualPolicy.contains("Euer besonderer Abend beginnt – was muss zuerst stimmen?"))
        assertTrue(visualPolicy.contains("Hier darf der Abend Luxus sein – wo ist er dir das Geld wert?"))
        assertTrue(visualPolicy.contains("Ihr sitzt drei Stunden hier – was macht daraus einen richtig guten Abend?"))
        assertTrue(visualPolicy.contains("Der Küchenchef lässt euch die Richtung bestimmen."))
        assertTrue(visualPolicy.contains("Der Abend kippt – was nervt dich am schnellsten?"))
        assertTrue(visualPolicy.contains("Morgen früh – was soll dir davon noch im Kopf sein?"))

        assertTrue(visualPolicy.contains("h500_104_fine_dining_ranking"))
        assertTrue(visualPolicy.contains("FineDiningRankingThumbnail"))
        assertTrue(visualPolicy.contains("fine_dining_ranking_atlas_01"))
        assertTrue(visualPolicy.contains("fine_dining_ranking_atlas_06"))
        assertTrue(rankingBoard.contains("fineDiningVisualPrompt"))
        assertTrue(rankingBoard.contains("fineDiningRankingCard"))
    }

    @Test
    fun `stored answer values stay stable while visuals are added`() {
        val curation = source("app/src/main/java/com/example/data/Harmony360FoodSectionCuration.kt")
        val stableValues = listOf(
            "Geschmack", "Menüfolge", "Service", "Atmosphäre",
            "Außergewöhnliche Zutaten", "Kreative Zubereitung", "Perfekter Service", "Besondere Location",
            "Überraschende Gänge", "Passende Portionsgröße", "Gutes Tempo", "Zeit zum Reden",
            "Regional", "Saisonal", "Experimentell", "Klassisch perfektioniert",
            "Steif", "Zu laut", "Zu langsam", "Mehr Show als Geschmack",
            "Ein neues Lieblingsgericht", "Eine überraschende Kombination", "Eine schöne Erinnerung", "Eine neue Geschmacksidee"
        )
        stableValues.forEach { assertTrue("Missing stable answer value: $it", curation.contains("\"$it\"")) }
    }

    @Test
    fun `fine dining atlas chunks decode into real webp artwork`() {
        val chunkPaths = (1..6).map { index ->
            "app/src/main/res/raw/fine_dining_ranking_atlas_%02d.b64".format(index)
        }
        chunkPaths.forEach { path ->
            assertTrue("Missing Fine Dining artwork chunk: $path", resolve(path).exists())
        }

        val encoded = chunkPaths.joinToString(separator = "") { path -> source(path).trim() }
        val bytes = Base64.getDecoder().decode(encoded)

        assertTrue("Fine Dining visual atlas is too small: ${bytes.size} bytes", bytes.size > 50_000)
        assertTrue("Fine Dining visual atlas unexpectedly large: ${bytes.size} bytes", bytes.size < 1_000_000)
        assertTrue("Fine Dining atlas header is incomplete", bytes.size >= 12)
        assertEquals("RIFF", String(bytes.copyOfRange(0, 4), Charsets.US_ASCII))
        assertEquals("WEBP", String(bytes.copyOfRange(8, 12), Charsets.US_ASCII))
    }

    @Test
    fun `visual ranking stays scoped to fine dining`() {
        val visualPolicy = sourceOrEmpty("app/src/main/java/com/example/ui/screens/FineDiningVisualRanking.kt")
        assertFalse(visualPolicy.contains("h500_114_kaffee_ranking"))
        assertFalse(visualPolicy.contains("h500_109_pasta_prioritaet"))
    }

    private fun source(path: String): String = resolve(path).readText()

    private fun sourceOrEmpty(path: String): String = resolve(path).takeIf(File::exists)?.readText().orEmpty()

    private fun resolve(path: String): File =
        listOf(File(path.removePrefix("app/")), File(path)).firstOrNull(File::exists) ?: File(path)
}
