package com.example.data.model

import java.util.Locale

enum class QuestionContentDefectKind {
    BLANK_QUESTION,
    MISSING_OPTIONS,
    BLANK_OPTION,
    DUPLICATE_OPTION,
    TOO_FEW_OPTIONS
}

data class QuestionContentDefect(
    val packId: String,
    val questionIndex: Int,
    val kind: QuestionContentDefectKind,
    val question: String,
    val detail: String
)

/**
 * Stage 06.1 audit for malformed question content on the same QuestionPack shape used by runtime.
 *
 * Empty options are only defects when the question is explicitly or structurally choice-based.
 * Open-text, photo-only, memory-match and deep-talk rounds may intentionally have no options.
 */
object QuestionContentDefectAudit {
    private val fixedChoiceCategories = setOf(
        "nie",
        "zust",
        "wer",
        "lieber",
        "h360_ranking",
        "h360_prognose",
        "h360_geheim",
        "h360_skala",
        "h360_szenario",
        "h360_prioritaet"
    )

    fun audit(packs: List<QuestionPack>): List<QuestionContentDefect> = buildList {
        packs.forEach { pack ->
            pack.questions.forEachIndexed { questionIndex, question ->
                val rawQuestion = question.q
                if (rawQuestion.isBlank()) {
                    add(
                        defect(
                            pack = pack,
                            questionIndex = questionIndex,
                            question = rawQuestion,
                            kind = QuestionContentDefectKind.BLANK_QUESTION,
                            detail = "Question text is blank."
                        )
                    )
                }

                val options = question.options
                if (options.isEmpty()) {
                    if (requiresOptions(pack, questionIndex, question)) {
                        add(
                            defect(
                                pack = pack,
                                questionIndex = questionIndex,
                                question = rawQuestion,
                                kind = QuestionContentDefectKind.MISSING_OPTIONS,
                                detail = "Choice-based question has no answer options."
                            )
                        )
                    }
                    return@forEachIndexed
                }

                if (options.any(String::isBlank)) {
                    add(
                        defect(
                            pack = pack,
                            questionIndex = questionIndex,
                            question = rawQuestion,
                            kind = QuestionContentDefectKind.BLANK_OPTION,
                            detail = "At least one answer option is blank."
                        )
                    )
                }

                val normalizedOptions = options
                    .filterNot(String::isBlank)
                    .map(::normalizeOption)
                if (normalizedOptions.distinct().size != normalizedOptions.size) {
                    add(
                        defect(
                            pack = pack,
                            questionIndex = questionIndex,
                            question = rawQuestion,
                            kind = QuestionContentDefectKind.DUPLICATE_OPTION,
                            detail = "Answer options contain duplicates after normalization."
                        )
                    )
                }

                if (requiresOptions(pack, questionIndex, question) && normalizedOptions.distinct().size < 2) {
                    add(
                        defect(
                            pack = pack,
                            questionIndex = questionIndex,
                            question = rawQuestion,
                            kind = QuestionContentDefectKind.TOO_FEW_OPTIONS,
                            detail = "Choice-based question needs at least two distinct answer options."
                        )
                    )
                }
            }
        }
    }

    private fun requiresOptions(
        pack: QuestionPack,
        questionIndex: Int,
        question: Question
    ): Boolean {
        when (QuestionResponseCuration.resolve(pack.id, question.q)) {
            QuestionResponseKind.OPEN_TEXT,
            QuestionResponseKind.PHOTO_ONLY -> return false

            QuestionResponseKind.FIXED_CHOICE,
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
            QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO -> return true

            null -> Unit
        }

        val spec = QuestionInteractionPolicy.resolveSpec(pack, questionIndex, question)
        return when (spec.fullscreenMechanic) {
            FullscreenGameMechanicKind.MEMORY_MATCH,
            FullscreenGameMechanicKind.DEEP_TALK -> false

            null -> if (question.options.isEmpty()) {
                pack.cat in fixedChoiceCategories
            } else {
                spec.responseKind in setOf(
                    QuestionResponseKind.FIXED_CHOICE,
                    QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
                    QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO
                )
            }

            else -> true
        }
    }

    private fun normalizeOption(option: String): String =
        option.trim().replace(Regex("\\s+"), " ").lowercase(Locale.ROOT)

    private fun defect(
        pack: QuestionPack,
        questionIndex: Int,
        question: String,
        kind: QuestionContentDefectKind,
        detail: String
    ): QuestionContentDefect = QuestionContentDefect(
        packId = pack.id,
        questionIndex = questionIndex,
        kind = kind,
        question = question,
        detail = detail
    )
}
