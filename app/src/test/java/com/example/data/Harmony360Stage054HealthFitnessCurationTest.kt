package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage054HealthFitnessCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section11GesundheitFitness.PACKS

    private val canonicalIds = listOf(
        "h500_231_ernaehrung_entweder_oder",
        "h500_236_sportliche_ziele_szenario",
        "h500_238_krank_sein_memory",
        "h500_239_gesunder_lebensstil_prioritaet",
        "h500_241_biorhythmus_entweder_oder",
        "h500_242_gesundes_kochen_wer_eher",
        "h500_247_sportarten_geheime_wahl",
        "h500_250_gemeinsame_gesundheit_offene_runde"
    )

    private val archivedIds = setOf(
        "h500_232_schlafgewohnheiten_wer_eher",
        "h500_233_mental_health_skala",
        "h500_234_arztbesuche_ranking",
        "h500_235_stressbewaeltigung_prognose",
        "h500_237_wellness_und_spa_geheime_wahl",
        "h500_240_koerpergefuehl_offene_runde",
        "h500_243_routinen_skala",
        "h500_244_vorsorge_ranking",
        "h500_245_suchtmittel_prognose",
        "h500_246_regeneration_szenario"
    )

    @Test
    fun `all 18 health packs have explicit decisions with 8 rewrites and 10 archives`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360HealthFitnessSectionCuration.decisions.keys)
        assertEquals(8, Harmony360HealthFitnessSectionCuration.decisions.values.count {
            it == Harmony360HealthFitnessSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(10, Harmony360HealthFitnessSectionCuration.decisions.values.count {
            it == Harmony360HealthFitnessSectionCuration.CurationDecision.ARCHIVE
        })
    }

    @Test
    fun `health curation keeps canonical ids and exactly six concrete questions per survivor`() {
        val curated = Harmony360HealthFitnessSectionCuration.apply(raw)
        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.none { it.id in archivedIds })
        assertTrue(curated.all { it.questions.size == 6 })

        assertTrue(curated.single { it.id == "h500_231_ernaehrung_entweder_oder" }
            .questions.flatMap { it.options }.contains("Frisch kochen"))
        assertTrue(curated.single { it.id == "h500_236_sportliche_ziele_szenario" }
            .questions.any { it.q.contains("ohne Druck", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_238_krank_sein_memory" }
            .questions.any { it.q.contains("unterstützen", ignoreCase = true) || it.q.contains("Fürsorge", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_239_gesunder_lebensstil_prioritaet" }
            .questions.flatMap { it.options }.contains("Quelle für Schuldgefühle"))
        assertTrue(curated.single { it.id == "h500_241_biorhythmus_entweder_oder" }
            .questions.flatMap { it.options }.contains("Morgens"))
        assertEquals(6, curated.single { it.id == "h500_242_gesundes_kochen_wer_eher" }.questions.size)
        assertTrue(curated.single { it.id == "h500_247_sportarten_geheime_wahl" }
            .questions.flatMap { it.options }.contains("Klettern"))
        assertTrue(curated.single { it.id == "h500_250_gemeinsame_gesundheit_offene_runde" }
            .questions.any { it.q.contains("ohne", ignoreCase = true) && it.q.contains("Kontrolle", ignoreCase = true) })
    }

    @Test
    fun `health output contains no generator quartets english leftovers or medical instructions`() {
        val curated = Harmony360HealthFitnessSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Aktiv", "Passiv", "Etwas lernen", "Einfach abschalten"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht")
        )
        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })

        val allText = curated.flatMap { pack -> pack.questions.map { it.q + " " + it.options.joinToString(" ") } }
            .joinToString("\n")
            .lowercase()

        listOf("what decides", "rank:", "rankt:", "diagnose stellen", "medikament absetzen", "behandlung ersetzen").forEach {
            assertFalse("Forbidden residue/instruction: $it", allText.contains(it))
        }
    }

    @Test
    fun `runtime registry exposes only canonical health packs with final topic routing`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_11_gesundheit_fitness" in it.tags
        }
        assertEquals(canonicalIds, runtime.map { it.id })
        assertTrue(runtime.none { it.id in archivedIds })
        assertTrue(runtime.all { it.questions.size == 6 })

        assertEquals("essen", runtime.single { it.id == "h500_231_ernaehrung_entweder_oder" }.topic)
        assertEquals("hobbys", runtime.single { it.id == "h500_236_sportliche_ziele_szenario" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_238_krank_sein_memory" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_239_gesunder_lebensstil_prioritaet" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_241_biorhythmus_entweder_oder" }.topic)
        assertEquals("essen", runtime.single { it.id == "h500_242_gesundes_kochen_wer_eher" }.topic)
        assertEquals("hobbys", runtime.single { it.id == "h500_247_sportarten_geheime_wahl" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_250_gemeinsame_gesundheit_offene_runde" }.topic)
    }
}
