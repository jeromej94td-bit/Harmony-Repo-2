package com.example.data.model

enum class QuestionInteractionKind {
    STANDARD,
    RANK_ORDER,
    PERSON_ASSIGNMENT
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

        return if (pack.cat == "h360_ranking" || pack.tags.any { it == "mechanik_ranking" }) {
            QuestionInteractionKind.RANK_ORDER
        } else {
            QuestionInteractionKind.STANDARD
        }
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
