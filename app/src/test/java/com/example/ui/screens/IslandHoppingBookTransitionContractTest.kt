package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IslandHoppingBookTransitionContractTest {

    private fun source(pathFromRepoRoot: String): String {
        val candidates = listOf(File(pathFromRepoRoot), File("../$pathFromRepoRoot"))
        val file = candidates.firstOrNull { it.exists() }
        assertTrue("Expected source file to exist: $pathFromRepoRoot", file != null)
        return file!!.readText()
    }

    @Test
    fun `answer touch starts page motion before committing answer`() {
        val board = source("app/src/main/java/com/example/ui/screens/IslandHoppingBookBoard.kt")
        val start = board.indexOf("fun beginPageTurn(answer: String)")
        val animate = board.indexOf("pageTurn.animateTo", start)
        val commit = board.indexOf("onPick(answer)", start)

        assertTrue("beginPageTurn must exist", start >= 0)
        assertTrue("page animation must start inside beginPageTurn", animate > start)
        assertTrue("answer callback must happen after page animation starts", commit > animate)
        val preCommit = board.substring(start, commit)
        assertFalse("no confirmation delay before page motion/commit", preCommit.contains("delay("))
        assertTrue(board.contains("if (turningAnswer != null) return"))
        assertTrue(board.contains("questionIndex: Int"))
        assertTrue(board.contains("totalQuestions: Int"))
    }

    @Test
    fun `page turn uses perspective book transform`() {
        val page = source("app/src/main/java/com/example/ui/screens/IslandHoppingPageTurn.kt")
        assertTrue(page.contains("rotationY"))
        assertTrue(page.contains("TransformOrigin(0f, 0.5f)"))
        assertTrue(page.contains("cameraDistance"))
        assertTrue(page.contains("IslandBookGold"))
    }
}
