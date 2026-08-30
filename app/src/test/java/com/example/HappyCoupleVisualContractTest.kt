package com.example

import java.io.File
import java.security.MessageDigest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HappyCoupleVisualContractTest {

    @Test
    fun `happy couple artwork is the approved clean number free set`() {
        val expected = mapOf(
            "happy_couple_01.webp" to "6452ec90f22e67c6929b824a8757102dd79e406a92dbcf90c52bd54352616d68",
            "happy_couple_02.webp" to "4f9d09682b5576e497029937144efbc7575b38a9341ea130612aae77197ef8eb",
            "happy_couple_03.webp" to "a5592ec92448daa6fc22db49652a90de4017015f23498bfa632144f510c3f242",
            "happy_couple_04.webp" to "787ce64a6933dbca6e52d9c95a0a464ebd4c284db64fb3108525a5e16904133d"
        )

        expected.forEach { (fileName, expectedSha) ->
            val file = projectFile("app/src/main/res/drawable-nodpi/$fileName")
            assertTrue("Missing Happy Couple asset $fileName", file.exists())
            assertTrue(
                "$fileName must match the approved clean artwork without baked-in number badges.",
                sha256(file) == expectedSha
            )
        }
    }

    @Test
    fun `happy couple renderer does not draw option numbers and gives cards more room`() {
        val source = source("app/src/main/java/com/example/ui/screens/HarmonyHappyCoupleQuestion.kt")

        assertFalse(
            "Happy Couple cards must not draw numeric badges over the artwork.",
            source.contains("text = (index + 1).toString()")
        )
        assertTrue(
            "Happy Couple cards should use the larger visual card ratio.",
            source.contains(".aspectRatio(0.88f)")
        )
        assertTrue(
            "Happy Couple heading should be compact so the images receive more vertical space.",
            source.contains("fontSize = 24.sp") && source.contains("lineHeight = 27.sp")
        )
    }

    @Test
    fun `happy couple first question hides the category chip`() {
        val source = source("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")
            .replace(Regex("\\s+"), " ")

        assertTrue(
            "The first Love Balance image question must hide CategoryTag to leave more room for the artwork.",
            source.contains("if (!isHappyCoupleQuestion) { CategoryTag(")
        )
    }

    @Test
    fun `happy couple first question hides the floating skip button only there`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
            .replace(Regex("\\s+"), " ")

        assertTrue(
            "The skip affordance must be suppressed for Love Balance question 0 only.",
            source.contains("!(activeRun.pack.id == LoveBalanceQuestionPolicy.PACK_ID && activeRun.currentIndex == 0)")
        )
    }

    private fun sha256(file: File): String = MessageDigest.getInstance("SHA-256")
        .digest(file.readBytes())
        .joinToString("") { "%02x".format(it) }

    private fun source(path: String): String = projectFile(path).readText()

    private fun projectFile(path: String): File {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path)
        )
        return candidates.firstOrNull(File::exists)
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
