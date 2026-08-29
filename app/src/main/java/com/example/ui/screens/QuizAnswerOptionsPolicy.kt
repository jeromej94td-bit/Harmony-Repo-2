package com.example.ui.screens

import com.example.data.model.QuestionInteractionSpec
import com.example.data.model.QuestionResponseKind

/** Builds the visible standard-quiz answer list from explicit interaction semantics. */
internal object QuizAnswerOptionsPolicy {
    fun build(
        options: List<String>,
        spec: QuestionInteractionSpec,
        customTextLabel: String,
        skipLabel: String
    ): List<String> {
        if (spec.fullscreenMechanic != null) return emptyList()

        val cleaned = options.filter { it.isNotBlank() }
        return when {
            spec.allowSkip -> appendOnce(cleaned, skipLabel)
            spec.responseKind == QuestionResponseKind.OPEN_TEXT && spec.allowCustomText ->
                listOf(customTextLabel)
            spec.responseKind == QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT && spec.allowCustomText ->
                appendOnce(cleaned, customTextLabel)
            else -> cleaned
        }
    }

    private fun appendOnce(options: List<String>, value: String): List<String> =
        if (options.any { it == value }) options else options + value
}
