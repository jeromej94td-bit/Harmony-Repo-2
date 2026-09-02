package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ChoiceMechanicsStateRestorationContractTest {

    @Test
    fun `unfinished choice mechanics survive activity recreation`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenChoiceMechanics.kt").readText()

        val poker = source.substringAfter("internal fun PriorityPokerBoard(")
            .substringBefore("internal fun MatchTournamentBoard(")
        assertTrue(poker.contains("var selected by rememberSaveable(question, selectedAnswer)"))

        val tournament = source.substringAfter("internal fun MatchTournamentBoard(")
            .substringBefore("internal fun ScenarioBoard(")
        assertTrue(tournament.contains("var championIndex by rememberSaveable(question, selectedAnswer)"))
        assertTrue(tournament.contains("var challengerIndex by rememberSaveable(question, selectedAnswer)"))
        assertTrue(tournament.contains("var finished by rememberSaveable(question, selectedAnswer)"))

        val scenario = source.substringAfter("internal fun ScenarioBoard(")
        assertTrue(scenario.contains("var selected by rememberSaveable(question, selectedAnswer)"))
        assertTrue(scenario.contains("var journeyChoices by rememberSaveable"))
        assertTrue(scenario.contains("mutableStateOf(IntArray(0))"))
        assertTrue(scenario.contains("var showJourneyResult by rememberSaveable(question)"))
    }
}
