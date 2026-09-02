package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthScreenV2RoutingContractTest {

    private fun source(pathFromRepoRoot: String): String {
        val candidates = listOf(
            File(pathFromRepoRoot),
            File("../$pathFromRepoRoot")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertTrue("Expected source file to exist: $pathFromRepoRoot", file != null)
        return file!!.readText()
    }

    @Test
    fun `signed out app routes through fresh auth v2 screen`() {
        val mainActivity = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(mainActivity.contains("import com.example.ui.screens.AuthScreenV2"))
        assertTrue(mainActivity.contains("AuthScreenV2("))
        assertFalse(mainActivity.contains("import com.example.ui.screens.AuthScreen\n"))
    }

    @Test
    fun `forgot password opens code mask before recovery email request`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreenV2.kt")

        assertTrue(auth.contains("fun AuthScreenV2("))
        assertTrue(auth.contains("Passwort-Code"))
        assertTrue(auth.contains("Neues Passwort"))
        assertTrue(auth.contains("Passwort wiederholen"))
        assertTrue(auth.contains("OtpType.Email.RECOVERY"))
        assertTrue(auth.contains("verifyEmailOtp"))
        assertTrue(auth.contains("updateUser"))

        val openMask = auth.indexOf("recoveryMode = true")
        val sendEmail = auth.indexOf("resetPasswordForEmail")
        assertTrue("Recovery mask must open before the mail request starts", openMask >= 0 && sendEmail >= 0 && openMask < sendEmail)
    }
}
