package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchTournamentResultLayoutPolicyTest {

    @Test
    fun `short phone uses compact winner result metrics so submit button keeps room`() {
        val metrics = MatchTournamentResultLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertEquals(34, metrics.trophySizeSp)
        assertEquals(120, metrics.winnerMinHeightDp)
        assertEquals(8, metrics.gapDp)
        assertTrue(metrics.minimumResultHeightDp <= 230)
    }

    @Test
    fun `large font scale triggers compact metrics even on a taller phone`() {
        val metrics = MatchTournamentResultLayoutPolicy.metrics(
            screenHeightDp = 760,
            fontScale = 1.35f
        )

        assertEquals(34, metrics.trophySizeSp)
        assertEquals(120, metrics.winnerMinHeightDp)
    }

    @Test
    fun `normal phone keeps the spacious winner presentation`() {
        val metrics = MatchTournamentResultLayoutPolicy.metrics(
            screenHeightDp = 760,
            fontScale = 1f
        )

        assertEquals(64, metrics.trophySizeSp)
        assertEquals(190, metrics.winnerMinHeightDp)
        assertEquals(14, metrics.gapDp)
    }
}
