package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage052TravelCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section04ReisenAbenteuer.PACKS

    @Test
    fun `all 18 travel packs have an explicit curation decision`() {
        assertEquals(18, raw.size)
        assertEquals(
            raw.map { it.id }.toSet(),
            Harmony360FoodTravelLeisureCultureQualityRework.travelDecisions.keys
        )
        assertTrue(
            Harmony360FoodTravelLeisureCultureQualityRework.travelDecisions.values.all {
                it == Harmony360FoodTravelLeisureCultureQualityRework.CurationDecision.REWRITE
            }
        )
    }

    @Test
    fun `travel curation keeps the distinct travel formats but replaces generator filler`() {
        val curated = Harmony360FoodTravelLeisureCultureQualityRework.apply(raw)

        assertEquals(18, curated.size)
        assertEquals(raw.map { it.id }, curated.map { it.id })

        val weekend = curated.single { it.id == "h500_076_wochenendtrip_szenario" }
        assertTrue(weekend.questions.any { it.q.contains("Freitagabend") })

        val wellness = curated.single { it.id == "h500_084_wellnessurlaub_ranking" }
        assertTrue(wellness.questions.flatMap { it.options }.contains("Sauna & Dampfbad"))
        assertFalse(wellness.questions.flatMap { it.options }.contains("Camping"))

        val adventure = curated.single { it.id == "h500_085_abenteuerurlaub_prognose" }
        assertTrue(adventure.questions.flatMap { it.options }.contains("Rafting"))

        val culinary = curated.single { it.id == "h500_086_kulinarische_reise_szenario" }
        assertTrue(culinary.questions.flatMap { it.options }.contains("Streetfood probieren"))

        val perfectDay = curated.single { it.id == "h500_100_unser_perfekter_reisetag_offene_runde" }
        assertFalse(perfectDay.questions.any { it.q.contains("fnnf", ignoreCase = true) })
        assertTrue(perfectDay.questions.any { it.q.contains("Frühstück", ignoreCase = true) })
    }

    @Test
    fun `curated travel output no longer contains known generic answer quartets`() {
        val curated = Harmony360FoodTravelLeisureCultureQualityRework.apply(raw)
        val forbidden = setOf(
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
            listOf("Respekt", "Ehrlichkeit", "Humor", "Freiraum")
        )

        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
    }

    @Test
    fun `runtime Harmony 360 registry uses the travel curation layer`() {
        val runtimeWellness = GeneratedHarmonyAdrenaline360.PACKS
            .single { it.id == "h500_084_wellnessurlaub_ranking" }

        assertTrue(runtimeWellness.questions.flatMap { it.options }.contains("Sauna & Dampfbad"))
    }
}
