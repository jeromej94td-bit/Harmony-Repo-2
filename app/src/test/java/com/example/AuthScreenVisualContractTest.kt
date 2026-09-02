package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthScreenVisualContractTest {

    @Test
    fun `auth screen uses the dedicated Harmony logo and animated cosmic backdrop`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("HarmonyLoginLogo("))
        assertTrue(auth.contains("CosmicLoginBackdrop("))
        assertTrue(auth.contains("floatingHearts"))
        assertFalse(auth.contains("Icons.Filled.Favorite"))
    }

    @Test
    fun `auth screen keeps real Google branding and every access path`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("GoogleBrandMark("))
        assertFalse(auth.contains("G  Mit Google anmelden"))
        assertTrue(auth.contains("credentialManager.getCredential"))
        assertTrue(auth.contains("auth.signInWith(EmailProvider)"))
        assertTrue(auth.contains("auth.signUpWith(EmailProvider)"))
        assertTrue(auth.contains("App im Demo-Modus testen"))
        assertTrue(auth.contains("onAuthSuccess()"))
    }

    @Test
    fun `explicit Google button uses dedicated Google button credential flow`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("GetSignInWithGoogleOption.Builder("))
        assertFalse(auth.contains("GetGoogleIdOption.Builder("))
    }

    @Test
    fun `Google button clears stale credential state and retries once after account reauth failure`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("ClearCredentialStateRequest"))
        assertTrue(auth.contains("credentialManager.clearCredentialState"))
        assertTrue(auth.contains("isGoogleAccountReauthFailure"))
        assertTrue(auth.contains("retryAfterCredentialReset"))
        assertTrue(auth.contains("retryAfterCredentialReset = false"))
        assertTrue(auth.contains("Account reauth failed"))
        assertTrue(auth.contains("[16]"))
    }

    @Test
    fun `forgot password button sends a Supabase password reset email`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("auth.resetPasswordForEmail("))
        assertTrue(auth.contains("Bitte gib eine gültige E-Mail-Adresse ein."))
        assertTrue(auth.contains("Passwort-Reset-Link wurde gesendet"))
        assertFalse(auth.contains("TODO: Forgot Password"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
