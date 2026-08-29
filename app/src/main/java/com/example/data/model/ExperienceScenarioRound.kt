package com.example.data.model

/** UI-independent scenario decision reusable by any Harmony experience. */
data class ExperienceScenarioRound(
    val id: String,
    val prompt: String,
    val options: List<String>
) {
    init {
        require(id.isNotBlank()) { "Scenario rounds need a stable id." }
        require(prompt.isNotBlank()) { "Scenario rounds need a prompt." }
        require(options.size >= 2) { "Scenario rounds need at least two options." }
        require(options.all(String::isNotBlank)) { "Scenario options cannot be blank." }
        require(options.distinct().size == options.size) {
            "Scenario options must be unique."
        }
    }
}

fun ProposalScenarioRound.toExperienceScenarioRound(): ExperienceScenarioRound =
    ExperienceScenarioRound(id = id, prompt = prompt, options = options)
