package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ExperienceEitherOrRoundTest {

    @Test
    fun `round keeps stable id prompt and two distinct choices`() {
        val round = ExperienceEitherOrRound(
            id = "home_energy",
            prompt = "Wie soll sich euer Zuhause anfühlen?",
            firstChoice = "Ruhig",
            secondChoice = "Lebendig"
        )

        assertEquals("home_energy", round.id)
        assertEquals("Wie soll sich euer Zuhause anfühlen?", round.prompt)
        assertEquals("Ruhig", round.firstChoice)
        assertEquals("Lebendig", round.secondChoice)
    }

    @Test
    fun `malformed rounds fail fast`() {
        expectIllegal { ExperienceEitherOrRound("", "Frage", "A", "B") }
        expectIllegal { ExperienceEitherOrRound("id", "", "A", "B") }
        expectIllegal { ExperienceEitherOrRound("id", "Frage", "", "B") }
        expectIllegal { ExperienceEitherOrRound("id", "Frage", "A", "") }
        expectIllegal { ExperienceEitherOrRound("id", "Frage", "Gleich", "Gleich") }
    }

    @Test
    fun `proposal rounds adapt without changing content`() {
        ProposalEitherOrRounds.byStepId.values.flatten().forEach { proposalRound ->
            val generic = proposalRound.toExperienceEitherOrRound()

            assertEquals(proposalRound.id, generic.id)
            assertEquals(proposalRound.prompt, generic.prompt)
            assertEquals(proposalRound.firstChoice, generic.firstChoice)
            assertEquals(proposalRound.secondChoice, generic.secondChoice)
        }
    }

    private fun expectIllegal(block: () -> Unit) {
        try {
            block()
            throw AssertionError("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
    }
}
