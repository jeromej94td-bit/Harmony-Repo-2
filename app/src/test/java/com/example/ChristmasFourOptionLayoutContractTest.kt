package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ChristmasFourOptionLayoutContractTest {

    @Test
    fun `christmas round content uses remaining height and distributes prompt and answers`() {
        val source = File("src/main/java/com/example/ui/christmas/ChristmasExperienceScreen.kt").readText()
        val roundPlayer = source.substringAfter("private fun ChristmasRoundPlayer(")
            .substringBefore("private fun ChristmasOptionCard(")

        assertTrue(roundPlayer.contains("modifier = Modifier.weight(1f).fillMaxWidth()"))
        assertTrue(roundPlayer.contains("verticalArrangement = Arrangement.SpaceEvenly"))
        assertTrue(roundPlayer.contains("Modifier.fillMaxSize()"))
    }
}
