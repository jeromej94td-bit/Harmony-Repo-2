package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage055TeamworkCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section20TeamworkChallenge.PACKS

    private val canonicalIds = listOf(
        "h500_411_zusammenhalt_entweder_oder",
        "h500_412_krisenmodus_wer_eher",
        "h500_413_teamgeist_skala",
        "h500_414_rollenverteilung_ranking",
        "h500_415_blindes_vertrauen_prognose",
        "h500_416_escape_room_szenario",
        "h500_418_groesster_triumph_memory",
        "h500_420_unschlagbar_offene_runde",
        "h500_421_wettbewerb_entweder_oder",
        "h500_422_mutprobe_wer_eher",
        "h500_424_staerken_ranking",
        "h500_425_gemeinsamer_sieg_prognose",
        "h500_426_notfallplan_szenario",
        "h500_430_team_zukunft_offene_runde"
    )

    private val archivedIds = setOf(
        "h500_417_geheimes_ziel_geheime_wahl",
        "h500_419_paarchallenge_prioritaet",
        "h500_423_durchhaltevermoegen_skala",
        "h500_427_mutiger_traum_geheime_wahl"
    )

    @Test
    fun `all 18 teamwork packs have explicit decisions with 14 rewrites and 4 archives`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360TeamworkSectionCuration.decisions.keys)
        assertEquals(14, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(4, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.ARCHIVE
        })
    }

    @Test
    fun `teamwork curation keeps only distinct packs with complete mechanic sized question sets`() {
        val curated = Harmony360TeamworkSectionCuration.apply(raw)
        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.none { it.id in archivedIds })

        curated.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            assertEquals(if (isScenario) 8 else 6, pack.questions.size)
        }

        assertTrue(curated.single { it.id == "h500_411_zusammenhalt_entweder_oder" }
            .questions.flatMap { it.options }.contains("Klar ehrlich sein"))
        assertTrue(curated.single { it.id == "h500_414_rollenverteilung_ranking" }
            .questions.any { it.q.contains("Ordne", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_416_escape_room_szenario" }
            .questions.any { it.q.contains("Rätsel", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_425_gemeinsamer_sieg_prognose" }
            .questions.any { it.q.contains("Quizshow", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_426_notfallplan_szenario" }
            .questions.any { it.q.contains("Panne", ignoreCase = true) || it.q.contains("ausfällt", ignoreCase = true) })
    }

    @Test
    fun `teamwork output contains no generator residue literal user labels or named reality show`() {
        val curated = Harmony360TeamworkSectionCuration.apply(raw)
        val allText = curated.flatMap { pack -> pack.questions.map { it.q + " " + it.options.joinToString(" ") } }
            .joinToString("\n")
            .lowercase()

        listOf("what decides", " rank:", "rankt:", "user mathe", "partner mathe", "user bleibt", "sommerhaus der stars").forEach {
            assertFalse("Forbidden residue: $it", allText.contains(it))
        }
    }

    @Test
    fun `runtime exposes only curated teamwork packs with existing topic routing and scenario contract`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_20_teamwork_challenge" in it.tags
        }

        assertEquals(canonicalIds, runtime.map { it.id })
        assertTrue(runtime.none { it.id in archivedIds })

        assertEquals("hobbys", runtime.single { it.id == "h500_416_escape_room_szenario" }.topic)
        assertEquals("hobbys", runtime.single { it.id == "h500_421_wettbewerb_entweder_oder" }.topic)
        assertEquals("aufwaermen", runtime.single { it.id == "h500_422_mutprobe_wer_eher" }.topic)
        assertEquals("aufwaermen", runtime.single { it.id == "h500_425_gemeinsamer_sieg_prognose" }.topic)
        assertEquals("beziehung", runtime.single { it.id == "h500_411_zusammenhalt_entweder_oder" }.topic)

        runtime.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            assertEquals(if (isScenario) 8 else 6, pack.questions.size)
        }
    }
}
