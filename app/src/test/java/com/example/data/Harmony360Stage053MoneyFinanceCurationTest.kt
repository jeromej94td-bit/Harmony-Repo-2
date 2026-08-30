package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage053MoneyFinanceCurationTest {
    private val raw = GeneratedHarmonyAdrenaline360Section09GeldFinanzen.PACKS

    private val expectedIds = listOf(
        "h500_191_gemeinsames_konto_entweder_oder",
        "h500_192_sparen_wer_eher",
        "h500_193_ausgaben_skala",
        "h500_194_investieren_ranking",
        "h500_195_groessere_anschaffungen_prognose",
        "h500_196_finanzielle_unabhaengigkeit_szenario",
        "h500_197_konsumverhalten_geheime_wahl",
        "h500_198_geld_in_der_kindheit_memory",
        "h500_199_finanzplanung_prioritaet",
        "h500_200_geld_und_werte_offene_runde",
        "h500_201_taschengeld_entweder_oder",
        "h500_202_haushaltsbuch_wer_eher",
        "h500_203_notgroschen_skala",
        "h500_204_luxus_ranking",
        "h500_205_spenden_prognose",
        "h500_206_erben_szenario",
        "h500_207_altersvorsorge_geheime_wahl",
        "h500_210_finanzielle_gespraeche_offene_runde"
    )

    @Test
    fun `all 18 money packs have explicit rewrite decisions`() {
        assertEquals(expectedIds, raw.map { it.id })
        assertEquals(expectedIds.toSet(), Harmony360MoneyFinanceSectionCuration.decisions.keys)
        assertTrue(Harmony360MoneyFinanceSectionCuration.decisions.values.all {
            it == Harmony360MoneyFinanceSectionCuration.CurationDecision.REWRITE
        })
    }

    @Test
    fun `curation preserves metadata and gives every money pack six concrete questions`() {
        val curated = Harmony360MoneyFinanceSectionCuration.apply(raw)
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
    fun `money questions are concrete relationship prompts not investment advice`() {
        val curated = Harmony360MoneyFinanceSectionCuration.apply(raw)

        val account = curated.single { it.id == "h500_191_gemeinsames_konto_entweder_oder" }
        assertTrue(account.questions.flatMap { it.options }.contains("Gemeinsame Fixkosten über ein Gemeinschaftskonto"))

        val investing = curated.single { it.id == "h500_194_investieren_ranking" }
        assertTrue(investing.questions.any { it.q.contains("Risikotoleranz", ignoreCase = true) })
        assertTrue(investing.questions.any { it.q.contains("gemeinsam", ignoreCase = true) })

        val childhood = curated.single { it.id == "h500_198_geld_in_der_kindheit_memory" }
        assertTrue(childhood.questions.any { it.q.contains("Kindheit", ignoreCase = true) })

        val inheritance = curated.single { it.id == "h500_206_erben_szenario" }
        assertTrue(inheritance.questions.any { it.q.contains("Erbe", ignoreCase = true) })

        val conversations = curated.single { it.id == "h500_210_finanzielle_gespraeche_offene_runde" }
        assertTrue(conversations.questions.any { it.q.contains("Schulden", ignoreCase = true) })

        val allText = curated.flatMap { pack -> pack.questions.map { it.q + " " + it.options.joinToString(" ") } }
            .joinToString("\n")
            .lowercase()
        listOf("kauf diese aktie", "kaufe diese aktie", "investiere in diesen etf", "garantierte rendite").forEach {
            assertFalse(allText.contains(it))
        }
    }

    @Test
    fun `curated output removes generator quartets and English leftovers`() {
        val curated = Harmony360MoneyFinanceSectionCuration.apply(raw)
        val forbidden = setOf(
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Alles teilen", "Getrennt halten", "Mischmodell", "Sehr unabhängig"),
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
                question.q.contains("Rankt:", ignoreCase = true) ||
                question.q.contains(" is deinem Partner", ignoreCase = true)
        })
    }

    @Test
    fun `runtime registry uses money finance curation without changing section size`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS.filter {
            "h360_section_09_geld_finanzen" in it.tags
        }

        assertEquals(expectedIds, runtime.map { it.id })
        assertTrue(runtime.all { it.questions.size == 6 })
        assertTrue(runtime.single { it.id == "h500_203_notgroschen_skala" }
            .questions.any { it.q.contains("Notgroschen", ignoreCase = true) })
    }
}
