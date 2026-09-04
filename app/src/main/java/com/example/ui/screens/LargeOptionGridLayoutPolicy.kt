package com.example.ui.screens

internal data class LargeOptionGridLayoutMetrics(
    val useScroll: Boolean,
    val scrollRowHeightDp: Int
)

/**
 * Fullscreen option grids are glanceable interactions: every card must stay in the viewport.
 * Rows therefore compress to the available stage instead of switching to a scroll container.
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
        val safeHeight = availableHeightDp.coerceAtLeast(0)
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val preferredRowHeight = when {
            safeFontScale >= 1.30f -> 92
            safeFontScale >= 1.15f -> 82
            else -> 72
        }
        val fittedRowHeight = if (safeRows == 0) {
            preferredRowHeight
        } else {
            ((safeHeight - (safeRows - 1) * safeGap) / safeRows)
                .coerceAtLeast(1)
                .coerceAtMost(preferredRowHeight)
        }

        return LargeOptionGridLayoutMetrics(
            useScroll = false,
            scrollRowHeightDp = fittedRowHeight
        )
    }
}
