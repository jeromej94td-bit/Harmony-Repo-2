package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WhoWouldLayoutPolicyTest {

    @Test
    fun `short phone reduces portrait chrome before cards can clip`() {
        val metrics = WhoWouldLayoutPolicy.metrics(
            screenWidthDp = 360,
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertEquals(76, metrics.avatarSizeDp)
        assertEquals(10, metrics.cardPaddingDp)
        assertEquals(17, metrics.nameSizeSp)
        assertEquals(74, metrics.bottomRowHeightDp)
        assertEquals(8, metrics.rowGapDp)
        assertTrue(metrics.personCardFixedContentDp <= 130)
    }

    @Test
    fun `large font scale switches Paarlabor to compact metrics`() {
        val metrics = WhoWouldLayoutPolicy.metrics(
            screenWidthDp = 412,
            screenHeightDp = 760,
            fontScale = 1.35f
        )

        assertEquals(76, metrics.avatarSizeDp)
        assertEquals(17, metrics.nameSizeSp)
        assertEquals(74, metrics.bottomRowHeightDp)
    }

    @Test
    fun `normal phone preserves current Paarlabor proportions`() {
        val metrics = WhoWouldLayoutPolicy.metrics(
            screenWidthDp = 412,
            screenHeightDp = 760,
            fontScale = 1f
        )

        assertEquals(122, metrics.avatarSizeDp)
        assertEquals(16, metrics.cardPaddingDp)
        assertEquals(22, metrics.nameSizeSp)
        assertEquals(94, metrics.bottomRowHeightDp)
        assertEquals(12, metrics.rowGapDp)
    }

    @Test
    fun `tall but narrow phone avoids the oversized normal portrait preset`() {
        val metrics = WhoWouldLayoutPolicy.metrics(
            screenWidthDp = 340,
            screenHeightDp = 760,
            fontScale = 1f
        )

        assertEquals(76, metrics.avatarSizeDp)
        assertEquals(10, metrics.cardPaddingDp)
        assertEquals(8, metrics.rowGapDp)
        assertTrue(metrics.avatarSizeDp + metrics.cardPaddingDp * 2 <= 110)
    }
}
