package com.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeepTalkInputSpacingContractTest {

    @Test
    fun `text input and voice button use horizontal spacing inside their row`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenDeepTalk.kt").readText()
        val answerPane = source.substringAfter("private fun DeepTalkAnswerPane(")
            .substringBefore("private fun DeepTalkRevealCard(")

        assertTrue(answerPane.contains("Spacer(Modifier.width(8.dp))"))
        assertFalse(answerPane.contains("Spacer(Modifier.height(8.dp))"))
    }
}
