package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalLegacyContentInventoryTest {

    @Test
    fun `every shipped standalone proposal ring or wedding pack is inventoried exactly once`() {
        val defaultCandidates = (HarmonyPacksData.DEFAULT_PACKS + HarmonyExpansionPacks.PACKS)
            .filter { pack ->
                ProposalLegacyContentInventory.isStandaloneCandidate(
                    id = pack.id,
                    title = pack.title,
                    tags = pack.tags
                )
            }
            .map { it.id }

        val generatedCandidates = GeneratedContentRegistry.PACKS
            .filter { pack ->
                ProposalLegacyContentInventory.isStandaloneCandidate(
                    id = pack.id,
                    title = pack.title,
                    tags = pack.tags
                )
            }
            .map { it.id }

        val shippedCandidates = (defaultCandidates + generatedCandidates).toSet()
        val inventoried = ProposalLegacyContentInventory.items.map { it.packId }

        assertEquals(inventoried.size, inventoried.distinct().size)
        assertEquals(shippedCandidates, inventoried.toSet())
    }

    @Test
    fun `stage 02 reuse decisions remain aligned with the stage 04 inventory`() {
        val reuseById = ProposalLegacyContentReuse.sourceDecisions.associateBy { it.packId }

        reuseById.forEach { (packId, decision) ->
            val item = ProposalLegacyContentInventory.items.single { it.packId == packId }
            assertEquals(decision.decision, item.stage02Decision)
        }
    }

    @Test
    fun `known legacy and harmony 360 wedding sources are explicitly classified`() {
        val byId = ProposalLegacyContentInventory.items.associateBy { it.packId }

        assertEquals(ProposalLegacyContentArea.PROPOSAL, byId.getValue("antrag").area)
        assertEquals(ProposalLegacyContentArea.RINGS, byId.getValue("ringe").area)
        assertEquals(ProposalLegacyContentArea.WEDDING, byId.getValue("straeusse").area)
        assertEquals(ProposalLegacyContentArea.WEDDING, byId.getValue("traumhochzeit").area)
        assertEquals(ProposalLegacyContentSource.GENERATED_360, byId.getValue("h500_060_hochzeit_offene_runde").source)
    }

    @Test
    fun `every inventory item has a concrete stage 04 plan and rationale`() {
        ProposalLegacyContentInventory.items.forEach { item ->
            assertTrue("Missing rationale for ${item.packId}", item.rationale.isNotBlank())
            assertTrue("Missing next step for ${item.packId}", item.nextStep.isNotBlank())
        }
    }
}
