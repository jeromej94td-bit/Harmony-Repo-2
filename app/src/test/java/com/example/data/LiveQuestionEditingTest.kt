package com.example.data

import com.example.data.model.Question
import com.example.data.model.QuestionPack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class LiveQuestionEditingTest {

    private val basePack = QuestionPack(
        id = "live_test",
        title = "Live Test",
        tags = listOf("unterhaltung"),
        cat = "tief",
        topic = "beziehung",
        type = "quiz",
        questions = listOf(
            Question("Frage A", listOf("A1", "A2")),
            Question("Frage B", listOf("B1", "B2")),
            Question("Frage C", listOf("C1", "C2"))
        )
    )

    @Test
    fun insertsQuestionAtExactRequestedPosition() {
        val newQuestion = LiveQuestionEditing.createQuestion(
            kind = LiveQuestionKind.CHOICE,
            text = "Neue Frage",
            options = listOf("Ja", "Nein", "Vielleicht")
        )

        val updated = LiveQuestionEditing.insertQuestion(basePack, 1, newQuestion)

        assertEquals(listOf("Frage A", "Neue Frage", "Frage B", "Frage C"), updated.questions.map { it.q })
        assertEquals(listOf("Ja", "Nein", "Vielleicht"), updated.questions[1].options)
        assertEquals("choice", updated.questions[1].kind)
    }

    @Test
    fun choiceQuestionAllowsArbitraryNumberOfAnswerOptions() {
        val question = LiveQuestionEditing.createQuestion(
            kind = LiveQuestionKind.CHOICE,
            text = "Was passt?",
            options = listOf("1", "2", "3", "4", "5", "6")
        )

        assertEquals(6, question.options.size)
    }

    @Test
    fun freeTextQuestionDropsAnswerOptions() {
        val question = LiveQuestionEditing.createQuestion(
            kind = LiveQuestionKind.FREE_TEXT,
            text = "Erzähl mir davon",
            options = listOf("soll", "weg")
        )

        assertEquals("free_text", question.kind)
        assertEquals(emptyList<String>(), question.options)
    }

    @Test
    fun thisOrThatRequiresExactlyTwoOptions() {
        assertThrows(IllegalArgumentException::class.java) {
            LiveQuestionEditing.createQuestion(
                kind = LiveQuestionKind.THIS_OR_THAT,
                text = "Was lieber?",
                options = listOf("Nur eins")
            )
        }

        val valid = LiveQuestionEditing.createQuestion(
            kind = LiveQuestionKind.THIS_OR_THAT,
            text = "Was lieber?",
            options = listOf("Links", "Rechts")
        )
        assertEquals(listOf("Links", "Rechts"), valid.options)
    }

    @Test
    fun duplicateMoveAndDeleteKeepQuestionOrderDeterministic() {
        val duplicated = LiveQuestionEditing.duplicateQuestion(basePack, 1)
        assertEquals(listOf("Frage A", "Frage B", "Frage B", "Frage C"), duplicated.questions.map { it.q })

        val moved = LiveQuestionEditing.moveQuestion(duplicated, fromIndex = 2, toIndex = 0)
        assertEquals(listOf("Frage B", "Frage A", "Frage B", "Frage C"), moved.questions.map { it.q })

        val deleted = LiveQuestionEditing.deleteQuestion(moved, 1)
        assertEquals(listOf("Frage B", "Frage B", "Frage C"), deleted.questions.map { it.q })
    }

    @Test
    fun legacyQuestionKindFallsBackToExistingPackType() {
        assertEquals(
            LiveQuestionKind.CHOICE,
            LiveQuestionEditing.effectiveKind(Question("Alt"), packType = "quiz")
        )
        assertEquals(
            LiveQuestionKind.FREE_TEXT,
            LiveQuestionEditing.effectiveKind(Question("Alt"), packType = "disc")
        )
        assertEquals(
            LiveQuestionKind.THIS_OR_THAT,
            LiveQuestionEditing.effectiveKind(Question("Alt"), packType = "tot")
        )
    }
}
