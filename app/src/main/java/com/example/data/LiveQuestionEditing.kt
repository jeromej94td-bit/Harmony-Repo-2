package com.example.data

import com.example.data.model.Question
import com.example.data.model.QuestionPack

enum class LiveQuestionKind(val key: String) {
    CHOICE("choice"),
    FREE_TEXT("free_text"),
    THIS_OR_THAT("this_or_that")
}

/**
 * Pure editing helpers for the developer-only Live Change mode.
 *
 * Harmony's existing content model remains unchanged:
 * - quiz question + options => choice question
 * - quiz question without options => free-text question
 * - `tot` packs keep using their native pair list
 */
object LiveQuestionEditing {

    fun createQuestion(
        kind: LiveQuestionKind,
        text: String,
        options: List<String> = emptyList(),
        defaultMine: String? = null
    ): Question {
        val cleanText = text.trim()
        require(cleanText.isNotEmpty()) { "Fragetext darf nicht leer sein" }

        val cleanOptions = options.map { it.trim() }.filter { it.isNotEmpty() }
        val storedOptions = when (kind) {
            LiveQuestionKind.CHOICE -> {
                require(cleanOptions.isNotEmpty()) { "Mindestens eine Antwortmöglichkeit ist erforderlich" }
                cleanOptions
            }
            LiveQuestionKind.FREE_TEXT -> emptyList()
            LiveQuestionKind.THIS_OR_THAT -> {
                require(cleanOptions.size == 2) { "Das-oder-Das benötigt genau zwei Optionen" }
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
        require(first.isNotEmpty() && second.isNotEmpty()) { "Beide Optionen sind erforderlich" }
        return first to second
    }
}

data class LiveChangeRecord(
    val timestamp: Long = System.currentTimeMillis(),
    val packId: String,
    val packTitle: String,
    val index: Int,
    val actionType: String,
    val details: String
)

object LiveChangeHistory {
    private val _records = mutableListOf<LiveChangeRecord>()
    val records: List<LiveChangeRecord> get() = _records

    fun record(
        packId: String,
        packTitle: String,
        index: Int,
        actionType: String,
        details: String
    ) {
        _records.add(
            LiveChangeRecord(
                packId = packId,
                packTitle = packTitle,
                index = index,
                actionType = actionType,
                details = details
            )
        )
    }

    fun clear() {
        _records.clear()
    }

    fun generateTxt(): String {
        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.GERMAN)
        val now = sdf.format(java.util.Date())
        return buildString {
            append("====================================================\n")
            append("HARMONY LIVE CHANGE PROTOKOLL\n")
            append("Erstellt am: $now\n")
            append("Gesamtanzahl Änderungen: ${_records.size}\n")
            append("====================================================\n\n")

            if (_records.isEmpty()) {
                append("Keine Änderungen in dieser Sitzung aufgezeichnet.\n")
            } else {
                _records.forEachIndexed { i, rec ->
                    val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.GERMAN).format(java.util.Date(rec.timestamp))
                    append("[${i + 1}] $time · Spiel: \"${rec.packTitle}\" (ID: ${rec.packId})\n")
                    append("    Aktion: ${rec.actionType} an Position ${rec.index + 1}\n")
                    append("    Details: ${rec.details}\n\n")
                }
            }
            append("====================================================\n")
        }
    }
}
