package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthSessionRestorationContractTest {

    @Test
    fun `auth screen restores an existing supabase session`() {
        val auth = source("app/src/main/java/com/example/ui/screens/AuthScreen.kt")

        assertTrue(auth.contains("LaunchedEffect(Unit)"))
        assertTrue(auth.contains("SupabaseConfig.client.auth.currentSessionOrNull()"))
        assertTrue(auth.contains("if (existingSession != null)"))
        assertTrue(auth.contains("onAuthSuccess()"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
