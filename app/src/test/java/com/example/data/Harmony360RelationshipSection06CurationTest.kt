package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360RelationshipSection06CurationTest {

    @Test
    fun `section 06 has explicit decisions for all visible packs`() {
        val visibleIds = GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS.map { it.id }.toSet()
        assertEquals(18, visibleIds.size)
        assertEquals(visibleIds, Harmony360RelationshipSection06Curation.decisions.keys)
        assertEquals(
            setOf("h500_148_wohnzimmer_memory", "h500_149_balkon_prioritaet"),
            Harmony360RelationshipSection06Curation.archivedIds
        )
    }

    @Test
    fun `everyday curation removes narrow filler packs and keeps canonical home themes`() {
        val curated = Harmony360RelationshipSection06Curation.apply(
            GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS
        )
        val ids = curated.map { it.id }
        assertEquals(16, curated.size)
        assertFalse("h500_148_wohnzimmer_memory" in ids)
        assertFalse("h500_149_balkon_prioritaet" in ids)
        assertTrue("h500_128_haushalt_memory" in ids)
        assertTrue("h500_150_unser_gemuetlichster_abend_offene_runde" in ids)
    }

    @Test
    fun `everyday rewrites cover real couple friction and routines`() {
        val curated = Harmony360RelationshipSection06Curation.apply(
            GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS
        ).associateBy { it.id }

        assertTrue(curated.getValue("h500_128_haushalt_memory").questions.any { "Mental Load" in it.q || "bemerkt" in it.q })
        assertTrue(curated.getValue("h500_129_ordnung_prioritaet").questions.any { "Sauberkeit" in it.q || "Unordnung" in it.q })
        assertTrue(curated.getValue("h500_132_einkaufen_wer_eher").questions.any { "Einkaufsliste" in it.q || "Budget" in it.q })
        assertTrue(curated.getValue("h500_133_kochen_im_alltag_skala").questions.any { "Essensplanung" in it.q || "Abwasch" in it.q })
        val sleepOptions = curated.getValue("h500_134_schlafen_ranking").questions.flatMap { it.options }
        assertTrue("Nähe beim Einschlafen" in sleepOptions)
        assertTrue("Mehr Freiraum im Bett" in sleepOptions)
        assertTrue(curated.getValue("h500_144_technik_zuhause_ranking").questions.any { "Handy" in it.q || "Bildschirm" in it.q })
        assertTrue(curated.getValue("h500_141_sonntage_entweder_oder").questions.any { "Sonntag" in it.q && it.options.size == 2 })
        assertTrue(curated.getValue("h500_150_unser_gemuetlichster_abend_offene_runde").questions.any { "gemütlichen Abend" in it.q || "Abend zu Hause" in it.q })
    }

    @Test
    fun `stage 05 1 pipeline applies section 06 after existing relationship curation`() {
        val raw = GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS
        val curated = Harmony360RelationshipStage051Pipeline.apply(raw)
        assertEquals(17, curated.size)
        assertFalse(curated.any { it.id == "h500_148_wohnzimmer_memory" })
    }
}
