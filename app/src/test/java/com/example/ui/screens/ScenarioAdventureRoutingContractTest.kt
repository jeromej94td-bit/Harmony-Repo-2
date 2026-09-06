package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioAdventureRoutingContractTest {

    private fun repoFile(path: String): File {
        val start = File(System.getProperty("user.dir"))
        return generateSequence(start) { it.parentFile }
            .map { root -> File(root, path) }
            .firstOrNull(File::exists)
            ?: File(start, path)
    }

    @Test
    fun `scenario renderer uses resumable adventure board`() {
        val renderer = repoFile(
            "app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt"
        ).readText()
        val board = repoFile(
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
        val board = repoFile(
            "app/src/main/java/com/example/ui/screens/ScenarioAdventureBoard.kt"
        ).readText()
        val skip = repoFile(
            "app/src/main/java/com/example/ui/screens/RunnerSkipButton.kt"
        ).readText()

        assertTrue(board.contains("ScenarioAdventurePresence.enter()"))
        assertTrue(board.contains("ScenarioAdventurePresence.leave()"))
        assertTrue(skip.contains("ScenarioAdventurePresence.isActive"))
    }
}
