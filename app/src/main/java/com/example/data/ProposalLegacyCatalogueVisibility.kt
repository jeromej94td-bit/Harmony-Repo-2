package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalLegacyContentInventory
import com.example.data.model.QuestionPack

/**
 * Stage 04.3 controls presentation only.
 *
 * These standalone proposal/ring/wedding packs remain in their source registries and in the full
 * runtime pack set for compatibility with saved answers, direct ids, Dev Studio and Stage 04.4.
 * Normal user-facing catalogue/navigation surfaces filter them after their strong material has
 * been migrated or superseded by the reusable Experience flow.
 */
object ProposalLegacyCatalogueVisibility {
    val hiddenPackIds: Set<String> = linkedSetOf(
        "antrag",
        "ringe",
        "straeusse",
        "traumhochzeit",
        ProposalLegacyExperienceMigration.WEDDING_OPEN_PACK_ID
    )

    fun isVisible(packId: String): Boolean = packId !in hiddenPackIds

    init {
        val inventoried = ProposalLegacyContentInventory.items.map { it.packId }.toSet()
        require(hiddenPackIds == inventoried) {
            "Stage 04.3 catalogue hide set must match the complete Stage-04 inventory."
        }

        val migratedOrSuperseded = ProposalLegacyExperienceMigration.migratedSourcePackIds + "antrag"
        require(hiddenPackIds == migratedOrSuperseded) {
            "Stage 04.3 may hide only content already migrated or superseded by the Experience flow."
        }
    }
}

/**
 * User-facing catalogue view. `HarmonyPacksData.PACKS` deliberately stays complete.
 */
val HarmonyPacksData.CATALOG_PACKS: List<QuestionPack>
    get() = PACKS.filter { pack -> ProposalLegacyCatalogueVisibility.isVisible(pack.id) }
