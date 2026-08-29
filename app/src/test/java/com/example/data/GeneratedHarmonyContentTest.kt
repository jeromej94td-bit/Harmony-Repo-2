package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedHarmonyContentTest {

    @Test
    fun `registry repairs missing options in hogwarts never-have-i-ever question`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "cj_hogwarts_quiz" }
        val malformedQuestion = pack.questions.single {
            it.q == "Ich habe noch nie behauptet, Slytherin sei eigentlich gar nicht so böse."
        }

        assertEquals(listOf("Habe ich", "Habe ich noch nie"), malformedQuestion.options)
    }

    @Test
    fun `all registered never-have-i-ever questions expose answer options`() {
        val packs = GeneratedContentRegistry.PACKS.filter { "ichhabenochnie" in it.tags }

        assertTrue(packs.isNotEmpty())
        assertTrue(packs.flatMap { it.questions }.all { it.options.isNotEmpty() })
    }
}
