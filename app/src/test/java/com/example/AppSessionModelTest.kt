package com.example

import com.example.data.session.AppSession
import com.example.data.session.PairingState
import com.example.data.session.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSessionModelTest {
    private val me = UserProfile("user-a", "Mia", "https://example.com/mia.jpg")
    private val partner = UserProfile("user-b", "Noah", "https://example.com/noah.jpg")

    @Test
    fun `solo session is usable without partner`() {
        val session = AppSession(
            userId = me.userId,
            email = "mia@example.com",
            profile = me,
            coupleId = null,
            partner = null
        )

        assertFalse(session.isPaired)
        assertEquals(PairingState.SOLO, session.pairingState)
    }

    @Test
    fun `paired session requires couple and partner`() {
        val session = AppSession(
            userId = me.userId,
            email = "mia@example.com",
            profile = me,
            coupleId = "couple-1",
            partner = partner
        )

        assertTrue(session.isPaired)
        assertEquals(PairingState.PAIRED, session.pairingState)
        assertEquals("https://example.com/noah.jpg", session.partner?.avatarUrl)
    }
}
