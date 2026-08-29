package com.example.data.model

/**
 * Compatibility bridge from the Stage-02 proposal-specific model to the reusable Stage-03 core.
 *
 * Proposal content, item counts and rendering remain proposal-owned. This adapter maps only the
 * stable flow definition and positions so existing callers can keep their public API unchanged.
 */
object ProposalExperienceAdapter {
    private fun ProposalFlowStepKind.toExperienceKind(): ExperienceStepKind = when (this) {
        ProposalFlowStepKind.EITHER_OR -> ExperienceStepKind.EITHER_OR
        ProposalFlowStepKind.IMAGE_DUEL -> ExperienceStepKind.IMAGE_DUEL
        ProposalFlowStepKind.RANKING -> ExperienceStepKind.RANKING
        ProposalFlowStepKind.PARTNER_PREDICTION -> ExperienceStepKind.PARTNER_PREDICTION
        ProposalFlowStepKind.SCENARIO -> ExperienceStepKind.SCENARIO
        ProposalFlowStepKind.OPEN_PROMPT -> ExperienceStepKind.OPEN_PROMPT
        ProposalFlowStepKind.REVEAL -> ExperienceStepKind.REVEAL
    }

    val definition: ExperienceDefinition = ProposalExperienceDefinitions.perfectProposal.let { proposal ->
        ExperienceDefinition(
            id = proposal.id,
            title = proposal.title,
            steps = proposal.steps.map { step ->
                ExperienceStep(
                    id = step.id,
                    kind = step.kind.toExperienceKind()
                )
            }
        )
    }

    val navigator: ExperienceNavigator by lazy {
        ExperienceNavigator(
            definition = definition,
            itemCountResolver = ProposalExperienceRunnerPolicy::itemCount
        )
    }

    fun toGenericPosition(position: ProposalRunnerPosition): ExperiencePosition =
        ExperiencePosition(
            stepIndex = position.stepIndex,
            itemIndex = position.itemIndex
        )

    fun toProposalPosition(position: ExperiencePosition): ProposalRunnerPosition =
        ProposalRunnerPosition(
            stepIndex = position.stepIndex,
            itemIndex = position.itemIndex
        )
}
