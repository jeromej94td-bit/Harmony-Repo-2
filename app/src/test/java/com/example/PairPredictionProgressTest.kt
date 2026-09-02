package com.example

import com.example.data.model.PredictionAnswerCodec
import com.example.ui.screens.pairPredictionProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class PairPredictionProgressTest {

    @Test
    fun progressUsesStoredPredictionAnswersAndExcludesCurrentQuestion() {
        val answers = mapOf(
            0 to PredictionAnswerCodec.encode("A", "A"),
            1 to PredictionAnswerCodec.encode("B", "C"),
            2 to PredictionAnswerCodec.encode("D", "D"),
            3 to "plain-answer"
        )

        val progress = pairPredictionProgress(
            answers = answers,
            currentIndex = 1,
            questionCount = 8
        )

        assertEquals(2, progress.hits)
        assertEquals(2, progress.completed)
        assertEquals(8, progress.questionCount)
    }

    @Test
    fun replacingCurrentRevealDoesNotDoubleCountIt() {
        val answers = mapOf(
            0 to PredictionAnswerCodec.encode("A", "A"),
            1 to PredictionAnswerCodec.encode("B", "B")
        )

        val progress = pairPredictionProgress(
            answers = answers,
            currentIndex = 1,
            questionCount = 2
        )

        assertEquals(1, progress.hits)
        assertEquals(1, progress.completed)
    }
}
