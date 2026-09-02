package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRepetitionAuditTest {

    @Test
    fun `detects repeated exact prompts across three packs`() {
        val packs = (1..3).map { index ->
            pack(
                id = "p$index",
                prompt = "Wie wichtig ist euch gemeinsame Zeit?",
                options = listOf("Sehr", "Eher", "Wenig", "Gar nicht")
            )
        }

        val issues = ContentRepetitionAudit.audit(packs)

        assertTrue(issues.any {
            it.kind == ContentRepetitionIssueKind.EXACT_PROMPT && it.packIds.size == 3
        })
    }

    @Test
    fun `detects repeated quoted subject templates`() {
        val packs = listOf(
            pack("a", "Was entscheidet, ob „Morgenroutine“ für dich besonders ist?", listOf("A", "B", "C", "D")),
            pack("b", "Was entscheidet, ob „Sport“ für dich besonders ist?", listOf("E", "F", "G", "H")),
            pack("c", "Was entscheidet, ob „Bücher“ für dich besonders ist?", listOf("I", "J", "K", "L"))
        )

        val issues = ContentRepetitionAudit.audit(packs)

        assertTrue(issues.any {
            it.kind == ContentRepetitionIssueKind.PROMPT_TEMPLATE && it.packIds.size == 3
        })
    }

    @Test
    fun `detects reused generic relationship quartet across three packs`() {
        val generic = listOf("Mehr Nähe", "Mehr Freiheit", "Mehr Sicherheit", "Mehr Abenteuer")
        val packs = listOf(
            pack("a", "Frage eins?", generic),
            pack("b", "Frage zwei?", generic),
            pack("c", "Frage drei?", generic)
        )

        val issues = ContentRepetitionAudit.audit(packs)

        assertTrue(issues.any {
            it.kind == ContentRepetitionIssueKind.OPTION_QUARTET && it.packIds.size == 3
        })
    }

    @Test
    fun `reused domain specific quartet is not treated as generic`() {
        val transport = listOf("Zug", "Auto", "Fahrrad", "Zu Fuß")
        val packs = listOf(
            pack("a", "Wie reist ihr am liebsten?", transport),
            pack("b", "Wie würdet ihr diesen Ausflug machen?", transport),
            pack("c", "Was passt für den Wochenendtrip?", transport)
        )

        val issues = ContentRepetitionAudit.audit(packs)

        assertEquals(
            emptyList<ContentRepetitionIssue>(),
            issues.filter { it.kind == ContentRepetitionIssueKind.OPTION_QUARTET }
        )
    }

    @Test
    fun `intentional who-would-rather mechanic quartet is exempt`() {
        val mechanic = listOf("{user}", "{partner}", "Beide", "Niemand")
        val packs = listOf(
            pack("a", "Wer würde eher A?", mechanic),
            pack("b", "Wer würde eher B?", mechanic),
            pack("c", "Wer würde eher C?", mechanic)
        )

        val issues = ContentRepetitionAudit.audit(packs)

        assertEquals(
            emptyList<ContentRepetitionIssue>(),
            issues.filter { it.kind == ContentRepetitionIssueKind.OPTION_QUARTET }
        )
    }

    @Test
    fun `unique wording remains clean`() {
        val packs = listOf(
            pack("a", "Was macht einen ruhigen Sonntag für euch schön?", listOf("A", "B", "C", "D")),
            pack("b", "Welche Reise würdet ihr morgen starten?", listOf("E", "F", "G", "H")),
            pack("c", "Welches gemeinsame Ritual mögt ihr?", listOf("I", "J", "K", "L"))
        )

        assertTrue(ContentRepetitionAudit.audit(packs).isEmpty())
    }

    private fun pack(id: String, prompt: String, options: List<String>) = QuestionPack(
        id = id,
        title = id,
        tags = emptyList(),
        cat = "test",
        topic = "kennen",
        type = "quiz",
        questions = listOf(Question(q = prompt, options = options)),
        pairs = emptyList(),
        emoji = ""
    )
}
