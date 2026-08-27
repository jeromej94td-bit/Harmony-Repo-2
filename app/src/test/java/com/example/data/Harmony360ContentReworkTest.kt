package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360ContentReworkTest {

    @Test
    fun arbeitswegRankingGetsCommuteSpecificQuestionsAndOptions() {
        val raw = GeneratedHarmonyAdrenaline360Section10ArbeitKarriere.PACKS
            .first { it.id == "h500_224_arbeitsweg_ranking" }

        val polished = Harmony360ContentRework.apply(raw)

        assertEquals(8, polished.questions.size)
        assertEquals(
            listOf("Kurze Fahrzeit", "Wenig Umstiege", "Geringe Kosten", "Bequemer Weg"),
            polished.questions.first().options
        )
        assertEquals("Was ist dir auf dem Arbeitsweg am wichtigsten?", polished.questions.first().q)
        assertFalse(polished.questions.first().q.contains("Sicherheit, Freiheit, Abenteuer, Komfort"))
    }

    @Test
    fun overusedGenericRankingOptionsAreReplacedAcrossHarmony360() {
        val raw = GenPack(
            id = "h500_test_generic_ranking",
            title = "Testthema – Zwei Perspektiven",
            cat = "h360_ranking",
            topic = "beziehung",
            type = "quiz",
            tags = listOf("harmony360", "h360_section_10_arbeit_karriere", "mechanik_ranking"),
            questions = listOf(
                GenQuestion(
                    q = "Deine persönliche Rangliste für „Testthema“: Sicherheit, Freiheit, Abenteuer, Komfort",
                    options = listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort")
                )
            )
        )

        val polished = Harmony360ContentRework.apply(raw)
        val question = polished.questions.single()

        assertNotEquals(raw.questions.single().options, question.options)
        assertTrue(question.options.all { it !in listOf("Abenteuer", "Komfort") })
        assertFalse(question.q.contains(question.options.joinToString(", ")))
    }

    @Test
    fun genericPartnerForecastFillerIsContextualizedToo() {
        val raw = GenPack(
            id = "h500_test_forecast",
            title = "Berufliche Veränderung – Herz oder Kopf",
            cat = "h360_prognose",
            topic = "beziehung",
            type = "quiz",
            tags = listOf("harmony360", "h360_section_10_arbeit_karriere", "mechanik_prognose"),
            questions = listOf(
                GenQuestion(
                    q = "Was glaubst du: Welche Seite ist deinem Partner wichtiger?",
                    options = listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer")
                )
            )
        )

        val polished = Harmony360ContentRework.apply(raw)
        val question = polished.questions.single()

        assertNotEquals(raw.questions.single().options, question.options)
        assertTrue(question.q.contains("deinem Partner"))
        assertTrue(question.q.contains("Berufliche Veränderung"))
    }

    @Test
    fun alreadySpecificRankingContentIsPreserved() {
        val raw = GenPack(
            id = "specific",
            title = "Lottogewinn – Zwei Perspektiven",
            cat = "h360_ranking",
            topic = "beziehung",
            type = "quiz",
            tags = listOf("harmony360", "h360_section_19_fantasie_was_waere_wenn", "mechanik_ranking"),
            questions = listOf(
                GenQuestion(
                    q = "Was würdest du zuerst tun?",
                    options = listOf("Traumhaus bauen", "Weltreise starten", "Spenden/Investieren", "Job sofort kündigen")
                )
            )
        )

        assertEquals(raw, Harmony360ContentRework.apply(raw))
    }
}
