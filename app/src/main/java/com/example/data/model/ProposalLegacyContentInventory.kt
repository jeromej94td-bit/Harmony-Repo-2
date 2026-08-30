package com.example.data.model

enum class ProposalLegacyContentArea {
    PROPOSAL,
    RINGS,
    WEDDING
}

enum class ProposalLegacyContentSource {
    DEFAULT,
    GENERATED_360
}

data class ProposalLegacyContentInventoryItem(
    val packId: String,
    val title: String,
    val area: ProposalLegacyContentArea,
    val source: ProposalLegacyContentSource,
    val stage02Decision: ProposalLegacyReuseDecision? = null,
    val nextStep: String,
    val rationale: String
) {
    init {
        require(packId.isNotBlank()) { "Stage 04 inventory items need a pack id." }
        require(title.isNotBlank()) { "Stage 04 inventory items need a title." }
        require(nextStep.isNotBlank()) { "Stage 04 inventory items need a next step." }
        require(rationale.isNotBlank()) { "Stage 04 inventory items need a rationale." }
    }
}

/**
 * Stage 04.1 inventory for every shipped standalone proposal, ring and wedding pack.
 *
 * This manifest is intentionally non-destructive. It makes duplicate content explicit so
 * Stage 04.2 can migrate strong material into the reusable Experience system before Stage
 * 04.3/04.4 remove duplicate catalogue presentation and archive the old standalone packs.
 */
object ProposalLegacyContentInventory {

    val items: List<ProposalLegacyContentInventoryItem> = listOf(
        ProposalLegacyContentInventoryItem(
            packId = "antrag",
            title = "Der perfekte Heiratsantrag",
            area = ProposalLegacyContentArea.PROPOSAL,
            source = ProposalLegacyContentSource.DEFAULT,
            stage02Decision = ProposalLegacyReuseDecision.SUPERSEDED_BY_STAGE_02,
            nextStep = "Stage 04.3/04.4: remove the duplicate standalone catalogue surface, then archive the legacy pack without deleting its source.",
            rationale = "Its two broad location/privacy questions are already covered more precisely by the Proposal Experience location and detail mechanics."
        ),
        ProposalLegacyContentInventoryItem(
            packId = "ringe",
            title = "Verlobungsringe",
            area = ProposalLegacyContentArea.RINGS,
            source = ProposalLegacyContentSource.DEFAULT,
            stage02Decision = ProposalLegacyReuseDecision.ALREADY_REUSED,
            nextStep = "Stage 04.2: verify all worthwhile ring labels/assets are represented; Stage 04.3/04.4 can then remove and archive the duplicate standalone pack.",
            rationale = "Stage 02.4 already reuses the ten strongest ring concepts in the Proposal Experience ring duels."
        ),
        ProposalLegacyContentInventoryItem(
            packId = "straeusse",
            title = "Hochzeitssträuße",
            area = ProposalLegacyContentArea.WEDDING,
            source = ProposalLegacyContentSource.DEFAULT,
            stage02Decision = ProposalLegacyReuseDecision.DEFER_TO_STAGE_04,
            nextStep = "Stage 04.2: decide which bouquet preferences deserve migration into a reusable wedding/proposal detail mechanic before the standalone pack is archived.",
            rationale = "Bouquet preferences are useful wedding-detail content but currently live as a separate legacy choice pack outside the richer Experience flow."
        ),
        ProposalLegacyContentInventoryItem(
            packId = "traumhochzeit",
            title = "Traumhochzeit",
            area = ProposalLegacyContentArea.WEDDING,
            source = ProposalLegacyContentSource.DEFAULT,
            stage02Decision = ProposalLegacyReuseDecision.DEFER_TO_STAGE_04,
            nextStep = "Stage 04.2: migrate the strongest wedding-day preferences into the Experience system, then remove duplicate catalogue presentation in Stage 04.3/04.4.",
            rationale = "The pack contains relevant wedding-day preference signals that complement the proposal flow but should not remain a second standalone presentation after migration."
        ),
        ProposalLegacyContentInventoryItem(
            packId = "h500_060_hochzeit_offene_runde",
            title = "Hochzeit – Offene Runde",
            area = ProposalLegacyContentArea.WEDDING,
            source = ProposalLegacyContentSource.GENERATED_360,
            nextStep = "Stage 04.2: reuse its curated open wedding prompts in the Experience system and preserve the questions while consolidating duplicate navigation later.",
            rationale = "Harmony-360 still ships this dedicated wedding discussion pack, so consolidating only the four Models.kt legacy packs would leave a fifth standalone wedding path active."
        )
    )

    /**
     * Narrow detector for standalone proposal/ring/wedding packs. It deliberately inspects
     * stable pack metadata rather than question bodies so a generic future/family pack that
     * merely mentions a wedding in one question does not become a false Stage-04 candidate.
     */
    fun isStandaloneCandidate(id: String, title: String, tags: List<String>): Boolean {
        val normalizedId = id.lowercase()
        val normalizedTitle = title.lowercase()
        val normalizedTags = tags.map { it.lowercase() }

        return normalizedId.contains("antrag") ||
            normalizedId.contains("hochzeit") ||
            normalizedId.contains("verlob") ||
            normalizedId == "ringe" ||
            normalizedTitle.contains("heiratsantrag") ||
            normalizedTitle.contains("hochzeit") ||
            normalizedTitle.contains("verlobungsring") ||
            normalizedTags.any { it == "hochzeit" || it == "antrag" || it == "verlobung" }
    }

    init {
        require(items.map(ProposalLegacyContentInventoryItem::packId).distinct().size == items.size) {
            "Stage 04 inventory must list every standalone source exactly once."
        }
    }
}
