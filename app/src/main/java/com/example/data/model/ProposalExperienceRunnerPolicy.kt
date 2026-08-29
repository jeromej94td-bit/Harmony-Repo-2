package com.example.data.model

object ProposalExperienceEntryPolicy {
    const val LEGACY_PACK_ID = "antrag"

    fun handlesPack(packId: String): Boolean = packId == LEGACY_PACK_ID
}

data class ProposalRunnerPosition(
    val stepIndex: Int,
    val itemIndex: Int
)

/**
 * Deterministic navigation for the Stage-02 proposal experience.
 *
 * Proposal-specific content still owns the item counts. Navigation/progress delegate to the
 * reusable Stage-03 experience core through [ProposalExperienceAdapter] so existing callers keep
 * the same public API and behavior.
 */
object ProposalExperienceRunnerPolicy {
    val steps: List<ProposalFlowStep> = ProposalExperienceDefinitions.perfectProposal.steps

    fun itemCount(stepId: String): Int = when (stepId) {
        "proposal_mood", "proposal_details" -> ProposalEitherOrRounds.roundsFor(stepId).size
        "proposal_location" -> ProposalLocationDuels.rounds.size
        ProposalRingImageDuels.STEP_ID -> ProposalRingImageDuels.rounds.size
        ProposalPriorityRanking.STEP_ID -> 1
        ProposalPartnerPrediction.STEP_ID -> ProposalPartnerPrediction.rounds.size
        ProposalScenarios.STEP_ID -> ProposalScenarios.rounds.size
        ProposalOpenPrompts.STEP_ID -> ProposalOpenPrompts.prompts.size
        ProposalReveal.STEP_ID -> 1
        else -> 0
    }

    fun next(position: ProposalRunnerPosition): ProposalRunnerPosition? =
        ProposalExperienceAdapter.navigator
            .next(ProposalExperienceAdapter.toGenericPosition(position))
            ?.let(ProposalExperienceAdapter::toProposalPosition)

    fun previous(position: ProposalRunnerPosition): ProposalRunnerPosition? =
        ProposalExperienceAdapter.navigator
            .previous(ProposalExperienceAdapter.toGenericPosition(position))
            ?.let(ProposalExperienceAdapter::toProposalPosition)

    fun progress(position: ProposalRunnerPosition): Float =
        ProposalExperienceAdapter.navigator.progress(
            ProposalExperienceAdapter.toGenericPosition(position)
        )
}
