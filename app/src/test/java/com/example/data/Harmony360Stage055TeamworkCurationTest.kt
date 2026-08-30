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
        "h500_416_escape_room_szenario",
        "h500_417_geheimes_ziel_geheime_wahl",
        "h500_418_groesster_triumph_memory",
        "h500_419_paarchallenge_prioritaet",
        "h500_421_wettbewerb_entweder_oder",
        "h500_425_gemeinsamer_sieg_prognose",
        "h500_426_notfallplan_szenario",
        "h500_430_team_zukunft_offene_runde"
    )

    private val archivedIds = setOf(
        "h500_415_blindes_vertrauen_prognose",
        "h500_420_unschlagbar_offene_runde",
        "h500_423_durchhaltevermoegen_skala",
        "h500_424_staerken_ranking",
        "h500_427_mutiger_traum_geheime_wahl"
    )

    private val mergedMutprobeId = "h500_422_mutprobe_wer_eher"

    @Test
    fun `all 18 teamwork packs have 12 rewrites 5 archives and one explicit merge`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360TeamworkSectionCuration.decisions.keys)
        assertEquals(12, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(5, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.ARCHIVE
        })
        assertEquals(1, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.MERGE
        })
        assertEquals(
            Harmony360TeamworkSectionCuration.CurationDecision.MERGE,
            Harmony360TeamworkSectionCuration.decisions.getValue(mergedMutprobeId)
        )
    }

    @Test
    fun `teamwork curation keeps the approved 12 packs and removes only archives plus merged source`() {
        val curated = Harmony360TeamworkSectionCuration.apply(raw)
        assertEquals(canonicalIds, curated.map { it.id })
        assertTrue(curated.none { it.id in archivedIds || it.id == mergedMutprobeId })

        curated.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            assertEquals(if (isScenario) 8 else 6, pack.questions.size)
        }

        assertTrue(curated.single { it.id == "h500_417_geheimes_ziel_geheime_wahl" }
            .questions.any { it.q.contains("gemeinsames", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_419_paarchallenge_prioritaet" }
            .questions.any { it.q.contains("Challenge", ignoreCase = true) })
        assertTrue(curated.single { it.id == "h500_413_teamgeist_skala" }
            .questions.any { it.q.contains("Komfortzone", ignoreCase = true) })

        val teamFuture = curated.single { it.id == "h500_430_team_zukunft_offene_runde" }
        assertFalse(teamFuture.questions.any {
            it.q.contains("Was möchtest du mir für unser gemeinsames Team heute von Herzen sagen", ignoreCase = true)
        })
    }

    @Test
    fun `mutprobe ideas are redistributed into their real destination packs instead of discarded`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS

        val travel = runtime.single { it.id == "h500_085_abenteuerurlaub_prognose" }
        assertTrue(travel.questions.any { question ->
            question.q.contains("Mutprobe", ignoreCase = true) &&
                question.options.any { it.contains("Bungee", ignoreCase = true) }
        })
        assertTrue(travel.questions.any { question ->
            question.q.contains("Wasser", ignoreCase = true) &&
                question.options.any { it.contains("kalte", ignoreCase = true) }
        })

        val restaurant = runtime.single { it.id == "h500_119_restaurantwahl_prioritaet" }
        assertTrue(restaurant.questions.any {
            it.q.contains("falsche", ignoreCase = true) && it.q.contains("Essen", ignoreCase = true)
        })

        val streetfood = runtime.single { it.id == "h500_103_streetfood_skala" }
        assertTrue(streetfood.questions.any {
            it.q.contains("auf Reisen", ignoreCase = true) && it.q.contains("Gericht", ignoreCase = true)
        })

        val justice = runtime.single { it.id == "h500_340_gerechtigkeit_offene_runde" }
        assertTrue(justice.questions.any {
            it.q.contains("unfair behandelt", ignoreCase = true)
        })

        val character = runtime.single { it.id == "h500_272_charaktereigenschaften_wer_eher" }
        assertTrue(character.questions.any {
            it.q.contains("vor vielen Menschen", ignoreCase = true)
        })
        assertTrue(character.questions.any {
            it.q.contains("Mut", ignoreCase = true) || it.q.contains("Überwindung", ignoreCase = true)
        })
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
    fun `runtime exposes only approved teamwork packs with existing routing and scenario contract`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_20_teamwork_challenge" in it.tags
        }

        assertEquals(canonicalIds, runtime.map { it.id })
        assertTrue(runtime.none { it.id in archivedIds || it.id == mergedMutprobeId })

        assertEquals("hobbys", runtime.single { it.id == "h500_416_escape_room_szenario" }.topic)
        assertEquals("hobbys", runtime.single { it.id == "h500_421_wettbewerb_entweder_oder" }.topic)
        assertEquals("aufwaermen", runtime.single { it.id == "h500_425_gemeinsamer_sieg_prognose" }.topic)
        assertEquals("beziehung", runtime.single { it.id == "h500_411_zusammenhalt_entweder_oder" }.topic)

        runtime.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            assertEquals(if (isScenario) 8 else 6, pack.questions.size)
        }
    }
}
