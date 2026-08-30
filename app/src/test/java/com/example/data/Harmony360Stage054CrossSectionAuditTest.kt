package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage054CrossSectionAuditTest {
    @Test
    fun `stage 05 4 has 36 raw 360 packs and 17 curated survivors`() {
        val raw = GeneratedHarmonyAdrenaline360Section11GesundheitFitness.PACKS +
            GeneratedHarmonyAdrenaline360Section17PsychologieGefuehle.PACKS
        assertEquals(36, raw.size)
        assertEquals(36, raw.map { it.id }.toSet().size)

        val runtime = Harmony360Stage054CrossSectionAudit.harmony360Packs(GeneratedHarmonyAdrenaline360.PACKS)
        assertEquals(17, runtime.size)
        assertEquals(17, runtime.map { it.id }.toSet().size)
        assertEquals(19, Harmony360Stage054CrossSectionAudit.archivedIds.size)
        assertTrue(Harmony360Stage054CrossSectionAudit.archivedIds.none(runtime.map { it.id }.toSet()::contains))
    }

    @Test
    fun `stage 05 4 intimacy override remains two stable curated packs`() {
        val intimacy = Harmony360Stage054CrossSectionAudit.intimacyPacks(GeneratedContentRegistry.PACKS)
        assertEquals(listOf("naehe", "intimleben"), intimacy.map { it.id })
        assertEquals(listOf(12, 18), intimacy.map { it.questions.size })
        assertTrue(intimacy.all { it.topic == "sex" })
    }

    @Test
    fun `stage 05 4 final output has no known generic or wording regressions`() {
        val violations = Harmony360Stage054CrossSectionAudit.audit(
            harmony360 = GeneratedHarmonyAdrenaline360.PACKS,
            runtimeRegistry = GeneratedContentRegistry.PACKS
        )
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun `stage 05 4 scenario packs keep eight final decisions`() {
        Harmony360Stage054CrossSectionAudit.harmony360Packs(GeneratedHarmonyAdrenaline360.PACKS)
            .filter { it.cat == "h360_szenario" || "mechanik_szenario" in it.tags }
            .forEach { pack -> assertEquals(8, pack.questions.size) }
    }

    @Test
    fun `audit catches a forbidden copy paste quartet`() {
        val broken = GenPack(
            id = "broken_health",
            title = "Broken",
            cat = "tot",
            topic = "kennen",
            tags = listOf("h360_section_11_gesundheit_fitness"),
            questions = listOf(
                GenQuestion("Kaputt?", listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"))
            )
        )
        assertTrue(Harmony360Stage054CrossSectionAudit.audit(listOf(broken), emptyList()).isNotEmpty())
    }
}
