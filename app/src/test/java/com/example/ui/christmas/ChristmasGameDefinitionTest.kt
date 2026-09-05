package com.example.ui.christmas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChristmasGameDefinitionTest {
    @Test
    fun `four parts contain exactly sixty unique rounds`() {
        assertEquals(4, ChristmasGameDefinition.parts.size)
        assertTrue(ChristmasGameDefinition.parts.all { it.rounds.size == 15 })
        assertEquals(60, ChristmasGameDefinition.allRounds.size)
        assertEquals(60, ChristmasGameDefinition.allRounds.map { it.id }.distinct().size)
    }

    @Test
    fun `every visual round has four choices`() {
        assertTrue(ChristmasGameDefinition.allRounds.all { it.options.size == 4 })
    }

    @Test
    fun `harry potter is part of the film round`() {
        assertTrue(
            ChristmasGameDefinition.parts[2].rounds
                .flatMap { it.options }
                .any { it.label.contains("Harry Potter") }
        )
    }
}
