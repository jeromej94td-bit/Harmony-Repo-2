package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalRevealTest {

    @Test
    fun `reveal binds to final proposal step`() {
        val revealStep = ProposalExperienceDefinitions.perfectProposal.steps.last()
        assertEquals(ProposalReveal.STEP_ID, revealStep.id)
        assertEquals(ProposalFlowStepKind.REVEAL, revealStep.kind)
    }

    @Test
    fun `intimate answers create a qualitative reveal from existing proposal choices`() {
        val result = ProposalReveal.build(
            ProposalRevealInput(
                eitherOrSelections = mapOf(
                    "mood_intimate_or_grand" to "Leise & intim",
                    "mood_private_or_shared" to "Nur wir zwei",
                    "detail_story_or_simple" to "Mit unserer Geschichte"
                ),
                locationSelections = mapOf("location_home_or_lake" to "location_lake"),
                ringSelections = mapOf("ring_classic_or_geometric" to "ring_klassisch_solitaer"),
                rankedPriorityIds = listOf("priority_emotional_moment", "priority_personal_story"),
                predictionMatches = 3,
                predictionTotal = 3,
                scenarioSelections = mapOf(
                    "scenario_weather_breaks_plan" to "Nach Hause wechseln und aus dem Plan einen ganz intimen Abend machen"
                ),
                personalWishAnswers = mapOf(
                    "personal_words_to_remember" to "Ich würde dich immer wieder wählen."
                )
            )
        )

        assertEquals("Ein stiller Moment, der nur euch gehört", result.title)
        assertTrue(result.sections.any { it.id == "location" && "Am stillen See" in it.values })
        assertTrue(result.sections.any { it.id == "ring" && "Klassisch & zeitlos" in it.values })
        assertTrue(result.sections.any { it.id == "priorities" && it.values.first() == "Ein emotionaler Moment nur für uns" })
        assertTrue(result.closing.contains("Ich würde dich immer wieder wählen."))
    }

    @Test
    fun `reveal stays qualitative and ignores unknown selections`() {
        val result = ProposalReveal.build(
            ProposalRevealInput(
                locationSelections = mapOf("unknown_round" to "unknown_option"),
                rankedPriorityIds = listOf("unknown_priority"),
                predictionMatches = 1,
                predictionTotal = 3
            )
        )

        val rendered = buildString {
            append(result.title)
            append(result.subtitle)
            result.sections.forEach { section ->
                append(section.title)
                append(section.values.joinToString(" "))
            }
            append(result.closing)
        }.lowercase()

        assertFalse(rendered.contains("punkte"))
        assertFalse(rendered.contains("score"))
        assertFalse(rendered.contains("%"))
        assertTrue(result.sections.none { it.values.any(String::isBlank) })
    }
}
