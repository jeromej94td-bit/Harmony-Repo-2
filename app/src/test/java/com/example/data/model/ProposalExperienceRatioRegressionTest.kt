package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Locks the Stage-02 pacing independently from the generic Stage-03 navigator.
 *
 * These expected ratios are derived from the shipped 35-position proposal journey:
 * 5 + 6 + 3 + 5 + 1 + 3 + 6 + 5 + 1 = 35 positions, hence 34 progress intervals.
 */
class ProposalExperienceRatioRegressionTest {

    private val counts = listOf(5, 6, 3, 5, 1, 3, 6, 5, 1)

    @Test
    fun `proposal progress keeps the shipped thirty five position weighting`() {
        assertEquals(35, counts.sum())

        var previous = -1f
        counts.forEachIndexed { stepIndex, count ->
            val completedBefore = counts.take(stepIndex).sum()
            repeat(count) { itemIndex ->
                val expected = if (stepIndex == counts.lastIndex) {
                    1f
                } else {
                    (completedBefore + itemIndex).toFloat() / 34f
                }
                val actual = ProposalExperienceRunnerPolicy.progress(
                    ProposalRunnerPosition(stepIndex, itemIndex)
                )

                assertEquals(expected, actual, 0.000001f)
                assertTrue(actual >= previous)
                previous = actual
            }
        }

        assertEquals(1f, previous, 0.000001f)
    }

    @Test
    fun `step boundary proportions remain stable`() {
        assertEquals(0f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(0, 0)), 0.000001f)
        assertEquals(5f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(1, 0)), 0.000001f)
        assertEquals(11f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(2, 0)), 0.000001f)
        assertEquals(14f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(3, 0)), 0.000001f)
        assertEquals(19f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(4, 0)), 0.000001f)
        assertEquals(20f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(5, 0)), 0.000001f)
        assertEquals(23f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(6, 0)), 0.000001f)
        assertEquals(29f / 34f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(7, 0)), 0.000001f)
        assertEquals(1f, ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(8, 0)), 0.000001f)
    }
}
