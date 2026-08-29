package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage052CultureMediaCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section14KulturMedien.PACKS

    @Test
    fun `all 18 culture media packs have an explicit rewrite decision`() {
        assertEquals(18, raw.size)
        assertEquals(raw.map { it.id }.toSet(), Harmony360CultureMediaSectionCuration.decisions.keys)
        assertTrue(Harmony360CultureMediaSectionCuration.decisions.values.all {
            it == Harmony360CultureMediaSectionCuration.CurationDecision.REWRITE
        })
    }

    @Test
    fun `culture media curation keeps ids and replaces generator filler with concrete media choices`() {
        val curated = Harmony360CultureMediaSectionCuration.apply(raw)
        assertEquals(18, curated.size)
        assertEquals(raw.map { it.id }, curated.map { it.id })
        assertTrue(curated.all { it.questions.size == 6 })

        val music = curated.single { it.id == "h500_291_musikgeschmack_entweder_oder" }
        assertTrue(music.questions.flatMap { it.options }.contains("Live-Version"))

        val streaming = curated.single { it.id == "h500_292_streaming_wer_eher" }
        assertTrue(streaming.questions.any { it.q.contains("Serie", ignoreCase = true) })

        val museum = curated.single { it.id == "h500_294_museen_ranking" }
        assertTrue(museum.questions.flatMap { it.options }.contains("Kunstmuseum"))

        val cinema = curated.single { it.id == "h500_297_kino_geheime_wahl" }
        assertTrue(cinema.questions.flatMap { it.options }.contains("Große Leinwand und Sound"))

        val sources = curated.single { it.id == "h500_299_informationsquellen_prioritaet" }
        assertTrue(sources.questions.flatMap { it.options }.contains("Originalquelle"))

        val podcasts = curated.single { it.id == "h500_301_podcasts_entweder_oder" }
        assertTrue(podcasts.questions.flatMap { it.options }.contains("True Crime"))

        val news = curated.single { it.id == "h500_303_nachrichten_skala" }
        assertTrue(news.questions.any { it.q.contains("Quellen", ignoreCase = true) })

        val theater = curated.single { it.id == "h500_304_theater_ranking" }
        assertTrue(theater.questions.flatMap { it.options }.contains("Schauspiel"))
    }

    @Test
    fun `curated culture media output has no known generic quartets english leftovers or raw typo`() {
        val curated = Harmony360CultureMediaSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Aktiv", "Passiv", "Etwas lernen", "Einfach abschalten"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht")
        )
        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
        assertFalse(curated.flatMap { it.questions }.any { question ->
            question.q.startsWith("What ") ||
                question.q.contains(" Rank:", ignoreCase = true) ||
                question.q.contains("givst", ignoreCase = true)
        })
    }

    @Test
    fun `runtime registry includes culture media curation`() {
        val runtimeTheater = GeneratedHarmonyAdrenaline360.PACKS.single { it.id == "h500_304_theater_ranking" }
        assertTrue(runtimeTheater.questions.flatMap { it.options }.contains("Schauspiel"))
    }
}
