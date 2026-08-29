package com.example.data.model

/**
 * Compatibility bridge for the two proposal IMAGE_DUEL steps.
 * Proposal content stays proposal-owned while the reusable engine consumes generic rounds.
 */
object ProposalImageDuelAdapter {
    val byStepId: Map<String, List<ExperienceImageDuelRound>> = linkedMapOf(
        "proposal_location" to ProposalLocationDuels.rounds.map(ProposalImageDuelRound::toExperienceImageDuelRound),
        ProposalRingImageDuels.STEP_ID to ProposalRingImageDuels.rounds.map(ProposalRingImageDuel::toExperienceImageDuelRound)
    )

    init {
        val genericImageDuelStepIds = ProposalExperienceAdapter.definition.steps
            .filter { it.kind == ExperienceStepKind.IMAGE_DUEL }
            .map(ExperienceStep::id)
            .toSet()
        require(byStepId.keys == genericImageDuelStepIds) {
            "Proposal image-duel adapter must cover exactly the proposal image-duel steps."
        }

        byStepId.forEach { (_, rounds) ->
            require(rounds.isNotEmpty()) { "Proposal image-duel steps need at least one round." }
            require(rounds.map(ExperienceImageDuelRound::id).distinct().size == rounds.size) {
                "Proposal image-duel round ids must stay unique within each step."
            }
        }
    }

    fun roundsFor(stepId: String): List<ExperienceImageDuelRound> = byStepId[stepId].orEmpty()
}
