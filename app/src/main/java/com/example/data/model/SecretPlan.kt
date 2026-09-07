package com.example.data.model

import java.util.Locale

sealed interface SecretPlanChoice {
    val text: String
    data class Preset(override val text: String) : SecretPlanChoice
    data class Custom(override val text: String) : SecretPlanChoice
}

data class SecretPlanPair(val first: SecretPlanChoice, val second: SecretPlanChoice) {
    constructor(first: String, second: String) : this(SecretPlanChoice.Preset(first), SecretPlanChoice.Preset(second))
}

data class SecretPlanPlanLine(val chapterIndex: Int, val first: SecretPlanChoice, val second: SecretPlanChoice) {
    val isShared: Boolean get() = SecretPlanProgress.normalized(first.text) == SecretPlanProgress.normalized(second.text)
}

object SecretPlanAnswerCodec {
    private const val PREFIX = "secret-plan-v1:"
    private const val ITEM_SEPARATOR = "\u001F"
    private const val TYPE_SEPARATOR = "\u001E"
    private const val MAX_CUSTOM_LENGTH = 80

    fun encode(first: String, second: String): String = encode(SecretPlanChoice.Preset(first), SecretPlanChoice.Preset(second))

    fun encode(first: SecretPlanChoice, second: SecretPlanChoice): String =
        PREFIX + encodeChoice(first) + ITEM_SEPARATOR + encodeChoice(second)

    fun decode(value: String): SecretPlanPair? {
        if (!value.startsWith(PREFIX)) return null
        val parts = value.removePrefix(PREFIX).split(ITEM_SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        return SecretPlanPair(decodeChoice(parts[0]) ?: return null, decodeChoice(parts[1]) ?: return null)
    }

    private fun encodeChoice(choice: SecretPlanChoice): String {
        val text = clean(choice.text) ?: return "invalid$TYPE_SEPARATOR"
        val type = if (choice is SecretPlanChoice.Custom) "custom" else "preset"
        return "$type$TYPE_SEPARATOR$text"
    }

    private fun decodeChoice(value: String): SecretPlanChoice? {
        val parts = value.split(TYPE_SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        val text = clean(parts[1]) ?: return null
        return when (parts[0]) {
            "preset" -> SecretPlanChoice.Preset(text)
            "custom" -> text.takeIf { it.length <= MAX_CUSTOM_LENGTH }?.let(SecretPlanChoice::Custom)
            else -> null
        }
    }

    private fun clean(value: String): String? = value
        .takeUnless { it.contains(ITEM_SEPARATOR) || it.contains(TYPE_SEPARATOR) }
        ?.trim()
        ?.replace(Regex("\\s+"), " ")
        ?.takeIf(String::isNotBlank)
}

object SecretPlanProgress {
    fun normalized(value: String): String = value
        .trim()
        .replace(Regex("\\s+"), " ")
        .lowercase(Locale.ROOT)

    fun lines(history: List<AnswerEntity>, currentIndex: Int, current: SecretPlanPair): List<SecretPlanPlanLine>? {
        val pairs = history.asSequence()
            .filter { it.packId == SecretPlanCatalog.PACK_ID && it.questionIndex in 0..2 }
            .mapNotNull { answer -> SecretPlanAnswerCodec.decode(answer.answerText)?.let { answer.questionIndex to it } }
            .toMap()
            .toMutableMap()
        if (currentIndex !in 0..2) return null
        pairs[currentIndex] = current
        if ((0..2).any { it !in pairs }) return null
        return (0..2).map { index -> SecretPlanPlanLine(index, pairs.getValue(index).first, pairs.getValue(index).second) }
    }
}
