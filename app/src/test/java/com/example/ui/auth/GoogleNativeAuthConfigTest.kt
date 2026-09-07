package com.example.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleNativeAuthConfigTest {
    @Test
    fun `uses the Harmony Supabase web client id`() {
        assertEquals(
            "1038373974684-lh5o0nhstljubgf76gfg62ifp302ulm6.apps.googleusercontent.com",
            GoogleNativeAuthConfig.WEB_CLIENT_ID
        )
    }

    @Test
    fun `hashes raw nonce with sha256 hex`() {
        assertEquals(
            "b2fdf71bebbc50c4b9aa868cdfbd3b73ab44ee728e4667e5fbdb17d397ebeb0d",
            GoogleNativeAuthConfig.sha256Hex("harmony-test-nonce")
        )
    }

    @Test
    fun `generates nonempty distinct raw nonces`() {
        val first = GoogleNativeAuthConfig.generateRawNonce()
        val second = GoogleNativeAuthConfig.generateRawNonce()
        assertTrue(first.length >= 32)
        assertNotEquals(first, second)
    }
}
