package com.example.data.model

/**
 * Stage 02.10 audit outcome for proposal/ring/wedding content that already ships in Harmony.
 *
 * This is deliberately a reuse manifest, not another content pack. It records what the perfect
 * proposal experience already reuses, what its richer Stage-02 mechanics supersede, and what must
 * stay outside the proposal flow until the later Stage-04 consolidation.
 */
enum class ProposalLegacyReuseDecision {
    ALREADY_REUSED,
    SUPERSEDED_BY_STAGE_02,
    DEFER_TO_STAGE_04
}

data class ProposalLegacySourceDecision(
    val packId: String,
    val decision: ProposalLegacyReuseDecision,
    val note: String
) {
    init {
        require(packId.isNotBlank()) { "Legacy proposal source decisions need a pack id." }
        require(note.isNotBlank()) { "Legacy proposal source decisions need a rationale." }
    }
}

object ProposalLegacyContentReuse {
    /**
     * The ten strongest ring concepts already reused by Stage 02.4 through the refreshed ring
     * duels. These labels intentionally point back to the existing `ringe` pack rather than
     * creating another copy of that content.
     */
    val alreadyReusedRingLabels: Set<String> = linkedSetOf(
        "Klassisch Solitär",
        "Vintage verspielt",
        "Modern geometrisch",
        "Vintage Art déco",
        "Schmal & zart",
        "Markant & breit",
        "Moderner Solitär",
        "Großer Stein",
        "Ohne Stein",
        "Diamanten im Band"
    )

    /**
     * Audit decision for the four shipped legacy sources relevant to proposal/ring/wedding.
     * No source pack is removed or hidden here.
     */
    val sourceDecisions: List<ProposalLegacySourceDecision> = listOf(
        ProposalLegacySourceDecision(
            packId = "ringe",
            decision = ProposalLegacyReuseDecision.ALREADY_REUSED,
            note = "Stage 02.4 already reuses the strongest ring concepts in the refreshed ring duels."
        ),
        ProposalLegacySourceDecision(
            packId = "antrag",
            decision = ProposalLegacyReuseDecision.SUPERSEDED_BY_STAGE_02,
            note = "Its two broad environment/privacy questions are covered more precisely by proposal mood/details and location duels."
        ),
        ProposalLegacySourceDecision(
            packId = "straeusse",
            decision = ProposalLegacyReuseDecision.DEFER_TO_STAGE_04,
            note = "Bouquet preferences belong to wedding-content consolidation, not the proposal experience."
        ),
        ProposalLegacySourceDecision(
            packId = "traumhochzeit",
            decision = ProposalLegacyReuseDecision.DEFER_TO_STAGE_04,
            note = "Wedding-day preferences are intentionally kept separate until Stage 04 consolidation."
        )
    )

    /**
     * Stage 02.10 deliberately adds no extra rounds. The audit found no legacy question that adds
     * enough value beyond 02.2–02.9 to justify duplicating it in the perfect-proposal flow.
     */
    val additionalFlowItems: List<String> = emptyList()

    init {
        require(sourceDecisions.map(ProposalLegacySourceDecision::packId).distinct().size == sourceDecisions.size) {
            "Legacy proposal source packs must be audited exactly once."
        }
        require(alreadyReusedRingLabels.size == ProposalRingImageDuels.rounds.size * 2) {
            "The Stage-02 ring reuse manifest must cover the ten refreshed ring concepts."
        }
    }
}
