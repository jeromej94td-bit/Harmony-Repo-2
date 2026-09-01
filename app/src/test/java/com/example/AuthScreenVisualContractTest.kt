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

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
