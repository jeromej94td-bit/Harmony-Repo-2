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
 * narrow or Android font scaling grows. Normal phones retain the current 122dp portrait
 * proportions.
 */
internal object WhoWouldLayoutPolicy {
    fun metrics(screenWidthDp: Int, screenHeightDp: Int, fontScale: Float): WhoWouldLayoutMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryCompact = screenHeightDp < 600 || screenWidthDp < 360 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || screenWidthDp < 400 || safeFontScale >= 1.15f

        return when {
            veryCompact -> WhoWouldLayoutMetrics(
                avatarSizeDp = 76,
                cardPaddingDp = 10,
                nameSizeSp = 17,
                avatarNameGapDp = 8,
                bottomRowHeightDp = 74,
                rowGapDp = 8
            )
            compact -> WhoWouldLayoutMetrics(
                avatarSizeDp = 96,
                cardPaddingDp = 12,
                nameSizeSp = 19,
                avatarNameGapDp = 10,
                bottomRowHeightDp = 84,
                rowGapDp = 10
            )
            else -> WhoWouldLayoutMetrics(
                avatarSizeDp = 122,
                cardPaddingDp = 16,
                nameSizeSp = 22,
                avatarNameGapDp = 16,
                bottomRowHeightDp = 94,
                rowGapDp = 12
            )
        }
    }
}
