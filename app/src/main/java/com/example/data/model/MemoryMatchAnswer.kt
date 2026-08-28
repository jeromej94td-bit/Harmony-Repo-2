package com.example.data.model

data class MemoryMatchAnswer(
    val text: String,
    val imagePath: String? = null
)

object MemoryMatchAnswerCodec {
    private const val PREFIX = "memory-match-v1:"
    private const val SEPARATOR = "\u001E"

    private fun clean(value: String): String = value.replace(SEPARATOR, " ")

    fun encode(text: String, imagePath: String?): String = buildString {
        append(PREFIX)
        append(clean(text))
        append(SEPARATOR)
        append(clean(imagePath.orEmpty()))
    }

    fun decode(value: String): MemoryMatchAnswer? {
        if (!value.startsWith(PREFIX)) return null
        val parts = value.removePrefix(PREFIX).split(SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        return MemoryMatchAnswer(
            text = parts[0],
            imagePath = parts[1].ifBlank { null }
        )
    }
}
