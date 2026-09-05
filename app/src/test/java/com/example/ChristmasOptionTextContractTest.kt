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
        assertTrue(optionCard.contains("Spacer(Modifier.width(if (compact) 11.dp else 7.dp))"))
        assertTrue(optionCard.contains("modifier = Modifier.weight(1f)"))
    }
}
