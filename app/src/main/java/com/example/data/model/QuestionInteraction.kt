package com.example.data.model

enum class QuestionInteractionKind {
    STANDARD,
    RANK_ORDER,
    PERSON_ASSIGNMENT,
    PARTNER_PREDICTION,
    SECRET_CHOICE,
    SCALE_MATCH,
    WHO_WOULD,
    MEMORY_MATCH,
    SCENARIO,
    PRIORITY_POKER,
    MATCH_TOURNAMENT,
    DEEP_TALK
}

enum class PersonSide(val token: String) {
    USER("user"),
    PARTNER("partner");

    companion object {
        fun fromToken(token: String): PersonSide? = entries.firstOrNull { it.token == token }
    }
}

object QuestionInteractionPolicy {
    private const val PERSON_ASSIGNMENT_PREFIX = "interaction_person_assignment_"
    private const val RANK_ORDER_PREFIX = "interaction_rank_order_"

    /**
     * Explicit per-question interaction metadata lives in pack tags so it survives
     * the existing Dev-Studio/export pipeline without changing the stored question schema.
     * Pack-level Harmony-360 mechanic tags then provide the reusable default interaction.
     */
    fun resolve(pack: QuestionPack, questionIndex: Int): QuestionInteractionKind {
        if (pack.tags.any { it == "$PERSON_ASSIGNMENT_PREFIX$questionIndex" }) {
            return QuestionInteractionKind.PERSON_ASSIGNMENT
        }
        if (pack.tags.any { it == "$RANK_ORDER_PREFIX$questionIndex" }) {
            return QuestionInteractionKind.RANK_ORDER
        }

        // The first shipped role-assignment question predates the interaction tag.
        // Keep the metadata centralized here rather than coupling UI behavior to question text.
        if (pack.id == "h500_414_rollenverteilung_ranking" && questionIndex == 1) {
            return QuestionInteractionKind.PERSON_ASSIGNMENT
        }

        return when {
            pack.tags.any { it == "mechanik_prognose" } || pack.cat == "h360_prognose" ->
                QuestionInteractionKind.PARTNER_PREDICTION

            pack.tags.any { it == "mechanik_geheime_wahl" } || pack.cat == "h360_geheim" ->
                QuestionInteractionKind.SECRET_CHOICE

            pack.tags.any { it == "mechanik_skala" } || pack.cat == "h360_skala" ->
                QuestionInteractionKind.SCALE_MATCH

            pack.tags.any { it == "mechanik_wer_eher" } ->
                QuestionInteractionKind.WHO_WOULD

            pack.tags.any { it == "mechanik_memory" } ->
                QuestionInteractionKind.MEMORY_MATCH

            pack.tags.any { it == "mechanik_szenario" } || pack.cat == "h360_szenario" ->
                QuestionInteractionKind.SCENARIO

            pack.tags.any { it == "mechanik_prioritaet" } ->
                QuestionInteractionKind.PRIORITY_POKER

            pack.tags.any { it == "mechanik_entweder_oder" } ->
                QuestionInteractionKind.MATCH_TOURNAMENT

            pack.tags.any { it == "mechanik_deep_talk" } ->
                QuestionInteractionKind.DEEP_TALK

            pack.cat == "h360_ranking" || pack.tags.any { it == "mechanik_ranking" } ->
                QuestionInteractionKind.RANK_ORDER

            else -> QuestionInteractionKind.STANDARD
        }
    }
}

/**
 * Specialized boards own their options. Some generated source questions still append the same
 * choices to the prompt (for example "Ordne: A, B, C, D"). This policy removes only a detected
 * repeated option tail so the fullscreen board never renders the same choices twice.
 */
object InteractionPromptPolicy {
    private val trailingInstruction = Regex(
        pattern = "(?i)\\s*(ordne|ordnet|rank|rankt|ranking|sortiere|sortiert|reihe|reiht)(\\s+(sie|es|diese|die))?\\s*[:\\-–—]?\\s*$"
    )

