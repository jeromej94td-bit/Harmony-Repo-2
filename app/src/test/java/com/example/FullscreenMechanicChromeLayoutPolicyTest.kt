package com.example

import com.example.ui.screens.FullscreenMechanicChromeLayoutPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FullscreenMechanicChromeLayoutPolicyTest {

    @Test
    fun `very short landscape prioritizes question and interactive content`() {
        val metrics = FullscreenMechanicChromeLayoutPolicy.metrics(
            screenHeightDp = 360,
            fontScale = 1f
        )

        assertFalse(metrics.showInstruction)
        assertEquals(6, metrics.verticalPaddingDp)
        assertEquals(3, metrics.headerGapDp)
        assertEquals(6, metrics.contentGapDp)
        assertEquals(10, metrics.kickerSizeSp)
        assertEquals(16, FullscreenMechanicChromeLayoutPolicy.questionSizeSp(360, 60, 1f))
    }

    @Test
    fun `short phones keep instruction but reclaim chrome space`() {
        val metrics = FullscreenMechanicChromeLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertTrue(metrics.showInstruction)
        assertEquals(10, metrics.verticalPaddingDp)
        assertEquals(4, metrics.headerGapDp)
        assertEquals(8, metrics.contentGapDp)
        assertEquals(11, metrics.instructionSizeSp)
        assertEquals(19, FullscreenMechanicChromeLayoutPolicy.questionSizeSp(520, 60, 1f))
    }

    @Test
    fun `normal compact phone preserves current compact chrome`() {
        val metrics = FullscreenMechanicChromeLayoutPolicy.metrics(
            screenHeightDp = 640,
            fontScale = 1f
        )

        assertTrue(metrics.showInstruction)
        assertEquals(12, metrics.verticalPaddingDp)
        assertEquals(4, metrics.headerGapDp)
        assertEquals(10, metrics.contentGapDp)
        assertEquals(11, metrics.kickerSizeSp)
        assertEquals(21, FullscreenMechanicChromeLayoutPolicy.questionSizeSp(640, 60, 1f))
    }

    @Test
    fun `normal portrait keeps existing spacious chrome`() {
        val metrics = FullscreenMechanicChromeLayoutPolicy.metrics(
            screenHeightDp = 800,
            fontScale = 1f
        )

        assertTrue(metrics.showInstruction)
        assertEquals(18, metrics.verticalPaddingDp)
        assertEquals(8, metrics.headerGapDp)
        assertEquals(18, metrics.contentGapDp)
        assertEquals(13, metrics.kickerSizeSp)
        assertEquals(27, FullscreenMechanicChromeLayoutPolicy.questionSizeSp(800, 60, 1f))
    }

    @Test
    fun `large font scale uses compact nominal sizes without dropping instruction on tall screens`() {
        val metrics = FullscreenMechanicChromeLayoutPolicy.metrics(
            screenHeightDp = 800,
            fontScale = 1.35f
        )

        assertTrue(metrics.showInstruction)
        assertEquals(10, metrics.verticalPaddingDp)
        assertEquals(19, FullscreenMechanicChromeLayoutPolicy.questionSizeSp(800, 60, 1.35f))
    }
}
