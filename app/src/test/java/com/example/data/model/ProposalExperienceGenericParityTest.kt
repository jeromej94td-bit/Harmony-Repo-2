package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProposalExperienceGenericParityTest {

    @Test
    fun `generic adapter mirrors proposal definition exactly`() {
        val legacy = ProposalExperienceDefinitions.perfectProposal
        val generic = ProposalExperienceAdapter.definition

        assertEquals(legacy.id, generic.id)
        assertEquals(legacy.title, generic.title)
        assertEquals(legacy.steps.map(ProposalFlowStep::id), generic.steps.map(ExperienceStep::id))
        assertEquals(
            legacy.steps.map { it.kind.name },
            generic.steps.map { it.kind.name }
        )
        assertEquals(ExperienceStepKind.REVEAL, generic.steps.last().kind)
    }

    @Test
    fun `proposal keeps nine steps and thirty five navigable subrounds`() {
        assertEquals(9, ProposalExperienceRunnerPolicy.steps.size)
        assertEquals(
            listOf(5, 6, 3, 5, 1, 3, 6, 5, 1),
            ProposalExperienceRunnerPolicy.steps.map { step ->
                ProposalExperienceRunnerPolicy.itemCount(step.id)
            }
        )
        assertEquals(35, ProposalExperienceAdapter.navigator.totalItemCount())
    }

    @Test
    fun `generic navigation and progress match every proposal position`() {
        val legacyPositions = generateSequence(ProposalRunnerPosition(0, 0)) { current ->
            ProposalExperienceRunnerPolicy.next(current)
        }.toList()
        val genericPositions = generateSequence(ExperiencePosition(0, 0)) { current ->
            ProposalExperienceAdapter.navigator.next(current)
        }.toList()

        assertEquals(35, legacyPositions.size)
        assertEquals(
            legacyPositions.map(ProposalExperienceAdapter::toGenericPosition),
            genericPositions
        )

        legacyPositions.forEach { legacyPosition ->
            val genericPosition = ProposalExperienceAdapter.toGenericPosition(legacyPosition)
            assertEquals(
                ProposalExperienceRunnerPolicy.progress(legacyPosition),
                ProposalExperienceAdapter.navigator.progress(genericPosition),
                0.0001f
            )
        }

        val lastLegacy = legacyPositions.last()
        val lastGeneric = genericPositions.last()
        assertEquals(ProposalRunnerPosition(8, 0), lastLegacy)
        assertEquals(ExperiencePosition(8, 0), lastGeneric)
        assertNull(ProposalExperienceRunnerPolicy.next(lastLegacy))
        assertNull(ProposalExperienceAdapter.navigator.next(lastGeneric))
    }
}
