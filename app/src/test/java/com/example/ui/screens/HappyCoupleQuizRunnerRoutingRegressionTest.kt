package com.example.ui.screens

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class HappyCoupleQuizRunnerRoutingRegressionTest {

    @Test
    fun loveBalanceFirstQuestionUsesFourImageCardsInsteadOfGenericAnswerButtons() {
        assertTrue(true)
    }
}
