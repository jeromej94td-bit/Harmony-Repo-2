package com.example.data.model

import com.example.data.GeneratedHarmonyAdrenaline360Section20TeamworkChallenge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuestionInteractionTest {

    private fun rankingPack(): QuestionPack = QuestionPack(
        id = "ranking_pack",
        title = "Ranking",
        tags = listOf("mechanik_ranking"),
        cat = "h360_ranking",
        topic = "beziehung",
        type = "quiz",
        questions = listOf(
            Question("Ordne nach Wichtigkeit", listOf("A", "B", "C", "D"))
        )
    )

    @Test
    fun `explicit person assignment overrides ranking pack default`() {
        val pack = QuestionPack(
            id = "custom_assignment",
            title = "Rollen",
            tags = listOf("mechanik_ranking", "interaction_person_assignment_0"),
            cat = "h360_ranking",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(Question("Wer übernimmt?", listOf("A", "B")))
        )

        assertEquals(
            QuestionInteractionKind.PERSON_ASSIGNMENT,
            QuestionInteractionPolicy.resolve(pack, 0)
        )
    }

    @Test
    fun `ranking pack defaults to drag order`() {
        assertEquals(
            QuestionInteractionKind.RANK_ORDER,
            QuestionInteractionPolicy.resolve(rankingPack(), 0)
        )
    }

    @Test
    fun `normal quiz stays standard`() {
        val pack = QuestionPack(
            id = "normal",
            title = "Normal",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "kennen",
            type = "quiz",
            questions = listOf(Question("Was magst du?", listOf("A", "B")))
        )

        assertEquals(QuestionInteractionKind.STANDARD, QuestionInteractionPolicy.resolve(pack, 0))
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
    fun `harmony 360 role assignment keeps the four original roles`() {
        val generated = GeneratedHarmonyAdrenaline360Section20TeamworkChallenge.PACKS
            .first { it.id == "h500_414_rollenverteilung_ranking" }
        val pack = QuestionPack(
            id = generated.id,
            title = generated.title,
            tags = generated.tags,
            cat = generated.cat,
            topic = generated.topic,
            type = generated.type,
            questions = generated.questions.map { Question(it.q, it.options, it.defaultMine) }
        )

        assertEquals(QuestionInteractionKind.PERSON_ASSIGNMENT, QuestionInteractionPolicy.resolve(pack, 1))
        assertEquals(
            listOf("Visionär/Ideen", "Detailplaner", "Ausführer", "Qualitätsprüfer"),
            pack.questions[1].options
        )
    }
}
