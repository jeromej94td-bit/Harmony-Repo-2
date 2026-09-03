package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalLegacyContentInventory
import com.example.data.model.QuestionPack

/**
 * Stage 04.3 controls presentation only.
 *
 * Migrated standalone ring/wedding packs remain in their source registries and in the full runtime
 * pack set for compatibility with saved answers, direct ids, Dev Studio and Stage 04.4.
 *
 * Important: `antrag` is not merely a legacy source. It is the live user-facing catalogue entry
 * that ProposalExperienceEntryPolicy routes into the complete "Unser perfekter Antrag" experience.
 * Hiding it makes the finished experience unreachable even though all of its UI, images and
 * animations are still present. Therefore the entry stays visible while the superseded source-only
 * packs remain filtered from normal catalogue/navigation surfaces.
 */
object ProposalLegacyCatalogueVisibility {
    const val PROPOSAL_EXPERIENCE_ENTRY_PACK_ID = "antrag"

    val hiddenPackIds: Set<String> = linkedSetOf(
        "ringe",
        "straeusse",
        "traumhochzeit",
        ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID
    )

    fun isVisible(packId: String): Boolean = packId !in hiddenPackIds

    init {
        val inventoried = ProposalLegacyContentInventory.items.map { it.packId }.toSet()
        require(hiddenPackIds == inventoried - PROPOSAL_EXPERIENCE_ENTRY_PACK_ID) {
            "Stage 04.3 catalogue hide set must match the Stage-04 inventory except the live proposal experience entry."
        }

        val migratedOrSuperseded = ProposalLegacyExperienceMigration.migratedSourcePackIds + PROPOSAL_EXPERIENCE_ENTRY_PACK_ID
        require(hiddenPackIds == migratedOrSuperseded - PROPOSAL_EXPERIENCE_ENTRY_PACK_ID) {
            "Stage 04.3 may hide only migrated source-only content; the live proposal experience entry must remain visible."
        }

        require(isVisible(PROPOSAL_EXPERIENCE_ENTRY_PACK_ID)) {
            "The perfect-proposal experience entry must stay reachable from the user catalogue."
        }
    }
}

/**
 * User-facing catalogue view. `HarmonyPacksData.PACKS` deliberately stays complete.
 */
val HarmonyPacksData.CATALOG_PACKS: List<QuestionPack>
    get() = PACKS.filter { pack -> ProposalLegacyCatalogueVisibility.isVisible(pack.id) }
