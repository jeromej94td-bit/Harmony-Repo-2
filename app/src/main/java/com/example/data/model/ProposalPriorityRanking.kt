package com.example.data.model

/**
 * Deterministic ranking content for Stage 02.5.
 *
 * The existing RankingSlotBoard can render these labels. This package only defines the stable
 * proposal-priority content and its binding to the proposal flow.
 */
data class ProposalPriority(
    val id: String,
    val label: String
) {
    init {
        require(id.isNotBlank()) { "Proposal priorities need a stable id." }
        require(label.isNotBlank()) { "Proposal priorities need a label." }
    }
}

object ProposalPriorityRanking {
    const val STEP_ID = "proposal_priorities"

    val priorities: List<ProposalPriority> = listOf(
        ProposalPriority("priority_emotional_moment", "Ein emotionaler Moment nur für uns"),
        ProposalPriority("priority_personal_story", "Unsere persönliche Geschichte"),
        ProposalPriority("priority_right_place", "Der Ort soll sich vollkommen richtig anfühlen"),
        ProposalPriority("priority_loved_ones", "Unsere wichtigsten Menschen dabei"),
        ProposalPriority("priority_surprise", "Eine echte Überraschung")
    )

    init {
        val rankingStep = ProposalExperienceDefinitions.perfectProposal.steps
            .firstOrNull { it.id == STEP_ID }
        require(rankingStep?.kind == ProposalFlowStepKind.RANKING) {
            "Proposal priorities must attach to the proposal_priorities ranking step."
        }
        require(priorities.map(ProposalPriority::id).distinct().size == priorities.size) {
            "Proposal priority ids must be unique."
        }
    }
}
