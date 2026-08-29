package com.example.data.model

/**
 * Stable, UI-independent contract for the proposal experience.
 *
 * Content and rendering deliberately stay outside this model. Later work packages can attach
 * their mechanics to the stable step ids without changing the intended pacing of the experience.
 */
enum class ProposalFlowStepKind {
    EITHER_OR,
    IMAGE_DUEL,
    RANKING,
    PARTNER_PREDICTION,
    SCENARIO,
    OPEN_PROMPT,
    REVEAL
}

data class ProposalFlowStep(
    val id: String,
    val kind: ProposalFlowStepKind
) {
    init {
        require(id.isNotBlank()) { "Proposal flow steps need a stable id." }
    }
}

class ProposalExperienceDefinition(
    val id: String,
    val title: String,
    steps: List<ProposalFlowStep>
) {
    val steps: List<ProposalFlowStep> = steps.toList()

    init {
        require(id.isNotBlank()) { "Proposal experiences need a stable id." }
        require(title.isNotBlank()) { "Proposal experiences need a title." }
        require(this.steps.isNotEmpty()) { "Proposal experiences need at least one step." }
        require(this.steps.map(ProposalFlowStep::id).distinct().size == this.steps.size) {
            "Proposal flow step ids must be unique."
        }
        require(this.steps.last().kind == ProposalFlowStepKind.REVEAL) {
            "A proposal flow must end with its reveal."
        }
        require(this.steps.dropLast(1).none { it.kind == ProposalFlowStepKind.REVEAL }) {
            "A proposal flow can contain its reveal only as the final step."
        }
    }

    fun nextStepAfter(stepId: String): ProposalFlowStep? {
        val index = steps.indexOfFirst { it.id == stepId }
        return if (index < 0) null else steps.getOrNull(index + 1)
    }
}

/**
 * The fixed Stage 02 proposal pacing. This is definition-only: it creates no navigation,
 * rendering, legacy migration, or mechanic implementation.
 */
object ProposalExperienceDefinitions {
    val perfectProposal = ProposalExperienceDefinition(
        id = "perfect_proposal",
        title = "Unser perfekter Antrag",
        steps = listOf(
            ProposalFlowStep("proposal_mood", ProposalFlowStepKind.EITHER_OR),
            ProposalFlowStep("proposal_details", ProposalFlowStepKind.EITHER_OR),
            ProposalFlowStep("proposal_location", ProposalFlowStepKind.IMAGE_DUEL),
            ProposalFlowStep("ring_style", ProposalFlowStepKind.IMAGE_DUEL),
            ProposalFlowStep("proposal_priorities", ProposalFlowStepKind.RANKING),
            ProposalFlowStep("partner_prediction", ProposalFlowStepKind.PARTNER_PREDICTION),
            ProposalFlowStep("proposal_scenarios", ProposalFlowStepKind.SCENARIO),
            ProposalFlowStep("personal_wishes", ProposalFlowStepKind.OPEN_PROMPT),
            ProposalFlowStep("perfect_proposal_reveal", ProposalFlowStepKind.REVEAL)
        )
    )
}
