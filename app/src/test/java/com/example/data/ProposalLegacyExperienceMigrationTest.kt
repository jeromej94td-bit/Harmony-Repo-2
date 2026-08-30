package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalLegacyContentReuse
import com.example.data.model.ProposalRingImageDuels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalLegacyExperienceMigrationTest {

    @Test
    fun `bouquet and wedding style migrations preserve the original legacy choices`() {
        val bouquets = HarmonyPacksData.DEFAULT_PACKS.single { it.id == "straeusse" }
        val weddingStyle = HarmonyPacksData.DEFAULT_PACKS.single { it.id == "traumhochzeit" }

        assertEquals(
            bouquets.pairs,
            ProposalLegacyExperienceMigration.bouquetPreferences.map { migrated ->
                migrated.round.firstChoice to migrated.round.secondChoice
            }
        )
        assertEquals(
            weddingStyle.pairs,
            ProposalLegacyExperienceMigration.weddingStylePreferences.map { migrated ->
                migrated.round.firstChoice to migrated.round.secondChoice
            }
        )
    }

    @Test
    fun `curated wedding open prompts are reused from the generated runtime source`() {
        val source = GeneratedContentRegistry.PACKS.single {
            it.id == ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID
        }

        assertEquals(
            source.questions.map { it.q },
            ProposalLegacyExperienceMigration.weddingOpenPrompts.map { it.round.prompt }
        )
        assertEquals(
            source.questions.indices.toList(),
            ProposalLegacyExperienceMigration.weddingOpenPrompts.map { it.sourceIndex }
        )
    }

    @Test
    fun `ring migration covers exactly the reused source concepts and proposal assets`() {
        val reusedLabels = ProposalLegacyExperienceMigration.ringAssetReuses
            .map { it.sourceLabel }
            .toSet()
        val migratedAssets = ProposalLegacyExperienceMigration.ringAssetReuses
            .map { it.assetKey }
            .toSet()
        val proposalAssets = ProposalRingImageDuels.rounds
            .flatMap { listOf(it.firstAssetKey, it.secondAssetKey) }
            .toSet()

        assertEquals(ProposalLegacyContentReuse.alreadyReusedRingLabels, reusedLabels)
        assertEquals(proposalAssets, migratedAssets)
    }

    @Test
    fun `all migrated content keeps traceable source ids and stable unique round ids`() {
        val eitherOr = ProposalLegacyExperienceMigration.bouquetPreferences +
            ProposalLegacyExperienceMigration.weddingStylePreferences
        val open = ProposalLegacyExperienceMigration.weddingOpenPrompts

        assertTrue(eitherOr.all { it.sourcePackId.isNotBlank() && it.sourceIndex >= 0 })
        assertTrue(open.all { it.sourcePackId.isNotBlank() && it.sourceIndex >= 0 })
        assertEquals(
            eitherOr.size,
            eitherOr.map { it.round.id }.distinct().size
        )
        assertEquals(
            open.size,
            open.map { it.round.id }.distinct().size
        )
    }

    @Test
    fun `superseded proposal quiz is not duplicated into the migration bundle`() {
        assertFalse("antrag" in ProposalLegacyExperienceMigration.migratedSourcePackIds)
        assertTrue("ringe" in ProposalLegacyExperienceMigration.migratedSourcePackIds)
        assertTrue("straeusse" in ProposalLegacyExperienceMigration.migratedSourcePackIds)
        assertTrue("traumhochzeit" in ProposalLegacyExperienceMigration.migratedSourcePackIds)
        assertTrue(
            ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID in
                ProposalLegacyExperienceMigration.migratedSourcePackIds
        )
    }
}
