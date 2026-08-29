package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedHarmonyContentTest {

    @Test
    fun `registry repairs missing options in magic academy never-have-i-ever question`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "cj_hogwarts_quiz" }
        val finalQuestion = pack.questions.last()

        assertEquals(
            "Ich habe noch nie behauptet, das vermeintlich düstere Haus sei eigentlich gar nicht so böse.",
            finalQuestion.q
        )
        assertEquals(listOf("Habe ich", "Habe ich noch nie"), finalQuestion.options)
    }

    @Test
    fun `magic academy pack is user-facing franchise neutral`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "cj_hogwarts_quiz" }
        val visibleText = buildString {
            append(pack.title).append('\n')
            append(pack.tags.joinToString(" ")).append('\n')
            pack.questions.forEach { append(it.q).append('\n') }
        }
        val forbiddenReferences = listOf(
            "Hogwarts",
            "Harry Potter",
            "Harry-Potter",
            "Quidditch",
            "Muggel",
            "Slytherin",
            "harrypotter"
        )

        assertEquals("Magische Akademie: Welches Haus passt zu dir?", pack.title)
        assertTrue("fantasy_magie" in pack.tags)
        assertFalse(forbiddenReferences.any { it in visibleText })
    }

    @Test
    fun `animation pack is user-facing franchise neutral`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "cj_disney_quiz" }
        val visibleText = buildString {
            append(pack.title).append('\n')
            append(pack.tags.joinToString(" ")).append('\n')
            pack.questions.forEach { append(it.q).append('\n') }
        }
        val forbiddenReferences = listOf(
            "Disney",
            "disney",
            "König der Löwen",
            "Schneewittchen",
            "Disneyland",
            "Yoda",
            "Star Wars",
            "Marvel",
            "Oben",
            "Toy Story",
            "Elsa"
        )

        assertEquals("Animationswelten, Abenteuer und Feenstaub", pack.title)
        assertTrue("animation_fantasy" in pack.tags)
        assertFalse(forbiddenReferences.any { it in visibleText })
    }

    @Test
    fun `entertainment pack avoids direct streaming and award brands`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "cj_entertainment_quiz" }
        val visibleText = pack.questions.joinToString("\n") { it.q }

        assertFalse("Netflix" in visibleText)
        assertFalse("Oscar" in visibleText)
        assertTrue("Streaming-Passwort" in visibleText)
        assertTrue("große Filmpreis-Verleihung" in visibleText)
    }

    @Test
    fun `all registered never-have-i-ever questions expose answer options`() {
        val packs = GeneratedContentRegistry.PACKS.filter { "ichhabenochnie" in it.tags }

        assertTrue(packs.isNotEmpty())
        assertTrue(packs.flatMap { it.questions }.all { it.options.isNotEmpty() })
    }
}
