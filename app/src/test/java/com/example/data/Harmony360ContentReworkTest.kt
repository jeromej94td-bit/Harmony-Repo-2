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
    fun workAndCareerTemplatePacksReceiveSubjectSpecificFirstRounds() {
        val expectedFirstOptions = mapOf(
            "h500_211_arbeitszeiten_entweder_oder" to listOf("Früh starten", "Spät starten", "Gleitzeit", "Vier-Tage-Woche"),
            "h500_214_selbststaendigkeit_ranking" to listOf("Eigene Entscheidungen", "Flexible Zeit", "Finanzielle Chancen", "Sinnvolle Arbeit"),
            "h500_215_berufliche_veraenderung_prognose" to listOf("Besseres Gehalt", "Mehr freie Zeit", "Spannendere Aufgaben", "Sicherer Vertrag"),
            "h500_216_work_life_balance_szenario" to listOf("Termin absagen", "Arbeit bewusst beenden", "Partner kurz anrufen", "Später Zeit nachholen"),
            "h500_217_geheimnis_arbeitsplatz_geheime_wahl" to listOf("Wechselgedanken", "Gehaltswunsch", "Konflikt mit dem Chef", "Bewerbung woanders"),
            "h500_219_berufliche_ziele_prioritaet" to listOf("Mehr Verantwortung", "Mehr Einkommen", "Mehr Freiheit", "Mehr Sinn"),
            "h500_221_nebenjob_entweder_oder" to listOf("Mehr Geld", "Spaßprojekt", "Neue Fähigkeiten", "Später selbstständig"),
            "h500_225_ruhestand_prognose" to listOf("Reisen", "Familie", "Hobbys", "Ruhe genießen"),
            "h500_226_kuendigung_szenario" to listOf("Sofort zuhören", "Finanzen prüfen", "Erst einmal auffangen", "Nächste Schritte planen"),
            "h500_227_kollegen_geheime_wahl" to listOf("Mehr Abstand", "Mehr Freundschaft", "Mehr Teamgefühl", "Klare Grenzen")
        )

        val packs = GeneratedHarmonyAdrenaline360Section10ArbeitKarriere.PACKS.associateBy { it.id }

        expectedFirstOptions.forEach { (id, expected) ->
            val raw = requireNotNull(packs[id])
            val polished = Harmony360ContentRework.apply(raw)
            assertEquals("$id should have eight polished rounds", 8, polished.questions.size)
            assertEquals(id, expected, polished.questions.first().options)
            assertNotEquals(id, raw.questions.first().options, polished.questions.first().options)
        }
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
