package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalExperienceEndToEndContractTest {

    @Test
    fun `runner visits every configured subround exactly once before reveal`() {
        val positions = generateSequence(ProposalRunnerPosition(0, 0)) { current ->
            ProposalExperienceRunnerPolicy.next(current)
        }.toList()

        val expectedCount = ProposalExperienceRunnerPolicy.steps.sumOf { step ->
            ProposalExperienceRunnerPolicy.itemCount(step.id).coerceAtLeast(1)
        }

        assertEquals(35, expectedCount)
        assertEquals(expectedCount, positions.size)
        assertEquals(ProposalRunnerPosition(0, 0), positions.first())
        assertEquals(
            ProposalRunnerPosition(ProposalExperienceRunnerPolicy.steps.lastIndex, 0),
            positions.last()
        )
        assertEquals(ProposalFlowStepKind.REVEAL, ProposalExperienceRunnerPolicy.steps.last().kind)
    }

    @Test
    fun `complete deterministic journey produces the qualitative final reveal`() {
        val eitherOr = ProposalEitherOrRounds.byStepId.values.flatten().associate { round ->
            round.id to round.firstChoice
        }
        val locations = ProposalLocationDuels.rounds.associate { round ->
            round.id to round.firstOption.id
        }
        val rings = ProposalRingImageDuels.rounds.associate { round ->
            round.id to round.firstAssetKey
        }
        val priorities = ProposalPriorityRanking.priorities.map(ProposalPriority::id)
        val scenarios = ProposalScenarios.rounds.associate { round ->
            round.id to round.options.first()
        }
        val personal = ProposalOpenPrompts.prompts.associate { prompt ->
            prompt.id to "Persönliche Antwort für ${prompt.id}"
        }

        val result = ProposalReveal.build(
            ProposalRevealInput(
                eitherOrSelections = eitherOr,
                locationSelections = locations,
                ringSelections = rings,
                rankedPriorityIds = priorities,
                predictionMatches = 2,
                predictionTotal = ProposalPartnerPrediction.rounds.size,
                scenarioSelections = scenarios,
                personalWishAnswers = personal
            )
        )

        val sectionIds = result.sections.map(ProposalRevealSection::id).toSet()
        assertTrue(
            sectionIds.containsAll(
                setOf(
                    "mood",
                    "details",
                    "location",
                    "ring",
                    "priorities",
                    "prediction",
                    "scenarios",
                    "personal_wishes"
                )
            )
        )
        assertTrue(result.title.isNotBlank())
        assertTrue(result.subtitle.isNotBlank())
        assertTrue(result.closing.isNotBlank())
        assertTrue(result.sections.all { section -> section.values.all(String::isNotBlank) })
    }
}
