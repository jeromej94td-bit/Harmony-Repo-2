package com.example.ui.screens

import com.example.data.model.Question
import com.example.data.model.QuestionPack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhotoQuestionRoutingTest {

    private fun pack(id: String, questions: List<Question>) = QuestionPack(
        id = id,
        title = id,
        tags = listOf("unterhaltung"),
        cat = "foto",
        topic = "beziehung",
        type = "quiz",
        questions = questions
    )

    @Test
    fun `moved conversation photo question still enters photo mechanic`() {
        val source = pack(
            "gespraechsanreger",
            listOf(
                Question("Andere Frage", listOf("A", "B")),
                Question("Noch eine Frage", listOf("A", "B")),
                Question("Was ist dein Lieblingsfoto von uns? 📸", listOf("A", "B", "C"))
            )
        )

        assertEquals(HarmonyImageChoiceKind.MEMORY_MATCH, harmonyImageChoiceKind(source, 2))
        assertNull(harmonyImageChoiceKind(source, 1))
    }

    @Test
    fun `moved snapshot photo question still enters photo mechanic`() {
        val source = pack(
            "schnapp",
            listOf(
                Question("Welches gemeinsame Foto ist dein Lieblingsfoto?", listOf("A", "B")),
                Question("Was war dein schönster Moment mit mir bisher?", listOf("A", "B"))
            )
        )

        assertEquals(HarmonyImageChoiceKind.MEMORY_MATCH, harmonyImageChoiceKind(source, 0))
        assertNull(harmonyImageChoiceKind(source, 1))
    }

    @Test
    fun `ordinary photo mention keeps normal quiz behavior`() {
        val source = pack(
            "ordinary_photo",
            listOf(Question("Wer würde eher das peinlichste Foto des Abends posten?", listOf("A", "B")))
        )

        assertNull(harmonyImageChoiceKind(source, 0))
    }
}
