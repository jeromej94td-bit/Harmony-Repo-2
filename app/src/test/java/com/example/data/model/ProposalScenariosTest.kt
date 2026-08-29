package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalScenariosTest {

    @Test
    fun `scenario content binds to the proposal scenario flow step`() {
        val step = ProposalExperienceDefinitions.perfectProposal.steps
            .single { it.id == ProposalScenarios.STEP_ID }

        assertEquals(ProposalFlowStepKind.SCENARIO, step.kind)
    }

    @Test
    fun `proposal scenarios are concrete four way decisions`() {
        val rounds = ProposalScenarios.rounds

        assertEquals(6, rounds.size)
        assertEquals(rounds.size, rounds.map(ProposalScenarioRound::id).distinct().size)
        assertEquals(rounds.size, rounds.map(ProposalScenarioRound::prompt).distinct().size)

        rounds.forEach { round ->
            assertTrue(round.id.isNotBlank())
            assertTrue(round.prompt.length >= 40)
            assertEquals(4, round.options.size)
            assertEquals(4, round.options.distinct().size)
            assertTrue(round.options.all { it.length >= 12 })
        }

        val forbiddenGenericAnswers = listOf("weiß nicht", "kommt drauf an", "sonstiges")
        assertTrue(
            rounds.flatMap(ProposalScenarioRound::options).none { option ->
                forbiddenGenericAnswers.any { forbidden -> option.contains(forbidden, ignoreCase = true) }
            }
        )
    }

    @Test
    fun `proposal scenarios cover six distinct pressure points`() {
        assertEquals(
            setOf(
                "scenario_weather_breaks_plan",
                "scenario_privacy_vs_loved_ones",
                "scenario_partner_senses_surprise",
                "scenario_ring_missing",
                "scenario_emotional_pause",
                "scenario_public_attention"
            ),
            ProposalScenarios.rounds.map(ProposalScenarioRound::id).toSet()
        )
    }
}
