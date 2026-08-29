package com.example.data

import com.example.data.model.LoveBalanceQuestionPolicy
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoveBalanceGlobalRuntimePackTest {

    @After
    fun resetCustomPacks() {
        DeveloperDataManager._customPacks.clear()
    }

    @Test
    fun `generated registry normalizes stale custom love balance pack before runtime merge`() {
        val staleQuestions = (0 until 10).map { index ->
            Question("legacy-$index", listOf("A", "B"))
        }
        val stalePack = QuestionPack(
            id = LoveBalanceQuestionPolicy.PACK_ID,
            title = "Liebe im Gleichgewicht",
            tags = listOf("unterhaltung"),
            cat = "lieber",
            topic = "beziehung",
            type = "quiz",
            questions = staleQuestions
        )

        DeveloperDataManager._customPacks.clear()
        DeveloperDataManager._customPacks.add(stalePack)

        GeneratedContentRegistry.PACKS

        val normalized = DeveloperDataManager._customPacks.single { it.id == LoveBalanceQuestionPolicy.PACK_ID }
        assertEquals(11, normalized.questions.size)
        assertEquals(LoveBalanceQuestionPolicy.QUESTION_TEXT, normalized.questions.first().q)
        assertEquals(listOf("1", "2", "3", "4"), normalized.questions.first().options)
        assertEquals("legacy-0", normalized.questions[1].q)
        assertTrue(normalized.questions.drop(1).map { it.q }.containsAll(staleQuestions.map { it.q }))
    }
}
