package com.example.ui.screens

internal data class ScenarioResultMetrics(
    val trophyContainerDp: Int,
    val trophySizeSp: Int,
    val cardPaddingDp: Int,
    val titleSizeSp: Int,
    val titleLineHeightSp: Int,
    val bodySizeSp: Int,
    val bodyLineHeightSp: Int,
    val metaSizeSp: Int,
    val gapDp: Int,
    val buttonHeightDp: Int
) {
    val fixedChromeHeightDp: Int
        get() = trophyContainerDp + buttonHeightDp + gapDp * 2
}

/**
 * Keeps the eight-decision scenario finale inside the fullscreen mechanic stage on short
 * phones and when Android font scaling is large. Normal phones retain the existing spacious
 * 150dp trophy / 72sp presentation.
 */
internal object ScenarioResultLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): ScenarioResultMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f
        val buttonHeightDp = if (screenHeightDp < 700) 52 else 58

        return when {
            veryCompact -> ScenarioResultMetrics(
                trophyContainerDp = 78,
                trophySizeSp = 40,
                cardPaddingDp = 10,
                titleSizeSp = 20,
                titleLineHeightSp = 24,
                bodySizeSp = 13,
                bodyLineHeightSp = 17,
                metaSizeSp = 11,
                gapDp = 6,
                buttonHeightDp = buttonHeightDp
            )
            compact -> ScenarioResultMetrics(
                trophyContainerDp = 104,
                trophySizeSp = 52,
                cardPaddingDp = 14,
                titleSizeSp = 23,
                titleLineHeightSp = 27,
                bodySizeSp = 14,
                bodyLineHeightSp = 19,
                metaSizeSp = 12,
                gapDp = 8,
                buttonHeightDp = buttonHeightDp
            )
            else -> ScenarioResultMetrics(
                trophyContainerDp = 150,
                trophySizeSp = 72,
                cardPaddingDp = 20,
                titleSizeSp = 27,
                titleLineHeightSp = 32,
                bodySizeSp = 16,
                bodyLineHeightSp = 22,
                metaSizeSp = 14,
                gapDp = 12,
                buttonHeightDp = buttonHeightDp
            )
        }
    }
}
