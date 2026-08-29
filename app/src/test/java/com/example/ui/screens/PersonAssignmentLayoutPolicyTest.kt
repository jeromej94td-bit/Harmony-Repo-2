package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonAssignmentLayoutPolicyTest {

    @Test
    fun `520dp phone fits four unassigned role cards into compact pool`() {
        val metrics = PersonAssignmentLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertEquals(44, metrics.avatarSizeDp)
        assertEquals(44, metrics.unassignedChipMinHeightDp)
        assertEquals(44, metrics.assignedChipMinHeightDp)
        assertEquals(8, metrics.submitGapDp)
        assertTrue(metrics.poolFixedHeightDp(roleCount = 4) <= 220)
    }

    @Test
    fun `compact target fits three assigned roles before completion`() {
        val metrics = PersonAssignmentLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertTrue(
            metrics.targetFixedHeightDp(
                roleCount = 3,
                complete = false
            ) <= 220
        )
    }

    @Test
    fun `compact completed target hides avatar so four roles remain visible`() {
        val metrics = PersonAssignmentLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1f
        )

        assertTrue(metrics.hideAvatarWhenComplete)
        assertTrue(
            metrics.targetFixedHeightDp(
                roleCount = 4,
                complete = true
            ) <= 220
        )
    }

    @Test
    fun `short phone plus large font scale still fits the role duel`() {
        val metrics = PersonAssignmentLayoutPolicy.metrics(
            screenHeightDp = 520,
            fontScale = 1.35f
        )

        assertEquals(12, metrics.nameSizeSp)
        assertEquals(2, metrics.roleMaxLines)
        assertTrue(metrics.poolFixedHeightDp(roleCount = 4) <= 220)
        assertTrue(metrics.targetFixedHeightDp(roleCount = 3, complete = false) <= 220)
        assertTrue(metrics.targetFixedHeightDp(roleCount = 4, complete = true) <= 220)
    }

    @Test
    fun `large font scale activates safe compact geometry`() {
        val metrics = PersonAssignmentLayoutPolicy.metrics(
            screenHeightDp = 760,
            fontScale = 1.35f
        )

        assertEquals(44, metrics.avatarSizeDp)
        assertEquals(11f, metrics.assignedTextSizeSp, 0f)
        assertTrue(metrics.hideAvatarWhenComplete)
    }

    @Test
    fun `normal phone preserves current role duel dimensions`() {
        val metrics = PersonAssignmentLayoutPolicy.metrics(
            screenHeightDp = 760,
            fontScale = 1f
        )

        assertEquals(86, metrics.avatarSizeDp)
        assertEquals(54, metrics.assignedChipMinHeightDp)
        assertEquals(62, metrics.unassignedChipMinHeightDp)
        assertEquals(16, metrics.nameSizeSp)
        assertEquals(14, metrics.submitGapDp)
        assertEquals(10, metrics.verticalSpacingDp)
        assertFalse(metrics.hideAvatarWhenComplete)
    }
}
