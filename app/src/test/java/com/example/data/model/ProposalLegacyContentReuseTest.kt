package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalLegacyContentReuseTest {

    @Test
    fun `audit covers the shipped proposal ring and wedding source packs`() {
        val shippedPackIds = HarmonyPacksData.DEFAULT_PACKS.map(QuestionPack::id).toSet()
        val auditedPackIds = ProposalLegacyContentReuse.sourceDecisions.map(ProposalLegacySourceDecision::packId).toSet()

        assertTrue(setOf("antrag", "ringe", "straeusse", "traumhochzeit").all { it in shippedPackIds })
        assertEquals(setOf("antrag", "ringe", "straeusse", "traumhochzeit"), auditedPackIds)
    }

    @Test
    fun `only strong ring concepts are recorded as already reused without adding duplicate flow rounds`() {
        val decisions = ProposalLegacyContentReuse.sourceDecisions.associateBy(ProposalLegacySourceDecision::packId)

        assertEquals(ProposalLegacyReuseDecision.ALREADY_REUSED, decisions.getValue("ringe").decision)
        assertEquals(ProposalLegacyReuseDecision.SUPERSEDED_BY_STAGE_02, decisions.getValue("antrag").decision)
        assertEquals(ProposalLegacyReuseDecision.DEFER_TO_STAGE_04, decisions.getValue("straeusse").decision)
        assertEquals(ProposalLegacyReuseDecision.DEFER_TO_STAGE_04, decisions.getValue("traumhochzeit").decision)
        assertTrue(ProposalLegacyContentReuse.additionalFlowItems.isEmpty())
    }

    @Test
    fun `recorded ring concepts resolve from the existing shipped ring pack`() {
        val ringPack = HarmonyPacksData.DEFAULT_PACKS.single { it.id == "ringe" }
        val shippedRingLabels = ringPack.pairs.flatMap { pair -> listOf(pair.first, pair.second) }.toSet()

        assertEquals(10, ProposalLegacyContentReuse.alreadyReusedRingLabels.size)
        assertTrue(ProposalLegacyContentReuse.alreadyReusedRingLabels.all { it in shippedRingLabels })
        assertEquals(ProposalRingImageDuels.rounds.size * 2, ProposalLegacyContentReuse.alreadyReusedRingLabels.size)
    }
}
