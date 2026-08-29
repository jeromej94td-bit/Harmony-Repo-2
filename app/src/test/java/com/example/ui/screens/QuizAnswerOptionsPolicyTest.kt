package com.example.ui.screens

import com.example.data.model.QuestionInteractionSpec
import com.example.data.model.QuestionResponseKind
import org.junit.Assert.assertEquals
import org.junit.Test

class QuizAnswerOptionsPolicyTest {

    @Test
    fun `fixed choices do not receive an artificial custom answer`() {
        val spec = QuestionInteractionSpec(QuestionResponseKind.FIXED_CHOICE)

        assertEquals(
            listOf("A", "B", "C"),
            QuizAnswerOptionsPolicy.build(
                options = listOf("A", "B", "C"),
                spec = spec,
                customTextLabel = "Schreibe deine eigene Antwort",
                skipLabel = "Überspringen"
            )
        )
    }

    @Test
    fun `optional text receives exactly one custom entry`() {
        val spec = QuestionInteractionSpec(
            responseKind = QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            allowCustomText = true
        )

        assertEquals(
            listOf("A", "B", "Schreibe deine eigene Antwort"),
            QuizAnswerOptionsPolicy.build(
                options = listOf("A", "B"),
                spec = spec,
                customTextLabel = "Schreibe deine eigene Antwort",
                skipLabel = "Überspringen"
            )
        )
    }

    @Test
    fun `custom entry is not duplicated when source already contains it`() {
        val label = "Schreibe deine eigene Antwort"
        val spec = QuestionInteractionSpec(
            responseKind = QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            allowCustomText = true
        )

        assertEquals(
            listOf("A", label),
            QuizAnswerOptionsPolicy.build(
                options = listOf("A", label),
                spec = spec,
                customTextLabel = label,
                skipLabel = "Überspringen"
            )
        )
    }

    @Test
    fun `never have I ever keeps skip instead of custom text`() {
        val spec = QuestionInteractionSpec(
            responseKind = QuestionResponseKind.FIXED_CHOICE,
            allowSkip = true
        )

        assertEquals(
            listOf("Habe ich", "Habe ich noch nie", "Überspringen"),
            QuizAnswerOptionsPolicy.build(
                options = listOf("Habe ich", "Habe ich noch nie"),
                spec = spec,
                customTextLabel = "Schreibe deine eigene Antwort",
                skipLabel = "Überspringen"
            )
        )
    }

    @Test
    fun `open text exposes only the text entry`() {
        val spec = QuestionInteractionSpec(
            responseKind = QuestionResponseKind.OPEN_TEXT,
            allowCustomText = true
        )

        assertEquals(
            listOf("Schreibe deine eigene Antwort"),
            QuizAnswerOptionsPolicy.build(
                options = emptyList(),
                spec = spec,
                customTextLabel = "Schreibe deine eigene Antwort",
                skipLabel = "Überspringen"
            )
        )
    }
}
