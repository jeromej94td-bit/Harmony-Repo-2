package com.example

import com.example.ui.screens.pairPredictionProgress
import com.example.ui.screens.upsertPairPredictionResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PairPredictionProgressTest {

    @Test
    fun progressCountsCurrentRevealWithoutDuplicatingAnExistingQuestion() {
        val progress = pairPredictionProgress(
            questions = listOf("Q1", "Q2", "Q3"),
            hits = booleanArrayOf(true, false, true),
            currentQuestion = "Q2",
            currentHit = true,
            questionCount = 8
        )

        assertEquals(3, progress.hits)
        assertEquals(3, progress.completed)
        assertEquals(8, progress.questionCount)
    }

    @Test
    fun progressAddsAQuestionOnlyOnceWhenItIsNew() {
        val progress = pairPredictionProgress(
            questions = listOf("Q1", "Q2"),
            hits = booleanArrayOf(true, false),
            currentQuestion = "Q3",
            currentHit = false,
            questionCount = 8
        )

        assertEquals(1, progress.hits)
        assertEquals(3, progress.completed)
    }

    @Test
    fun upsertReplacesExistingRevealInsteadOfAppendingDuplicate() {
        val updated = upsertPairPredictionResult(
            questions = listOf("Q1", "Q2"),
            hits = booleanArrayOf(true, false),
            currentQuestion = "Q2",
            currentHit = true
        )

        assertEquals(listOf("Q1", "Q2"), updated.questions)
        assertEquals(2, updated.hits.size)
        assertTrue(updated.hits[0])
        assertTrue(updated.hits[1])
    }

    @Test
    fun upsertAppendsNewRevealAtEnd() {
        val updated = upsertPairPredictionResult(
            questions = listOf("Q1"),
            hits = booleanArrayOf(true),
            currentQuestion = "Q2",
            currentHit = false
        )

        assertEquals(listOf("Q1", "Q2"), updated.questions)
        assertEquals(2, updated.hits.size)
        assertTrue(updated.hits[0])
        assertFalse(updated.hits[1])
    }
}
