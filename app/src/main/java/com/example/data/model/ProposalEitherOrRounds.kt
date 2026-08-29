package com.example.data.model

/**
 * Content for Stage 02.2 of the proposal experience.
 *
 * These rounds are intentionally model-only. A later experience runner can render them without
 * treating them as standalone packs or adding a second navigation surface.
 */
data class ProposalEitherOrRound(
    val id: String,
    val prompt: String,
    val firstChoice: String,
    val secondChoice: String
) {
    init {
        require(id.isNotBlank()) { "Proposal either-or rounds need a stable id." }
        require(prompt.isNotBlank()) { "Proposal either-or rounds need a prompt." }
        require(firstChoice.isNotBlank() && secondChoice.isNotBlank()) {
            "Proposal either-or rounds need two choices."
        }
        require(firstChoice != secondChoice) { "Proposal either-or choices must differ." }
    }
}

object ProposalEitherOrRounds {
    private const val MOOD_STEP_ID = "proposal_mood"
    private const val DETAILS_STEP_ID = "proposal_details"

    val byStepId: Map<String, List<ProposalEitherOrRound>> = linkedMapOf(
        MOOD_STEP_ID to listOf(
            ProposalEitherOrRound(
                id = "mood_intimate_or_grand",
                prompt = "Wie soll sich euer Moment anfühlen?",
                firstChoice = "Leise & intim",
                secondChoice = "Groß & feierlich"
            ),
            ProposalEitherOrRound(
                id = "mood_surprise_or_planned",
                prompt = "Wie möchtet ihr den Moment erleben?",
                firstChoice = "Überraschend",
                secondChoice = "Gemeinsam geplant"
            ),
            ProposalEitherOrRound(
                id = "mood_spontaneous_or_prepared",
                prompt = "Wie viel Vorbereitung fühlt sich richtig an?",
                firstChoice = "Spontan im Augenblick",
                secondChoice = "Bis ins Detail vorbereitet"
            ),
            ProposalEitherOrRound(
                id = "mood_playful_or_moving",
                prompt = "Welchen Ton soll euer Moment tragen?",
                firstChoice = "Humorvoll & leicht",
                secondChoice = "Tief berührend"
            ),
            ProposalEitherOrRound(
                id = "mood_private_or_shared",
                prompt = "Mit wem möchtet ihr diesen Augenblick teilen?",
                firstChoice = "Nur wir zwei",
                secondChoice = "Mit unseren Lieblingsmenschen"
            )
        ),
        DETAILS_STEP_ID to listOf(
            ProposalEitherOrRound(
                id = "detail_story_or_simple",
                prompt = "Wie soll die Frage ihren Anfang finden?",
                firstChoice = "Mit unserer Geschichte",
                secondChoice = "Mit einer klaren, einfachen Frage"
            ),
            ProposalEitherOrRound(
                id = "detail_ritual_or_surprise",
                prompt = "Welches Element passt besser zu euch?",
                firstChoice = "Ein kleines Ritual nur für uns",
                secondChoice = "Ein Moment voller Überraschungen"
            ),
            ProposalEitherOrRound(
                id = "detail_pause_or_celebrate",
                prompt = "Was soll direkt danach im Mittelpunkt stehen?",
                firstChoice = "Zeit zum Innehalten",
                secondChoice = "Direkt gemeinsam feiern"
            ),
            ProposalEitherOrRound(
                id = "detail_captured_or_unplugged",
                prompt = "Wie möchtet ihr euch später erinnern?",
                firstChoice = "Ein Foto als Erinnerung",
                secondChoice = "Den Moment ganz ohne Kamera erleben"
            ),
            ProposalEitherOrRound(
                id = "detail_past_or_future",
                prompt = "Welche Bedeutung soll das Zeichen tragen?",
                firstChoice = "Unsere gemeinsame Reise",
                secondChoice = "Unser nächster gemeinsamer Schritt"
            ),
            ProposalEitherOrRound(
                id = "detail_written_or_spoken",
                prompt = "Wie sollen die wichtigsten Worte entstehen?",
                firstChoice = "Vorher aufgeschrieben",
                secondChoice = "Frei im Moment gesprochen"
            )
        )
    )

    init {
        val proposalFlow = ProposalExperienceDefinitions.perfectProposal
        val eitherOrStepIds = proposalFlow.steps
            .filter { it.kind == ProposalFlowStepKind.EITHER_OR }
            .map(ProposalFlowStep::id)
            .toSet()
        require(byStepId.keys == eitherOrStepIds) {
            "Either-or content must cover exactly the proposal either-or steps."
        }

        val allRoundIds = byStepId.values.flatten().map(ProposalEitherOrRound::id)
        require(allRoundIds.distinct().size == allRoundIds.size) {
            "Proposal either-or round ids must be unique."
        }
    }

    fun roundsFor(stepId: String): List<ProposalEitherOrRound> = byStepId[stepId].orEmpty()
}
