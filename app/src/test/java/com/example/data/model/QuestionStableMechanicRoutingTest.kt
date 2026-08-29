package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionStableMechanicRoutingTest {
    private val roleQuestion = Question(
        "Wer übernimmt welche Rolle bei gemeinsamen Plänen? Rank: Visionär/Ideen, Detailplaner, Ausführer, Qualitätsprüfer",
        listOf("Visionär/Ideen", "Detailplaner", "Ausführer", "Qualitätsprüfer")
    )

    @Test
    fun `role assignment survives question reordering`() {
        val pack = QuestionPack(
            id = "h500_414_rollenverteilung_ranking",
            title = "Rollenverteilung – Zwei Perspektiven",
            tags = listOf("harmony360", "mechanik_ranking"),
            cat = "h360_ranking",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                roleQuestion,
                Question("Andere Rankingfrage", listOf("A", "B", "C", "D"))
            )
        )

        assertEquals(
            QuestionInteractionKind.PERSON_ASSIGNMENT,
            QuestionInteractionPolicy.resolve(pack, 0)
        )
        assertEquals(
            FullscreenGameMechanicKind.PERSON_ASSIGNMENT,
            FullscreenGameMechanicPolicy.resolve(pack, 0)
        )
    }
}
