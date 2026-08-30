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
    val scaleInputTextSizeSp: Int,
    val scaleCardPaddingDp: Int,
    val finalePaddingDp: Int,
    val finaleTitleSizeSp: Int,
    val finaleBodySizeSp: Int,
    val finaleBodyLineHeightSp: Int
)

/**
 * Sizes fixed reveal surfaces from the height that the fullscreen mechanic can actually use,
 * not from the physical screen height. The mechanic shell reserves app/experience chrome before
 * rendering its stage, so a tall phone can still have a sub-700dp reveal area.
 */
internal object PairMechanicRevealLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): PairMechanicRevealMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val stageHeightDp = FullscreenMechanicStageHeightPolicy.stageHeightDp(screenHeightDp)
        val veryCompact = stageHeightDp < 600 || safeFontScale >= 1.30f
        val compact = stageHeightDp < 700 || safeFontScale >= 1.15f

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
                scaleInputTextSizeSp = 20,
                scaleCardPaddingDp = 10,
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
                scaleInputTextSizeSp = 23,
                scaleCardPaddingDp = 14,
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
                scaleInputTextSizeSp = 27,
                scaleCardPaddingDp = 20,
                finalePaddingDp = 12,
                finaleTitleSizeSp = 17,
                finaleBodySizeSp = 14,
                finaleBodyLineHeightSp = 18
            )
        }
    }
}
