package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordRecoveryContractTest {

    @Test
    fun `forgot password completes recovery inside the app`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val supabase = source("app/src/main/java/com/example/data/SupabaseClient.kt")
        val manifest = source("app/src/main/AndroidManifest.xml")

        assertTrue(auth.contains("resetPasswordForEmail("))
        assertTrue(auth.contains("email = currentEmail"))
        assertTrue(auth.contains("redirectUrl = SupabaseConfig.PASSWORD_RECOVERY_REDIRECT_URL"))
        assertFalse(auth.contains("resetPasswordForEmail(currentEmail)"))

        // Recovery emails currently contain a numeric code. The login screen must
        // immediately switch into a dedicated code + new-password step after sending it.
        assertTrue(auth.contains("recoveryMode"))
        assertTrue(auth.contains("Passwort-Code"))
        assertTrue(auth.contains("verifyEmailOtp("))
        assertTrue(auth.contains("OtpType.Email.RECOVERY"))
        assertTrue(auth.contains("token = recoveryCode.trim()"))
        assertTrue(auth.contains("auth.updateUser"))
        assertTrue(auth.contains("password = recoveryNewPassword"))
        assertTrue(auth.contains("recoveryNewPassword != recoveryConfirmPassword"))
        assertTrue(auth.contains("Code erneut senden"))

        assertTrue(supabase.contains("AUTH_DEEP_LINK_SCHEME = \"harmony\""))
        assertTrue(supabase.contains("AUTH_DEEP_LINK_HOST = \"auth\""))
        assertTrue(supabase.contains("AUTH_DEEP_LINK_PATH = \"/callback\""))
        assertTrue(supabase.contains("\"harmony://auth/callback\""))
        assertTrue(supabase.contains("scheme = AUTH_DEEP_LINK_SCHEME"))
        assertTrue(supabase.contains("host = AUTH_DEEP_LINK_HOST"))
        assertTrue(supabase.contains("defaultRedirectUrl = PASSWORD_RECOVERY_REDIRECT_URL"))

        assertTrue(manifest.contains(".AuthDeepLinkActivity"))
        assertTrue(manifest.contains("android:launchMode=\"singleTask\""))
        assertTrue(manifest.contains("android:scheme=\"harmony\""))
        assertTrue(manifest.contains("android:host=\"auth\""))
        assertTrue(manifest.contains("android:path=\"/callback\""))
        assertFalse(manifest.contains("android:scheme=\"com.aistudio.harmony.couples.xqvz\""))

        val recovery = source("app/src/main/java/com/example/AuthDeepLinkActivity.kt")
        assertTrue(recovery.contains("handleDeeplinks"))
        assertTrue(recovery.contains("linkType == \"recovery\""))
        assertTrue(recovery.contains("override fun onNewIntent(intent: Intent)"))
        assertTrue(recovery.contains("recoveryFlowActive"))
        assertTrue(recovery.contains("isConsumedRecoveryError(intent)"))
        assertTrue(recovery.contains("error_code"))
        assertTrue(recovery.contains("otp_expired"))
        assertTrue(recovery.contains("auth.updateUser"))
        assertTrue(recovery.contains("password = newPassword"))
        assertTrue(recovery.contains("newPassword != confirmPassword"))
        assertTrue(recovery.contains("auth.signOut()"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
