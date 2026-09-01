package com.example

import com.example.ui.screens.LargeOptionGridLayoutPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class LargeOptionGridLayoutPolicyTest {

    @Test
    fun `two rows keep the normal weighted layout when enough height exists`() {
        val metrics = LargeOptionGridLayoutPolicy.metrics(
            availableHeightDp = 260,
            rowCount = 2,
            gapDp = 8,
            fontScale = 1f
        )

        assertFalse(metrics.useScroll)
    }

    @Test
    fun `four rows switch to scroll when short landscape would crush cards`() {
        val metrics = LargeOptionGridLayoutPolicy.metrics(
            availableHeightDp = 240,
            rowCount = 4,
            gapDp = 8,
            fontScale = 1f
        )

        assertTrue(metrics.useScroll)
        assertEquals(72, metrics.scrollRowHeightDp)
    }

    @Test
    fun `large font scale reserves taller rows before falling back to scroll`() {
        val metrics = LargeOptionGridLayoutPolicy.metrics(
            availableHeightDp = 340,
            rowCount = 4,
            gapDp = 8,
            fontScale = 1.3f
        )

        assertTrue(metrics.useScroll)
        assertEquals(92, metrics.scrollRowHeightDp)
    }

    @Test
    fun `four rows stay weighted on a tall stage`() {
        val metrics = LargeOptionGridLayoutPolicy.metrics(
            availableHeightDp = 420,
            rowCount = 4,
            gapDp = 12,
            fontScale = 1f
        )

        assertFalse(metrics.useScroll)
    }
}
