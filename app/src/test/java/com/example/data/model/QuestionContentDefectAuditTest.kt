package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionContentDefectAuditTest {

    @Test
    fun `audit flags malformed fixed choice questions`() {
        val pack = QuestionPack(
            id = "broken-pack",
            title = "Broken",
            tags = emptyList(),
            cat = "zust",
            topic = "moral",
            type = "quiz",
            questions = listOf(
                Question("", listOf("Ja", "Nein")),
                Question("Leere Option?", listOf("Ja", "  ")),
                Question("Doppelte Option?", listOf("Ja", " ja ")),
                Question("Zu wenig Auswahl?", listOf("Nur eine"))
            )
        )

        val issues = QuestionContentDefectAudit.audit(listOf(pack))

        assertTrue(issues.any { it.kind == QuestionContentDefectKind.BLANK_QUESTION })
        assertTrue(issues.any { it.kind == QuestionContentDefectKind.BLANK_OPTION })
        assertTrue(issues.any { it.kind == QuestionContentDefectKind.DUPLICATE_OPTION })
        assertTrue(issues.any { it.kind == QuestionContentDefectKind.TOO_FEW_OPTIONS })
    }

    @Test
    fun `audit allows open text and open fullscreen mechanics without options`() {
        val freeTextPack = QuestionPack(
            id = "free-text",
            title = "Free text",
            tags = emptyList(),
            cat = "tief",
            topic = "kennen",
            type = "quiz",
            questions = listOf(Question("Erzähl mir etwas."))
        )
        val memoryPack = QuestionPack(
            id = "memory",
            title = "Memory",
            tags = listOf("mechanik_memory"),
            cat = "h360_memory",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(Question("Welche Erinnerung fällt dir ein?"))
        )

        assertEquals(
            emptyList<QuestionContentDefect>(),
            QuestionContentDefectAudit.audit(listOf(freeTextPack, memoryPack))
        )
    }
}
