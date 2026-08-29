package com.example.ui.screens

import kotlin.math.ceil

internal data class PersonAssignmentLayoutMetrics(
    val avatarSizeDp: Int,
    val avatarInitialSizeSp: Int,
    val nameSizeSp: Int,
    val verticalSpacingDp: Int,
    val targetVerticalPaddingDp: Int,
    val centerVerticalPaddingDp: Int,
    val assignedChipMinHeightDp: Int,
    val unassignedChipMinHeightDp: Int,
    val chipVerticalPaddingDp: Int,
    val assignedTextSizeSp: Float,
    val unassignedTextSizeSp: Float,
    val assignedLineHeightSp: Int,
    val unassignedLineHeightSp: Int,
    val roleMaxLines: Int,
    val assignedHandleSizeSp: Int,
    val unassignedHandleSizeSp: Int,
    val submitGapDp: Int,
    val headerSizeSp: Int,
    val columnGapDp: Int,
    val hideAvatarWhenComplete: Boolean,
    val fontScale: Float
) {
    private val estimatedHeaderHeightDp: Int
        get() = ceil(headerSizeSp * fontScale.coerceAtLeast(1f) * 1.2f).toInt()

    private val estimatedNameHeightDp: Int
        get() = ceil(nameSizeSp * fontScale.coerceAtLeast(1f) * 1.2f).toInt()

    fun poolFixedHeightDp(roleCount: Int): Int {
        val safeCount = roleCount.coerceAtLeast(0)
        if (safeCount == 0) return centerVerticalPaddingDp * 2 + estimatedHeaderHeightDp
        return centerVerticalPaddingDp * 2 +
            estimatedHeaderHeightDp +
            verticalSpacingDp * safeCount +
            unassignedChipMinHeightDp * safeCount
    }

    fun targetFixedHeightDp(roleCount: Int, complete: Boolean): Int {
        val safeCount = roleCount.coerceAtLeast(0)
        val showAvatar = !(complete && hideAvatarWhenComplete)
        val fixedChildren = 1 + safeCount + if (showAvatar) 1 else 0
        val gaps = (fixedChildren - 1).coerceAtLeast(0)
        return targetVerticalPaddingDp * 2 +
            (if (showAvatar) avatarSizeDp else 0) +
            estimatedNameHeightDp +
            assignedChipMinHeightDp * safeCount +
            verticalSpacingDp * gaps
    }
}

/** Responsive geometry for the three-column Rollen-Duell board. */
internal object PersonAssignmentLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): PersonAssignmentLayoutMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f

        return when {
            veryCompact -> PersonAssignmentLayoutMetrics(
                avatarSizeDp = 44,
                avatarInitialSizeSp = 18,
                nameSizeSp = 12,
                verticalSpacingDp = 4,
                targetVerticalPaddingDp = 4,
                centerVerticalPaddingDp = 4,
                assignedChipMinHeightDp = 44,
                unassignedChipMinHeightDp = 44,
                chipVerticalPaddingDp = 4,
                assignedTextSizeSp = 11f,
                unassignedTextSizeSp = 11f,
                assignedLineHeightSp = 13,
                unassignedLineHeightSp = 13,
                roleMaxLines = 2,
                assignedHandleSizeSp = 16,
                unassignedHandleSizeSp = 16,
                submitGapDp = 8,
                headerSizeSp = 12,
                columnGapDp = 6,
                hideAvatarWhenComplete = true,
                fontScale = safeFontScale
            )
            compact -> PersonAssignmentLayoutMetrics(
                avatarSizeDp = 60,
                avatarInitialSizeSp = 23,
                nameSizeSp = 14,
                verticalSpacingDp = 6,
                targetVerticalPaddingDp = 7,
                centerVerticalPaddingDp = 7,
                assignedChipMinHeightDp = 48,
                unassignedChipMinHeightDp = 50,
                chipVerticalPaddingDp = 6,
                assignedTextSizeSp = 12f,
                unassignedTextSizeSp = 13f,
                assignedLineHeightSp = 15,
                unassignedLineHeightSp = 16,
                roleMaxLines = 2,
                assignedHandleSizeSp = 18,
                unassignedHandleSizeSp = 20,
                submitGapDp = 10,
                headerSizeSp = 13,
                columnGapDp = 8,
                hideAvatarWhenComplete = true,
                fontScale = safeFontScale
            )
            else -> PersonAssignmentLayoutMetrics(
                avatarSizeDp = 86,
                avatarInitialSizeSp = 32,
                nameSizeSp = 16,
                verticalSpacingDp = 10,
                targetVerticalPaddingDp = 14,
                centerVerticalPaddingDp = 12,
                assignedChipMinHeightDp = 54,
                unassignedChipMinHeightDp = 62,
                chipVerticalPaddingDp = 11,
                assignedTextSizeSp = 13.5f,
                unassignedTextSizeSp = 15f,
                assignedLineHeightSp = 17,
                unassignedLineHeightSp = 19,
                roleMaxLines = 3,
                assignedHandleSizeSp = 19,
                unassignedHandleSizeSp = 23,
                submitGapDp = 14,
                headerSizeSp = 14,
                columnGapDp = 10,
                hideAvatarWhenComplete = false,
                fontScale = safeFontScale
            )
        }
    }
}
