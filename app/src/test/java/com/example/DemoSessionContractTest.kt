package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoSessionContractTest {
    @Test
    fun `demo access is local and never creates an anonymous supabase account`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")
        val sessionVm = source("app/src/main/java/com/example/ui/session/AppSessionViewModel.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(auth.contains("onDemoRequested"))
        assertTrue(sessionVm.contains("SessionPhase.DEMO"))
        assertTrue(sessionVm.contains("fun enterDemo()"))
        assertTrue(main.contains("sessionViewModel.enterDemo()"))
        assertFalse(sessionVm.contains("signInAnonymously"))
        assertFalse(auth.contains("signInAnonymously"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
