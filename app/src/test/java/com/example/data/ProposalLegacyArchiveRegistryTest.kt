package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalLegacyContentInventory
import com.example.data.model.ProposalLegacyContentSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalLegacyArchiveRegistryTest {

    @Test
    fun `archive registry covers exactly the Stage 04 hidden legacy ids`() {
        val archivedIds = ProposalLegacyArchiveRegistry.entries.map { it.packId }.toSet()
        val inventoriedIds = ProposalLegacyContentInventory.items.map { it.packId }.toSet()

        assertEquals(ProposalLegacyCatalogueVisibility.hiddenPackIds, archivedIds)
        assertEquals(inventoriedIds, archivedIds)
        assertEquals(5, ProposalLegacyArchiveRegistry.entries.size)
    }

    @Test
    fun `every archive entry keeps source metadata and a concrete reason`() {
        ProposalLegacyArchiveRegistry.entries.forEach { entry ->
            val inventory = ProposalLegacyContentInventory.items.single { it.packId == entry.packId }
            assertEquals(inventory.title, entry.title)
            assertEquals(inventory.area, entry.area)
            assertEquals(inventory.source, entry.source)
            assertTrue(entry.reason.isNotBlank())
            assertTrue(entry.replacement.isNotBlank())
        }
    }

    @Test
    fun `archived legacy sources remain resolvable by their original source type`() {
        ProposalLegacyArchiveRegistry.entries.forEach { entry ->
            when (entry.source) {
                ProposalLegacyContentSource.DEFAULT -> {
                    assertNotNull(HarmonyPacksData.DEFAULT_PACKS.singleOrNull { it.id == entry.packId })
                }
                ProposalLegacyContentSource.DEFAULT_WITH_GENERATED_OVERRIDE -> {
                    assertNotNull(HarmonyPacksData.DEFAULT_PACKS.singleOrNull { it.id == entry.packId })
                    assertNotNull(GeneratedContentRegistry.PACKS.singleOrNull { it.id == entry.packId })
                }
                ProposalLegacyContentSource.GENERATED_360 -> {
                    assertNotNull(GeneratedContentRegistry.PACKS.singleOrNull { it.id == entry.packId })
                }
            }
        }
    }

    @Test
    fun `archived ids stay out of user catalogue while ordinary packs stay visible`() {
        val visibleIds = HarmonyPacksData.CATALOG_PACKS.map { it.id }.toSet()
        assertTrue(ProposalLegacyArchiveRegistry.entries.none { it.packId in visibleIds })
        assertFalse("essenreden" in ProposalLegacyArchiveRegistry.archivedPackIds)
        assertTrue("essenreden" in visibleIds)
    }
}
