package com.example.data

import com.example.data.model.Question
import com.example.data.model.QuestionPack

enum class LiveQuestionKind(val key: String) {
    CHOICE("choice"),
    FREE_TEXT("free_text"),
    THIS_OR_THAT("this_or_that")
}

/**
 * Pure editing helpers used by Live Change.
 *
 * The existing Harmony model stays unchanged: a quiz question with options is a choice
 * question, a quiz question without options is a free-text question, and a `tot` pack
 * continues to use its native pair list. This keeps existing content and exports fully
 * source-compatible.
 */
object LiveQuestionEditing {

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
            LiveQuestionKind.CHOICE -> {
                require(cleanOptions.isNotEmpty()) { "Choice questions require at least one option" }
                cleanOptions
            }
            LiveQuestionKind.FREE_TEXT -> emptyList()
            LiveQuestionKind.THIS_OR_THAT -> {
                require(cleanOptions.size == 2) { "This-or-that questions require exactly two options" }
                cleanOptions
            }
        }
        return Question(q = cleanText, options = storedOptions, defaultMine = defaultMine)
    }

    fun effectiveKind(question: Question, packType: String): LiveQuestionKind = when (packType) {
        "disc" -> LiveQuestionKind.FREE_TEXT
        "tot" -> LiveQuestionKind.THIS_OR_THAT
        else -> if (question.options.isEmpty()) LiveQuestionKind.FREE_TEXT else LiveQuestionKind.CHOICE
    }

    fun visibleOptions(question: Question): List<String> = question.options

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

    fun insertPair(pack: QuestionPack, index: Int, pair: Pair<String, String>): QuestionPack {
        val cleanPair = cleanPair(pair)
        val target = index.coerceIn(0, pack.pairs.size)
        val updated = pack.pairs.toMutableList().apply { add(target, cleanPair) }
        return pack.copy(pairs = updated)
    }

    fun replacePair(pack: QuestionPack, index: Int, pair: Pair<String, String>): QuestionPack {
        require(index in pack.pairs.indices) { "Pair index out of range" }
        val updated = pack.pairs.toMutableList().apply { this[index] = cleanPair(pair) }
        return pack.copy(pairs = updated)
    }

    fun duplicatePair(pack: QuestionPack, index: Int): QuestionPack {
        require(index in pack.pairs.indices) { "Pair index out of range" }
        return insertPair(pack, index + 1, pack.pairs[index])
    }

    fun movePair(pack: QuestionPack, fromIndex: Int, toIndex: Int): QuestionPack {
        require(fromIndex in pack.pairs.indices) { "Pair index out of range" }
        if (pack.pairs.size <= 1) return pack
        val target = toIndex.coerceIn(0, pack.pairs.lastIndex)
        if (target == fromIndex) return pack

        val updated = pack.pairs.toMutableList()
        val item = updated.removeAt(fromIndex)
        updated.add(target, item)
        return pack.copy(pairs = updated)
    }

    fun deletePair(pack: QuestionPack, index: Int): QuestionPack {
        require(index in pack.pairs.indices) { "Pair index out of range" }
        val updated = pack.pairs.toMutableList().apply { removeAt(index) }
        return pack.copy(pairs = updated)
    }

    private fun cleanPair(pair: Pair<String, String>): Pair<String, String> {
        val first = pair.first.trim()
        val second = pair.second.trim()
        require(first.isNotEmpty() && second.isNotEmpty()) { "Both this-or-that options are required" }
        return first to second
    }
}
