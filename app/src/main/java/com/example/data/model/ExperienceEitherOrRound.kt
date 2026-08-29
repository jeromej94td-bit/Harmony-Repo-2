package com.example.data.model

/**
 * UI-independent two-choice round that can be reused by any Harmony experience.
 */
data class ExperienceEitherOrRound(
    val id: String,
    val prompt: String,
    val firstChoice: String,
    val secondChoice: String
) {
    init {
        require(id.isNotBlank()) { "Either-or rounds need a stable id." }
        require(prompt.isNotBlank()) { "Either-or rounds need a prompt." }
        require(firstChoice.isNotBlank() && secondChoice.isNotBlank()) {
            "Either-or rounds need two choices."
        }
        require(firstChoice != secondChoice) { "Either-or choices must differ." }
    }
}

/**
 * Compatibility adapter: proposal content remains proposal-owned while the renderer consumes the
 * generic experience model.
 */
fun ProposalEitherOrRound.toExperienceEitherOrRound(): ExperienceEitherOrRound =
    ExperienceEitherOrRound(
        id = id,
        prompt = prompt,
        firstChoice = firstChoice,
        secondChoice = secondChoice
    )
