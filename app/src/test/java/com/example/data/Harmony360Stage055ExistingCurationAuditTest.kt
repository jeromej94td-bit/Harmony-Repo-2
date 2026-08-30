package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage055ExistingCurationAuditTest {
    private val raw =
        GeneratedHarmonyAdrenaline360Section13PersoenlichkeitWerte.PACKS +
            GeneratedHarmonyAdrenaline360Section15GlaubeReligion.PACKS +
            GeneratedHarmonyAdrenaline360Section16PolitikGesellschaft.PACKS +
            GeneratedHarmonyAdrenaline360Section19FantasieWasWaereWenn.PACKS

    @Test
    fun `stage 05 5 existing sections keep 72 raw packs and 29 intentional survivors`() {
        assertEquals(72, raw.size)
        assertEquals(72, raw.map { it.id }.toSet().size)
        assertEquals(29, Harmony360Stage055ExistingCurationAudit.expectedVisibleIds.size)
        assertEquals(43, raw.map { it.id }.toSet().minus(Harmony360Stage055ExistingCurationAudit.expectedVisibleIds).size)
    }

    @Test
    fun `runtime exposes only the intentional values faith society and fantasy survivors`() {
        val runtime = Harmony360Stage055ExistingCurationAudit.targetPacks(GeneratedHarmonyAdrenaline360.PACKS)
        assertEquals(Harmony360Stage055ExistingCurationAudit.expectedVisibleIds, runtime.map { it.id }.toSet())

        assertEquals(8, runtime.count { "h360_section_13_persoenlichkeit_werte" in it.tags })
        assertEquals(4, runtime.count { "h360_section_15_glaube_religion" in it.tags })
        assertEquals(4, runtime.count { "h360_section_16_politik_gesellschaft" in it.tags })
        assertEquals(13, runtime.count { "h360_section_19_fantasie_was_waere_wenn" in it.tags })
    }

    @Test
    fun `existing curated sections remain free of generator regressions`() {
        val violations = Harmony360Stage055ExistingCurationAudit.audit(raw, GeneratedHarmonyAdrenaline360.PACKS)
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun `key topic routing remains inside the visible taxonomy`() {
        val runtime = GeneratedHarmonyAdrenaline360.PACKS

        assertEquals("moral", runtime.single { it.id == "h500_271_werte_im_alltag_entweder_oder" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_275_staerken_und_schwaechen_prognose" }.topic)
        assertEquals("familie", runtime.single { it.id == "h500_318_religioese_erziehung_memory" }.topic)
        assertEquals("moral", runtime.single { it.id == "h500_340_gerechtigkeit_offene_runde" }.topic)
        assertEquals("geld", runtime.single { it.id == "h500_394_lottogewinn_ranking" }.topic)
        assertEquals("reisen", runtime.single { it.id == "h500_396_einsame_insel_szenario" }.topic)
        assertEquals("aufwaermen", runtime.single { it.id == "h500_399_drei_wuensche_prioritaet" }.topic)
        assertEquals("kennen", runtime.single { it.id == "h500_407_geheime_fantasie_geheime_wahl" }.topic)
        assertEquals(8, runtime.single { it.id == "h500_396_einsame_insel_szenario" }.questions.size)
    }
}
