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
        questions = listOf(Question("Ordne nach Wichtigkeit", listOf("A", "B", "C", "D")))
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

    private fun normalPack(question: Question, id: String = "normal_response_pack"): QuestionPack = QuestionPack(
        id = id,
        title = "Normal",
        tags = listOf("unterhaltung"),
        cat = "tief",
        topic = "kennen",
        type = "quiz",
        questions = listOf(question)
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

        assertEquals(QuestionInteractionKind.PERSON_ASSIGNMENT, QuestionInteractionPolicy.resolve(pack, 0))
        assertEquals(FullscreenGameMechanicKind.PERSON_ASSIGNMENT, FullscreenGameMechanicPolicy.resolve(pack, 0))
    }

    @Test
    fun `ranking pack defaults to drag order`() {
        assertEquals(QuestionInteractionKind.RANK_ORDER, QuestionInteractionPolicy.resolve(rankingPack(), 0))
        assertEquals(FullscreenGameMechanicKind.RANK_ORDER, FullscreenGameMechanicPolicy.resolve(rankingPack(), 0))
    }

    @Test
    fun `harmony 360 mechanic tags route to dedicated fullscreen interactions`() {
        val expectations = listOf(
            "mechanik_prognose" to FullscreenGameMechanicKind.PARTNER_PREDICTION,
            "mechanik_geheime_wahl" to FullscreenGameMechanicKind.SECRET_CHOICE,
            "mechanik_skala" to FullscreenGameMechanicKind.SCALE_MATCH,
            "mechanik_wer_eher" to FullscreenGameMechanicKind.WHO_WOULD,
            "mechanik_memory" to FullscreenGameMechanicKind.MEMORY_MATCH,
            "mechanik_szenario" to FullscreenGameMechanicKind.SCENARIO,
            "mechanik_prioritaet" to FullscreenGameMechanicKind.PRIORITY_POKER,
            "mechanik_entweder_oder" to FullscreenGameMechanicKind.MATCH_TOURNAMENT,
            "mechanik_deep_talk" to FullscreenGameMechanicKind.DEEP_TALK
        )

        expectations.forEach { (tag, expected) ->
            assertEquals(tag, expected, FullscreenGameMechanicPolicy.resolve(mechanicPack(tag), 0))
        }
    }

    @Test
    fun `category fallback also routes scale prediction and secret choice`() {
        assertEquals(
            FullscreenGameMechanicKind.SCALE_MATCH,
            FullscreenGameMechanicPolicy.resolve(mechanicPack("other", "h360_skala"), 0)
        )
        assertEquals(
            FullscreenGameMechanicKind.PARTNER_PREDICTION,
            FullscreenGameMechanicPolicy.resolve(mechanicPack("other", "h360_prognose"), 0)
        )
        assertEquals(
            FullscreenGameMechanicKind.SECRET_CHOICE,
            FullscreenGameMechanicPolicy.resolve(mechanicPack("other", "h360_geheim"), 0)
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
        assertNull(FullscreenGameMechanicPolicy.resolve(pack, 0))
    }

    @Test
    fun `ordinary choice is fixed by default`() {
        val question = Question("Was magst du?", listOf("A", "B"))
        val spec = QuestionInteractionPolicy.resolveSpec(normalPack(question), 0, question)

        assertEquals(QuestionResponseKind.FIXED_CHOICE, spec.responseKind)
        assertEquals(false, spec.allowCustomText)
    }

    @Test
    fun `curated optional text stays available`() {
        val question = Question(
            "Wie würdest du unsere Beziehung in 3 Worten beschreiben?",
            listOf(
                "Liebevoll, ehrlich, wild",
                "Ruhig, sicher, warm",
                "Spannend, witzig, tief",
                "Ich brauche mehr Worte"
            )
        )
        val spec = QuestionInteractionPolicy.resolveSpec(
            normalPack(question, id = "gespraechsanreger"),
            0,
            question
        )

        assertEquals(QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT, spec.responseKind)
        assertEquals(true, spec.allowCustomText)
    }

    @Test
    fun `question without options is open text by default`() {
        val question = Question("Was möchtest du mir sagen?")
        val spec = QuestionInteractionPolicy.resolveSpec(normalPack(question), 0, question)

        assertEquals(QuestionResponseKind.OPEN_TEXT, spec.responseKind)
        assertEquals(true, spec.allowCustomText)
    }

    @Test
    fun `never have I ever allows skip without custom text`() {
        val question = Question("Ich habe noch nie getanzt.", listOf("Habe ich", "Habe ich noch nie"))
        val pack = normalPack(question).copy(cat = "nie")
        val spec = QuestionInteractionPolicy.resolveSpec(pack, 0, question)

        assertEquals(QuestionResponseKind.FIXED_CHOICE, spec.responseKind)
        assertEquals(true, spec.allowSkip)
        assertEquals(false, spec.allowCustomText)
    }

    @Test
    fun `photo semantics are explicit and ordinary photo mentions stay fixed`() {
        val optionalPhoto = Question("Was ist dein Lieblingsfoto von uns? 📸", listOf("A", "B", "C"))
        val photoOnly = Question("Welches gemeinsame Foto ist dein Lieblingsfoto?", listOf("A", "B"))
        val ordinaryMention = Question(
            "Wer würde eher das peinlichste Foto des Abends posten?",
            listOf("Ich", "Mein Partner", "Beide", "Niemand")
        )

        assertEquals(
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO,
            QuestionInteractionPolicy.resolveSpec(
                normalPack(optionalPhoto, "gespraechsanreger"),
                1,
                optionalPhoto
            ).responseKind
        )
        assertEquals(
            QuestionResponseKind.PHOTO_ONLY,
            QuestionInteractionPolicy.resolveSpec(normalPack(photoOnly, "schnapp"), 1, photoOnly).responseKind
        )
        assertEquals(
            QuestionResponseKind.FIXED_CHOICE,
            QuestionInteractionPolicy.resolveSpec(normalPack(ordinaryMention), 0, ordinaryMention).responseKind
        )
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