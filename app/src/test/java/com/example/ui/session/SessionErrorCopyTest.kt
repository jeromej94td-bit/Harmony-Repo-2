package com.example.ui.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SessionErrorCopyTest {
    @Test
    fun `account deletion infrastructure errors never expose request secrets`() {
        val raw = """
            {"ok":false,"error":"avatar_cleanup_failed"}
            URL: https://example.supabase.co/functions/v1/delete-account
            Headers: [Authorization=[Bearer synthetic-secret-token]]
        """.trimIndent()

        val copy = sessionErrorCopy(raw)

        assertEquals(
            "Dein Konto konnte gerade nicht gelöscht werden. Bitte versuche es erneut.",
            copy
        )
        assertFalse(copy.contains("Authorization", ignoreCase = true))
        assertFalse(copy.contains("Bearer", ignoreCase = true))
        assertFalse(copy.contains("https://", ignoreCase = true))
    }
}
