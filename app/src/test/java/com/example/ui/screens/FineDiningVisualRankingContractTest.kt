package com.example.ui.screens

import java.io.File
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
        assertTrue(visualPolicy.contains("fineDiningRankingImageRes"))
        assertTrue(visualPolicy.contains("FineDiningRankingThumbnail"))
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
    fun `fine dining atlas exists and contains real artwork bytes`() {
        val atlasPath = "app/src/main/res/drawable/fine_dining_ranking_atlas.webp"
        val candidateModule = File(atlasPath.removePrefix("app/"))
        val candidateRepo = File(atlasPath)
        val atlas = resolve(atlasPath)
        val diagnostics = buildString {
            appendLine("user.dir=${System.getProperty("user.dir")}")
            appendLine("candidateModule=${candidateModule.absolutePath} exists=${candidateModule.exists()} length=${candidateModule.length()}")
            appendLine("candidateRepo=${candidateRepo.absolutePath} exists=${candidateRepo.exists()} length=${candidateRepo.length()}")
            appendLine("resolved=${atlas.absolutePath} exists=${atlas.exists()} length=${atlas.length()}")
        }

        assertTrue("Missing Fine Dining visual atlas\n$diagnostics", atlas.exists())
        if (atlas.exists()) {
            assertTrue("Fine Dining visual atlas is too small\n$diagnostics", atlas.length() > 50_000L)
            val header = atlas.inputStream().use { stream -> ByteArray(12).also(stream::read) }
            assertTrue(String(header.copyOfRange(0, 4), Charsets.US_ASCII) == "RIFF")
            assertTrue(String(header.copyOfRange(8, 12), Charsets.US_ASCII) == "WEBP")
        }
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
