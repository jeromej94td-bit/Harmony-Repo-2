package com.example.ui

import com.example.data.model.AnswerEntity
import com.example.ui.screens.answerCountsByPack
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerProgressIndexTest {

    @Test
    fun `answer counts are indexed once per pack`() {
        val answers = listOf(
            AnswerEntity("pack_a", 0, "A"),
            AnswerEntity("pack_a", 1, "B"),
            AnswerEntity("pack_b", 0, "C")
        )

        assertEquals(mapOf("pack_a" to 2, "pack_b" to 1), answerCountsByPack(answers))
    }

    @Test
    fun `games and pack list use the cached answer index`() {
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        val packs = source("app/src/main/java/com/example/ui/screens/PackListScreen.kt")

        assertTrue(games.contains("remember(answers) { answerCountsByPack(answers) }"))
        assertTrue(packs.contains("remember(answers) { answerCountsByPack(answers) }"))
        assertFalse(games.contains("answers.count { it.packId == pack.id }"))
        assertFalse(packs.contains("answers.count { it.packId == pack.id }"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
