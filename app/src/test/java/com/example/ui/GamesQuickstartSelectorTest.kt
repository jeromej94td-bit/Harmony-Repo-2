package com.example.ui

import com.example.data.model.AnswerEntity
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import com.example.ui.screens.buildGamesQuickstartPool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GamesQuickstartSelectorTest {

    @Test
    fun `completed packs are never offered by quickstart`() {
        val completed = pack(id = "done", category = "tief", questionCount = 2)
        val open = pack(id = "open", category = "wer", questionCount = 2)
        val answers = listOf(
            AnswerEntity("done", 0, "A"),
            AnswerEntity("done", 1, "B")
        )

        val pool = buildGamesQuickstartPool(listOf(completed, open), answers)

        assertFalse(pool.candidates.any { it.packId == "done" })
        assertTrue(pool.candidates.any { it.packId == "open" })
        assertEquals(2, pool.openQuestionCount)
    }

    @Test
    fun `partially answered pack remains available and counts only unanswered questions`() {
        val partial = pack(id = "partial", category = "tief", questionCount = 3)
        val answers = listOf(AnswerEntity("partial", 1, "Schon beantwortet"))

        val pool = buildGamesQuickstartPool(listOf(partial), answers)

        assertEquals(1, pool.openPackCount)
        assertEquals(2, pool.openQuestionCount)
        assertEquals(listOf(0, 2), pool.candidates.single().unansweredIndexes)
    }

    @Test
    fun `selection is category fair before choosing a pack`() {
        val manyA = listOf(
            pack("a1", "cat_a", 1),
            pack("a2", "cat_a", 1),
            pack("a3", "cat_a", 1)
        )
        val oneB = pack("b1", "cat_b", 1)
        val pool = buildGamesQuickstartPool(manyA + oneB, emptyList())
        val choices = ArrayDeque(listOf(1, 0))

        val selected = pool.pick { bound ->
            val requested = choices.removeFirst()
            requested.coerceIn(0, bound - 1)
        }

        assertEquals("cat_b", selected?.categoryId)
        assertEquals("b1", selected?.packId)
    }

    @Test
    fun `all answered returns no quickstart candidate`() {
        val only = pack("only", "tief", 1)
        val pool = buildGamesQuickstartPool(
            packs = listOf(only),
            answers = listOf(AnswerEntity("only", 0, "A"))
        )

        assertNull(pool.pick { 0 })
        assertEquals(0, pool.openQuestionCount)
    }

    private fun pack(id: String, category: String, questionCount: Int): QuestionPack =
        QuestionPack(
            id = id,
            title = id,
            tags = emptyList(),
            cat = category,
            topic = "beziehung",
            type = "quiz",
            questions = List(questionCount) { index -> Question("Frage $index") }
        )
}
