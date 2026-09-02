package com.example

import com.example.ui.session.normalizeHarmonyDisplayName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileInputPolicyTest {
    @Test
    fun `display name is trimmed before server update`() {
        assertEquals("Mia", normalizeHarmonyDisplayName("  Mia  "))
    }

    @Test
    fun `blank and oversized display names are rejected`() {
        assertNull(normalizeHarmonyDisplayName("   "))
        assertNull(normalizeHarmonyDisplayName("x".repeat(61)))
    }
}