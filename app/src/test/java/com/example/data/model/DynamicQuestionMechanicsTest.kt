package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DynamicQuestionMechanicsTest {

    private fun pack(cat: String): QuestionPack = QuestionPack(
        id = "dynamic_$cat",
        title = cat,
        tags = listOf("supabase"),
        cat = cat,
        topic = "beziehung",
        type = "quiz",
        questions = listOf(Question("Dynamische Frage", listOf("A", "B", "C", "D")))
    )

    @Test
    fun `remote answer mode parser recognizes supported response semantics`() {
        assertEquals(
            QuestionResponseKind.FIXED_CHOICE,
            QuestionResponseCuration.parseAnswerMode("fixed_choice")
        )
        assertEquals(
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            QuestionResponseCuration.parseAnswerMode("choice_with_optional_text")
        )
        assertEquals(
            QuestionResponseKind.OPEN_TEXT,
            QuestionResponseCuration.parseAnswerMode("free_text")
        )
        assertEquals(
            QuestionResponseKind.PHOTO_ONLY,
            QuestionResponseCuration.parseAnswerMode("photo_only")
        )
        assertEquals(
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO,
            QuestionResponseCuration.parseAnswerMode("choice_with_optional_photo")
        )
        assertNull(QuestionResponseCuration.parseAnswerMode("unknown_future_mode"))
    }

    @Test
    fun `dynamic explicit response overrides local safe default`() {
        val packId = "dynamic_photo_pack"
        val prompt = "Wähle ein gemeinsames Bild"
        QuestionResponseCuration.replaceDynamic(
            mapOf(
                QuestionResponseCuration.key(packId, prompt) to QuestionResponseKind.PHOTO_ONLY
            )
        )

        try {
            val question = Question(prompt, listOf("A", "B"))
            val pack = QuestionPack(
                id = packId,
                title = "Dynamic",
                tags = listOf("supabase"),
                cat = "foto",
                topic = "beziehung",
                type = "quiz",
                questions = listOf(question)
            )

            assertEquals(
                QuestionResponseKind.PHOTO_ONLY,
                QuestionInteractionPolicy.resolveSpec(pack, 0, question).responseKind
            )
        } finally {
            QuestionResponseCuration.replaceDynamic(emptyMap())
        }
    }

    @Test
    fun `dynamic harmony categories route without generated source tags`() {
        assertEquals(
            FullscreenGameMechanicKind.MEMORY_MATCH,
            FullscreenGameMechanicPolicy.resolve(pack("h360_memory"), 0)
        )
        assertEquals(
            FullscreenGameMechanicKind.PRIORITY_POKER,
            FullscreenGameMechanicPolicy.resolve(pack("h360_prioritaet"), 0)
        )
        assertEquals(
            FullscreenGameMechanicKind.WHO_WOULD,
            FullscreenGameMechanicPolicy.resolve(pack("wer"), 0)
        )
    }
}
