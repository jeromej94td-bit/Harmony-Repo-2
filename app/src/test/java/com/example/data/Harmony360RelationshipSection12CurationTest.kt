package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360RelationshipSection12CurationTest {

    @Test
    fun `section 12 has an explicit decision for every visible pack`() {
        val visibleIds = GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS.map { it.id }.toSet()

        assertEquals(18, visibleIds.size)
        assertEquals(visibleIds, Harmony360RelationshipSection12Curation.decisions.keys)
        assertEquals(
            setOf("h500_259_gespraechsthemen_prioritaet"),
            Harmony360RelationshipSection12Curation.archivedIds
        )
    }

    @Test
    fun `generic conversation topics pack is removed but real conflict topics remain`() {
        val curated = Harmony360RelationshipSection12Curation.apply(
            GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS
        )
        val ids = curated.map { it.id }

        assertEquals(17, curated.size)
        assertFalse("h500_259_gespraechsthemen_prioritaet" in ids)
        assertTrue("h500_251_streitkultur_entweder_oder" in ids)
        assertTrue("h500_256_missverstaendnisse_szenario" in ids)
        assertTrue("h500_270_versoehnung_offene_runde" in ids)
    }

    @Test
    fun `conflict packs use concrete repair behavior instead of generic templates`() {
        val curated = Harmony360RelationshipSection12Curation.apply(
            GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS
        ).associateBy { it.id }

        val conflict = curated.getValue("h500_251_streitkultur_entweder_oder")
        assertTrue(conflict.questions.any { "Pause" in it.options || "Direkt klären" in it.options })

        val apology = curated.getValue("h500_252_entschuldigung_wer_eher")
        assertTrue(apology.questions.any { "Entschuldigung" in it.q || "sorry" in it.q.lowercase() })

        val silence = curated.getValue("h500_253_schweigen_skala")
        assertTrue(silence.questions.any { "Pause" in it.q || "Strafe" in it.q })

        val compromise = curated.getValue("h500_254_kompromisse_ranking")
        assertTrue(compromise.questions.flatMap { it.options }.any { "Grenze" in it || "Fairness" in it })

        val feedback = curated.getValue("h500_255_feedback_prognose")
        assertTrue(feedback.questions.any { "Partner" in it.q && ("Feedback" in it.q || "Kritik" in it.q) })

        val misunderstanding = curated.getValue("h500_256_missverstaendnisse_szenario")
        assertTrue(misunderstanding.questions.any { "Nachricht" in it.q || "Ton" in it.q || "missverstanden" in it.q.lowercase() })

        val humor = curated.getValue("h500_267_humor_im_streit_geheime_wahl")
        assertTrue(humor.questions.any { "Humor" in it.q && ("hilft" in it.q || "falsch" in it.q) })

        val repair = curated.getValue("h500_270_versoehnung_offene_runde")
        assertTrue(repair.questions.any { "wieder gut" in it.q.lowercase() || "Versöhnung" in it.q })
    }

    @Test
    fun `curated section 12 contains no accidental english ranking template`() {
        val curated = Harmony360RelationshipSection12Curation.apply(
            GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS
        )

        assertFalse(
            curated.flatMap { it.questions }
                .any { it.q.contains("What decides whether", ignoreCase = true) }
        )
    }
}
