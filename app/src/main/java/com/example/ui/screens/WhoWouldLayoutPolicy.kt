package com.example.ui.screens

internal data class WhoWouldLayoutMetrics(
    val avatarSizeDp: Int,
    val cardPaddingDp: Int,
    val nameSizeSp: Int,
    val avatarNameGapDp: Int,
    val bottomRowHeightDp: Int,
    val rowGapDp: Int
) {
    val personCardFixedContentDp: Int
        get() = cardPaddingDp * 2 + avatarSizeDp + avatarNameGapDp
}

/**
 * Protects the Paarlabor person cards from clipping when the fullscreen stage becomes short,
 * narrow or Android font scaling grows. On regular phones the portraits deliberately dominate
 * the interaction so the two people are immediately readable as the primary answer targets.
 */
internal object WhoWouldLayoutPolicy {
    fun metrics(screenWidthDp: Int, screenHeightDp: Int, fontScale: Float): WhoWouldLayoutMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryCompact = screenHeightDp < 600 || screenWidthDp < 360 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || screenWidthDp < 400 || safeFontScale >= 1.15f

        return when {
            veryCompact -> WhoWouldLayoutMetrics(
                avatarSizeDp = 84,
                cardPaddingDp = 9,
                nameSizeSp = 18,
                avatarNameGapDp = 8,
                bottomRowHeightDp = 72,
                rowGapDp = 8
            )
            compact -> WhoWouldLayoutMetrics(
                avatarSizeDp = 108,
                cardPaddingDp = 11,
                nameSizeSp = 20,
                avatarNameGapDp = 10,
                bottomRowHeightDp = 82,
                rowGapDp = 10
            )
            else -> WhoWouldLayoutMetrics(
                avatarSizeDp = 140,
                cardPaddingDp = 15,
                nameSizeSp = 23,
                avatarNameGapDp = 14,
                bottomRowHeightDp = 90,
                rowGapDp = 12
            )
        }
    }
}
