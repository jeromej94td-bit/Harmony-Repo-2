package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage053FutureCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section03ZukunftLebensplanung.PACKS

    private val canonicalIds = listOf(
        "h500_051_unser_naechstes_jahr_entweder_oder",
        "h500_054_traumhaus_ranking",
        "h500_056_auswandern_szenario",
        "h500_058_finanzielle_ziele_memory",
        "h500_060_hochzeit_offene_runde",
        "h500_061_familienplanung_entweder_oder",
        "h500_064_abenteuerliste_ranking",
        "h500_066_wohnort_szenario",
        "h500_070_sicherheit_oder_freiheit_offene_runde",
        "h500_075_das_leben_mit_60_prognose"
    )

    private val archivedIds = setOf(
        "h500_052_in_fuenf_jahren_wer_eher",
        "h500_053_traumwohnung_skala",
        "h500_055_stadt_oder_land_prognose",
        "h500_057_karriereplaene_geheime_wahl",
        "h500_062_lebensstil_wer_eher",
        "h500_065_bucket_list_prognose",
        "h500_067_prioritaeten_geheime_wahl",
        "h500_069_selbststaendigkeit_prioritaet"
    )

    @Test
    fun `all 18 raw future packs have explicit decisions with 10 rewrites and 8 archives`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360FutureSectionCuration.decisions.keys)
        assertEquals(
            10,
            Harmony360FutureSectionCuration.decisions.values.count {
                it == Harmony360FutureSectionCuration.CurationDecision.REWRITE
            }
        )
        assertEquals(
            8,
            Harmony360FutureSectionCuration.decisions.values.count {
                it == Harmony360FutureSectionCuration.CurationDecision.ARCHIVE
            }
        )
    }

    @Test
    fun `future curation keeps the 10 canonical packs in stable order and gives every survivor six concrete questions`() {
        val curated = Harmony360FutureSectionCuration.apply(raw)

        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.all { it.questions.size == 6 })
        assertTrue(curated.none { it.id in archivedIds })

        val nextYear = curated.single { it.id == "h500_051_unser_naechstes_jahr_entweder_oder" }
        assertTrue(nextYear.questions.flatMap { it.options }.contains("Ein großer gemeinsamer Schritt"))

        val dreamHome = curated.single { it.id == "h500_054_traumhaus_ranking" }
        assertTrue(dreamHome.questions.flatMap { it.options }.contains("Garten"))

        val emigration = curated.single { it.id == "h500_056_auswandern_szenario" }
        assertTrue(emigration.questions.any { it.q.contains("Jobangebot", ignoreCase = true) })

        val familyPlanning = curated.single { it.id == "h500_061_familienplanung_entweder_oder" }
        assertTrue(familyPlanning.questions.any { it.q.contains("Familienplanung", ignoreCase = true) })

        val ageSixty = curated.single { it.id == "h500_075_das_leben_mit_60_prognose" }
        assertTrue(ageSixty.questions.flatMap { it.options }.contains("Viel reisen"))
    }

    @Test
    fun `future curation output removes known generator quartets and wording leftovers`() {
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
    fun `runtime restores future packs while keeping curated rewrites and section topic moves`() {
        val runtimeFuture = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_03_zukunft_lebensplanung" in it.tags
        }

        assertEquals(raw.map { it.id }.toSet(), runtimeFuture.map { it.id }.toSet())
        assertTrue(runtimeFuture.map { it.id }.containsAll(archivedIds))

        assertEquals("reisen", runtimeFuture.single { it.id == "h500_056_auswandern_szenario" }.topic)
        assertEquals("geld", runtimeFuture.single { it.id == "h500_058_finanzielle_ziele_memory" }.topic)
        assertEquals("familie", runtimeFuture.single { it.id == "h500_060_hochzeit_offene_runde" }.topic)
        assertEquals("familie", runtimeFuture.single { it.id == "h500_061_familienplanung_entweder_oder" }.topic)
        assertEquals("reisen", runtimeFuture.single { it.id == "h500_064_abenteuerliste_ranking" }.topic)
        assertEquals("moral", runtimeFuture.single { it.id == "h500_070_sicherheit_oder_freiheit_offene_runde" }.topic)

        val runtimeDreamHome = runtimeFuture.single { it.id == "h500_054_traumhaus_ranking" }
        assertEquals("kennen", runtimeDreamHome.topic)
        assertTrue(runtimeDreamHome.questions.flatMap { it.options }.contains("Garten"))

        archivedIds.forEach { id ->
            assertEquals("kennen", runtimeFuture.single { it.id == id }.topic)
        }
    }
}
