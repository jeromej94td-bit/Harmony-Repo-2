package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioAdventureRoutingContractTest {

    @Test
    fun `scenario renderer uses resumable adventure board`() {
        val renderer = File(
            "app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt"
        ).readText()
        val board = File(
            "app/src/main/java/com/example/ui/screens/ScenarioAdventureBoard.kt"
        )

        assertTrue("Adventure board must exist", board.isFile)
        assertTrue(renderer.contains("FullscreenGameMechanicKind.SCENARIO -> ScenarioAdventureBoard("))
        assertFalse(renderer.contains("FullscreenGameMechanicKind.SCENARIO -> ScenarioBoard("))

        val boardSource = board.readText()
        assertTrue(boardSource.contains("Euer Abenteuer beginnt"))
        assertTrue(boardSource.contains("Abenteuer abschließen"))
        assertTrue(boardSource.contains("scenarioRouteIndexes("))
        assertTrue(boardSource.contains("HarmonyDatabase.getInstance"))
    }

    @Test
    fun `generic runner skip is hidden while adventure is mounted`() {
        val board = File(
            "app/src/main/java/com/example/ui/screens/ScenarioAdventureBoard.kt"
        ).readText()
        val skip = File(
            "app/src/main/java/com/example/ui/screens/RunnerSkipButton.kt"
        ).readText()

        assertTrue(board.contains("ScenarioAdventurePresence.enter()"))
        assertTrue(board.contains("ScenarioAdventurePresence.leave()"))
        assertTrue(skip.contains("ScenarioAdventurePresence.isActive"))
    }
}
