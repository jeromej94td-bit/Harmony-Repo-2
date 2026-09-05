package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FineDiningVisualRankingContractTest {

    @Test
    fun `fine dining ranking is visual for exactly six rounds`() {
        val curation = source("app/src/main/java/com/example/data/Harmony360FoodSectionCuration.kt")
        val visualPolicy = sourceOrEmpty("app/src/main/java/com/example/ui/screens/FineDiningVisualRanking.kt")
        val rankingBoard = source("app/src/main/java/com/example/ui/screens/RankingSlotBoard.kt")

        assertTrue(curation.contains("Euer besonderer Abend beginnt – was muss zuerst stimmen?"))
        assertTrue(curation.contains("Hier darf der Abend Luxus sein – wo ist er dir das Geld wert?"))
        assertTrue(curation.contains("Ihr sitzt drei Stunden hier – was macht daraus einen richtig guten Abend?"))
        assertTrue(curation.contains("Der Küchenchef lässt euch die Richtung bestimmen."))
        assertTrue(curation.contains("Der Abend kippt – was nervt dich am schnellsten?"))
        assertTrue(curation.contains("Morgen früh – was soll dir davon noch im Kopf sein?"))

        assertTrue(visualPolicy.contains("h500_104_fine_dining_ranking"))
        assertTrue(visualPolicy.contains("fineDiningRankingImageRes"))
        assertTrue(rankingBoard.contains("imageResFor"))
        assertTrue(rankingBoard.contains("painterResource"))
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
    fun `all twenty four fine dining card artworks exist as webp assets`() {
        val assets = listOf(
            "fine_dining_rank_q01_geschmack.webp",
            "fine_dining_rank_q01_menufolge.webp",
            "fine_dining_rank_q01_service.webp",
            "fine_dining_rank_q01_atmosphaere.webp",
            "fine_dining_rank_q02_aussergewoehnliche_zutaten.webp",
            "fine_dining_rank_q02_kreative_zubereitung.webp",
            "fine_dining_rank_q02_perfekter_service.webp",
            "fine_dining_rank_q02_besondere_location.webp",
            "fine_dining_rank_q03_ueberraschende_gaenge.webp",
            "fine_dining_rank_q03_passende_portionsgroesse.webp",
            "fine_dining_rank_q03_gutes_tempo.webp",
            "fine_dining_rank_q03_zeit_zum_reden.webp",
            "fine_dining_rank_q04_regional.webp",
            "fine_dining_rank_q04_saisonal.webp",
            "fine_dining_rank_q04_experimentell.webp",
            "fine_dining_rank_q04_klassisch_perfektioniert.webp",
            "fine_dining_rank_q05_steif.webp",
            "fine_dining_rank_q05_zu_laut.webp",
            "fine_dining_rank_q05_zu_langsam.webp",
            "fine_dining_rank_q05_show_statt_geschmack.webp",
            "fine_dining_rank_q06_neues_lieblingsgericht.webp",
            "fine_dining_rank_q06_ueberraschende_kombination.webp",
            "fine_dining_rank_q06_schoene_erinnerung.webp",
            "fine_dining_rank_q06_neue_geschmacksidee.webp"
        )

        assets.forEach { name ->
            val file = resolve("app/src/main/res/drawable/$name")
            assertTrue("Missing visual asset: $name", file.exists())
            if (file.exists()) {
                assertTrue("Asset too small: $name", file.length() > 128L)
            }
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
