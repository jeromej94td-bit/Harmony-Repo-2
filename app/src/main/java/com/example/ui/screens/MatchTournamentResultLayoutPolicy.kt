package com.example.ui.screens

import kotlin.math.ceil

internal data class MatchTournamentResultMetrics(
    val trophySizeSp: Int,
    val winnerMinHeightDp: Int,
    val gapDp: Int,
    val buttonHeightDp: Int,
    val estimatedTrophyHeightDp: Int
) {
    val minimumResultHeightDp: Int
        get() = estimatedTrophyHeightDp + winnerMinHeightDp + buttonHeightDp + gapDp * 2
}

/**
 * Keeps the tournament winner state usable on short phones and with large font scaling.
 *
 * The old layout always reserved a 64sp trophy and at least 190dp for the winner card.
 * Together with the primary action that can exceed the remaining mechanic-stage height on
 * compact devices, causing the card or submit button to be clipped.
 */
internal object MatchTournamentResultLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): MatchTournamentResultMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f
        val buttonHeightDp = if (screenHeightDp < 700) 52 else 58

        val trophySizeSp: Int
        val winnerMinHeightDp: Int
        val gapDp: Int

        when {
            veryCompact -> {
                trophySizeSp = 34
                winnerMinHeightDp = 120
                gapDp = 8
            }
            compact -> {
                trophySizeSp = 44
                winnerMinHeightDp = 145
                gapDp = 10
            }
            else -> {
                trophySizeSp = 64
                winnerMinHeightDp = 190
                gapDp = 14
            }
        }

        return MatchTournamentResultMetrics(
            trophySizeSp = trophySizeSp,
            winnerMinHeightDp = winnerMinHeightDp,
            gapDp = gapDp,
            buttonHeightDp = buttonHeightDp,
            estimatedTrophyHeightDp = ceil(trophySizeSp * safeFontScale).toInt()
        )
    }
}
