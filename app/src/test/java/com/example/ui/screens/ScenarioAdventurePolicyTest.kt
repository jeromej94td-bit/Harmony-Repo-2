package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScenarioAdventurePolicyTest {

    private val packA = ScenarioAdventurePackRef(
        packId = "a",
        chapters = listOf(
            ScenarioAdventureChapterRef("Kapitel A1", listOf("A", "B", "C", "D")),
            ScenarioAdventureChapterRef("Kapitel A2", listOf("E", "F", "G", "H")),
            ScenarioAdventureChapterRef("Kapitel A3", listOf("I", "J", "K", "L"))
        )
    )

    private val packB = ScenarioAdventurePackRef(
        packId = "b",
        chapters = listOf(
            ScenarioAdventureChapterRef("Kapitel B1", listOf("M", "N", "O", "P")),
            ScenarioAdventureChapterRef("Kapitel B2", listOf("Q", "R", "S", "T"))
        )
    )

    @Test
    fun `resolves an exact current chapter to one unique pack`() {
        val result = resolveScenarioAdventureLocation(
            packs = listOf(packA, packB),
            question = "  Kapitel B2 ",
            options = listOf("Q", "R", "S", "T")
        )

        assertEquals("b", result?.pack?.packId)
        assertEquals(1, result?.chapterIndex)
        assertEquals(2, result?.chapterCount)
    }

    @Test
    fun `does not guess when two packs contain the same chapter`() {
        val duplicate = ScenarioAdventurePackRef(
            packId = "duplicate",
            chapters = listOf(packA.chapters.first())
        )

        val result = resolveScenarioAdventureLocation(
            packs = listOf(packA, duplicate),
            question = "Kapitel A1",
            options = listOf("A", "B", "C", "D")
        )

        assertNull(result)
    }

    @Test
    fun `reconstructs route from persisted answers in chapter order`() {
        val route = scenarioRouteIndexes(
            chapters = packA.chapters,
            answers = mapOf(
                2 to "J",
                0 to "C"
            )
        )

        assertEquals(listOf(2, 1), route)
    }

    @Test
    fun `ignores persisted answers that are not options of their chapter`() {
        val route = scenarioRouteIndexes(
            chapters = packA.chapters,
            answers = mapOf(
                0 to "unbekannt",
                1 to "H"
            )
        )

        assertEquals(listOf(3), route)
        assertNull(scenarioChoiceIndex(packA.chapters.first().options, "unbekannt"))
    }

    @Test
    fun `dominant style includes pending final choice`() {
        val previousRoute = listOf(1, 0, 1)
        val withPendingFinal = previousRoute + 1

        assertEquals(1, scenarioDominantStyle(withPendingFinal))
    }

    @Test
    fun `dominant style is deterministic on ties`() {
        assertEquals(0, scenarioDominantStyle(listOf(0, 1, 2, 3)))
        assertNull(scenarioDominantStyle(emptyList()))
    }
}
