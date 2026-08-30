package com.example.data

import com.example.data.model.ProposalLegacyContentArea
import com.example.data.model.ProposalLegacyContentInventory
import com.example.data.model.ProposalLegacyContentSource

data class ProposalLegacyArchiveEntry(
    val packId: String,
    val title: String,
    val area: ProposalLegacyContentArea,
    val source: ProposalLegacyContentSource,
    val reason: String,
    val replacement: String
) {
    init {
        require(packId.isNotBlank()) { "Archived legacy content needs a stable pack id." }
        require(title.isNotBlank()) { "Archived legacy content needs a title." }
        require(reason.isNotBlank()) { "Archived legacy content needs an explicit archive reason." }
        require(replacement.isNotBlank()) { "Archived legacy content needs a replacement path." }
    }
}

/**
 * Stage 04.4 non-destructive archive manifest.
 *
 * "Archived" means these standalone packs are superseded as normal user-facing catalogue
 * destinations after Stage 04.2 migration and Stage 04.3 catalogue consolidation. Their original
 * definitions intentionally remain in the shipped source registries so historic answer ids,
 * direct ids, Dev Studio tooling and migration traceability keep working.
 */
object ProposalLegacyArchiveRegistry {

    private val archiveMetadata: Map<String, Pair<String, String>> = linkedMapOf(
        "antrag" to (
            "The broad standalone proposal quiz is superseded by the richer Proposal Experience." to
                "Proposal Experience: location, details, scenarios and reveal"
            ),
        "ringe" to (
            "The standalone ring pack duplicates ring concepts already reused as refreshed image duels." to
                "Proposal Experience: refreshed ring image duels"
            ),
        "straeusse" to (
            "Its four bouquet preferences were migrated into reusable Experience Either-Or rounds." to
                "Reusable Experience: migrated bouquet preferences"
            ),
        "traumhochzeit" to (
            "Its four wedding-style preferences were migrated into reusable Experience Either-Or rounds." to
                "Reusable Experience: migrated wedding-style preferences"
            ),
        ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID to (
            "Its curated wedding prompts were migrated into reusable Experience open-prompt rounds." to
                "Reusable Experience: migrated wedding open prompts"
            )
    )

    val entries: List<ProposalLegacyArchiveEntry> = ProposalLegacyContentInventory.items.map { item ->
        val metadata = archiveMetadata[item.packId]
            ?: error("Every Stage 04 inventory item needs explicit archive metadata.")
        ProposalLegacyArchiveEntry(
            packId = item.packId,
            title = item.title,
            area = item.area,
            source = item.source,
            reason = metadata.first,
            replacement = metadata.second
        )
    }

    val archivedPackIds: Set<String> = entries.mapTo(linkedSetOf()) { it.packId }

    fun isArchived(packId: String): Boolean = packId in archivedPackIds

    fun entryFor(packId: String): ProposalLegacyArchiveEntry? = entries.firstOrNull { it.packId == packId }

    init {
        val inventoriedIds = ProposalLegacyContentInventory.items.map { it.packId }.toSet()
        require(archiveMetadata.keys == inventoriedIds) {
            "Stage 04.4 archive metadata must cover exactly the Stage-04 inventory."
        }
        require(archivedPackIds == ProposalLegacyCatalogueVisibility.hiddenPackIds) {
            "Stage 04.4 archive ids must stay aligned with the Stage-04.3 catalogue hide set."
        }
        require(entries.map { it.packId }.distinct().size == entries.size) {
            "Stage 04.4 archive registry must keep every stable pack id exactly once."
        }
    }
}
