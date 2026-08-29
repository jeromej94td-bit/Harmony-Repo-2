package com.example.data.model

/**
 * One personal free-text reflection for Stage 02.8 of the perfect-proposal experience.
 *
 * Rendering and answer persistence stay outside this model. The existing open-text mechanics can
 * later consume these stable prompts when the proposal runner is wired end to end.
 */
data class ProposalOpenPrompt(
    val id: String,
    val prompt: String
) {
    init {
        require(id.isNotBlank()) { "Proposal open prompts need a stable id." }
        require(prompt.isNotBlank()) { "Proposal open prompts need a prompt." }
    }
}

object ProposalOpenPrompts {
    const val STEP_ID = "personal_wishes"

    val prompts: List<ProposalOpenPrompt> = listOf(
        ProposalOpenPrompt(
            id = "personal_words_to_remember",
            prompt = "Wenn von eurem Antrag Jahre später nur ein einziger Satz im Herzen bleibt: Welche Worte sollen ausgerechnet von dir in Erinnerung bleiben?"
        ),
        ProposalOpenPrompt(
            id = "personal_private_detail",
            prompt = "Welches kleine Detail aus eurer gemeinsamen Geschichte würdest du in diesen Moment einbauen, das für andere unscheinbar wäre, aber nur ihr beide wirklich versteht?"
        ),
        ProposalOpenPrompt(
            id = "personal_future_feeling",
            prompt = "Was soll dein Partner in diesem Augenblick über eure gemeinsame Zukunft fühlen – nicht planen oder erklären, sondern ganz unmittelbar spüren?"
        ),
        ProposalOpenPrompt(
            id = "personal_non_negotiable",
            prompt = "Was darf bei eurem Antrag auf keinen Fall passieren, damit sich der Moment wirklich nach euch anfühlt und nicht wie eine perfekte Show für andere?"
        ),
        ProposalOpenPrompt(
            id = "personal_imperfect_moment",
            prompt = "Wenn am echten Tag plötzlich alles anders läuft als geplant: Was muss trotzdem erhalten bleiben, damit du später sagen kannst, genau so war es richtig?"
        )
    )

    init {
        val openPromptStep = ProposalExperienceDefinitions.perfectProposal.steps
            .singleOrNull { it.id == STEP_ID }
        require(openPromptStep?.kind == ProposalFlowStepKind.OPEN_PROMPT) {
            "Proposal open prompts must attach to the personal_wishes open-prompt step."
        }

        require(prompts.map(ProposalOpenPrompt::id).distinct().size == prompts.size) {
            "Proposal open-prompt ids must be unique."
        }
        require(prompts.map(ProposalOpenPrompt::prompt).distinct().size == prompts.size) {
            "Proposal open-prompt texts must be unique."
        }
    }
}
