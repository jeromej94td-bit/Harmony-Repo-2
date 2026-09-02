package com.example

import com.example.data.couple.RapidAnswerSubmissionGuard
import com.example.data.couple.CoupleAnswerStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RapidAnswerSubmissionGuardTest {
    @Test
    fun `recent accepted submission is reused during stale tap window`() {
        var now = 1_000L
        val guard = RapidAnswerSubmissionGuard(windowMs = 750L, nowMs = { now })
        val status = CoupleAnswerStatus("round-1", true, false, false)

        assertNull(guard.recent("pack", 2))
        guard.record("pack", 2, status)

        now += 300L
        assertEquals(status, guard.recent("pack", 2))

        now += 800L
        assertNull(guard.recent("pack", 2))
    }
}
