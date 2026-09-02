package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PairedPackResultsRoutingContractTest {
    @Test
    fun `completed paired pack routes to secure couple reveal`() {
        val source = source("app/src/main/java/com/example/ui/screens/PackResultsScreen.kt")
        assertTrue(source.contains("SessionPhase.READY"))
        assertTrue(source.contains("session.isPaired"))
        assertTrue(source.contains("CouplePackRevealScreen("))
        assertTrue(source.contains("answers.associate { it.questionIndex to it.answerText }"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
