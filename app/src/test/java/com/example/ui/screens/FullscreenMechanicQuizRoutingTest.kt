package com.example.ui.screens

import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.QuestionInteractionSpec
import com.example.data.model.QuestionResponseKind
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FullscreenMechanicQuizRoutingTest {

    @Test
    fun `every fullscreen mechanic owns its answers and standard quiz options stay hidden`() {
        FullscreenGameMechanicKind.entries.forEach { kind ->
            val spec = QuestionInteractionSpec(
                responseKind = QuestionResponseKind.FIXED_CHOICE,
                fullscreenMechanic = kind
            )

            val visibleOptions = QuizAnswerOptionsPolicy.build(
                options = listOf("Herz", "Kopf"),
                spec = spec,
                customTextLabel = "Eigene Antwort",
                skipLabel = "Überspringen"
            )

            assertTrue("Generic options leaked for $kind", visibleOptions.isEmpty())
        }
    }

    @Test
    fun `standard questions keep their normal answer options`() {
        val spec = QuestionInteractionSpec(
            responseKind = QuestionResponseKind.FIXED_CHOICE
        )

        assertEquals(
            listOf("A", "B"),
            QuizAnswerOptionsPolicy.build(
                options = listOf("A", "B"),
                spec = spec,
                customTextLabel = "Eigene Antwort",
                skipLabel = "Überspringen"
            )
        )
    }

    @Test
    fun `quiz runner routes fullscreen mechanics before the standard scrolling question stack`() {
        val source = source("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")

        assertTrue(source.contains("interactionSpec?.fullscreenMechanic"))
        assertTrue(source.contains("imageChoiceKind == null"))
        assertTrue(source.contains("FullscreenQuestionMechanicBoard("))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
