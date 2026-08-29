package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionResponseCurationTest {

    private fun resolve(packId: String, prompt: String, options: List<String>): QuestionInteractionSpec {
        val question = Question(prompt, options)
        val pack = QuestionPack(
            id = packId,
            title = packId,
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(question)
        )
        return QuestionInteractionPolicy.resolveSpec(pack, 0, question)
    }

    @Test
    fun `deep trust definition allows a personal answer`() {
        assertEquals(
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            resolve(
                "tiefe",
                "Was bedeutet Vertrauen für dich konkret?",
                listOf(
                    "Dass ich alles erzählen kann",
                    "Dass ich mich nicht sorgen muss",
                    "Dass Zusagen gehalten werden",
                    "Alles davon"
                )
            ).responseKind
        )
    }

    @Test
    fun `unsaid truth prompt allows the actual unsaid thought`() {
        assertEquals(
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            resolve(
                "tiefe",
                "Gibt es etwas, das du mir schon immer sagen wolltest, aber dich nie getraut hast?",
                listOf("Ja, einiges", "Nur Kleinigkeiten", "Nein, ich bin immer offen", "Weiß nicht")
            ).responseKind
        )
    }

    @Test
    fun `falling in love moment allows a concrete personal memory`() {
        assertEquals(
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            resolve(
                "tiefe",
                "Was war der Moment, in dem du wusstest, dass du mich liebst?",
                listOf(
                    "Das war ein schleichender Prozess",
                    "Ein ganz bestimmter Moment",
                    "Weiß ich gar nicht mehr genau",
                    "Ich wusste es sofort"
                )
            ).responseKind
        )
    }

    @Test
    fun `ordinary deep choices remain fixed`() {
        val fixedPrompts = listOf(
            "Was ist für dich ein absoluter Dealbreaker?" to
                listOf("Lügen", "Respektlosigkeit", "Gleichgültigkeit", "Untreue"),
            "Wann fühlst du dich mir am nächsten?" to
                listOf("Beim Reden", "In Stille nebeneinander", "Wenn wir zusammen lachen", "Wenn es schwierig ist"),
            "Wie sehr darf ich dich bei Entscheidungen beeinflussen?" to
                listOf("Sehr stark", "Ein bisschen", "Nur als Ratgeber", "Gar nicht")
        )

        fixedPrompts.forEach { (prompt, options) ->
            assertEquals(
                prompt,
                QuestionResponseKind.FIXED_CHOICE,
                resolve("tiefe", prompt, options).responseKind
            )
        }
    }

    @Test
    fun `stable curation key ignores repeated whitespace but not punctuation`() {
        assertEquals(
            QuestionResponseCuration.key("tiefe", "  Was bedeutet   Vertrauen für dich konkret?  "),
            QuestionResponseCuration.key("tiefe", "Was bedeutet Vertrauen für dich konkret?")
        )
    }
}
