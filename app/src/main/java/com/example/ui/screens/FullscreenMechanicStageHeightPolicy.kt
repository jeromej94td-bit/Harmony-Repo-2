package com.example.ui.screens

internal object FullscreenMechanicStageHeightPolicy {
    private const val MAX_STAGE_HEIGHT_DP = 720

    private fun reservedChromeDp(screenHeightDp: Int): Int = when {
        screenHeightDp < 600 -> 96
        screenHeightDp < 700 -> 118
        else -> 150
    }

    fun usableHeightDp(screenHeightDp: Int): Int =
        (screenHeightDp - reservedChromeDp(screenHeightDp)).coerceAtLeast(0)

    fun stageHeightDp(screenHeightDp: Int): Int =
        usableHeightDp(screenHeightDp).coerceAtMost(MAX_STAGE_HEIGHT_DP)
}
