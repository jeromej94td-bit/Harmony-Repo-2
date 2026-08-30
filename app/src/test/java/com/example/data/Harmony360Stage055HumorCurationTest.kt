package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage055HumorCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section18HumorLachen.PACKS

    private val canonicalIds = listOf(
        "h500_371_humor_entweder_oder",
        "h500_372_lachen_wer_eher",
        "h500_376_peinliche_momente_szenario",
        "h500_377_insider_witze_geheime_wahl",
        "h500_378_lachflashs_memory",
        "h500_379_humor_im_alltag_prioritaet",
        "h500_380_ironie_offene_runde",
        "h500_382_necken_wer_eher",
        "h500_387_schwarzer_humor_geheime_wahl",
        "h500_390_gemeinsam_lachen_offene_runde"
    )

    private val archivedIds = setOf(
        "h500_373_schadenfreude_skala",
        "h500_374_witze_ranking",
        "h500_375_comedy_prognose",
        "h500_381_galgenhumor_entweder_oder",
        "h500_383_kitzelig_skala",
        "h500_384_karikaturen_ranking",
        "h500_385_parodien_prognose",
        "h500_386_missgeschicke_szenario"
    )

    @Test
    fun `all 18 humor packs have explicit decisions with 10 rewrites and 8 archives`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360HumorSectionCuration.decisions.keys)
        assertEquals(10, Harmony360HumorSectionCuration.decisions.values.count {
            it == Harmony360HumorSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(8, Harmony360HumorSectionCuration.decisions.values.count {
            it == Harmony360HumorSectionCuration.CurationDecision.ARCHIVE
        })
    }

    @Test
    fun `humor curation keeps only distinct packs with six concrete core questions`() {
        val curated = Harmony360HumorSectionCuration.apply(raw)
        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.none { it.id in archivedIds })
        assertTrue(curated.all { it.questions.size == 6 })

        assertTrue(curated.single { it.id == "h500_371_humor_entweder_oder" }
            .questions.flatMap { it.options }.contains("Trocken & subtil"))
        assertTrue(curated.single { it.id == "h500_376_peinliche_momente_szenario" }
            .questions.any { it.q.contains("vor anderen", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_377_insider_witze_geheime_wahl" }
            .questions.any { it.q.contains("nur ihr", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_378_lachflashs_memory" }
            .questions.any { it.q.contains("Lachflash", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_380_ironie_offene_runde" }
            .questions.any { it.q.contains("missverstanden", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_387_schwarzer_humor_geheime_wahl" }
            .questions.any { it.q.contains("Grenze", ignoreCase = true) })
    }

    @Test
    fun `humor output contains no generator quartets or english ranking residue`() {
        val curated = Harmony360HumorSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Aktiv", "Passiv", "Etwas lernen", "Einfach abschalten"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
            listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung")
        )

        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
        assertFalse(curated.flatMap { it.questions }.any { question ->
            question.q.startsWith("What ") ||
                question.q.contains("Rank:", ignoreCase = true) ||
                question.q.contains("Rankt:", ignoreCase = true) ||
                question.q.contains("Plot-Twist", ignoreCase = true)
        })
    }

    @Test
    fun `runtime exposes only curated humor packs as warm up content and preserves scenario contract`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_18_humor_lachen" in it.tags
        }

        assertEquals(canonicalIds, runtime.map { it.id })
        assertTrue(runtime.none { it.id in archivedIds })
        assertTrue(runtime.all { it.topic == "aufwaermen" })

        runtime.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            assertEquals(if (isScenario) 8 else 6, pack.questions.size)
        }
    }
}
