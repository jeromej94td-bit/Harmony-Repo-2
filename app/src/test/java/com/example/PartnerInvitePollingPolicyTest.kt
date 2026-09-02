package com.example

import com.example.ui.session.shouldPollPartnerInvite
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PartnerInvitePollingPolicyTest {
    @Test
    fun `poll only while a solo user is showing an active invite`() {
        assertTrue(shouldPollPartnerInvite(isPaired = false, hasActiveInvite = true))
        assertFalse(shouldPollPartnerInvite(isPaired = true, hasActiveInvite = true))
        assertFalse(shouldPollPartnerInvite(isPaired = false, hasActiveInvite = false))
    }
}