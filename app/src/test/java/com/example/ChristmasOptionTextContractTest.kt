package com.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ChristmasOptionTextContractTest {

    @Test
    fun `christmas option cards do not truncate answer labels`() {
        val source = File("src/main/java/com/example/ui/christmas/ChristmasExperienceScreen.kt").readText()
        val optionCard = source.substringAfter("private fun ChristmasOptionCard(")
            .substringBefore("private fun ChristmasOptionArt(")

        assertFalse(optionCard.contains("TextOverflow.Ellipsis"))
        assertFalse(optionCard.contains("maxLines = 2"))
        assertTrue(optionCard.contains("if (compact)"))
        assertTrue(optionCard.contains("Column("))
    }
}
