package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `German prompts stay untouched`() {
        val rawPack = GeneratedHarmonyAdrenaline360Section19FantasieWasWaereWenn.PACKS
            .single { it.id == "h500_391_zeitreise_entweder_oder" }

        assertEquals(rawPack, GeneratedHarmony360TextCleanup.apply(rawPack))
    }
}
