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
        assertTrue(auth.contains("performResilientGoogleSignIn"))
        assertTrue(auth.contains("auth.signInWith(EmailProvider)"))
        assertTrue(auth.contains("auth.signUpWith(EmailProvider)"))
        assertTrue(auth.contains("App im Demo-Modus testen"))
        assertTrue(auth.contains("onAuthSuccess()"))
    }

    @Test
    fun `explicit Google button uses dedicated Google button credential flow`() {
        val googleAuth = source("app/src/main/java/com/example/ui/screens/GoogleAuthRecovery.kt")

        assertTrue(googleAuth.contains("GetSignInWithGoogleOption.Builder("))
        assertFalse(googleAuth.contains("GetGoogleIdOption.Builder("))
    }

    @Test
    fun `Google button recovers from account reauth failure and has OAuth fallback`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val googleAuth = source("app/src/main/java/com/example/ui/screens/GoogleAuthRecovery.kt")

        assertTrue(auth.contains("performResilientGoogleSignIn"))
        assertTrue(googleAuth.contains("ClearCredentialStateRequest"))
        assertTrue(googleAuth.contains("credentialManager.clearCredentialState"))
        assertTrue(googleAuth.contains("isGoogleAccountReauthFailure"))
        assertTrue(googleAuth.contains("retryAfterCredentialReset"))
        assertTrue(googleAuth.contains("retryAfterCredentialReset = false"))
        assertTrue(googleAuth.contains("Account reauth failed"))
        assertTrue(googleAuth.contains("[16]"))
        assertTrue(googleAuth.contains("SupabaseConfig.client.auth.signInWith(Google)"))
        assertTrue(googleAuth.contains("GoogleSignInOutcome.OAUTH_REDIRECT_STARTED"))
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
