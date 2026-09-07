package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAuthRegressionContractTest {
    @Test
    fun `google button stays on native credential manager id token flow`() {
        val authScreen = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val coordinator = source("app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt")

        assertTrue(authScreen.contains("performHarmonyGoogleSignIn("))
        assertFalse(authScreen.contains("auth.signInWith(Google)"))
        assertFalse(authScreen.contains("OAUTH_REDIRECT_STARTED"))

        assertTrue(coordinator.contains("GetGoogleIdOption"))
        assertTrue(coordinator.contains("GoogleIdTokenCredential"))
        assertTrue(coordinator.contains("generateRawNonce"))
        assertTrue(coordinator.contains("setNonce(GoogleNativeAuthConfig.sha256Hex(rawNonce))"))
        assertTrue(coordinator.contains("signInWith(IDToken)"))
        assertTrue(coordinator.contains("nonce = rawNonce"))
        assertTrue(coordinator.contains("clearCredentialState"))
        assertFalse(coordinator.contains("signInWith(Google)"))
        assertFalse(coordinator.contains("OAUTH_REDIRECT_STARTED"))
        assertFalse(coordinator.contains("signInAnonymously"))
        assertFalse(coordinator.contains("/auth/v1/signup"))
    }

    @Test
    fun `native google failure never silently switches to browser oauth`() {
        val coordinator = source("app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt")

        assertFalse(coordinator.contains("startGoogleOAuthFallback"))
        assertFalse(coordinator.contains("using OAuth fallback"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
