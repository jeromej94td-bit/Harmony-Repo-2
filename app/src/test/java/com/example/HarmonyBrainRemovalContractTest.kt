package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HarmonyBrainRemovalContractTest {
    @Test
    fun `production source contains no Harmony Brain runtime or UI`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val vm = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        val db = source("app/src/main/java/com/example/data/db/AppDatabase.kt")

        assertFalse(main.contains("attachAutoGeneration"))
        assertFalse(main.contains("isBrainChatMode"))
        assertFalse(vm.contains("SupabaseHarmonyBrainGateway"))
        assertFalse(vm.contains("ForegroundGameGenerator"))
        assertFalse(vm.contains("brainRepository"))
        assertFalse(home.contains("Harmony Brain"))
        assertFalse(games.contains("generatedGames"))
        assertFalse(chat.contains("BrainMessage"))
        assertFalse(chat.contains("Harmony Brain"))
        assertTrue(db.contains("version = 10"))
        assertFalse(db.contains("abstract fun brainDao"))
        assertFalse(db.contains("abstract fun brainRoomDao"))

        assertFalse(exists("app/src/main/java/com/example/data/brain"))
        assertFalse(exists("app/src/main/java/com/example/ui/screens/DevBrainTab.kt"))
        assertFalse(exists("app/src/main/java/com/example/data/model/HarmonyBrainModels.kt"))
        assertTrue(exists("app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()

    private fun exists(path: String): Boolean =
        listOf(File(path.removePrefix("app/")), File(path)).any(File::exists)
}
