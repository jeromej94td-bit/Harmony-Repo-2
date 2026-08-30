package com.example.ui.screens

import com.example.data.model.Question
import com.example.data.model.QuestionPack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StableSpecialQuestionRoutingTest {

    private fun pack(id: String, questions: List<Question>) = QuestionPack(
        id = id,
        title = id,
        tags = listOf("unterhaltung"),
        cat = "reden",
        topic = "beziehung",
        type = "quiz",
        questions = questions
    )

    @Test
    fun `egg mechanic follows its actual prompt after reorder`() {
        val source = pack(
            "essenreden",
            listOf(
                Question("Wie möchtest du dein Ei am liebsten?", listOf("4 Minuten", "8 Minuten")),
                Question("Andere Frage", listOf("A", "B")),
                Question("Noch eine Frage", listOf("A", "B")),
                Question("Keine Eierfrage", listOf("A", "B"))
            )
        )

        assertEquals(HarmonyImageChoiceKind.EGG, harmonyImageChoiceKind(source, 0))
        assertNull(harmonyImageChoiceKind(source, 3))
    }

    @Test
    fun `steak mechanic follows its actual prompt after reorder`() {
        val source = pack(
            "essenreden",
            listOf(
                Question("Andere Frage", listOf("A", "B")),
                Question("Wie willst du dein Steak?", listOf("Rare", "Medium"))
            )
        )

        assertEquals(HarmonyImageChoiceKind.STEAK, harmonyImageChoiceKind(source, 1))
    }

    @Test
    fun `travel mechanic follows its actual prompt after reorder`() {
        val source = pack(
            "reisevor",
            listOf(
                Question("Wie sieht deine Traumreise aus?", listOf("Strand", "Roadtrip")),
                Question("Andere Frage", listOf("A", "B"))
            )
        )

        assertEquals(HarmonyImageChoiceKind.TRAVEL, harmonyImageChoiceKind(source, 0))
    }

    @Test
    fun `happy couple mechanic only follows its exact visual prompt after reorder`() {
        val source = pack(
            "liebegleichgewicht",
            listOf(
                Question("Andere Frage", listOf("A", "B")),
                Question("Welches Paar ist GLÜCKLICH?", listOf("1", "2", "3", "4")),
                Question("Was fällt dir in unserer Beziehung leichter?", listOf("Geben", "Nehmen"))
            )
        )

        assertEquals(HarmonyImageChoiceKind.HAPPY_COUPLE, harmonyImageChoiceKind(source, 1))
        assertNull(harmonyImageChoiceKind(source, 0))
        assertNull(harmonyImageChoiceKind(source, 2))
    }
}
