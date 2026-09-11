package com.example.data.couple

/**
 * Opt-in gate for the whole-pack partner reveal pilot.
 *
 * Keep this deliberately narrow until the pilot has been verified with a real paired couple.
 * Existing packs continue using the established per-question reveal behavior.
 */
object PartnerPackRevealPolicy {
    const val PILOT_PACK_ID = "aufwaermen1"
    const val PILOT_DISPLAY_TITLE = "Einander kennenlernen"
    const val PILOT_QUESTION_COUNT = 10

    fun isWholePackRevealEnabled(packId: String): Boolean = packId == PILOT_PACK_ID

    fun canRevealPartnerAnswers(myCompleted: Boolean, partnerCompleted: Boolean): Boolean =
        myCompleted && partnerCompleted

    fun completionNotificationBody(partnerName: String): String {
        val safeName = partnerName.trim().ifBlank { "Dein Partner" }
        return "$PILOT_DISPLAY_TITLE wurde von $safeName ausgefüllt. Antworte jetzt, um die Antworten zu sehen."
    }
}
