package com.example.ui.screens

internal data class GoldenMasterImageChoiceLayout(
    val columns: Int,
    val containerRadiusDp: Int,
    val horizontalPaddingDp: Int,
    val verticalPaddingDp: Int,
    val headerIconSizeDp: Int,
    val questionFontSp: Float,
    val subtitleFontSp: Float,
    val columnSpacingDp: Int,
    val rowSpacingDp: Int,
    val cardAspectRatio: Float,
    val cardRadiusDp: Int,
    val startRotationY: Float,
    val startTranslationXDp: Float,
    val titleFontSp: Float,
    val detailFontSp: Float,
    val selectionIconSizeDp: Int
)

internal object GoldenMasterImageChoiceLayoutPolicy {
    private val goldenMasterKinds = setOf("EGG", "STEAK", "TRAVEL")

    fun forKindName(kindName: String): GoldenMasterImageChoiceLayout? {
        if (kindName !in goldenMasterKinds) return null

        return GoldenMasterImageChoiceLayout(
            columns = 3,
            containerRadiusDp = 28,
            horizontalPaddingDp = 11,
            verticalPaddingDp = 15,
            headerIconSizeDp = 42,
            questionFontSp = 21f,
            subtitleFontSp = 12f,
            columnSpacingDp = 7,
            rowSpacingDp = 8,
            cardAspectRatio = 0.63f,
            cardRadiusDp = 17,
            startRotationY = -82f,
            startTranslationXDp = -18f,
            titleFontSp = 10.5f,
            detailFontSp = 8f,
            selectionIconSizeDp = 17
        )
    }
}
