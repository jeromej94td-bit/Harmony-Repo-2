package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecretPlanMotionTimelineTest {
    @Test
    fun `answer remains locked while page lifts turns and seals`() {
        assertEquals(SecretPlanMotionStage.LIFT, SecretPlanMotionTimeline.frameAt(0).stage)
        assertEquals(SecretPlanMotionStage.PAGE_TURN, SecretPlanMotionTimeline.frameAt(300).stage)
        assertEquals(SecretPlanMotionStage.SEAL, SecretPlanMotionTimeline.frameAt(1_400).stage)
        assertEquals(SecretPlanMotionStage.SETTLE, SecretPlanMotionTimeline.frameAt(1_850).stage)
        assertFalse(SecretPlanMotionTimeline.frameAt(1_999).acceptsInput)

        val complete = SecretPlanMotionTimeline.frameAt(2_000)
        assertEquals(SecretPlanMotionStage.COMPLETE, complete.stage)
        assertTrue(complete.acceptsInput)
    }

    @Test
    fun `negative elapsed time starts at the first animation frame`() {
        assertEquals(
            SecretPlanMotionTimeline.frameAt(0),
            SecretPlanMotionTimeline.frameAt(-250)
        )
    }
}
