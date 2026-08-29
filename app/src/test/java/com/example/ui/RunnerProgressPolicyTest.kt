package com.example.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RunnerProgressPolicyTest {

    @Test
    fun `resume starts at first unanswered question`() {
        assertEquals(1, RunnerProgressPolicy.firstUnanswered(5, setOf(0, 2, 3)))
        assertEquals(0, RunnerProgressPolicy.firstUnanswered(5, emptySet()))
        assertNull(RunnerProgressPolicy.firstUnanswered(3, setOf(0, 1, 2)))
    }

    @Test
    fun `discussion finds next unanswered and wraps around`() {
        assertEquals(4, RunnerProgressPolicy.nextUnanswered(5, setOf(0, 1, 2), afterIndex = 2))
        assertEquals(1, RunnerProgressPolicy.nextUnanswered(5, setOf(0, 2, 3, 4), afterIndex = 4))
        assertNull(RunnerProgressPolicy.nextUnanswered(3, setOf(0, 1, 2), afterIndex = 1))
    }

    @Test
    fun `drawing completion defers automatic advance to canvas next step callback`() {
        assertTrue(RunnerProgressPolicy.deferAutomaticAdvance("draw", "DRAWING_COMPLETED"))
        assertFalse(RunnerProgressPolicy.deferAutomaticAdvance("quiz", "DRAWING_COMPLETED"))
        assertFalse(RunnerProgressPolicy.deferAutomaticAdvance("draw", "other"))
    }
}
