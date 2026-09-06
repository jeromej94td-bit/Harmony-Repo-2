package com.example.data.model

data class MemoryMatchAnswer(
    val text: String,
    val imagePath: String? = null,
    val partnerText: String = "",
    val partnerImagePath: String? = null
) {
    val hasPartnerMemory: Boolean
        get() = partnerText.isNotBlank() || !partnerImagePath.isNullOrBlank()
}

object MemoryMatchAnswerCodec {
    private const val LEGACY_PREFIX = "memory-match-v1:"
    private const val PREFIX = "memory-match-v2:"
    private const val SEPARATOR = "\u001E"

    private fun clean(value: String): String = value.replace(SEPARATOR, " ")

    /**
     * Kept for compatibility with older call sites. New pair memories should use the four-argument
     * overload so both private perspectives survive navigation and app restarts.
     */
    fun encode(text: String, imagePath: String?): String = buildString {
        append(LEGACY_PREFIX)
        append(clean(text))
        append(SEPARATOR)
        append(clean(imagePath.orEmpty()))
    }

    fun encode(
        text: String,
        imagePath: String?,
        partnerText: String,
        partnerImagePath: String?
    ): String = buildString {
        append(PREFIX)
        append(clean(text))
        append(SEPARATOR)
        append(clean(imagePath.orEmpty()))
        append(SEPARATOR)
        append(clean(partnerText))
        append(SEPARATOR)
        append(clean(partnerImagePath.orEmpty()))
    }

    fun decode(value: String): MemoryMatchAnswer? = when {
        value.startsWith(PREFIX) -> decodeV2(value)
        value.startsWith(LEGACY_PREFIX) -> decodeV1(value)
        else -> null
    }

    private fun decodeV2(value: String): MemoryMatchAnswer? {
        val parts = value.removePrefix(PREFIX).split(SEPARATOR, limit = 4)
        if (parts.size != 4) return null
        return MemoryMatchAnswer(
            text = parts[0],
            imagePath = parts[1].ifBlank { null },
            partnerText = parts[2],
            partnerImagePath = parts[3].ifBlank { null }
        )
    }

    private fun decodeV1(value: String): MemoryMatchAnswer? {
        val parts = value.removePrefix(LEGACY_PREFIX).split(SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        return MemoryMatchAnswer(
            text = parts[0],
            imagePath = parts[1].ifBlank { null }
        )
    }
}
