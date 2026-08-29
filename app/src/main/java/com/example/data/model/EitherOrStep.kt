package com.example.data.model

/**
 * UI-independent content model for a reusable two-choice experience step.
 *
 * Rendering, answer persistence and navigation stay outside this model. The generic experience
 * engine owns only step order/navigation while this type owns the content for one Either-Or step.
 */
data class EitherOrRound(
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

class EitherOrStepContent(
    val stepId: String,
    rounds: List<EitherOrRound>
) {
    val rounds: List<EitherOrRound> = rounds.toList()
    val itemCount: Int
        get() = rounds.size

    init {
        require(stepId.isNotBlank()) { "Either-or step content needs a stable step id." }
        require(this.rounds.isNotEmpty()) { "Either-or step content needs at least one round." }
        require(this.rounds.map(EitherOrRound::id).distinct().size == this.rounds.size) {
            "Either-or round ids must be unique within a step."
        }
    }

    fun roundAt(index: Int): EitherOrRound? = rounds.getOrNull(index)
}
