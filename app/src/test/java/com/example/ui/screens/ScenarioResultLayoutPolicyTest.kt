package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioResultLayoutPolicyTest {

    @Test
    fun `short phone uses compact finale metrics so action remains visible`() {
        val metrics = ScenarioResultLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertEquals(78, metrics.trophyContainerDp)
        assertEquals(40, metrics.trophySizeSp)
        assertEquals(10, metrics.cardPaddingDp)
        assertEquals(20, metrics.titleSizeSp)
        assertEquals(13, metrics.bodySizeSp)
        assertEquals(6, metrics.gapDp)
        assertTrue(metrics.fixedChromeHeightDp <= 150)
    }

    @Test
    fun `large font scale uses compact finale metrics on taller phone`() {
        val metrics = ScenarioResultLayoutPolicy.metrics(
            screenHeightDp = 760,
            fontScale = 1.35f
        )

        assertEquals(78, metrics.trophyContainerDp)
        assertEquals(40, metrics.trophySizeSp)
        assertEquals(20, metrics.titleSizeSp)
        assertEquals(13, metrics.bodySizeSp)
    }

    @Test
    fun `normal phone keeps spacious finale presentation`() {
        val metrics = ScenarioResultLayoutPolicy.metrics(
            screenHeightDp = 760,
            fontScale = 1f
        )

        assertEquals(150, metrics.trophyContainerDp)
        assertEquals(72, metrics.trophySizeSp)
        assertEquals(20, metrics.cardPaddingDp)
        assertEquals(27, metrics.titleSizeSp)
        assertEquals(16, metrics.bodySizeSp)
        assertEquals(12, metrics.gapDp)
    }
}
