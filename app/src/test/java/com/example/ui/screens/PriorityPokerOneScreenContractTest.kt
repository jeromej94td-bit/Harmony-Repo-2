package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PriorityPokerOneScreenContractTest {

    private fun source(pathFromRepoRoot: String): String {
        val candidates = listOf(
            File(pathFromRepoRoot),
            File("../$pathFromRepoRoot")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertTrue("Expected source file to exist: $pathFromRepoRoot", file != null)
        return file!!.readText()
    }

    @Test
    fun `priority poker is question only and advances on card tap`() {
        val board = source("app/src/main/java/com/example/ui/screens/DirectPriorityPokerBoard.kt")
        val shell = source("app/src/main/java/com/example/ui/screens/QuestionOnlyMechanicShell.kt")

        assertTrue(board.contains("QuestionOnlyMechanicShell("))
        assertTrue(board.contains("onPick(item.raw)"))
        assertFalse(board.contains("PRIORITÄTEN-POKER"))
        assertFalse(board.contains("PRIORITY POKER"))
        assertFalse(board.contains("instruction ="))
        assertFalse(board.contains("PrimaryMechanicButton("))
        assertFalse(shell.contains("kicker"))
        assertFalse(shell.contains("instruction"))
    }

    @Test
    fun `shared fullscreen option grids never require scrolling`() {
        val policy = source("app/src/main/java/com/example/ui/screens/LargeOptionGridLayoutPolicy.kt")

        assertTrue(policy.contains("useScroll = false"))
    }

    @Test
    fun `fullscreen stage leaves enough room for runner chrome`() {
        val policy = source("app/src/main/java/com/example/ui/screens/FullscreenMechanicStageHeightPolicy.kt")

        assertTrue(policy.contains("else -> 190"))
    }
}
