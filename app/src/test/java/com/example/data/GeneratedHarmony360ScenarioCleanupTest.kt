package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedHarmony360ScenarioCleanupTest {
    private val genericOptions = listOf(
        "Eigenen Wunsch offen sagen",
        "Partner zuerst verstehen",
        "Gemeinsamen dritten Weg suchen",
        "Kurz Abstand und dann entscheiden"
    )

    private fun pack(id: String, prompt: String): GenPack = GenPack(
        id = id,
        title = "Test",
        cat = "h360_szenario",
        tags = listOf("harmony360", "mechanik_szenario"),
        questions = listOf(
            GenQuestion(prompt, genericOptions),
            GenQuestion("Zweite Frage bleibt gleich", listOf("A", "B", "C", "D"))
        )
    )

    @Test
    fun `morning routine scenario gets a concrete opening`() {
        val cleaned = GeneratedHarmony360ScenarioCleanup.apply(
            pack(
                "h500_126_morgenroutine_szenario",
                "Ihr seid euch bei „Morgenroutine“ null einig. Wie gehst du rein?"
            )
        )

        assertTrue(cleaned.questions.first().q.contains("morgen zur gleichen Zeit los"))
        assertEquals(
            listOf(
                "10 Minuten Ruhe, dann gemeinsam planen",
                "Aufgaben am Vorabend verteilen",
                "Jeder macht seine Routine, Treffpunkt beim Frühstück",
                "Eine Woche lang zwei Varianten testen"
            ),
            cleaned.questions.first().options
        )
        assertEquals("Zweite Frage bleibt gleich", cleaned.questions[1].q)
    }

    @Test
    fun `sports and books no longer reuse the generic starter quartet`() {
        val sports = GeneratedHarmony360ScenarioCleanup.apply(
            pack(
                "h500_236_sportliche_ziele_szenario",
                "Ihr merkt bei „Sportliche Ziele“, dass ihr komplett unterschiedliche Vorstellungen habt. Was machst du zuerst?"
            )
        )
        val books = GeneratedHarmony360ScenarioCleanup.apply(
            pack(
                "h500_296_buecher_szenario",
                "Ihr merkt bei „Bücher“, dass ihr komplett unterschiedliche Vorstellungen habt. Was machst du zuerst?"
            )
        )

        assertTrue(sports.questions.first().q.contains("Halbmarathon"))
        assertTrue(books.questions.first().q.contains("gemeinsam lesen"))
        assertNotEquals(genericOptions, sports.questions.first().options)
        assertNotEquals(genericOptions, books.questions.first().options)
    }

    @Test
    fun `unrelated scenario pack stays unchanged`() {
        val original = pack("other_pack", "Andere Frage")

        assertEquals(original, GeneratedHarmony360ScenarioCleanup.apply(original))
    }
}
