package com.example

import com.example.data.couple.CoupleRevealState
import com.example.data.couple.coupleRevealState
import org.junit.Assert.assertEquals
import org.junit.Test

class CoupleRevealPolicyTest {
    @Test
    fun `partner answer stays hidden until both answered`() {
        assertEquals(
            CoupleRevealState.WAITING_FOR_PARTNER,
            coupleRevealState(myAnswered = true, partnerAnswered = false, partnerAnswerText = null)
        )
    }

    @Test
    fun `result is revealable only when both answered and server returned partner value`() {
        assertEquals(
            CoupleRevealState.READY,
            coupleRevealState(myAnswered = true, partnerAnswered = true, partnerAnswerText = "Am Meer")
        )
    }

    @Test
    fun `missing own answer never reveals partner value`() {
        assertEquals(
            CoupleRevealState.NEEDS_OWN_ANSWER,
            coupleRevealState(myAnswered = false, partnerAnswered = true, partnerAnswerText = "Geheim")
        )
    }
}
