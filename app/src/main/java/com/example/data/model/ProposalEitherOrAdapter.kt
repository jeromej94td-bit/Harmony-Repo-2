package com.example.data.model

/**
 * Compatibility bridge from the Stage-02 proposal Either-Or content to the reusable Stage-03
 * content model. The shipped proposal copy and ordering remain owned by [ProposalEitherOrRounds].
 */
object ProposalEitherOrAdapter {
    val byStepId: Map<String, EitherOrStepContent> = ProposalEitherOrRounds.byStepId
        .mapValues { (stepId, rounds) ->
            EitherOrStepContent(
                stepId = stepId,
                rounds = rounds.map { round ->
                    EitherOrRound(
                        id = round.id,
                        prompt = round.prompt,
                        firstChoice = round.firstChoice,
                        secondChoice = round.secondChoice
                    )
                }
            )
        }

    init {
        val genericEitherOrStepIds = ProposalExperienceAdapter.definition.steps
            .filter { it.kind == ExperienceStepKind.EITHER_OR }
            .map(ExperienceStep::id)
            .toSet()
        require(byStepId.keys == genericEitherOrStepIds) {
            "Proposal Either-Or adapter must cover exactly the proposal Either-Or steps."
        }
    }

    fun contentFor(stepId: String): EitherOrStepContent? = byStepId[stepId]
}
