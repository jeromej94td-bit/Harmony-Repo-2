package com.example.data.model

/**
 * Explicit response semantics for legacy questions and dynamic content.
 *
 * Runtime behavior never uses broad keyword inference. Each entry is bound to a stable pack id
 * plus normalized raw question text so inserting or moving another question cannot change it.
 */
object QuestionResponseCuration {
    private val repeatedWhitespace = Regex("\\s+")
    private val dynamicResponseKinds = linkedMapOf<String, QuestionResponseKind>()

    private val responseKinds: Map<String, QuestionResponseKind> = mapOf(
        key("gespraechsanreger", "Was ist dein Lieblingsfoto von uns? 📸") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO,
        key("schnapp", "Welches gemeinsame Foto ist dein Lieblingsfoto?") to
            QuestionResponseKind.PHOTO_ONLY,
        key("gespraechsanreger", "Wie würdest du unsere Beziehung in 3 Worten beschreiben?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("gespraechsanreger", "Was möchtest du, dass dein Partner öfter tut?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("gespraechsanreger", "Welcher gemeinsame Moment bringt dich immer zum Lächeln?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("gespraechsanreger", "Gibt es ein Thema, über das wir zu wenig reden?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("schnapp", "Was war dein schönster Moment mit mir bisher?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("tagesfragen", "Wie kann dein Partner ein noch besserer Partner für dich sein?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("tiefe", "Was bedeutet Vertrauen für dich konkret?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("tiefe", "Gibt es etwas, das du mir schon immer sagen wolltest, aber dich nie getraut hast?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        key("tiefe", "Was war der Moment, in dem du wusstest, dass du mich liebst?") to
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT
    )

    fun key(packId: String, rawQuestion: String): String =
        "${packId.trim()}|${normalizeQuestion(rawQuestion)}"

    @Synchronized
    fun resolve(packId: String, rawQuestion: String): QuestionResponseKind? {
        val stableKey = key(packId, rawQuestion)
        return dynamicResponseKinds[stableKey] ?: responseKinds[stableKey]
    }

    @Synchronized
    fun replaceDynamic(entries: Map<String, QuestionResponseKind>) {
        dynamicResponseKinds.clear()
        dynamicResponseKinds.putAll(entries)
    }

    fun parseAnswerMode(rawMode: String?): QuestionResponseKind? = when (
        rawMode?.trim()?.lowercase()?.replace('-', '_')?.replace(' ', '_')
    ) {
        "fixed_choice", "choice", "single_choice", "multiple_choice" ->
            QuestionResponseKind.FIXED_CHOICE

        "choice_with_optional_text", "choice_plus_text", "choice_text", "optional_text" ->
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT

        "open_text", "free_text", "freetext", "text" ->
            QuestionResponseKind.OPEN_TEXT

        "photo_only", "photo", "image_only" ->
            QuestionResponseKind.PHOTO_ONLY

        "choice_with_optional_photo", "choice_plus_photo", "optional_photo" ->
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO

        else -> null
    }

    private fun normalizeQuestion(rawQuestion: String): String =
        rawQuestion.trim().replace(repeatedWhitespace, " ")
}
