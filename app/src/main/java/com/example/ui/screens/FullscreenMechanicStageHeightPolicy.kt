package com.example.ui.screens

internal object FullscreenMechanicStageHeightPolicy {
    private const val MAX_STAGE_HEIGHT_DP = 720

    private fun reservedChromeDp(screenHeightDp: Int): Int = when {
        screenHeightDp < 600 -> 120
        screenHeightDp < 700 -> 145
        else -> 190
    }

    fun usableHeightDp(screenHeightDp: Int): Int =
        (screenHeightDp - reservedChromeDp(screenHeightDp)).coerceAtLeast(0)

    fun stageHeightDp(screenHeightDp: Int): Int =
        usableHeightDp(screenHeightDp).coerceAtMost(MAX_STAGE_HEIGHT_DP)
}
