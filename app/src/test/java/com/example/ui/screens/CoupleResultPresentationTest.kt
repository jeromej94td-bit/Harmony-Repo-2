package com.example.ui.screens

import com.example.R
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.PersonAssignmentCodec
import com.example.data.model.PersonSide
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.MemoryMatchAnswerCodec
import com.example.data.model.PredictionAnswerCodec
import com.example.data.model.Question
import com.example.data.model.SecretPlanAnswerCodec
import com.example.data.model.WeekendEchoAnswerCodec
import com.example.data.model.QuestionPack
import com.example.data.model.RankingAnswerCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CoupleResultPresentationTest {

    @Test
    fun `ranking storage answer becomes readable ordered result`() {
        val pack = pack(
            id = "rank-test",
            question = Question("Ordne", listOf("A", "B", "C"))
        )
        val result = coupleResultPresentation(
            pack,
            0,
            RankingAnswerCodec.encode(listOf("B", "A", "C"))
        )

        assertEquals("Ranking", result.displayText)
        assertEquals(listOf("1. B", "2. A", "3. C"), result.detailLines)
        assertTrue(result.structured)
    }

    @Test
    fun `prediction and paired codecs never leak their storage prefixes`() {
        val pack = pack(id = "prediction-test", question = Question("Frage", listOf("A", "B")))

        val prediction = coupleResultPresentation(pack, 0, PredictionAnswerCodec.encode("A", "B"))
        assertEquals("Vermutung: A", prediction.displayText)
        assertEquals(listOf("Tatsächlich: B", "Anders eingeschätzt"), prediction.detailLines)

        val paired = coupleResultPresentation(pack, 0, PairedChoiceAnswerCodec.encode("Erste", "Zweite"))
        assertEquals("Erste", paired.displayText)
        assertEquals(listOf("Zweite Antwort: Zweite"), paired.detailLines)
    }

    @Test
    fun `person assignment result names every assigned role`() {
        val options = listOf("Planung", "Ideen")
        val pack = pack(id = "roles-test", question = Question("Wer macht was", options))
        val answer = PersonAssignmentCodec.encode(
            options,
            linkedMapOf(
                "Planung" to PersonSide.USER,
                "Ideen" to PersonSide.PARTNER
            )
        )

        val result = coupleResultPresentation(pack, 0, answer)
        assertEquals("Rollenverteilung", result.displayText)
        assertEquals(listOf("Planung → Ich", "Ideen → Partner"), result.detailLines)
    }

    @Test
    fun `either or and drawing storage values become customer readable`() {
        val pack = pack(id = "readable-test", question = Question("Frage", listOf("A", "B")))
        val eitherOr = coupleResultPresentation(pack, 0, EitherOrAnswerCodec.encode("A", "B"))
        assertEquals("A", eitherOr.displayText)
        assertEquals(listOf("Partner: B", "Unterschiedliche Wahl"), eitherOr.detailLines)

        val drawing = coupleResultPresentation(
            QuestionPack(
                id = "zeichnen-test",
                title = "Zeichnen",
                tags = emptyList(),
                cat = "zeich",
                topic = "hobbys",
                type = "draw",
                questions = listOf(Question("Male etwas", emptyList()))
            ),
            0,
            "DRAWING_COMPLETED"
        )
        assertEquals("Zeichnung abgeschlossen", drawing.displayText)
    }

    @Test
    fun `memory secret plan and weekend echo codecs become readable`() {
        val pack = pack(id = "structured-test", question = Question("Frage", listOf("A", "B")))

        val memory = coupleResultPresentation(
            pack,
            0,
            MemoryMatchAnswerCodec.encode("Unser erster Urlaub", null, "Der Zug war lustig", null)
        )
        assertEquals("Unser erster Urlaub", memory.displayText)
        assertEquals(listOf("Zweite Erinnerung: Der Zug war lustig"), memory.detailLines)

        val secret = coupleResultPresentation(pack, 0, SecretPlanAnswerCodec.encode("Picknick", "Kino"))
        assertEquals("Picknick", secret.displayText)
        assertEquals(listOf("Zweite Wahl: Kino"), secret.detailLines)

        val echo = coupleResultPresentation(
            pack,
            0,
            WeekendEchoAnswerCodec.encode("roadtrip", "hotel")
        )
        assertEquals("Roadtrip", echo.displayText)
        assertEquals(listOf("Zweite Auswahl: 5-Sterne-Hotel"), echo.detailLines)
    }

    @Test
    fun `autumn evening result reuses the exact gameplay image`() {
        val pack = QuestionPack(
            id = "herbstabend",
            title = "Unser Herbstabend",
            tags = emptyList(),
            cat = "lieber",
            topic = "hobbys",
            type = "quiz",
            questions = listOf(
                Question("Welche Geschichte zieht dich in den Herbst?", listOf("Mystery", "Thriller", "Dark Academia", "Cozy Fantasy"))
            )
        )

        val result = coupleResultPresentation(pack, 0, "Thriller")
        assertEquals(R.drawable.autumn_story_02, result.imageRes)
    }

    @Test
    fun `legacy image choice result reuses egg gameplay image`() {
        val options = (1..12).map { "Option $it" }
        val pack = QuestionPack(
            id = "essenreden",
            title = "Essen & Genuss",
            tags = emptyList(),
            cat = "reden",
            topic = "essen",
            type = "quiz",
            questions = listOf(
                Question("Wie möchtest du dein Ei am liebsten?", options)
            )
        )

        val result = coupleResultPresentation(pack, 0, "Option 3")
        assertEquals(R.drawable.egg_choice_03, result.imageRes)
    }

    private fun pack(id: String, question: Question): QuestionPack = QuestionPack(
        id = id,
        title = id,
        tags = emptyList(),
        cat = "test",
        topic = "test",
        type = "quiz",
        questions = listOf(question)
    )
}
