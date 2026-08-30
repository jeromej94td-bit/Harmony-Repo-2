package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage053WorkCareerCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section10ArbeitKarriere.PACKS

    private val canonicalIds = listOf(
        "h500_211_arbeitszeiten_entweder_oder",
        "h500_214_selbststaendigkeit_ranking",
        "h500_215_berufliche_veraenderung_prognose",
        "h500_216_work_life_balance_szenario",
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl",
        "h500_219_berufliche_ziele_prioritaet",
        "h500_220_job_und_beziehung_offene_runde",
        "h500_221_nebenjob_entweder_oder",
        "h500_224_arbeitsweg_ranking",
        "h500_225_ruhestand_prognose",
        "h500_226_kuendigung_szenario",
        "h500_227_kollegen_geheime_wahl",
        "h500_230_beruflicher_erfolg_offene_runde"
    )

    private val archivedIds = setOf(
        "h500_212_ueberstunden_wer_eher",
        "h500_213_karriere_skala",
        "h500_218_erster_job_memory",
        "h500_222_chef_sein_wer_eher",
        "h500_223_weiterbildung_skala"
    )

    @Test
    fun `all 18 work packs have explicit decisions with 13 rewrites and 5 archives`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360WorkCareerSectionCuration.decisions.keys)
        assertEquals(13, Harmony360WorkCareerSectionCuration.decisions.values.count {
            it == Harmony360WorkCareerSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(5, Harmony360WorkCareerSectionCuration.decisions.values.count {
            it == Harmony360WorkCareerSectionCuration.CurationDecision.ARCHIVE
        })
    }

    @Test
    fun `work curation keeps canonical ids and gives each survivor six concrete questions`() {
        val curated = Harmony360WorkCareerSectionCuration.apply(raw)
        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.all { it.questions.size == 6 })
        assertTrue(curated.none { it.id in archivedIds })

        assertTrue(curated.single { it.id == "h500_211_arbeitszeiten_entweder_oder" }
            .questions.flatMap { it.options }.contains("Feste Arbeitszeiten"))
        assertTrue(curated.single { it.id == "h500_214_selbststaendigkeit_ranking" }
            .questions.any { it.q.contains("Selbstständigkeit", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_216_work_life_balance_szenario" }
            .questions.any { it.q.contains("Überstunden", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_225_ruhestand_prognose" }
            .questions.flatMap { it.options }.contains("Früher aufhören, wenn es finanziell passt"))
        assertTrue(curated.single { it.id == "h500_230_beruflicher_erfolg_offene_runde" }
            .questions.any { it.q.contains("Erfolg", ignoreCase = true) })
    }

    @Test
    fun `work output removes generic generator options and english leftovers`() {
        val curated = Harmony360WorkCareerSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Karriere", "Familie", "Ausgewogen", "Sehr unabhängig"),
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht")
        )
        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
        assertFalse(curated.flatMap { it.questions }.any { question ->
            question.q.startsWith("What ") || question.q.contains("Rank:", ignoreCase = true)
        })
    }

    @Test
    fun `runtime registry preserves work visibility and final topic routing`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_10_arbeit_karriere" in it.tags
        }
        assertEquals(canonicalIds.toSet(), runtime.map { it.id }.toSet())
        assertTrue(runtime.none { it.id in archivedIds })
        assertEquals("beziehung", runtime.single { it.id == "h500_220_job_und_beziehung_offene_runde" }.topic)
        assertEquals("geld", runtime.single { it.id == "h500_225_ruhestand_prognose" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_216_work_life_balance_szenario" }.topic)
    }
}
