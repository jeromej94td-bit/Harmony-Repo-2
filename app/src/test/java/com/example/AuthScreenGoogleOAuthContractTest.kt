package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthScreenGoogleOAuthContractTest {

    @Test
    fun `google button uses direct Supabase OAuth and demo stays separate`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("onDemoRequested: () -> Unit"))
        assertTrue(auth.contains("SupabaseConfig.client.auth.signInWith(Google)"))
        assertTrue(auth.contains("onClick = onDemoRequested"))
        assertFalse(auth.contains("CredentialManager"))
        assertFalse(auth.contains("GetCredentialException"))
        assertFalse(auth.contains("performResilientGoogleSignIn"))
        assertFalse(auth.contains("context.findActivity()"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
