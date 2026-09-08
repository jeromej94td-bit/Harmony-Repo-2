package com.example

import com.example.data.couple.PartnerPackRevealPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PartnerPackRevealPilotTest {
    @Test
    fun `whole pack reveal is enabled only for einander kennenlernen pilot`() {
        assertTrue(PartnerPackRevealPolicy.isWholePackRevealEnabled("aufwaermen1"))
        assertFalse(PartnerPackRevealPolicy.isWholePackRevealEnabled("gelegenheit"))
        assertFalse(PartnerPackRevealPolicy.isWholePackRevealEnabled("zuhause"))
    }

    @Test
    fun `partner answers stay hidden until both users completed the full pack`() {
        assertFalse(
            PartnerPackRevealPolicy.canRevealPartnerAnswers(
                myCompleted = true,
                partnerCompleted = false
            )
        )
        assertFalse(
            PartnerPackRevealPolicy.canRevealPartnerAnswers(
                myCompleted = false,
                partnerCompleted = true
            )
        )
        assertTrue(
            PartnerPackRevealPolicy.canRevealPartnerAnswers(
                myCompleted = true,
                partnerCompleted = true
            )
        )
    }

    @Test
    fun `partner completion notification uses requested copy`() {
        assertEquals(
            "Einander kennenlernen wurde von Anna ausgefüllt. Antworte jetzt, um die Antworten zu sehen.",
            PartnerPackRevealPolicy.completionNotificationBody("Anna")
        )
    }
}
