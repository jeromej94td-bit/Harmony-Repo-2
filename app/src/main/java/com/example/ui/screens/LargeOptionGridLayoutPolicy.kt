package com.example.ui.screens

internal data class LargeOptionGridLayoutMetrics(
    val useScroll: Boolean,
    val scrollRowHeightDp: Int
)

/**
 * Keeps multi-row fullscreen option grids usable when the available stage becomes too short.
 * Normal layouts keep the existing equal-weight rows; only compressed layouts switch to a
 * bounded vertical scroll area with a readable minimum row height.
 */
internal object LargeOptionGridLayoutPolicy {
    fun metrics(
        availableHeightDp: Int,
        rowCount: Int,
        gapDp: Int,
        fontScale: Float
    ): LargeOptionGridLayoutMetrics {
        val safeRows = rowCount.coerceAtLeast(0)
        val safeGap = gapDp.coerceAtLeast(0)
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val minimumRowHeight = when {
            safeFontScale >= 1.30f -> 92
            safeFontScale >= 1.15f -> 82
            else -> 72
        }
        val requiredHeight = if (safeRows == 0) {
            0
        } else {
            safeRows * minimumRowHeight + (safeRows - 1) * safeGap
        }

        return LargeOptionGridLayoutMetrics(
            useScroll = safeRows > 0 && requiredHeight > availableHeightDp.coerceAtLeast(0),
            scrollRowHeightDp = minimumRowHeight
        )
    }
}
