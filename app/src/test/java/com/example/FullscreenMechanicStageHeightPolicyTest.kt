package com.example

import com.example.ui.screens.FullscreenMechanicStageHeightPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FullscreenMechanicStageHeightPolicyTest {

    @Test
    fun `short and landscape screens never get a stage taller than their usable viewport`() {
        val cases = listOf(
            360 to 264,
            400 to 304,
            455 to 359,
            520 to 424,
            599 to 503,
            600 to 482,
            699 to 581,
            700 to 550,
            760 to 610
        )

        cases.forEach { (screenHeight, expectedStageHeight) ->
            assertEquals(
                "unexpected stage height for ${screenHeight}dp screen",
                expectedStageHeight,
                FullscreenMechanicStageHeightPolicy.stageHeightDp(screenHeight)
            )
            assertTrue(
                "stage must fit the usable viewport for ${screenHeight}dp screen",
                FullscreenMechanicStageHeightPolicy.stageHeightDp(screenHeight) <=
                    FullscreenMechanicStageHeightPolicy.usableHeightDp(screenHeight)
            )
        }
    }

    @Test
    fun `tall screens keep the existing 720dp stage cap`() {
        assertEquals(720, FullscreenMechanicStageHeightPolicy.stageHeightDp(900))
        assertEquals(720, FullscreenMechanicStageHeightPolicy.stageHeightDp(1200))
    }

    @Test
    fun `invalid tiny heights degrade safely instead of producing negative geometry`() {
        assertEquals(0, FullscreenMechanicStageHeightPolicy.usableHeightDp(80))
        assertEquals(0, FullscreenMechanicStageHeightPolicy.stageHeightDp(80))
    }
}
