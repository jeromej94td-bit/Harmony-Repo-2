package com.example

import com.example.ui.screens.WeekendEchoPhase
import com.example.ui.screens.WeekendEchoRoundState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WeekendEchoRoundPolicyTest {
    @Test
    fun `first choice remains hidden while the phone changes hands`() {
        val picked = WeekendEchoRoundState.initial().pick("cinema")

        assertEquals(WeekendEchoPhase.HANDOFF, picked.phase)
        assertFalse(picked.exposesAnswers)
        assertFalse(picked.canContinue)
    }

    @Test
    fun `second person cannot choose before handoff confirmation`() {
        val waiting = WeekendEchoRoundState.initial().pick("cinema")

        assertEquals(waiting, waiting.pick("couch_blanket"))
    }

    @Test
    fun `reveal is impossible until both people chose`() {
        val waiting = WeekendEchoRoundState.initial().pick("cinema")

        assertEquals(waiting, waiting.reveal())
    }

    @Test
    fun `both selections become visible only after explicit reveal`() {
        val ready = WeekendEchoRoundState.initial()
            .pick("cinema")
            .handoffReady()
            .pick("couch_blanket")

        assertEquals(WeekendEchoPhase.READY_TO_REVEAL, ready.phase)
        assertFalse(ready.exposesAnswers)
        assertFalse(ready.canContinue)

        val revealed = ready.reveal()
        assertEquals(WeekendEchoPhase.REVEALED, revealed.phase)
        assertTrue(revealed.exposesAnswers)
        assertTrue(revealed.canContinue)
    }

    @Test
    fun `unknown motif keys never enter private state`() {
        assertEquals(WeekendEchoRoundState.initial(), WeekendEchoRoundState.initial().pick("unknown"))
    }
}
