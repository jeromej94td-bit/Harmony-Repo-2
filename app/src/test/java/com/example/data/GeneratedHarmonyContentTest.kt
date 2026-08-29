package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedHarmonyContentTest {

    @Test
    fun `fantasy artifact temptation question exposes four answer options`() {
        val pack = GeneratedHarmonyContent.generatedPacks.single { it.id == "discovers_universe" }
        val artifactQuestion = pack.questions.single {
            it.question.startsWith("Ihr findet ein Artefakt")
        }

        assertEquals(4, artifactQuestion.options.size)
        assertTrue(artifactQuestion.options.all { it.isNotBlank() })
    }
}
