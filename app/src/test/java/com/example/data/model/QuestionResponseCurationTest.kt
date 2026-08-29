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

    private fun assertOptionalText(packId: String, prompt: String, options: List<String>) {
        assertEquals(
            prompt,
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            resolve(packId, prompt, options).responseKind
        )
    }

    @Test
    fun `deep prompts that ask for a concrete personal thought allow text`() {
        assertOptionalText(
            "tiefe",
            "Was bedeutet Vertrauen für dich konkret?",
            listOf("Dass ich alles erzählen kann", "Dass ich mich nicht sorgen muss", "Dass Zusagen gehalten werden", "Alles davon")
        )
        assertOptionalText(
            "tiefe",
            "Gibt es etwas, das du mir schon immer sagen wolltest, aber dich nie getraut hast?",
            listOf("Ja, einiges", "Nur Kleinigkeiten", "Nein, ich bin immer offen", "Weiß nicht")
        )
        assertOptionalText(
            "tiefe",
            "Was war der Moment, in dem du wusstest, dass du mich liebst?",
            listOf("Das war ein schleichender Prozess", "Ein ganz bestimmter Moment", "Weiß ich gar nicht mehr genau", "Ich wusste es sofort")
        )
    }

    @Test
    fun `conversation starters with genuinely personal content allow a specific answer`() {
        assertOptionalText(
            "gespraechsanreger",
            "Was möchtest du, dass dein Partner öfter tut?",
            listOf("Mich überraschen", "Zuhören", "Im Haushalt helfen", "Zärtlich sein")
        )
        assertOptionalText(
            "gespraechsanreger",
            "Welcher gemeinsame Moment bringt dich immer zum Lächeln?",
            listOf("Unser erster Kuss", "Ein lustiger Fail", "Ein tiefer Blick", "Etwas Alltägliches")
        )
        assertOptionalText(
            "gespraechsanreger",
            "Gibt es ein Thema, über das wir zu wenig reden?",
            listOf("Unsere Zukunft", "Unsere Ängste", "Finanzen", "Nein, alles super")
        )
    }

    @Test
    fun `snapshot memory prompt can capture the actual shared moment`() {
        assertOptionalText(
            "schnapp",
            "Was war dein schönster Moment mit mir bisher?",
            listOf("Unser erstes Treffen", "Ein ganz normaler Alltagstag", "Eine gemeinsame Reise", "Ein schwerer Moment, den wir geschafft haben")
        )
    }

    @Test
    fun `daily relationship improvement can be more specific than four presets`() {
        assertOptionalText(
            "tagesfragen",
            "Wie kann dein Partner ein noch besserer Partner für dich sein?",
            listOf("Mehr zuhören", "Mehr gemeinsame Zeit", "Mehr Unterstützung", "Ist schon perfekt")
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
