package com.example.data

import com.example.data.model.Question
import com.example.data.model.QuestionPack

enum class LiveQuestionKind(val key: String) {
    CHOICE("choice"),
    FREE_TEXT("free_text"),
    THIS_OR_THAT("this_or_that");

    companion object {
        fun fromKey(key: String?): LiveQuestionKind? = values().firstOrNull { it.key == key }
    }
}

/**
 * Pure editing helpers used by Live Change.
 *
 * Question kinds are stored in the existing `options` payload using a private marker.
 * That keeps old generated content, locale files and exports source-compatible while
 * allowing a single quiz pack to contain different question kinds.
 */
object LiveQuestionEditing {
    private const val KIND_PREFIX = "__harmony_live_kind:"

    fun createQuestion(
        kind: LiveQuestionKind,
        text: String,
        options: List<String> = emptyList(),
        defaultMine: String? = null
    ): Question {
        val cleanText = text.trim()
        require(cleanText.isNotEmpty()) { "Question text must not be blank" }

        val cleanOptions = options.map { it.trim() }.filter { it.isNotEmpty() }
        val storedOptions = when (kind) {
            LiveQuestionKind.CHOICE -> cleanOptions
            LiveQuestionKind.FREE_TEXT -> listOf(marker(kind))
            LiveQuestionKind.THIS_OR_THAT -> {
                require(cleanOptions.size == 2) { "This-or-that questions require exactly two options" }
                listOf(marker(kind)) + cleanOptions
            }
        }
        return Question(q = cleanText, options = storedOptions, defaultMine = defaultMine)
    }

    fun effectiveKind(question: Question, packType: String): LiveQuestionKind {
        val explicit = question.options.firstOrNull()
            ?.takeIf { it.startsWith(KIND_PREFIX) }
            ?.removePrefix(KIND_PREFIX)
            ?.let(LiveQuestionKind::fromKey)
        if (explicit != null) return explicit

        return when (packType) {
            "disc" -> LiveQuestionKind.FREE_TEXT
            "tot" -> LiveQuestionKind.THIS_OR_THAT
            else -> LiveQuestionKind.CHOICE
        }
    }

    fun visibleOptions(question: Question): List<String> =
        question.options.filterNot { it.startsWith(KIND_PREFIX) }

    fun insertQuestion(pack: QuestionPack, index: Int, question: Question): QuestionPack {
        val target = index.coerceIn(0, pack.questions.size)
        val updated = pack.questions.toMutableList().apply { add(target, question) }
        return pack.copy(questions = updated)
    }

    fun replaceQuestion(pack: QuestionPack, index: Int, question: Question): QuestionPack {
        require(index in pack.questions.indices) { "Question index out of range" }
        val updated = pack.questions.toMutableList().apply { this[index] = question }
        return pack.copy(questions = updated)
    }

    fun duplicateQuestion(pack: QuestionPack, index: Int): QuestionPack {
        require(index in pack.questions.indices) { "Question index out of range" }
        return insertQuestion(pack, index + 1, pack.questions[index].copy())
    }

    fun moveQuestion(pack: QuestionPack, fromIndex: Int, toIndex: Int): QuestionPack {
        require(fromIndex in pack.questions.indices) { "Question index out of range" }
        if (pack.questions.size <= 1) return pack
        val target = toIndex.coerceIn(0, pack.questions.lastIndex)
        if (target == fromIndex) return pack

        val updated = pack.questions.toMutableList()
        val item = updated.removeAt(fromIndex)
        updated.add(target, item)
        return pack.copy(questions = updated)
    }

    fun deleteQuestion(pack: QuestionPack, index: Int): QuestionPack {
        require(index in pack.questions.indices) { "Question index out of range" }
        val updated = pack.questions.toMutableList().apply { removeAt(index) }
        return pack.copy(questions = updated)
    }

    private fun marker(kind: LiveQuestionKind): String = "$KIND_PREFIX${kind.key}"
}
