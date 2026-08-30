package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage053FriendsFamilyCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section08FreundeFamilie.PACKS

    private val expectedIds = listOf(
        "h500_171_paarabende_entweder_oder",
        "h500_172_spieleabende_wer_eher",
        "h500_173_party_skala",
        "h500_174_familientreffen_ranking",
        "h500_175_schwiegereltern_prognose",
        "h500_176_beste_freunde_szenario",
        "h500_177_alte_freunde_geheime_wahl",
        "h500_178_neue_leute_memory",
        "h500_179_nachbarn_prioritaet",
        "h500_180_konflikte_im_umfeld_offene_runde",
        "h500_181_geburtstage_entweder_oder",
        "h500_182_feiertage_wer_eher",
        "h500_183_traditionen_skala",
        "h500_184_geschenke_ranking",
        "h500_185_kinderbesuch_prognose",
        "h500_186_elternabende_szenario",
        "h500_187_familienurlaub_geheime_wahl",
        "h500_190_gemeinsamer_freundeskreis_offene_runde"
    )

    @Test
    fun `all 18 friends and family packs have explicit rewrite decisions`() {
        assertEquals(expectedIds, raw.map { it.id })
        assertEquals(expectedIds.toSet(), Harmony360FriendsFamilySectionCuration.decisions.keys)
        assertTrue(Harmony360FriendsFamilySectionCuration.decisions.values.all {
            it == Harmony360FriendsFamilySectionCuration.CurationDecision.REWRITE
        })
    }

    @Test
    fun `curation preserves pack metadata and gives every pack six concrete questions`() {
        val curated = Harmony360FriendsFamilySectionCuration.apply(raw)

        assertEquals(expectedIds, curated.map { it.id })
        assertTrue(curated.all { it.questions.size == 6 })

        raw.zip(curated).forEach { (source, result) ->
            assertEquals(source.id, result.id)
            assertEquals(source.title, result.title)
            assertEquals(source.cat, result.cat)
            assertEquals(source.topic, result.topic)
            assertEquals(source.type, result.type)
            assertEquals(source.tags, result.tags)
            assertEquals(source.emoji, result.emoji)
        }
    }

    @Test
    fun `friends and family questions are specific and child related prompts stay conditional`() {
        val curated = Harmony360FriendsFamilySectionCuration.apply(raw)

        val familyMeeting = curated.single { it.id == "h500_174_familientreffen_ranking" }
        assertTrue(familyMeeting.questions.flatMap { it.options }.contains("Zeit für echte Gespräche"))

        val inLaws = curated.single { it.id == "h500_175_schwiegereltern_prognose" }
        assertTrue(inLaws.questions.any { it.q.contains("Grenzen", ignoreCase = true) })

        val birthdays = curated.single { it.id == "h500_181_geburtstage_entweder_oder" }
        assertTrue(birthdays.questions.flatMap { it.options }.contains("Klein und persönlich"))

        val gifts = curated.single { it.id == "h500_184_geschenke_ranking" }
        assertTrue(gifts.questions.flatMap { it.options }.contains("Persönliche Bedeutung"))

        val childVisit = curated.single { it.id == "h500_185_kinderbesuch_prognose" }
        assertTrue(childVisit.questions.all {
            it.q.contains("falls", ignoreCase = true) ||
                it.q.contains("wenn", ignoreCase = true) ||
                it.q.contains("Kinder", ignoreCase = true)
        })

        val parentEvenings = curated.single { it.id == "h500_186_elternabende_szenario" }
        assertTrue(parentEvenings.questions.all {
            it.q.contains("falls", ignoreCase = true) ||
                it.q.contains("wenn", ignoreCase = true) ||
                it.q.contains("Elternabend", ignoreCase = true)
        })
    }

    @Test
    fun `curated output removes generator quartets and English leftovers`() {
        val curated = Harmony360FriendsFamilySectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Große Runde", "Kleine Runde", "Nur wir zwei", "Eher zurückhaltend"),
            listOf("Feste Traditionen", "Neue Wege", "Spontane Aktionen", "Genaue Planung"),
            listOf("Mehr Familie", "Mehr Freunde", "Ausgewogen", "Sehr unabhängig"),
            listOf("Integrieren", "Abgrenzen", "Kompromisse suchen", "Eigene Akzente setzen"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Spontan", "Geplant", "Vertraut", "Etwas völlig Neues"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
            listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung"),
            listOf("Mehr Zeit", "Mehr Aufmerksamkeit", "Mehr Komfort", "Mehr Freiheit")
        )

        assertFalse(curated.flatMap { it.questions }.any { it.options in forbidden })
        assertFalse(curated.flatMap { it.questions }.any { question ->
            question.q.startsWith("What ") ||
                question.q.contains("Rank:", ignoreCase = true) ||
                question.q.contains("Rankt:", ignoreCase = true)
        })
    }

    @Test
    fun `runtime registry uses friends and family curation without changing section size`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_08_freunde_familie" in it.tags
        }

        assertEquals(expectedIds, runtime.map { it.id })
        assertTrue(runtime.all { it.questions.size == 6 })
        assertTrue(runtime.single { it.id == "h500_174_familientreffen_ranking" }
            .questions.flatMap { it.options }.contains("Zeit für echte Gespräche"))
    }
}
