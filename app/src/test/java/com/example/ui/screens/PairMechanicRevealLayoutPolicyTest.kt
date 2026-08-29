package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class PairMechanicRevealLayoutPolicyTest {

    @Test
    fun `short phone reduces fixed reveal surfaces`() {
        val metrics = PairMechanicRevealLayoutPolicy.metrics(520, 1f)

        assertEquals(100, metrics.predictionCardMinHeightDp)
        assertEquals(105, metrics.secretCardMinHeightDp)
        assertEquals(100, metrics.scaleInputHeightDp)
        assertEquals(115, metrics.scaleRevealHeightDp)
        assertEquals(21, metrics.predictionTitleSizeSp)
        assertEquals(20, metrics.secretTitleSizeSp)
        assertEquals(21, metrics.scaleTitleSizeSp)
        assertEquals(8, metrics.finalePaddingDp)
    }

    @Test
    fun `large font scale uses shortest safe reveal metrics`() {
        val metrics = PairMechanicRevealLayoutPolicy.metrics(760, 1.35f)

        assertEquals(100, metrics.predictionCardMinHeightDp)
        assertEquals(105, metrics.secretCardMinHeightDp)
        assertEquals(115, metrics.scaleRevealHeightDp)
        assertEquals(12, metrics.finaleBodySizeSp)
    }

    @Test
    fun `normal phone preserves current reveal proportions`() {
        val metrics = PairMechanicRevealLayoutPolicy.metrics(760, 1f)

        assertEquals(155, metrics.predictionCardMinHeightDp)
        assertEquals(160, metrics.secretCardMinHeightDp)
        assertEquals(150, metrics.scaleInputHeightDp)
        assertEquals(175, metrics.scaleRevealHeightDp)
        assertEquals(28, metrics.predictionTitleSizeSp)
        assertEquals(27, metrics.secretTitleSizeSp)
        assertEquals(28, metrics.scaleTitleSizeSp)
        assertEquals(12, metrics.finalePaddingDp)
        assertEquals(17, metrics.finaleTitleSizeSp)
        assertEquals(14, metrics.finaleBodySizeSp)
    }
}
