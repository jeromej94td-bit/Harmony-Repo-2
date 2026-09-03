package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalLegacyContentInventory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalLegacyCatalogueVisibilityTest {

    @Test
    fun `stage 04 inventory except live proposal entry is the standalone catalogue hide set`() {
        assertEquals(
            ProposalLegacyContentInventory.items.map { it.packId }.toSet() -
                ProposalLegacyCatalogueVisibility.PROPOSAL_EXPERIENCE_ENTRY_PACK_ID,
            ProposalLegacyCatalogueVisibility.hiddenPackIds
        )
    }

    @Test
    fun `catalogue keeps perfect proposal entry visible while hiding migrated source-only packs`() {
        val catalogueIds = HarmonyPacksData.CATALOG_PACKS.map { it.id }.toSet()

        assertTrue(ProposalLegacyCatalogueVisibility.PROPOSAL_EXPERIENCE_ENTRY_PACK_ID in catalogueIds)
        assertTrue(ProposalLegacyCatalogueVisibility.hiddenPackIds.none { it in catalogueIds })

        val defaultIds = HarmonyPacksData.DEFAULT_PACKS.map { it.id }.toSet()
        assertTrue("antrag" in defaultIds)
        assertTrue("ringe" in defaultIds)
        assertTrue("straeusse" in defaultIds)
        assertTrue("traumhochzeit" in defaultIds)
        assertTrue(
            GeneratedContentRegistry.PACKS.any {
                it.id == ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID
            }
        )
    }

    @Test
    fun `ordinary runtime packs and perfect proposal entry remain available in the catalogue`() {
        assertTrue(HarmonyPacksData.CATALOG_PACKS.any { it.id == "essenreden" })
        assertTrue(ProposalLegacyCatalogueVisibility.isVisible("antrag"))
        assertFalse(ProposalLegacyCatalogueVisibility.isVisible("ringe"))
        assertTrue(ProposalLegacyCatalogueVisibility.isVisible("essenreden"))
    }
}
