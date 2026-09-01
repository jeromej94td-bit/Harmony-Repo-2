package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrainFreezeContractTest {
    @Test
    fun `production app does not start or expose harmony brain`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val vm = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        val brainAuth = source("app/src/main/java/com/example/data/brain/gateway/SupabaseBrainAuthSession.kt")

        assertFalse(main.contains("attachAutoGeneration(this)"))
        assertTrue(vm.contains("private const val HARMONY_BRAIN_ENABLED = false"))
        assertTrue(vm.contains("if (HARMONY_BRAIN_ENABLED)"))
        assertTrue(vm.contains("if (!HARMONY_BRAIN_ENABLED) return"))
        assertTrue(home.contains("brainEnabled: Boolean = false"))
        assertTrue(games.contains("brainEnabled: Boolean = false"))
        assertTrue(chat.contains("brainEnabled: Boolean = false"))
        assertFalse(brainAuth.contains("/auth/v1/signup"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
