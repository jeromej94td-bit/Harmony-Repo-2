package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileAccountEmailContractTest {

    @Test
    fun `profile shows current supabase account email and demo fallback`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")

        assertTrue(profile.contains("SupabaseConfig.client.auth.currentSessionOrNull()?.user?.email"))
        assertTrue(profile.contains("Angemeldet als"))
        assertTrue(profile.contains("Demo-Modus"))
        assertTrue(profile.contains("Kein Benutzerkonto angemeldet"))
        assertTrue(profile.contains("account_email"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
