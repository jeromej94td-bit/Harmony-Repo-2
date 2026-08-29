package com.example.data

import com.example.data.model.Question
import com.example.data.model.QuestionAuditKind
import com.example.data.model.QuestionMechanicsAudit
import com.example.data.model.QuestionPack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionMechanicsAuditTest {

    private fun pack(
        id: String,
        question: Question,
        tags: List<String> = listOf("unterhaltung"),
        cat: String = "tief"
    ) = QuestionPack(
        id = id,
        title = id,
        tags = tags,
        cat = cat,
        topic = "beziehung",
        type = "quiz",
        questions = listOf(question)
    )

    @Test
    fun `photo selection wording is flagged only as an advisory candidate`() {
        val source = pack(
            "photo_candidate",
            Question("Welches gemeinsame Foto würdest du für unser Album auswählen?", listOf("A", "B"))
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(findings.any { it.kind == QuestionAuditKind.PHOTO_SEMANTICS_CANDIDATE })
        assertEquals(listOf("A", "B"), source.questions.single().options)
    }

    @Test
    fun `ordinary photo mention is not mistaken for direct photo input`() {
        val source = pack(
            "photo_mention",
            Question(
                "Wer würde eher das peinlichste Foto des Abends posten?",
                listOf("Ich", "Mein Partner", "Beide", "Niemand")
            )
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(findings.none { it.kind == QuestionAuditKind.PHOTO_SEMANTICS_CANDIDATE })
    }

    @Test
    fun `ordering wording without a ranking mechanic is flagged`() {
        val source = pack(
            "ordering_candidate",
            Question("Ordne diese vier Dinge nach Wichtigkeit", listOf("A", "B", "C", "D"))
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(
            findings.any { it.kind == QuestionAuditKind.ORDERING_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC }
        )
    }

    @Test
    fun `existing ranking mechanic suppresses ordering warning`() {
        val source = pack(
            "ranking_pack",
            Question("Ordne diese vier Dinge nach Wichtigkeit", listOf("A", "B", "C", "D")),
            tags = listOf("mechanik_ranking"),
            cat = "h360_ranking"
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(
            findings.none { it.kind == QuestionAuditKind.ORDERING_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC }
        )
    }

    @Test
    fun `partner prediction wording without prediction mechanic is flagged`() {
        val source = pack(
            "prediction_candidate",
            Question("Was glaubst du, was dein Partner zuerst wählen würde?", listOf("A", "B", "C"))
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(
            findings.any { it.kind == QuestionAuditKind.PREDICTION_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC }
        )
    }

    @Test
    fun `duplicate source options are flagged`() {
        val source = pack("duplicate_options", Question("Wähle", listOf("A", "B", "A")))

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(findings.any { it.kind == QuestionAuditKind.DUPLICATE_OPTIONS })
    }

    @Test
    fun `generic own-answer option embedded in source is flagged`() {
        val source = pack(
            "source_fallback",
            Question("Wähle", listOf("A", "B", "Schreibe deine eigene Antwort"))
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(findings.any { it.kind == QuestionAuditKind.GENERIC_FALLBACK_IN_SOURCE_OPTIONS })
    }

    @Test
    fun `index based interaction tags are flagged as unstable legacy routing`() {
        val source = pack(
            "legacy_index",
            Question("Wer übernimmt?", listOf("A", "B")),
            tags = listOf("interaction_person_assignment_0")
        )

        val findings = QuestionMechanicsAudit.scan(listOf(source))

        assertTrue(findings.any { it.kind == QuestionAuditKind.UNSTABLE_INDEX_SPECIAL_CASE })
    }
}
