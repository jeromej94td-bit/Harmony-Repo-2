package com.example.data.model

/** UI-independent free-text prompt reusable by any Harmony experience. */
data class ExperienceOpenPromptRound(
    val id: String,
    val prompt: String
) {
    init {
        require(id.isNotBlank()) { "Open-prompt rounds need a stable id." }
        require(prompt.isNotBlank()) { "Open-prompt rounds need a prompt." }
    }
}

fun ProposalOpenPrompt.toExperienceOpenPromptRound(): ExperienceOpenPromptRound =
    ExperienceOpenPromptRound(id = id, prompt = prompt)
