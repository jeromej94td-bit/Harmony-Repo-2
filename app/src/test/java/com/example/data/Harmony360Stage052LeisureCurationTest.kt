package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage052LeisureCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section07FreizeitHobbys.PACKS

    @Test
    fun `all 18 leisure packs have an explicit rewrite decision`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360LeisureSectionCuration.decisions.keys)
        assertTrue(Harmony360LeisureSectionCuration.decisions.values.all {
            it == Harmony360LeisureSectionCuration.CurationDecision.REWRITE
        })
    }

    @Test
    fun `leisure curation keeps ids and replaces generator filler with concrete hobby choices`() {
        val curated = Harmony360LeisureSectionCuration.apply(raw)
        assertEquals(18, curated.size)
        assertEquals(raw.map { it.id }, curated.map { it.id })

        val series = curated.single { it.id == "h500_151_serien_und_filme_entweder_oder" }
        assertTrue(series.questions.flatMap { it.options }.contains("Eine Folge Serie"))

        val gaming = curated.single { it.id == "h500_153_gaming_skala" }
        assertTrue(gaming.questions.any { it.q.contains("Koop", ignoreCase = true) })

        val concert = curated.single { it.id == "h500_155_konzerte_prognose" }
        assertTrue(concert.questions.flatMap { it.options }.contains("Stehplatz mitten drin"))

        val hiking = curated.single { it.id == "h500_159_wandern_prioritaet" }
        assertTrue(hiking.questions.flatMap { it.options }.contains("Aussicht"))

        val photography = curated.single { it.id == "h500_161_fotografie_entweder_oder" }
        assertTrue(photography.questions.flatMap { it.options }.contains("Menschen fotografieren"))

        val boardGames = curated.single { it.id == "h500_164_brettspiele_ranking" }
        assertTrue(boardGames.questions.flatMap { it.options }.contains("Kooperativ"))

        val languages = curated.single { it.id == "h500_165_sprachen_lernen_prognose" }
        assertTrue(languages.questions.flatMap { it.options }.contains("Im Alltag sprechen"))

        val instruments = curated.single { it.id == "h500_166_instrumente_szenario" }
        assertTrue(instruments.questions.flatMap { it.options }.contains("Gemeinsam ausprobieren"))
    }

    @Test
    fun `curated leisure output has no known generic quartets or english generator leftovers`() {
        val curated = Harmony360LeisureSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Spontan", "Geplant", "Gemeinsam", "Allein"),
            listOf("Kreativ", "Sportlich", "Kulturell", "Entspannung"),
            listOf("Drinnen", "Draußen", "Mit Freunden", "Nur wir zwei"),
            listOf("Aktiv", "Passiv", "Etwas lernen", "Einfach abschalten"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht")
        )
        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
        assertFalse(curated.flatMap { it.questions }.any { question ->
            question.q.startsWith("What ") ||
                question.q.contains(" would ", ignoreCase = true) ||
                question.q.contains(" sounds like ", ignoreCase = true) ||
                question.q.contains("Rank:", ignoreCase = true)
        })
    }

    @Test
    fun `runtime registry includes leisure curation`() {
        val runtimeBoardGames = GeneratedHarmonyAdrenaline360.PACKS.single { it.id == "h500_164_brettspiele_ranking" }
        assertTrue(runtimeBoardGames.questions.flatMap { it.options }.contains("Kooperativ"))
    }
}
