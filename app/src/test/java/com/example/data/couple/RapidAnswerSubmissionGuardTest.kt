package com.example.data.couple

import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class RapidAnswerSubmissionGuardTest {
    @Test
    fun `changed answer is never swallowed by rapid submission guard`() {
        var now = 1_000L
        val guard = RapidAnswerSubmissionGuard(windowMs = 750L, nowMs = { now })
        val status = CoupleAnswerStatus(
            roundId = "round-1",
            myAnswered = true,
            partnerAnswered = true,
            readyToReveal = true
        )

        guard.record("liebegleichgewicht", 0, "1", status)
        assertSame(status, guard.recent("liebegleichgewicht", 0, "1"))
        assertNull(guard.recent("liebegleichgewicht", 0, "2"))

        guard.record("liebegleichgewicht", 0, "2", status)
        assertSame(status, guard.recent("liebegleichgewicht", 0, "2"))

        now += 751L
        assertNull(guard.recent("liebegleichgewicht", 0, "2"))
    }
}
