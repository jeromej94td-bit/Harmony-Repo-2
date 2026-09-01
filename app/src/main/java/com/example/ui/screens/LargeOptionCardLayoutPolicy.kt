package com.example.ui.screens

internal data class LargeOptionCardLayoutMetrics(
    val horizontalPaddingDp: Int,
    val verticalPaddingDp: Int
)

/** Keeps option labels readable when weighted fullscreen rows become very short in landscape. */
internal object LargeOptionCardLayoutPolicy {
    fun metrics(cardHeightDp: Int): LargeOptionCardLayoutMetrics = when {
        cardHeightDp < 64 -> LargeOptionCardLayoutMetrics(
            horizontalPaddingDp = 8,
            verticalPaddingDp = 4
        )
        cardHeightDp < 96 -> LargeOptionCardLayoutMetrics(
            horizontalPaddingDp = 10,
            verticalPaddingDp = 6
        )
        cardHeightDp < 145 -> LargeOptionCardLayoutMetrics(
            horizontalPaddingDp = 10,
            verticalPaddingDp = 8
        )
        else -> LargeOptionCardLayoutMetrics(
            horizontalPaddingDp = 12,
            verticalPaddingDp = 12
        )
    }
}
