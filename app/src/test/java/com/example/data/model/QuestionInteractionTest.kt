package com.example.data.model

import com.example.data.GenQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuestionInteractionTest {

    private fun rankingPack(question: Question): QuestionPack = QuestionPack(
        id = "ranking_pack",
        title = "Ranking",
        tags = listOf("mechanik_ranking"),
        cat = "h360_ranking",
        topic = "beziehung",
        type = "quiz",
        questions = listOf(question)
    )

    @Test
    fun `explicit person assignment overrides ranking pack default`() {
        val question = Question(
            q = "Wer übernimmt welche Rolle?",
            options = listOf("Visionär", "Detailplaner", "Ausführer", "Qualitätsprüfer"),
            interaction = "person_assignment"
        )

        assertEquals(
            QuestionInteractionKind.PERSON_ASSIGNMENT,
            QuestionInteractionPolicy.resolve(rankingPack(question), question)
        )
    }

    @Test
    fun `ranking pack defaults to drag order`() {
        val question = Question(
            q = "Ordne nach Wichtigkeit",
            options = listOf("Vertrauen", "Kommunikation", "Humor", "Leidenschaft")
        )

        assertEquals(
            QuestionInteractionKind.RANK_ORDER,
            QuestionInteractionPolicy.resolve(rankingPack(question), question)
        )
    }

    @Test
    fun `normal quiz stays standard`() {
        val question = Question(q = "Was magst du?", options = listOf("A", "B"))
        val pack = QuestionPack(
            id = "normal",
            title = "Normal",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "kennen",
            type = "quiz",
            questions = listOf(question)
        )

        assertEquals(QuestionInteractionKind.STANDARD, QuestionInteractionPolicy.resolve(pack, question))
    }

    @Test
    fun `person assignments round trip with role text and both people`() {
        val options = listOf("Visionär/Ideen", "Detailplaner", "Ausführer", "Qualitätsprüfer")
        val assignments = linkedMapOf(
            "Visionär/Ideen" to PersonSide.USER,
            "Detailplaner" to PersonSide.PARTNER,
            "Ausführer" to PersonSide.USER,
            "Qualitätsprüfer" to PersonSide.PARTNER
        )

        val encoded = PersonAssignmentCodec.encode(options, assignments)

        assertEquals(assignments, PersonAssignmentCodec.decode(encoded, options))
        assertEquals(null, PersonAssignmentCodec.decode(encoded, options.dropLast(1)))
    }

    @Test
    fun `ranking round trip requires an exact permutation`() {
        val options = listOf("Vertrauen", "Kommunikation", "Humor", "Leidenschaft")
        val order = listOf("Humor", "Vertrauen", "Leidenschaft", "Kommunikation")

        val encoded = RankingAnswerCodec.encode(order)

        assertEquals(order, RankingAnswerCodec.decode(encoded, options))
        assertNull(RankingAnswerCodec.decode(RankingAnswerCodec.encode(listOf("Humor", "Humor")), options))
    }

    @Test
    fun `generated question can preserve explicit interaction metadata`() {
        val generated = GenQuestion(
            q = "Wer übernimmt?",
            options = listOf("A", "B"),
            interaction = "person_assignment"
        )

        assertEquals("person_assignment", generated.interaction)
    }
}
