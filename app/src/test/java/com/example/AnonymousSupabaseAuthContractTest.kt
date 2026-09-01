package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnonymousSupabaseAuthContractTest {
    @Test
    fun `brain auth reuses canonical signed in session and never signs up anonymously`() {
        val brainAuth = source("app/src/main/java/com/example/data/brain/gateway/SupabaseBrainAuthSession.kt")

        assertTrue(brainAuth.contains("SupabaseConfig.client.auth.currentSessionOrNull()"))
        assertTrue(brainAuth.contains("requires a signed-in Supabase account"))
        assertFalse(brainAuth.contains("auth/v1/signup"))
        assertFalse(brainAuth.contains("Request.Builder"))
        assertFalse(brainAuth.contains("Fetching anonymous Supabase token"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
