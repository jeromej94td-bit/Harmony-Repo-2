package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceRevealRoutingContractTest {

    @Test
    fun `proposal reveal routes through reusable experience board`() {
        val reusableBoard = File(
            "app/src/main/java/com/example/ui/screens/ExperienceRevealBoard.kt"
        )
        val proposalScreen = File(
            "app/src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt"
        ).readText()

        assertTrue("Reusable reveal board must exist", reusableBoard.isFile)
        val boardSource = reusableBoard.readText()

        assertTrue(boardSource.contains("internal fun ExperienceRevealBoard("))
        assertTrue(boardSource.contains("reveal: ExperienceRevealResult"))
        assertTrue(boardSource.contains("testTag(\"experience_reveal_board\")"))
        assertTrue(boardSource.contains("closeButtonTestTag"))

        assertTrue(proposalScreen.contains("ExperienceRevealBoard("))
        assertTrue(proposalScreen.contains("reveal = reveal.toExperienceRevealResult()"))
        assertTrue(proposalScreen.contains("closeButtonTestTag = \"proposal_finish\""))
        assertFalse(proposalScreen.contains("private fun ProposalRevealPane("))
    }
}
