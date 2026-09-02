package com.example.ui.screens

internal data class StandardQuizLayoutMetrics(
    val questionHorizontalPaddingDp: Int = 19,
    val questionVerticalPaddingDp: Int = 21,
    val questionFontSizeSp: Float = 24f,
    val questionLineHeightSp: Float = 31f,
    val categoryGapDp: Int = 14,
    val questionToOptionsGapDp: Int = 26,
    val optionGapDp: Int = 11,
    val optionPaddingDp: Int = 15,
    val optionFontSizeSp: Float = 14.5f,
    val optionLineHeightSp: Float = 19f,
    val optionBadgeSizeDp: Int = 27,
    val optionBadgeFontSizeSp: Float = 12f,
    val optionBadgeGapDp: Int = 13
)

internal object StandardQuizLayoutPolicy {
    fun metrics(
        screenHeightDp: Int,
        optionCount: Int,
        questionLength: Int,
        longestOptionLength: Int,
        fontScale: Float
    ): StandardQuizLayoutMetrics {
        val tight = screenHeightDp < 680 ||
            optionCount >= 6 ||
            questionLength > 150 ||
            longestOptionLength > 72 ||
            fontScale > 1.30f

        if (tight) {
            return StandardQuizLayoutMetrics(
                questionHorizontalPaddingDp = 15,
                questionVerticalPaddingDp = 13,
                questionFontSizeSp = 19f,
                questionLineHeightSp = 24f,
                categoryGapDp = 7,
                questionToOptionsGapDp = 11,
                optionGapDp = 6,
                optionPaddingDp = 8,
                optionFontSizeSp = 12.5f,
                optionLineHeightSp = 15.5f,
                optionBadgeSizeDp = 22,
                optionBadgeFontSizeSp = 10f,
                optionBadgeGapDp = 9
            )
        }

        val compact = screenHeightDp < 760 ||
            optionCount >= 5 ||
            questionLength > 90 ||
            longestOptionLength > 48 ||
            fontScale > 1.15f

        if (compact) {
            return StandardQuizLayoutMetrics(
                questionHorizontalPaddingDp = 17,
                questionVerticalPaddingDp = 16,
                questionFontSizeSp = 21f,
                questionLineHeightSp = 27f,
                categoryGapDp = 10,
                questionToOptionsGapDp = 16,
                optionGapDp = 8,
                optionPaddingDp = 11,
                optionFontSizeSp = 13.5f,
                optionLineHeightSp = 17f,
                optionBadgeSizeDp = 24,
                optionBadgeFontSizeSp = 11f,
                optionBadgeGapDp = 11
            )
        }

        return StandardQuizLayoutMetrics()
    }
}
