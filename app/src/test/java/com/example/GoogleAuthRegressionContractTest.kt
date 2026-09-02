package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAuthRegressionContractTest {
    @Test
    fun `auth screen cannot bypass resilient google coordinator`() {
        val authScreen = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val recovery = source("app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt")

        assertTrue(authScreen.contains("performHarmonyGoogleSignIn("))
        assertFalse(
            "AuthScreen must never route the Google button directly through OAuth again",
            authScreen.contains("auth.signInWith(Google)") ||
                authScreen.contains("client.auth.signInWith(Google)")
        )

        assertTrue(recovery.contains("GetSignInWithGoogleOption"))
        assertTrue(recovery.contains("GoogleIdTokenCredential"))
        assertTrue(recovery.contains("clearCredentialState"))
        assertTrue(recovery.contains("Account reauth failed"))
        assertTrue(recovery.contains("signInWith(IDToken)"))
        assertTrue(recovery.contains("signInWith(Google)"))
        assertFalse(recovery.contains("signInAnonymously"))
        assertFalse(recovery.contains("/auth/v1/signup"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
