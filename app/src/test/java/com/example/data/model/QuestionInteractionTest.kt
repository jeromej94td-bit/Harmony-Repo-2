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

    private fun mechanicPack(tag: String, cat: String = "h360_test"): QuestionPack = QuestionPack(
        id = "pack_$tag",
        title = tag,
        tags = listOf("harmony360", tag),
        cat = cat,
        topic = "beziehung",
        type = "quiz",
        questions = listOf(Question("Testfrage", listOf("A", "B", "C", "D")))
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
    fun `harmony 360 mechanic tags route to dedicated fullscreen interactions`() {
        val expectations = listOf(
            "mechanik_prognose" to QuestionInteractionKind.PARTNER_PREDICTION,
            "mechanik_geheime_wahl" to QuestionInteractionKind.SECRET_CHOICE,
            "mechanik_skala" to QuestionInteractionKind.SCALE_MATCH,
            "mechanik_wer_eher" to QuestionInteractionKind.WHO_WOULD,
            "mechanik_memory" to QuestionInteractionKind.MEMORY_MATCH,
            "mechanik_szenario" to QuestionInteractionKind.SCENARIO,
            "mechanik_prioritaet" to QuestionInteractionKind.PRIORITY_POKER,
            "mechanik_entweder_oder" to QuestionInteractionKind.MATCH_TOURNAMENT,
            "mechanik_deep_talk" to QuestionInteractionKind.DEEP_TALK
        )

        expectations.forEach { (tag, expected) ->
            assertEquals(tag, expected, QuestionInteractionPolicy.resolve(mechanicPack(tag), 0))
        }
    }

    @Test
    fun `category fallback also routes scale prediction and secret choice`() {
        assertEquals(
            QuestionInteractionKind.SCALE_MATCH,
            QuestionInteractionPolicy.resolve(mechanicPack("other", "h360_skala"), 0)
        )
        assertEquals(
            QuestionInteractionKind.PARTNER_PREDICTION,
            QuestionInteractionPolicy.resolve(mechanicPack("other", "h360_prognose"), 0)
        )
        assertEquals(
            QuestionInteractionKind.SECRET_CHOICE,
            QuestionInteractionPolicy.resolve(mechanicPack("other", "h360_geheim"), 0)
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
    fun `specialized prompt removes duplicated options from question text`() {
        val options = listOf("Traumhaus bauen", "Weltreise starten", "Spenden/Investieren", "Job sofort kündigen")
        val raw = "Rangliste bei 50 Millionen Euro Gewinn: Traumhaus bauen, Weltreise starten, Spenden/Investieren, Job sofort kündigen"

        assertEquals(
            "Rangliste bei 50 Millionen Euro Gewinn",
            InteractionPromptPolicy.displayPrompt(raw, options)
        )
    }

    @Test
    fun `specialized prompt removes dangling rank instruction after a real question`() {
        val options = listOf("Niemandem erzählen", "Familie einladen", "Champagner öffnen", "Finanzberater suchen")
        val raw = "Was wäre der erste Schritt nach dem Gewinn? Ordne: Niemandem erzählen, Familie einladen, Champagner öffnen, Finanzberater suchen"

        assertEquals(
            "Was wäre der erste Schritt nach dem Gewinn?",
            InteractionPromptPolicy.displayPrompt(raw, options)
        )
    }

    @Test
    fun `prompt without repeated option remains unchanged`() {
        val raw = "Was glaubst du: Was würde dein Partner zuerst tun?"
        assertEquals(
            raw,
            InteractionPromptPolicy.displayPrompt(raw, listOf("Reisen", "Schlafen", "Feiern", "Lesen"))
        )
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
    fun `paired choice codecs keep both hidden answers`() {
        val encoded = PairedChoiceAnswerCodec.encode("A", "D")
        assertEquals(PairedChoiceAnswer("A", "D"), PairedChoiceAnswerCodec.decode(encoded))
    }

    @Test
    fun `prediction codec keeps prediction and actual choice`() {
        val encoded = PredictionAnswerCodec.encode("B", "B")
        assertEquals(PredictionAnswer("B", "B"), PredictionAnswerCodec.decode(encoded))
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
