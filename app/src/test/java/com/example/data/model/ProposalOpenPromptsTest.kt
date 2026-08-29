package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalOpenPromptsTest {

    @Test
    fun `open prompts bind to the personal wishes step`() {
        val step = ProposalExperienceDefinitions.perfectProposal.steps.single { it.id == ProposalOpenPrompts.STEP_ID }

        assertEquals(ProposalFlowStepKind.OPEN_PROMPT, step.kind)
    }

    @Test
    fun `open prompts are stable unique and substantial`() {
        assertEquals(5, ProposalOpenPrompts.prompts.size)
        assertEquals(
            listOf(
                "personal_words_to_remember",
                "personal_private_detail",
                "personal_future_feeling",
                "personal_non_negotiable",
                "personal_imperfect_moment"
            ),
            ProposalOpenPrompts.prompts.map(ProposalOpenPrompt::id)
        )
        assertEquals(ProposalOpenPrompts.prompts.size, ProposalOpenPrompts.prompts.map(ProposalOpenPrompt::id).distinct().size)
        assertEquals(ProposalOpenPrompts.prompts.size, ProposalOpenPrompts.prompts.map(ProposalOpenPrompt::prompt).distinct().size)
        assertTrue(ProposalOpenPrompts.prompts.all { it.prompt.length >= 70 })
    }

    @Test
    fun `open prompts avoid canned answer choices`() {
        assertTrue(ProposalOpenPrompts.prompts.all { it.prompt.isNotBlank() })
        assertTrue(ProposalOpenPrompts.prompts.none { it.prompt.contains("A)") || it.prompt.contains("B)") })
    }
}
