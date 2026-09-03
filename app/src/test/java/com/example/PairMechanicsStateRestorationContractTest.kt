package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PairMechanicsStateRestorationContractTest {

    @Test
    fun `private pair mechanics preserve unfinished handoff state across recreation`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenPairMechanics.kt").readText()

        val partnerPrediction = source.substringAfter("internal fun PartnerPredictionBoard(")
            .substringBefore("internal fun SecretChoiceBoard(")
        assertTrue(partnerPrediction.contains("var prediction by rememberSaveable(question, selectedAnswer)"))
        assertTrue(partnerPrediction.contains("var actual by rememberSaveable(question, selectedAnswer)"))
        assertTrue(partnerPrediction.contains("var phase by rememberSaveable(question, selectedAnswer)"))
        assertTrue(partnerPrediction.contains("var predictionSeries by rememberSaveable"))

        val secretChoice = source.substringAfter("internal fun SecretChoiceBoard(")
            .substringBefore("internal fun ScaleMatchBoard(")
        assertTrue(secretChoice.contains("var first by rememberSaveable(question, selectedAnswer)"))
        assertTrue(secretChoice.contains("var second by rememberSaveable(question, selectedAnswer)"))
        assertTrue(secretChoice.contains("var phase by rememberSaveable(question, selectedAnswer)"))

        val scaleMatch = source.substringAfter("internal fun ScaleMatchBoard(")
        assertTrue(scaleMatch.contains("var first by rememberSaveable(question, selectedAnswer)"))
        assertTrue(scaleMatch.contains("var second by rememberSaveable(question, selectedAnswer)"))
        assertTrue(scaleMatch.contains("var phase by rememberSaveable(question, selectedAnswer)"))
        assertTrue(scaleMatch.contains("var sliderValue by rememberSaveable(question, selectedAnswer)"))
    }
}
