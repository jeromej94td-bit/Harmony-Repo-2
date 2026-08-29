package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage053FutureCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section03ZukunftLebensplanung.PACKS

    @Test
    fun `all 18 future packs have an explicit rewrite decision`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360FutureSectionCuration.decisions.keys)
        assertTrue(Harmony360FutureSectionCuration.decisions.values.all {
            it == Harmony360FutureSectionCuration.CurationDecision.REWRITE
        })
    }

    @Test
    fun `future curation keeps ids and gives every pack six concrete questions`() {
        val curated = Harmony360FutureSectionCuration.apply(raw)
        assertEquals(18, curated.size)
        assertEquals(raw.map { it.id }, curated.map { it.id })
        assertTrue(curated.all { it.questions.size == 6 })

        val nextYear = curated.single { it.id == "h500_051_unser_naechstes_jahr_entweder_oder" }
        assertTrue(nextYear.questions.flatMap { it.options }.contains("Ein großer gemeinsamer Schritt"))

        val dreamHome = curated.single { it.id == "h500_054_traumhaus_ranking" }
        assertTrue(dreamHome.questions.flatMap { it.options }.contains("Garten"))

        val cityCountry = curated.single { it.id == "h500_055_stadt_oder_land_prognose" }
        assertTrue(cityCountry.questions.flatMap { it.options }.contains("Kurze Wege"))

        val emigration = curated.single { it.id == "h500_056_auswandern_szenario" }
        assertTrue(emigration.questions.any { it.q.contains("Jobangebot", ignoreCase = true) })

        val familyPlanning = curated.single { it.id == "h500_061_familienplanung_entweder_oder" }
        assertTrue(familyPlanning.questions.any { it.q.contains("Familienplanung", ignoreCase = true) })

        val ageSixty = curated.single { it.id == "h500_075_das_leben_mit_60_prognose" }
        assertTrue(ageSixty.questions.flatMap { it.options }.contains("Viel reisen"))
    }

    @Test
    fun `future output removes known generator quartets and wording leftovers`() {
        val curated = Harmony360FutureSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Karriere", "Familie", "Erlebnisse", "Finanzielle Freiheit"),
            listOf("Großstadt", "Kleinstadt", "Land", "Ausland"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht")
        )

        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
        assertFalse(curated.flatMap { it.questions }.any { question ->
            question.q.startsWith("What ") ||
                question.q.contains(" Rank:", ignoreCase = true) ||
                question.q.contains(" variante ", ignoreCase = true) ||
                question.q.contains(" energie ", ignoreCase = true) ||
                question.q.contains(" is einer ", ignoreCase = true)
        })
    }

    @Test
    fun `runtime registry includes future curation`() {
        val runtimeDreamHome = GeneratedHarmonyAdrenaline360.PACKS.single { it.id == "h500_054_traumhaus_ranking" }
        assertTrue(runtimeDreamHome.questions.flatMap { it.options }.contains("Garten"))
    }
}
