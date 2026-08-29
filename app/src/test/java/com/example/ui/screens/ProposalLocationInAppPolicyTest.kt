package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProposalLocationInAppPolicyTest {

    @Test
    fun `first proposal question opens proposal location duel`() {
        assertEquals(
            HarmonyImageChoiceKind.PROPOSAL_LOCATION,
            harmonyImageChoiceKind(packId = "antrag", questionIndex = 0)
        )
    }

    @Test
    fun `second proposal question keeps its existing standard quiz mechanic`() {
        assertNull(harmonyImageChoiceKind(packId = "antrag", questionIndex = 1))
    }
}
