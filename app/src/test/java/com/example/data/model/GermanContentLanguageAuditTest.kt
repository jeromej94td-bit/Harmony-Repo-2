package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GermanContentLanguageAuditTest {

    @Test
    fun `audit flags clear English sentence prompts in German content`() {
        val pack = QuestionPack(
            id = "mixed-language-pack",
            title = "Gemischter Inhalt",
            tags = emptyList(),
            cat = "tief",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("What makes a relationship feel safe?", listOf("Vertrauen", "Nähe")),
                Question("Which memory would you relive together?", listOf("Urlaub", "Erstes Date")),
                Question("Would you rather plan everything or stay spontaneous?", listOf("Planen", "Spontan"))
            )
        )

        val issues = GermanContentLanguageAudit.audit(listOf(pack))

        assertEquals(3, issues.size)
        assertTrue(issues.all { it.kind == GermanContentLanguageIssueKind.ENGLISH_PROMPT })
    }

    @Test
    fun `audit flags clear English sentence options without treating short terms as errors`() {
        val pack = QuestionPack(
            id = "mixed-option-pack",
            title = "Gemischte Antworten",
            tags = emptyList(),
            cat = "tief",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question(
                    "Was wäre dir lieber?",
                    listOf(
                        "I would rather stay home tonight",
                        "We would plan it together",
                        "Happy End",
                        "Fine Dining"
                    )
                )
            )
        )

        val issues = GermanContentLanguageAudit.audit(listOf(pack))

        assertEquals(2, issues.size)
        assertTrue(issues.all { it.kind == GermanContentLanguageIssueKind.ENGLISH_OPTION })
    }

    @Test
    fun `audit does not reject common loanwords brands or food terms`() {
        val pack = QuestionPack(
            id = "legitimate-terms",
            title = "Filmabend",
            tags = emptyList(),
            cat = "reden",
            topic = "filme_serien",
            type = "quiz",
            questions = listOf(
                Question("Welches Ende magst du lieber?", listOf("Happy End", "Bittersüß")),
                Question("Was würdest du eher ausprobieren?", listOf("Fine Dining", "Street Food")),
                Question("Was schaut ihr heute?", listOf("Netflix", "Science-Fiction")),
                Question("Wie willst du dein Steak?", listOf("Blue-Rare", "Medium"))
            )
        )

        assertEquals(emptyList<GermanContentLanguageIssue>(), GermanContentLanguageAudit.audit(listOf(pack)))
    }
}
