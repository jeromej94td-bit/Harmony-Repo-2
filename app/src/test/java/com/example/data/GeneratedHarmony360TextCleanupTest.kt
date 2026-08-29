package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedHarmony360TextCleanupTest {

    @Test
    fun `accidental English ranking template is translated before runtime`() {
        val rawPack = GeneratedHarmonyAdrenaline360Section14KulturMedien.PACKS
            .single { it.id == "h500_294_museen_ranking" }

        val cleaned = GeneratedHarmony360TextCleanup.apply(rawPack)
        val question = cleaned.questions.single {
            it.q.contains("Museen") && it.q.contains("besonders")
        }

        assertEquals(
            "Was entscheidet, ob „Museen“ für dich besonders ist? Ordne: Sicherheit, Freiheit, Abenteuer, Komfort",
            question.q
        )
        assertFalse(cleaned.questions.any { it.q.startsWith("What ") })
    }

    @Test
    fun `sleep habits typo is repaired`() {
        val rawPack = GeneratedHarmonyAdrenaline360Section11GesundheitFitness.PACKS
            .single { it.id == "h500_232_schlafgewohnheiten_wer_eher" }

        val cleaned = GeneratedHarmony360TextCleanup.apply(rawPack)

        assertFalse(cleaned.questions.any { "Schlagwewohnheiten" in it.q })
        assertTrue(cleaned.questions.any {
            it.q == "Wer wäre bei „Schlafgewohnheiten“ eher überraschend mutig?"
        })
    }

    @Test
    fun `missing t in German partner prediction is repaired`() {
        val rawPack = GeneratedHarmonyAdrenaline360Section11GesundheitFitness.PACKS
            .single { it.id == "h500_245_suchtmittel_prognose" }

        val cleaned = GeneratedHarmony360TextCleanup.apply(rawPack)

        assertTrue(cleaned.questions.any {
            it.q == "Was glaubst du: Welche Seite von „Suchtmittel“ ist deinem Partner wichtiger?"
        })
        assertFalse(cleaned.questions.any { " is deinem Partner " in it.q })
    }

    @Test
    fun `German prompts stay untouched`() {
        val rawPack = GeneratedHarmonyAdrenaline360Section19FantasieWasWaereWenn.PACKS
            .single { it.id == "h500_391_zeitreise_entweder_oder" }

        assertEquals(rawPack, GeneratedHarmony360TextCleanup.apply(rawPack))
    }
}
