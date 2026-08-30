package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class PairMechanicRevealLayoutPolicyTest {

    @Test
    fun `proposal sized stage uses compact prediction cards even on a tall phone`() {
        val screenHeightDp = 820

        assertEquals(670, FullscreenMechanicStageHeightPolicy.stageHeightDp(screenHeightDp))

        val metrics = PairMechanicRevealLayoutPolicy.metrics(
            screenHeightDp = screenHeightDp,
            fontScale = 1f
        )

        // The proposal header/progress chrome reduces the actual mechanic stage below 700dp.
        // Keeping 155dp reveal cards here can push "Reveal speichern & weiter" below the viewport.
        assertEquals(125, metrics.predictionCardMinHeightDp)
        assertEquals(23, metrics.secretTitleSizeSp)
    }
}
