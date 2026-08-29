package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LoveBalanceRuntimeVisibilityTest {

    @Test
    fun `stale ten question love balance pack is normalized to happy couple plus ten originals`() {
        val staleRuntimePack = QuestionPack(
            id = LoveBalanceQuestionPolicy.PACK_ID,
            title = "Liebe im Gleichgewicht",
            tags = listOf("unterhaltung"),
            cat = "lieber",
            topic = "beziehung",
            type = "quiz",
            questions = (1..10).map { index ->
                Question(
                    q = "Bestehende Frage $index",
                    options = listOf("A", "B", "C", "D")
                )
            }
        )

        val normalized = LoveBalanceQuestionPolicy.ensureHappyCoupleFirst(staleRuntimePack)

        assertEquals(11, normalized.questions.size)
        assertEquals(LoveBalanceQuestionPolicy.QUESTION_TEXT, normalized.questions.first().q)
        assertEquals(listOf("1", "2", "3", "4"), normalized.questions.first().options)
        assertEquals(
            (1..10).map { "Bestehende Frage $it" },
            normalized.questions.drop(1).map { it.q }
        )
    }

    @Test
    fun `normalization is idempotent and never duplicates the visual question`() {
        val once = LoveBalanceQuestionPolicy.ensureHappyCoupleFirst(
            QuestionPack(
                id = LoveBalanceQuestionPolicy.PACK_ID,
                title = "Liebe im Gleichgewicht",
                tags = listOf("unterhaltung"),
                cat = "lieber",
                topic = "beziehung",
                type = "quiz",
                questions = listOf(
                    Question("Frage A", listOf("A", "B")),
                    LoveBalanceQuestionPolicy.happyCoupleQuestion,
                    Question("Frage B", listOf("A", "B"))
                )
            )
        )
        val twice = LoveBalanceQuestionPolicy.ensureHappyCoupleFirst(once)

        assertEquals(once, twice)
        assertEquals(1, twice.questions.count { it.q == LoveBalanceQuestionPolicy.QUESTION_TEXT })
        assertEquals(LoveBalanceQuestionPolicy.QUESTION_TEXT, twice.questions.first().q)
    }
}
