package com.example.ui.screens

import com.example.data.model.HarmonyPacksData
import com.example.data.model.LoveBalanceQuestionPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HappyCoupleQuizRunnerRoutingRegressionTest {

    @Test
    fun `love balance routes only its first question to the four image cards`() {
        val pack = HarmonyPacksData.DEFAULT_PACKS.first {
            it.id == LoveBalanceQuestionPolicy.PACK_ID
        }

        assertEquals(
            HarmonyImageChoiceKind.HAPPY_COUPLE,
            harmonyImageChoiceKind(pack, 0)
        )
        assertNull(harmonyImageChoiceKind(pack, 1))
    }
}
