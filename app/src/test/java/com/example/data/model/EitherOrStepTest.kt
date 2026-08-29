package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EitherOrStepTest {

    private fun round(id: String = "round-1") = EitherOrRound(
        id = id,
        prompt = "Was passt besser?",
        firstChoice = "Option A",
        secondChoice = "Option B"
    )

    @Test
    fun `generic either or content keeps an immutable ordered copy`() {
        val source = mutableListOf(round("first"), round("second"))
        val content = EitherOrStepContent(stepId = "demo_step", rounds = source)
        source.clear()

        assertEquals("demo_step", content.stepId)
        assertEquals(2, content.itemCount)
        assertEquals(listOf("first", "second"), content.rounds.map(EitherOrRound::id))
        assertEquals("first", content.roundAt(0)?.id)
        assertEquals("second", content.roundAt(1)?.id)
        assertNull(content.roundAt(-1))
        assertNull(content.roundAt(2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank round id is rejected`() {
        round(" ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank prompt is rejected`() {
        EitherOrRound("round", " ", "A", "B")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank choices are rejected`() {
        EitherOrRound("round", "Prompt", "A", " ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `identical choices are rejected`() {
        EitherOrRound("round", "Prompt", "Gleich", "Gleich")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank step id is rejected`() {
        EitherOrStepContent(" ", listOf(round()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty round list is rejected`() {
        EitherOrStepContent("demo", emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `duplicate round ids are rejected`() {
        EitherOrStepContent("demo", listOf(round("same"), round("same")))
    }

    @Test
    fun `proposal adapter preserves both either or steps and every shipped field`() {
        val expectedStepIds = listOf("proposal_mood", "proposal_details")

        assertEquals(expectedStepIds, ProposalEitherOrAdapter.byStepId.keys.toList())
        assertEquals(listOf(5, 6), expectedStepIds.map { ProposalEitherOrAdapter.contentFor(it)?.itemCount })
        assertNull(ProposalEitherOrAdapter.contentFor("ring_style"))

        expectedStepIds.forEach { stepId ->
            val legacy = ProposalEitherOrRounds.roundsFor(stepId)
            val generic = requireNotNull(ProposalEitherOrAdapter.contentFor(stepId)).rounds

            assertEquals(legacy.size, generic.size)
            legacy.zip(generic).forEach { (oldRound, newRound) ->
                assertEquals(oldRound.id, newRound.id)
                assertEquals(oldRound.prompt, newRound.prompt)
                assertEquals(oldRound.firstChoice, newRound.firstChoice)
                assertEquals(oldRound.secondChoice, newRound.secondChoice)
                assertNotSame(oldRound, newRound)
            }
        }
    }

    @Test
    fun `proposal item counts continue to use eleven either or rounds`() {
        assertEquals(5, ProposalExperienceRunnerPolicy.itemCount("proposal_mood"))
        assertEquals(6, ProposalExperienceRunnerPolicy.itemCount("proposal_details"))
        assertEquals(11, listOf("proposal_mood", "proposal_details").sumOf {
            requireNotNull(ProposalEitherOrAdapter.contentFor(it)).itemCount
        })
        assertTrue(ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(1, 0)) > 0f)
    }
}
