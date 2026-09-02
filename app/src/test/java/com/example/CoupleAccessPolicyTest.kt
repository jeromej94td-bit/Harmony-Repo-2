package com.example

import com.example.ui.session.requiresPartnerForTab
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoupleAccessPolicyTest {
    @Test
    fun `chat and shared moments require a connected partner`() {
        assertTrue(requiresPartnerForTab(2))
        assertTrue(requiresPartnerForTab(3))
    }

    @Test
    fun `home games memory dev and pack browsing stay available solo`() {
        listOf(0, 1, 4, 5, 6).forEach { tab ->
            assertFalse("tab $tab should remain available in solo mode", requiresPartnerForTab(tab))
        }
    }
}