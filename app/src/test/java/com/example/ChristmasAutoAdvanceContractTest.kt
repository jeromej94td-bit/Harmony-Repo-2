package com.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ChristmasAutoAdvanceContractTest {

    @Test
    fun `choosing a christmas answer advances without a second confirmation button`() {
        val source = File("src/main/java/com/example/ui/christmas/ChristmasExperienceScreen.kt").readText()
        val roundPlayer = source.substringAfter("private fun ChristmasRoundPlayer(")
            .substringBefore("private fun ChristmasOptionCard(")

        assertTrue(roundPlayer.contains("fun advanceAfterSelection(option: ChristmasOption)"))
        assertTrue(roundPlayer.contains("if (roundIndex == part.rounds.lastIndex) onComplete() else roundIndex++"))
        assertTrue(roundPlayer.split("advanceAfterSelection(option)").size - 1 >= 3)
        assertFalse(roundPlayer.contains("christmas_next_round"))
        assertFalse(roundPlayer.contains("\"Nächste Runde\""))
    }
}
