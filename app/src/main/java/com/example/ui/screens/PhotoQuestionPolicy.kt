package com.example.ui.screens

import java.security.MessageDigest

enum class PhotoQuestionMode {
    CHOICE_WITH_OPTIONAL_PHOTO,
    PHOTO_ONLY
}

data class PhotoQuestionPresentation(
    val question: String,
    val options: List<String>
)

object PhotoQuestionPolicy {
    private const val OPTIONAL_PACK = "gespraechsanreger"
    private const val PHOTO_ONLY_PACK = "schnapp"

    private const val LEGACY_OPTIONAL_QUESTION = "Was ist dein Lieblingsfoto von uns? 📸"
    private const val LEGACY_PHOTO_ONLY_QUESTION = "Welches gemeinsame Foto ist dein Lieblingsfoto?"

    /**
     * Compatibility-only fallback for callers that do not have the raw question yet.
     * New runtime routing should pass rawQuestion so a moved question cannot inherit behavior by index.
     */
    fun modeFor(packId: String, questionIndex: Int): PhotoQuestionMode? =
        modeFor(packId, questionIndex, rawQuestion = null)

    fun modeFor(
        packId: String,
        questionIndex: Int,
        rawQuestion: String?
    ): PhotoQuestionMode? {
        if (!rawQuestion.isNullOrBlank()) {
            return when (packId.trim()) {
                OPTIONAL_PACK -> rawQuestion.trim()
                    .takeIf { it == LEGACY_OPTIONAL_QUESTION }
                    ?.let { PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO }

                PHOTO_ONLY_PACK -> rawQuestion.trim()
                    .takeIf { it == LEGACY_PHOTO_ONLY_QUESTION }
                    ?.let { PhotoQuestionMode.PHOTO_ONLY }

                else -> null
            }
        }

        return when {
            packId == OPTIONAL_PACK && questionIndex == 1 -> PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO
            packId == PHOTO_ONLY_PACK && questionIndex == 1 -> PhotoQuestionMode.PHOTO_ONLY
            else -> null
        }
    }

    fun modeForQuestion(question: String): PhotoQuestionMode? = when (question.trim()) {
        LEGACY_OPTIONAL_QUESTION -> PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO
        LEGACY_PHOTO_ONLY_QUESTION -> PhotoQuestionMode.PHOTO_ONLY
        else -> null
    }

    fun presentation(mode: PhotoQuestionMode): PhotoQuestionPresentation = when (mode) {
        PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO -> PhotoQuestionPresentation(
            question = "Welche Art von gemeinsamen Fotos magst du am liebsten? 📸",
            options = listOf(
                "Lustige Schnappschüsse",
                "Romantische Fotos",
                "Urlaubsfotos"
            )
        )

        PhotoQuestionMode.PHOTO_ONLY -> PhotoQuestionPresentation(
            question = "Welches gemeinsame Foto bedeutet dir besonders viel? 📸",
            options = emptyList()
        )
    }

    fun normalizeLegacyChoice(choice: String?): String? = when (choice?.trim()) {
        "Ein lustiges Bild" -> "Lustige Schnappschüsse"
        "Ein romantisches Bild" -> "Romantische Fotos"
        "Aus dem Urlaub" -> "Urlaubsfotos"
        else -> choice?.trim()?.takeIf { it.isNotEmpty() }
    }

    fun storageFileName(question: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(question.trim().toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }
        return "photo_${digest.take(32)}.img"
    }
}
