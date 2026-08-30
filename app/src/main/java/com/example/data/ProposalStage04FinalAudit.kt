package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalExperienceRunnerPolicy
import com.example.data.model.ProposalFlowStepKind
import com.example.data.model.ProposalLegacyContentInventory
import com.example.data.model.ProposalLegacyContentReuse
import com.example.data.model.ProposalLegacyContentSource
import com.example.data.model.ProposalRingImageDuels
import com.example.data.model.ProposalRunnerPosition

data class ProposalStage04AuditReport(
    val failures: List<String>,
    val legacyPackCount: Int,
    val bouquetRoundCount: Int,
    val weddingStyleRoundCount: Int,
    val weddingOpenPromptCount: Int,
    val ringAssetReuseCount: Int,
    val proposalJourneyPositionCount: Int
)

/**
 * Stage 04.5 is a non-mutating final regression gate for the proposal/ring/wedding consolidation.
 * It deliberately reuses production models instead of creating another copy of migrated content.
 */
object ProposalStage04FinalAudit {

    fun run(): ProposalStage04AuditReport {
        val failures = mutableListOf<String>()

        val inventoryIds = ProposalLegacyContentInventory.items.map { it.packId }.toSet()
        val migratedOrSupersededIds = ProposalLegacyExperienceMigration.migratedSourcePackIds + "antrag"
        val hiddenIds = ProposalLegacyCatalogueVisibility.hiddenPackIds
        val archivedIds = ProposalLegacyArchiveRegistry.archivedPackIds

        if (inventoryIds.size != 5) failures += "Stage 04 inventory must contain exactly five legacy pack ids."
        if (inventoryIds != migratedOrSupersededIds) failures += "Migration/supersede ids no longer match the Stage 04 inventory."
        if (inventoryIds != hiddenIds) failures += "Catalogue hide ids no longer match the Stage 04 inventory."
        if (inventoryIds != archivedIds) failures += "Archive ids no longer match the Stage 04 inventory."

        val bouquetSource = HarmonyPacksData.DEFAULT_PACKS.singleOrNull { it.id == "straeusse" }
        val bouquetMigrated = ProposalLegacyExperienceMigration.bouquetPreferences
        if (bouquetSource == null) {
            failures += "Bouquet legacy source 'straeusse' is no longer resolvable."
        } else if (bouquetSource.pairs != bouquetMigrated.map { it.round.firstChoice to it.round.secondChoice }) {
            failures += "Migrated bouquet choices no longer match their source pairs."
        }

        val weddingStyleSource = HarmonyPacksData.DEFAULT_PACKS.singleOrNull { it.id == "traumhochzeit" }
        val weddingStyleMigrated = ProposalLegacyExperienceMigration.weddingStylePreferences
        if (weddingStyleSource == null) {
            failures += "Wedding-style legacy source 'traumhochzeit' is no longer resolvable."
        } else if (weddingStyleSource.pairs != weddingStyleMigrated.map { it.round.firstChoice to it.round.secondChoice }) {
            failures += "Migrated wedding-style choices no longer match their source pairs."
        }

        val weddingOpenSource = GeneratedContentRegistry.PACKS.singleOrNull {
            it.id == ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID
        }
        val weddingOpenMigrated = ProposalLegacyExperienceMigration.weddingOpenPrompts
        if (weddingOpenSource == null) {
            failures += "Generated wedding open-prompt source is no longer resolvable."
        } else if (weddingOpenSource.questions.map { it.q } != weddingOpenMigrated.map { it.round.prompt }) {
            failures += "Migrated wedding open prompts no longer match their curated runtime source."
        }

        val ringReuses = ProposalLegacyExperienceMigration.ringAssetReuses
        val ringSourceLabels = ringReuses.map { it.sourceLabel }.toSet()
        val ringAssetKeys = ringReuses.map { it.assetKey }.toSet()
        val proposalRingAssetKeys = ProposalRingImageDuels.rounds
            .flatMap { round -> listOf(round.firstAssetKey, round.secondAssetKey) }
            .toSet()
        if (ringSourceLabels != ProposalLegacyContentReuse.alreadyReusedRingLabels) {
            failures += "Ring source-label reuse no longer matches the Stage 02 reuse manifest."
        }
        if (ringAssetKeys != proposalRingAssetKeys) {
            failures += "Stage 04 ring migration no longer covers every Proposal ring asset exactly."
        }

        ProposalLegacyArchiveRegistry.entries.forEach { entry ->
            when (entry.source) {
                ProposalLegacyContentSource.DEFAULT -> {
                    if (HarmonyPacksData.DEFAULT_PACKS.none { it.id == entry.packId }) {
                        failures += "Archived DEFAULT source '${entry.packId}' is no longer resolvable."
                    }
                }
                ProposalLegacyContentSource.DEFAULT_WITH_GENERATED_OVERRIDE -> {
                    if (HarmonyPacksData.DEFAULT_PACKS.none { it.id == entry.packId }) {
                        failures += "Archived layered default source '${entry.packId}' is no longer resolvable."
                    }
                    if (GeneratedContentRegistry.PACKS.none { it.id == entry.packId }) {
                        failures += "Archived generated override '${entry.packId}' is no longer resolvable."
                    }
                }
                ProposalLegacyContentSource.GENERATED_360 -> {
                    if (GeneratedContentRegistry.PACKS.none { it.id == entry.packId }) {
                        failures += "Archived GENERATED_360 source '${entry.packId}' is no longer resolvable."
                    }
                }
            }
        }

        val catalogueIds = HarmonyPacksData.CATALOG_PACKS.map { it.id }.toSet()
        val leakedLegacyIds = inventoryIds intersect catalogueIds
        if (leakedLegacyIds.isNotEmpty()) {
            failures += "Archived Stage 04 ids leaked back into the user catalogue: ${leakedLegacyIds.sorted()}"
        }
        if ("essenreden" !in catalogueIds) {
            failures += "Unrelated ordinary pack 'essenreden' disappeared from the user catalogue."
        }

        val journeyPositions = generateSequence(ProposalRunnerPosition(0, 0)) { current ->
            ProposalExperienceRunnerPolicy.next(current)
        }.toList()
        val configuredJourneyCount = ProposalExperienceRunnerPolicy.steps.sumOf { step ->
            ProposalExperienceRunnerPolicy.itemCount(step.id).coerceAtLeast(1)
        }
        if (configuredJourneyCount != 35 || journeyPositions.size != 35) {
            failures += "Proposal Experience journey must remain exactly 35 positions."
        }
        if (journeyPositions.firstOrNull() != ProposalRunnerPosition(0, 0)) {
            failures += "Proposal Experience journey no longer starts at its first configured position."
        }
        if (ProposalExperienceRunnerPolicy.steps.lastOrNull()?.kind != ProposalFlowStepKind.REVEAL) {
            failures += "Proposal Experience no longer ends in the Reveal step."
        }

        return ProposalStage04AuditReport(
            failures = failures,
            legacyPackCount = inventoryIds.size,
            bouquetRoundCount = bouquetMigrated.size,
            weddingStyleRoundCount = weddingStyleMigrated.size,
            weddingOpenPromptCount = weddingOpenMigrated.size,
            ringAssetReuseCount = ringReuses.size,
            proposalJourneyPositionCount = journeyPositions.size
        )
    }
}
