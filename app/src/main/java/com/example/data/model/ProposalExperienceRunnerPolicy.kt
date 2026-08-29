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
 * Pure deterministic navigation for the Stage-02 proposal experience.
 * UI state stays in the Compose runner; this policy is the single source for step order,
 * subround counts and progress so the legacy `antrag` pack can open one coherent experience.
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

    fun next(position: ProposalRunnerPosition): ProposalRunnerPosition? {
        val step = steps.getOrNull(position.stepIndex) ?: return null
        val count = itemCount(step.id).coerceAtLeast(1)
        if (position.itemIndex + 1 < count) {
            return position.copy(itemIndex = position.itemIndex + 1)
        }
        if (position.stepIndex + 1 >= steps.size) return null
        return ProposalRunnerPosition(stepIndex = position.stepIndex + 1, itemIndex = 0)
    }

    fun progress(position: ProposalRunnerPosition): Float {
        if (steps.isEmpty()) return 0f
        if (position.stepIndex >= steps.lastIndex) return 1f

        val totalItems = steps.sumOf { itemCount(it.id).coerceAtLeast(1) }
        if (totalItems <= 1) return 1f

        val completedBefore = steps.take(position.stepIndex)
            .sumOf { itemCount(it.id).coerceAtLeast(1) }
        val currentCount = itemCount(steps[position.stepIndex].id).coerceAtLeast(1)
        val currentItem = position.itemIndex.coerceIn(0, currentCount - 1)
        return ((completedBefore + currentItem).toFloat() / (totalItems - 1).toFloat())
            .coerceIn(0f, 1f)
    }
}
