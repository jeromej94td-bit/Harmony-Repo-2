package com.example.ui.session

internal fun normalizeHarmonyDisplayName(rawValue: String): String? {
    val normalized = rawValue.trim()
    return normalized.takeIf { it.isNotEmpty() && it.length <= 60 }
}
