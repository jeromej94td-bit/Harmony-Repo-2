package com.example

import com.example.ui.screens.RunnerContinuePolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RunnerContinuePolicyTest {

    @Test
    fun `answered question exposes continue so back navigation cannot trap the runner`() {
        assertTrue(
            RunnerContinuePolicy.shouldShow(
                isFinished = false,
                packType = "quiz",
                currentIndex = 2,
                answeredIndexes = setOf(0, 1, 2)
            )
        )
    }

    @Test
    fun `answered either-or and drawing questions can also continue after navigating back`() {
        assertTrue(
            RunnerContinuePolicy.shouldShow(
                isFinished = false,
                packType = "tot",
                currentIndex = 1,
                answeredIndexes = setOf(0, 1)
            )
        )
        assertTrue(
            RunnerContinuePolicy.shouldShow(
                isFinished = false,
                packType = "draw",
                currentIndex = 0,
                answeredIndexes = setOf(0)
            )
        )
    }

    @Test
    fun `unanswered finished and discussion states do not expose a misleading continue action`() {
        assertFalse(
            RunnerContinuePolicy.shouldShow(
                isFinished = false,
                packType = "quiz",
                currentIndex = 2,
                answeredIndexes = setOf(0, 1)
            )
        )
        assertFalse(
            RunnerContinuePolicy.shouldShow(
                isFinished = true,
                packType = "quiz",
                currentIndex = 2,
                answeredIndexes = setOf(0, 1, 2)
            )
        )
        assertFalse(
            RunnerContinuePolicy.shouldShow(
                isFinished = false,
                packType = "disc",
                currentIndex = 0,
                answeredIndexes = setOf(0)
            )
        )
    }
}
