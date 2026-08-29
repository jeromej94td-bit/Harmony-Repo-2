package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class LoveBalanceRuntimeVisibilityTest {

    @Test
    fun `stale dynamic love balance pack cannot hide happy couple question`() {
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

        try {
            HarmonyPacksData.setDynamicPacks(listOf(staleRuntimePack))

            val visiblePack = HarmonyPacksData.PACKS.single {
                it.id == LoveBalanceQuestionPolicy.PACK_ID
            }

            assertEquals(11, visiblePack.questions.size)
            assertEquals(
                LoveBalanceQuestionPolicy.QUESTION_TEXT,
                visiblePack.questions.first().q
            )
            assertEquals(listOf("1", "2", "3", "4"), visiblePack.questions.first().options)
        } finally {
            HarmonyPacksData.setDynamicPacks(emptyList())
        }
    }
}
