package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ChristmasBottomSafeAreaContractTest {

    @Test
    fun `christmas round keeps next button above system navigation`() {
        val source = File("src/main/java/com/example/ui/christmas/ChristmasExperienceScreen.kt").readText()
        val roundPlayer = source.substringAfter("private fun ChristmasRoundPlayer(")
            .substringBefore("private fun ChristmasOptionCard(")

        assertTrue(source.contains("import androidx.compose.foundation.layout.navigationBarsPadding"))
        assertTrue(roundPlayer.contains(".navigationBarsPadding()"))
    }
}
