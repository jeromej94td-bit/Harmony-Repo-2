package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GermanWordingDefectAuditTest {

    @Test
    fun `known typo regression is detected`() {
        val issues = GermanWordingDefectAudit.audit(
            listOf(pack(question = "Wie sehen eure Schlagwewohnheiten aus?"))
        )

        assertEquals(1, issues.size)
        assertEquals(GermanWordingDefectKind.KNOWN_TYPO, issues.single().kind)
    }

    @Test
    fun `unresolved template token is detected`() {
        val issues = GermanWordingDefectAudit.audit(
            listOf(pack(question = "Was würde {partner} hier wählen?"))
        )

        assertEquals(1, issues.size)
        assertEquals(GermanWordingDefectKind.UNRESOLVED_TEMPLATE, issues.single().kind)
    }

    @Test
    fun `intentional mechanic identity option tokens remain allowed`() {
        val issues = GermanWordingDefectAudit.audit(
            listOf(pack(options = listOf("{user}", "{partner}", "Beide", "Niemand")))
        )

        assertTrue(issues.joinToString(), issues.isEmpty())
    }

    @Test
    fun `placeholder text in option is detected`() {
        val issues = GermanWordingDefectAudit.audit(
            listOf(pack(options = listOf("Gemeinsam reden", "TODO")))
        )

        assertTrue(issues.any { it.kind == GermanWordingDefectKind.PLACEHOLDER_TEXT })
    }

    @Test
    fun `duplicated functional word is detected`() {
        val issues = GermanWordingDefectAudit.audit(
            listOf(pack(question = "Was würdest du lieber lieber wählen?"))
        )

        assertTrue(issues.any { it.kind == GermanWordingDefectKind.DUPLICATED_WORD })
    }

    @Test
    fun `natural German wording and emphasis remain allowed`() {
        val issues = GermanWordingDefectAudit.audit(
            listOf(
                pack(
                    question = "Was bedeutet dir im Alltag sehr sehr viel?",
                    options = listOf("Zeit füreinander", "Gemeinsam lachen", "Ruhe und Nähe")
                )
            )
        )

        assertTrue(issues.joinToString(), issues.isEmpty())
    }

    private fun pack(
        title: String = "Testpaket",
        question: String = "Was ist euch wichtig?",
        options: List<String> = listOf("Nähe", "Humor")
    ): QuestionPack = QuestionPack(
        id = "wording_test",
        title = title,
        tags = emptyList(),
        cat = "tief",
        topic = "kennen",
        type = "quiz",
        questions = listOf(Question(question, options))
    )
}
