package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage053CrossSectionAuditTest {
    @Test
    fun `stage 05 3 has 72 raw packs and 59 curated survivors`() {
        val raw = listOf(
            GeneratedHarmonyAdrenaline360Section03ZukunftLebensplanung.PACKS,
            GeneratedHarmonyAdrenaline360Section08FreundeFamilie.PACKS,
            GeneratedHarmonyAdrenaline360Section09GeldFinanzen.PACKS,
            GeneratedHarmonyAdrenaline360Section10ArbeitKarriere.PACKS
        ).flatten()

        assertEquals(72, raw.size)
        assertEquals(72, raw.map { it.id }.toSet().size)

        val curated = Harmony360Stage053CrossSectionAudit.stagePacks(GeneratedHarmonyAdrenaline360.PACKS)
        assertEquals(59, curated.size)
        assertEquals(59, curated.map { it.id }.toSet().size)
    }

    @Test
    fun `stage 05 3 archive set remains absent from runtime`() {
        val ids = Harmony360Stage053CrossSectionAudit.stagePacks(GeneratedHarmonyAdrenaline360.PACKS)
            .map { it.id }
            .toSet()

        assertTrue(Harmony360Stage053CrossSectionAudit.archivedIds.none(ids::contains))
        assertEquals(13, Harmony360Stage053CrossSectionAudit.archivedIds.size)
    }

    @Test
    fun `stage 05 3 curated output has no generic copy paste or english residue`() {
        val violations = Harmony360Stage053CrossSectionAudit.audit(GeneratedHarmonyAdrenaline360.PACKS)
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun `family and key work money topic routing stays visible and intentional`() {
        val packs = Harmony360Stage053CrossSectionAudit.stagePacks(GeneratedHarmonyAdrenaline360.PACKS)

        val family = packs.filter { "h360_section_08_freunde_familie" in it.tags }
        assertEquals(18, family.size)
        assertTrue(family.all { it.topic == "familie" })

        assertEquals("familie", packs.single { it.id == "h500_061_familienplanung_entweder_oder" }.topic)
        assertEquals("geld", packs.single { it.id == "h500_058_finanzielle_ziele_memory" }.topic)
        assertEquals("geld", packs.single { it.id == "h500_225_ruhestand_prognose" }.topic)
        assertEquals("beziehung", packs.single { it.id == "h500_220_job_und_beziehung_offene_runde" }.topic)
    }

    @Test
    fun `audit detects a known forbidden quartet`() {
        val broken = GenPack(
            id = "broken_money",
            title = "Broken",
            cat = "tot",
            topic = "geld",
            tags = listOf("h360_section_09_geld_finanzen"),
            questions = listOf(
                GenQuestion("Kaputt?", listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"))
            )
        )
        assertFalse(Harmony360Stage053CrossSectionAudit.audit(listOf(broken)).isEmpty())
    }
}
