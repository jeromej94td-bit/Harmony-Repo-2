package com.example.data.model

/**
 * Explicit response semantics for legacy questions that do not carry their own metadata yet.
 *
 * Runtime behavior never uses broad keyword inference. Each entry is bound to a stable pack id
 * plus normalized raw question text so inserting or moving another question cannot change it.
 */
object QuestionResponseCuration {
    private val repeatedWhitespace = Regex("\\s+")

    private val responseKinds: Map<String, QuestionResponseKind> = mapOf(
        key("gespraechsanreger", "Was ist dein Lieblingsfoto von uns? 📸") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO,
        key("schnapp", "Welches gemeinsame Foto ist dein Lieblingsfoto?") to
            QuestionResponseKind.PHOTO_ONLY,
        key("gespraechsanreger", "Wie würdest du unsere Beziehung in 3 Worten beschreiben?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT
    )

    fun key(packId: String, rawQuestion: String): String =
        "${packId.trim()}|${normalizeQuestion(rawQuestion)}"

    fun resolve(packId: String, rawQuestion: String): QuestionResponseKind? =
        responseKinds[key(packId, rawQuestion)]

    private fun normalizeQuestion(rawQuestion: String): String =
        rawQuestion.trim().replace(repeatedWhitespace, " ")
}