    fun displayPrompt(question: String, options: List<String>): String {
        val prompt = question.trim()
        if (prompt.isBlank()) return prompt

        val occurrences = options
            .asSequence()
            .map(String::trim)
            .filter { it.length >= 2 }
            .mapNotNull { option ->
                prompt.indexOf(option, ignoreCase = true).takeIf { it >= 0 }
            }
            .sorted()
            .toList()

        // One incidental word can legitimately occur in a question. Two or more repeated choices
        // are a strong signal that the generated prompt contains an answer list.
        if (occurrences.size < 2) return prompt

        var cleaned = prompt.substring(0, occurrences.first()).trim()
        cleaned = cleaned.trimEnd(' ', ':', ',', ';', '-', '–', '—')
        cleaned = cleaned.replace(trailingInstruction, "").trim()
        cleaned = cleaned.trimEnd(' ', ':', ',', ';', '-', '–', '—')
        return cleaned.ifBlank { prompt }
    }
}

data class PairedChoiceAnswer(
    val first: String,
    val second: String
)

object PairedChoiceAnswerCodec {
    private const val PREFIX = "paired-choice-v1:"
    private const val SEPARATOR = "\u001E"

    private fun clean(value: String): String = value.replace(SEPARATOR, " ")

    fun encode(first: String, second: String): String =
        PREFIX + clean(first) + SEPARATOR + clean(second)

    fun decode(value: String): PairedChoiceAnswer? {
        if (!value.startsWith(PREFIX)) return null
        val parts = value.removePrefix(PREFIX).split(SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        return PairedChoiceAnswer(parts[0], parts[1])
    }
}

data class PredictionAnswer(
    val prediction: String,
    val actual: String
) {
    val isHit: Boolean get() = prediction == actual
}

object PredictionAnswerCodec {
    private const val PREFIX = "prediction-v1:"
    private const val SEPARATOR = "\u001E"

    private fun clean(value: String): String = value.replace(SEPARATOR, " ")

    fun encode(prediction: String, actual: String): String =
        PREFIX + clean(prediction) + SEPARATOR + clean(actual)

    fun decode(value: String): PredictionAnswer? {
        if (!value.startsWith(PREFIX)) return null
        val parts = value.removePrefix(PREFIX).split(SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        return PredictionAnswer(parts[0], parts[1])
    }
}

object PersonAssignmentCodec {
    private const val PREFIX = "person-assignment-v1:"
    private const val ITEM_SEPARATOR = "\u001F"
    private const val VALUE_SEPARATOR = "\u001E"

    private fun clean(value: String): String = value
        .replace(ITEM_SEPARATOR, " ")
        .replace(VALUE_SEPARATOR, " ")

    fun encode(options: List<String>, assignments: Map<String, PersonSide>): String {
        require(options.distinct().size == options.size) { "Person-assignment options must be unique" }
        require(options.all { assignments.containsKey(it) }) { "Every role must be assigned" }
        return PREFIX + options.joinToString(ITEM_SEPARATOR) { option ->
            clean(option) + VALUE_SEPARATOR + assignments.getValue(option).token
        }
    }

    fun decode(value: String, options: List<String>): LinkedHashMap<String, PersonSide>? {
        if (!value.startsWith(PREFIX) || options.distinct().size != options.size) return null
        val payload = value.removePrefix(PREFIX)
        if (payload.isBlank() && options.isNotEmpty()) return null

        val decoded = linkedMapOf<String, PersonSide>()
        val records = if (payload.isBlank()) emptyList() else payload.split(ITEM_SEPARATOR)
        for (record in records) {
            val parts = record.split(VALUE_SEPARATOR, limit = 2)
            if (parts.size != 2 || decoded.containsKey(parts[0])) return null
            val side = PersonSide.fromToken(parts[1]) ?: return null
            decoded[parts[0]] = side
        }

        if (decoded.keys.toList() != options) return null
        return LinkedHashMap(decoded)
    }
}

object RankingAnswerCodec {
    private const val PREFIX = "ranking-v1:"
    private const val SEPARATOR = "\u001F"

    private fun clean(value: String): String = value.replace(SEPARATOR, " ")

    fun encode(order: List<String>): String = PREFIX + order.joinToString(SEPARATOR) { clean(it) }

    fun decode(value: String, options: List<String>): List<String>? {
        if (!value.startsWith(PREFIX) || options.distinct().size != options.size) return null
        val payload = value.removePrefix(PREFIX)
        val order = if (payload.isBlank()) emptyList() else payload.split(SEPARATOR)
        if (order.size != options.size) return null
        if (order.distinct().size != order.size) return null
        if (order.toSet() != options.toSet()) return null
        return order
    }
}
