package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage054PsychologyFeelingsCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section17PsychologieGefuehle.PACKS

    private val canonicalIds = listOf(
        "h500_352_einfuehlungsvermoegen_wer_eher",
        "h500_353_verletzlichkeit_skala",
        "h500_355_eifersucht_prognose",
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl",
        "h500_358_kindheitstraumata_memory",
        "h500_361_selbstwertgefuehl_entweder_oder",
        "h500_362_troesten_wer_eher",
        "h500_366_stressreaktionen_szenario",
        "h500_367_sehnsuechte_geheime_wahl"
    )

    private val archivedIds = setOf(
        "h500_351_selbstreflexion_entweder_oder",
        "h500_354_bindungsmuster_ranking",
        "h500_356_aengste_szenario",
        "h500_359_emotionale_sicherheit_prioritaet",
        "h500_360_liebeserklaerung_offene_runde",
        "h500_363_stimmungsschwankungen_skala",
        "h500_364_vertrauen_ranking",
        "h500_365_vergebung_prognose",
        "h500_370_gemeinsame_psychohygiene_offene_runde"
    )

    @Test
    fun `all 18 psychology packs have explicit decisions with 9 rewrites and 9 archives`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360PsychologyFeelingsSectionCuration.decisions.keys)
        assertEquals(9, Harmony360PsychologyFeelingsSectionCuration.decisions.values.count {
            it == Harmony360PsychologyFeelingsSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(9, Harmony360PsychologyFeelingsSectionCuration.decisions.values.count {
            it == Harmony360PsychologyFeelingsSectionCuration.CurationDecision.ARCHIVE
        })
    }

    @Test
    fun `psychology curation keeps nine canonical packs with six concrete core questions`() {
        val curated = Harmony360PsychologyFeelingsSectionCuration.apply(raw)
        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.none { it.id in archivedIds })
        assertTrue(curated.all { it.questions.size == 6 })

        assertTrue(curated.single { it.id == "h500_352_einfuehlungsvermoegen_wer_eher" }
            .questions.any { it.q.contains("Stimmung", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_353_verletzlichkeit_skala" }
            .questions.any { it.q.contains("offen", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_355_eifersucht_prognose" }
            .questions.any { it.q.contains("Grenze", ignoreCase = true) || it.q.contains("Unsicherheit", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_357_wuensche_und_beduerfnisse_geheime_wahl" }
            .questions.any { it.q.contains("Bedürfnis", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_358_kindheitstraumata_memory" }
            .questions.any { it.q.contains("Kindheit", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_361_selbstwertgefuehl_entweder_oder" }
            .questions.any { it.q.contains("Anerkennung", ignoreCase = true) || it.q.contains("Selbst", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_362_troesten_wer_eher" }
            .questions.any { it.q.contains("trösten", ignoreCase = true) || it.q.contains("auffangen", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_366_stressreaktionen_szenario" }
            .questions.any { it.q.contains("Stress", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_367_sehnsuechte_geheime_wahl" }
            .questions.any { it.q.contains("Sehnsucht", ignoreCase = true) || it.q.contains("wünsch", ignoreCase = true) })
    }

    @Test
    fun `psychology output avoids generator filler and diagnostic labels`() {
        val curated = Harmony360PsychologyFeelingsSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Kopf", "Herz", "Bauch", "Erfahrung"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
            listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung")
        )
        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })

        val allText = curated.flatMap { pack -> pack.questions.map { it.q + " " + it.options.joinToString(" ") } }
            .joinToString("\n")
            .lowercase()
        listOf("what decides", "rank:", "rankt:", "du hast eine störung", "dein partner ist toxisch", "diagnose").forEach {
            assertFalse("Forbidden residue/label: $it", allText.contains(it))
        }
    }

    @Test
    fun `runtime registry exposes canonical psychology packs and intended final topics`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_17_psychologie_gefuehle" in it.tags
        }
        assertEquals(canonicalIds, runtime.map { it.id })
        runtime.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            assertEquals(if (isScenario) 8 else 6, pack.questions.size)
        }

        listOf(
            "h500_352_einfuehlungsvermoegen_wer_eher",
            "h500_353_verletzlichkeit_skala",
            "h500_357_wuensche_und_beduerfnisse_geheime_wahl",
            "h500_358_kindheitstraumata_memory",
            "h500_361_selbstwertgefuehl_entweder_oder",
            "h500_362_troesten_wer_eher",
            "h500_366_stressreaktionen_szenario",
            "h500_367_sehnsuechte_geheime_wahl"
        ).forEach { id -> assertEquals("kennen", runtime.single { it.id == id }.topic) }
        assertEquals("beziehung", runtime.single { it.id == "h500_355_eifersucht_prognose" }.topic)
    }
}
