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
        assertTrue(auth.contains("performHarmonyGoogleSignIn(context)"))
        assertTrue(auth.contains("auth.signInWith(EmailProvider)"))
        assertTrue(auth.contains("auth.signUpWith(EmailProvider)"))
        assertTrue(auth.contains("App im Demo-Modus testen"))
        assertTrue(auth.contains("onDemoRequested: () -> Unit"))
    }

    @Test
    fun `Google button stays native and never falls back to browser OAuth`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val coordinator = source("app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt")
        assertTrue(auth.contains("performHarmonyGoogleSignIn(context)"))
        assertFalse(auth.contains("SupabaseConfig.client.auth.signInWith(Google)"))
        assertTrue(coordinator.contains("GetGoogleIdOption"))
        assertTrue(coordinator.contains("signInWith(IDToken)"))
        assertTrue(coordinator.contains("nonce = rawNonce"))
        assertFalse(coordinator.contains("signInWith(Google)"))
    }

    @Test
    fun `demo button uses its dedicated callback`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        assertTrue(auth.contains("onDemoRequested: () -> Unit = onAuthSuccess"))
        assertTrue(auth.contains("onClick = onDemoRequested"))
    }

    @Test
    fun `forgot password button sends a Supabase password reset email`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        assertTrue(auth.contains("auth.resetPasswordForEmail("))
        assertTrue(auth.contains("redirectUrl = SupabaseConfig.PASSWORD_RECOVERY_REDIRECT_URL"))
        assertTrue(auth.contains("Bitte gib eine gültige E-Mail-Adresse ein."))
        assertTrue(auth.contains("Passwort-Code wurde gesendet. Bitte prüfe dein Postfach."))
        assertFalse(auth.contains("TODO: Forgot Password"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
