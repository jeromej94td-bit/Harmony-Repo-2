package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage055FinalAuditTest {
    private val raw = buildList<GenPack> {
        addAll(GeneratedHarmonyAdrenaline360Section13PersoenlichkeitWerte.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section15GlaubeReligion.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section16PolitikGesellschaft.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section18HumorLachen.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section19FantasieWasWaereWenn.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section20TeamworkChallenge.PACKS)
    }

    @Test
    fun `Stage 05_5 covers 108 raw packs with 51 visible 56 archived and one merged source`() {
        assertEquals(108, raw.size)
        assertEquals(108, raw.map { it.id }.toSet().size)
        assertEquals(51, Harmony360Stage055FinalAudit.targetPacks(GeneratedHarmonyAdrenaline360.PACKS).size)
        assertEquals(56, Harmony360Stage055FinalAudit.expectedArchivedIds.size)
        assertEquals(setOf("h500_422_mutprobe_wer_eher"), Harmony360Stage055FinalAudit.expectedMergedIds)
    }

    @Test
    fun `final Stage 05_5 audit is clean`() {
        val violations = Harmony360Stage055FinalAudit.audit(raw, GeneratedHarmonyAdrenaline360.PACKS)
        println("VIOLATIONS:\n" + violations.joinToString("\n"))
        assertEquals(emptyList<String>(), violations)
    }

    @Test
    fun `humor and teamwork decision ledgers are fully represented`() {
        assertEquals(18, Harmony360HumorSectionCuration.decisions.size)
        assertEquals(18, Harmony360TeamworkSectionCuration.decisions.size)
        assertEquals(10, Harmony360HumorSectionCuration.decisions.values.count {
            it == Harmony360HumorSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(12, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.REWRITE
        })
        assertEquals(5, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.ARCHIVE
        })
        assertEquals(1, Harmony360TeamworkSectionCuration.decisions.values.count {
            it == Harmony360TeamworkSectionCuration.CurationDecision.MERGE
        })
    }

    @Test
    fun `audit catches ordinary four option sets copied across three packs`() {
        val fixtureOptions = listOf("Alpha", "Beta", "Gamma", "Delta")
        val fixtureIds = setOf(
            "h500_371_humor_entweder_oder",
            "h500_411_zusammenhalt_entweder_oder",
            "h500_391_zeitreise_entweder_oder"
        )
        val corrupted = GeneratedHarmonyAdrenaline360.PACKS.map { pack ->
            if (pack.id !in fixtureIds) return@map pack
            pack.copy(
                questions = pack.questions.mapIndexed { index, question ->
                    if (index == 0) question.copy(options = fixtureOptions) else question
                }
            )
        }

        val violations = Harmony360Stage055FinalAudit.audit(raw, corrupted)
        assertTrue(violations.any { it.contains("reused 4-option set across 3 packs") })
    }
}
