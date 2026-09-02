package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class CoupleRevealIntegrationContractTest {
    @Test
    fun `main quiz syncs paired answers and uses secure reveal screen`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        assertTrue(source.contains("CoupleQuestionRepository"))
        assertTrue(source.contains("coupleQuestionRepository.submitAnswer("))
        assertTrue(source.contains("CouplePackRevealScreen("))
        assertTrue(source.contains("!isDemoMode && appSession.isPaired"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
