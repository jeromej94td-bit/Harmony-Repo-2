package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage052FoodCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section05EssenGenuss.PACKS

    @Test
    fun `all 18 food packs have an explicit rewrite decision`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360FoodSectionCuration.decisions.keys)
        assertTrue(Harmony360FoodSectionCuration.decisions.values.all {
            it == Harmony360FoodSectionCuration.CurationDecision.REWRITE
        })
    }

    @Test
    fun `food curation keeps ids but replaces generator filler with food choices`() {
        val curated = Harmony360FoodSectionCuration.apply(raw)
        assertEquals(18, curated.size)
        assertEquals(raw.map { it.id }, curated.map { it.id })

        val breakfast = curated.single { it.id == "h500_101_fruehstueck_entweder_oder" }
        assertTrue(breakfast.questions.flatMap { it.options }.contains("Frühstücksei"))

        val fineDining = curated.single { it.id == "h500_104_fine_dining_ranking" }
        assertTrue(fineDining.questions.flatMap { it.options }.contains("Menüfolge"))

        val comfort = curated.single { it.id == "h500_105_comfort_food_prognose" }
        assertTrue(comfort.questions.flatMap { it.options }.contains("Suppe"))

        val coffee = curated.single { it.id == "h500_114_kaffee_ranking" }
        assertTrue(coffee.questions.flatMap { it.options }.contains("Espresso"))

        val cooking = curated.single { it.id == "h500_117_kochen_zu_zweit_geheime_wahl" }
        assertTrue(cooking.questions.any { it.q.contains("Aufgaben", ignoreCase = true) })
    }

    @Test
    fun `curated food output no longer contains known generic relationship quartets`() {
        val curated = Harmony360FoodSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
            listOf("Respekt", "Ehrlichkeit", "Humor", "Freiraum"),
            listOf("Streetfood", "Hausmannskost", "Fine Dining", "Selbst gekocht")
        )
        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
    }

    @Test
    fun `runtime registry includes food curation`() {
        val runtimeCoffee = GeneratedHarmonyAdrenaline360.PACKS.single { it.id == "h500_114_kaffee_ranking" }
        assertTrue(runtimeCoffee.questions.flatMap { it.options }.contains("Espresso"))
    }
}
