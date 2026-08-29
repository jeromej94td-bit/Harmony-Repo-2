package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalExperienceRunnerPolicyTest {

    @Test
    fun `legacy proposal pack opens the dedicated experience`() {
        assertTrue(ProposalExperienceEntryPolicy.handlesPack("antrag"))
        assertFalse(ProposalExperienceEntryPolicy.handlesPack("ringe"))
        assertFalse(ProposalExperienceEntryPolicy.handlesPack("traumhochzeit"))
    }

    @Test
    fun `runner follows the fixed nine step proposal definition`() {
        assertEquals(
            listOf(
                "proposal_mood",
                "proposal_details",
                "proposal_location",
                "ring_style",
                "proposal_priorities",
                "partner_prediction",
                "proposal_scenarios",
                "personal_wishes",
                "perfect_proposal_reveal"
            ),
            ProposalExperienceRunnerPolicy.steps.map(ProposalFlowStep::id)
        )
    }

    @Test
    fun `subround counts are bound to the existing proposal content`() {
        assertEquals(5, ProposalExperienceRunnerPolicy.itemCount("proposal_mood"))
        assertEquals(6, ProposalExperienceRunnerPolicy.itemCount("proposal_details"))
        assertEquals(3, ProposalExperienceRunnerPolicy.itemCount("proposal_location"))
        assertEquals(5, ProposalExperienceRunnerPolicy.itemCount("ring_style"))
        assertEquals(1, ProposalExperienceRunnerPolicy.itemCount("proposal_priorities"))
        assertEquals(ProposalPartnerPrediction.rounds.size, ProposalExperienceRunnerPolicy.itemCount("partner_prediction"))
        assertEquals(ProposalScenarios.rounds.size, ProposalExperienceRunnerPolicy.itemCount("proposal_scenarios"))
        assertEquals(ProposalOpenPrompts.prompts.size, ProposalExperienceRunnerPolicy.itemCount("personal_wishes"))
        assertEquals(1, ProposalExperienceRunnerPolicy.itemCount("perfect_proposal_reveal"))
    }

    @Test
    fun `progress advances within a step before moving to the next step`() {
        val first = ProposalRunnerPosition(stepIndex = 0, itemIndex = 0)
        assertEquals(ProposalRunnerPosition(0, 1), ProposalExperienceRunnerPolicy.next(first))

        val lastMoodItem = ProposalRunnerPosition(stepIndex = 0, itemIndex = 4)
        assertEquals(ProposalRunnerPosition(1, 0), ProposalExperienceRunnerPolicy.next(lastMoodItem))

        val beforeReveal = ProposalRunnerPosition(stepIndex = 7, itemIndex = ProposalOpenPrompts.prompts.lastIndex)
        assertEquals(ProposalRunnerPosition(8, 0), ProposalExperienceRunnerPolicy.next(beforeReveal))

        assertEquals(null, ProposalExperienceRunnerPolicy.next(ProposalRunnerPosition(8, 0)))
    }

    @Test
    fun `overall progress grows across subrounds and reaches one on reveal`() {
        val start = ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(0, 0))
        val middle = ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(4, 0))
        val reveal = ProposalExperienceRunnerPolicy.progress(ProposalRunnerPosition(8, 0))

        assertTrue(start in 0f..1f)
        assertTrue(middle > start)
        assertEquals(1f, reveal)
    }
}
