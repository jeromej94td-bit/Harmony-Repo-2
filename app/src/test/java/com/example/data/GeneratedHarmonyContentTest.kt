package com.example.data

import org.junit.Assert.assertEquals
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
}
