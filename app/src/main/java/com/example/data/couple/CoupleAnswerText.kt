package com.example.data.couple

/**
 * Normalizes human text without damaging the control-character separators used by Harmony's
 * structured answer codecs. Kotlin's String.trim() treats U+001E/U+001F as whitespace; trimming
 * an encoded payload can therefore remove a trailing empty field and make the answer undecodable.
 */
private val structuredAnswerPrefix = Regex("^[a-z][a-z0-9-]*-v\\d+:")

internal fun normalizeCoupleAnswerText(value: String): String =
    if (structuredAnswerPrefix.containsMatchIn(value)) value else value.trim()
