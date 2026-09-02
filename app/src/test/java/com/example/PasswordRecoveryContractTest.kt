package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordRecoveryContractTest {

    @Test
    fun `forgot password completes recovery inside the app`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val supabase = source("app/src/main/java/com/example/data/SupabaseClient.kt")
        val manifest = source("app/src/main/AndroidManifest.xml")

        assertTrue(auth.contains("SupabaseConfig.client.auth.resetPasswordForEmail(currentEmail)"))
        assertTrue(supabase.contains("PASSWORD_RECOVERY_REDIRECT_URL"))
        assertTrue(supabase.contains("scheme = AUTH_DEEP_LINK_SCHEME"))
        assertTrue(supabase.contains("host = AUTH_DEEP_LINK_HOST"))
        assertTrue(manifest.contains(".AuthDeepLinkActivity"))
        assertTrue(manifest.contains("android:scheme=\"com.aistudio.harmony.couples.xqvz\""))
        assertTrue(manifest.contains("android:host=\"auth-callback\""))

        val recovery = source("app/src/main/java/com/example/AuthDeepLinkActivity.kt")
        assertTrue(recovery.contains("handleDeeplinks"))
        assertTrue(recovery.contains("linkType == \"recovery\""))
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
