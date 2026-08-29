package com.example.ui.screens

internal data class PairMechanicRevealMetrics(
    val predictionCardMinHeightDp: Int,
    val secretCardMinHeightDp: Int,
    val scaleInputHeightDp: Int,
    val scaleRevealHeightDp: Int,
    val predictionTitleSizeSp: Int,
    val predictionCountSizeSp: Int,
    val sparkleSizeSp: Int,
    val secretTitleSizeSp: Int,
    val scaleTitleSizeSp: Int,
    val finalePaddingDp: Int,
    val finaleTitleSizeSp: Int,
    val finaleBodySizeSp: Int,
    val finaleBodyLineHeightSp: Int
)

/**
 * Shrinks the fixed reveal surfaces of the pair mechanics only when the fullscreen stage is
 * tight. Normal phones keep the original 155/160/150/175dp proportions and text sizes.
 */
internal object PairMechanicRevealLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): PairMechanicRevealMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f

        return when {
            veryCompact -> PairMechanicRevealMetrics(
                predictionCardMinHeightDp = 100,
                secretCardMinHeightDp = 105,
                scaleInputHeightDp = 100,
                scaleRevealHeightDp = 115,
                predictionTitleSizeSp = 21,
                predictionCountSizeSp = 14,
                sparkleSizeSp = 17,
                secretTitleSizeSp = 20,
                scaleTitleSizeSp = 21,
                finalePaddingDp = 8,
                finaleTitleSizeSp = 14,
                finaleBodySizeSp = 12,
                finaleBodyLineHeightSp = 15
            )
            compact -> PairMechanicRevealMetrics(
                predictionCardMinHeightDp = 125,
                secretCardMinHeightDp = 130,
                scaleInputHeightDp = 125,
                scaleRevealHeightDp = 145,
                predictionTitleSizeSp = 24,
                predictionCountSizeSp = 15,
                sparkleSizeSp = 19,
                secretTitleSizeSp = 23,
                scaleTitleSizeSp = 24,
                finalePaddingDp = 10,
                finaleTitleSizeSp = 15,
                finaleBodySizeSp = 13,
                finaleBodyLineHeightSp = 17
            )
            else -> PairMechanicRevealMetrics(
                predictionCardMinHeightDp = 155,
                secretCardMinHeightDp = 160,
                scaleInputHeightDp = 150,
                scaleRevealHeightDp = 175,
                predictionTitleSizeSp = 28,
                predictionCountSizeSp = 17,
                sparkleSizeSp = 21,
                secretTitleSizeSp = 27,
                scaleTitleSizeSp = 28,
                finalePaddingDp = 12,
                finaleTitleSizeSp = 17,
                finaleBodySizeSp = 14,
                finaleBodyLineHeightSp = 18
            )
        }
    }
}
