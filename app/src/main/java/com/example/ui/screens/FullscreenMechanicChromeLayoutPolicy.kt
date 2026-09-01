package com.example.ui.screens

internal data class FullscreenMechanicChromeMetrics(
    val showInstruction: Boolean,
    val verticalPaddingDp: Int,
    val headerGapDp: Int,
    val contentGapDp: Int,
    val kickerSizeSp: Int,
    val instructionSizeSp: Int,
    val instructionLineHeightSp: Int
)

/**
 * Keeps the shared fullscreen header subordinate to the interactive mechanic on short displays.
 * The question always remains visible; only the secondary instruction is omitted on extremely
 * short landscape heights where it would otherwise consume the answer area.
 */
internal object FullscreenMechanicChromeLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): FullscreenMechanicChromeMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryShort = screenHeightDp < 480
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f

        return when {
            veryShort -> FullscreenMechanicChromeMetrics(
                showInstruction = false,
                verticalPaddingDp = 6,
                headerGapDp = 3,
                contentGapDp = 6,
                kickerSizeSp = 10,
                instructionSizeSp = 11,
                instructionLineHeightSp = 14
            )
            veryCompact -> FullscreenMechanicChromeMetrics(
                showInstruction = true,
                verticalPaddingDp = 10,
                headerGapDp = 4,
                contentGapDp = 8,
                kickerSizeSp = 11,
                instructionSizeSp = 11,
                instructionLineHeightSp = 15
            )
            compact -> FullscreenMechanicChromeMetrics(
                showInstruction = true,
                verticalPaddingDp = 12,
                headerGapDp = 4,
                contentGapDp = 10,
                kickerSizeSp = 11,
                instructionSizeSp = 12,
                instructionLineHeightSp = 16
            )
            else -> FullscreenMechanicChromeMetrics(
                showInstruction = true,
                verticalPaddingDp = 18,
                headerGapDp = 8,
                contentGapDp = 18,
                kickerSizeSp = 13,
                instructionSizeSp = 14,
                instructionLineHeightSp = 19
            )
        }
    }

    fun questionSizeSp(screenHeightDp: Int, questionLength: Int, fontScale: Float): Int {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryShort = screenHeightDp < 480
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f

        return when {
            veryShort && questionLength > 95 -> 14
            veryShort && questionLength > 70 -> 15
            veryShort && questionLength > 48 -> 16
            veryShort -> 18
            veryCompact && questionLength > 95 -> 16
            veryCompact && questionLength > 70 -> 17
            veryCompact && questionLength > 48 -> 19
            veryCompact -> 21
            compact && questionLength > 95 -> 18
            compact && questionLength > 70 -> 19
            compact && questionLength > 48 -> 21
            compact -> 23
            questionLength > 110 -> 22
            questionLength > 80 -> 24
            else -> 27
        }
    }

    fun questionLineHeightSp(screenHeightDp: Int, questionSizeSp: Int): Int =
        questionSizeSp + if (screenHeightDp < 480) 3 else if (screenHeightDp < 700) 4 else 6
}
