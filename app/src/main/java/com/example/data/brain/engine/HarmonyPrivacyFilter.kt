package com.example.data.brain.engine

object HarmonyPrivacyFilter {

    private val EMAIL_REGEX = Regex("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+")
    private val PHONE_REGEX = Regex("(?:\\+?\\d{1,3}[- ]?)?\\(?\\d{2,5}\\)?[- ]?\\d{3,9}")
    private val URL_PATH_REGEX = Regex("(file://|content://|/data/user/|/storage/|/var/)[^\\s]+")
    private val ADDRESS_REGEX = Regex("\\b[A-ZÄÖÜ][a-zäöüß]+(?:straße|str\\.|gasse|weg|platz|allee)\\s+\\d+[a-zA-Z]?\\b", RegexOption.IGNORE_CASE)
    private val POSTCODE_REGEX = Regex("\\b\\d{5}\\b")
    private val COORDINATES_REGEX = Regex("-?\\d{1,3}\\.\\d{4,},\\s*-?\\d{1,3}\\.\\d{4,}")

    /**
     * Sanitizes raw text by removing email addresses, phone numbers, addresses, file paths, and exact names.
     */
    fun sanitizeText(
        text: String?,
        userName: String? = null,
        partnerName: String? = null
    ): String {
        if (text.isNullOrBlank()) return ""

        var sanitized = text
            .replace(EMAIL_REGEX, "[E-Mail entfernt]")
            .replace(COORDINATES_REGEX, "[Standort entfernt]")
            .replace(URL_PATH_REGEX, "[Pfad entfernt]")
            .replace(ADDRESS_REGEX, "[Adresse entfernt]")
            .replace(POSTCODE_REGEX, "[PLZ]")

        // Phone numbers (ensure we don't accidentally wipe standard 1-4 digit numbers)
        if (sanitized.contains("+") || sanitized.count { it.isDigit() } >= 6) {
            sanitized = sanitized.replace(PHONE_REGEX, "[Telefonnummer entfernt]")
        }

        // Anonymize user and partner names if provided
        if (!userName.isNullOrBlank() && userName.length >= 2) {
            sanitized = sanitized.replace(Regex("\\b${Regex.escape(userName)}\\b", RegexOption.IGNORE_CASE), "Person A")
        }
        if (!partnerName.isNullOrBlank() && partnerName.length >= 2) {
            sanitized = sanitized.replace(Regex("\\b${Regex.escape(partnerName)}\\b", RegexOption.IGNORE_CASE), "Person B")
        }

        return sanitized.trim()
    }
}
