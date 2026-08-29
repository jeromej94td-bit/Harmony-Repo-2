package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceDefinitionTest {

    private fun validSteps() = listOf(
        ExperienceStep("choice", ExperienceStepKind.EITHER_OR),
        ExperienceStep("result", ExperienceStepKind.REVEAL)
    )

    @Test
    fun `valid definition keeps ordered copy and supports next step lookup`() {
        val source = validSteps().toMutableList()
        val definition = ExperienceDefinition("demo", "Demo", source)
        source.clear()

        assertEquals(listOf("choice", "result"), definition.steps.map(ExperienceStep::id))
        assertEquals("result", definition.nextStepAfter("choice")?.id)
        assertNull(definition.nextStepAfter("result"))
        assertNull(definition.nextStepAfter("missing"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank experience id is rejected`() {
        ExperienceDefinition(" ", "Demo", validSteps())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank title is rejected`() {
        ExperienceDefinition("demo", " ", validSteps())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty steps are rejected`() {
        ExperienceDefinition("demo", "Demo", emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank step id is rejected`() {
        ExperienceStep(" ", ExperienceStepKind.EITHER_OR)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `duplicate step ids are rejected`() {
        ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("same", ExperienceStepKind.EITHER_OR),
                ExperienceStep("same", ExperienceStepKind.REVEAL)
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `flow without reveal is rejected`() {
        ExperienceDefinition(
            "demo",
            "Demo",
            listOf(ExperienceStep("choice", ExperienceStepKind.EITHER_OR))
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reveal before final step is rejected`() {
        ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("result", ExperienceStepKind.REVEAL),
                ExperienceStep("choice", ExperienceStepKind.EITHER_OR)
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `multiple reveals are rejected`() {
        ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("result1", ExperienceStepKind.REVEAL),
                ExperienceStep("result2", ExperienceStepKind.REVEAL)
            )
        )
    }

    @Test
    fun `navigator advances within step then between steps and stops after reveal`() {
        val definition = ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("choice", ExperienceStepKind.EITHER_OR),
                ExperienceStep("result", ExperienceStepKind.REVEAL)
            )
        )
        val navigator = ExperienceNavigator(definition) { stepId ->
            if (stepId == "choice") 3 else 1
        }

        assertEquals(ExperiencePosition(0, 1), navigator.next(ExperiencePosition(0, 0)))
        assertEquals(ExperiencePosition(0, 2), navigator.next(ExperiencePosition(0, 1)))
        assertEquals(ExperiencePosition(1, 0), navigator.next(ExperiencePosition(0, 2)))
        assertNull(navigator.next(ExperiencePosition(1, 0)))
    }

    @Test
    fun `navigator moves backward within step then to previous steps last item`() {
        val definition = ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("choice", ExperienceStepKind.EITHER_OR),
                ExperienceStep("ranking", ExperienceStepKind.RANKING),
                ExperienceStep("result", ExperienceStepKind.REVEAL)
            )
        )
        val navigator = ExperienceNavigator(definition) { stepId ->
            when (stepId) {
                "choice" -> 3
                "ranking" -> 2
                else -> 1
            }
        }

        assertEquals(ExperiencePosition(1, 0), navigator.previous(ExperiencePosition(1, 1)))
        assertEquals(ExperiencePosition(0, 2), navigator.previous(ExperiencePosition(1, 0)))
        assertEquals(ExperiencePosition(1, 1), navigator.previous(ExperiencePosition(2, 0)))
        assertNull(navigator.previous(ExperiencePosition(0, 0)))
        assertNull(navigator.previous(ExperiencePosition(99, 0)))
    }

    @Test
    fun `navigator normalizes nonpositive item counts to one`() {
        val definition = ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("zero", ExperienceStepKind.EITHER_OR),
                ExperienceStep("result", ExperienceStepKind.REVEAL)
            )
        )
        val navigator = ExperienceNavigator(definition) { -4 }

        assertEquals(2, navigator.totalItemCount())
        assertEquals(ExperiencePosition(1, 0), navigator.next(ExperiencePosition(0, 0)))
    }

    @Test
    fun `navigator handles invalid positions safely`() {
        val navigator = ExperienceNavigator(ExperienceDefinition("demo", "Demo", validSteps())) { 1 }

        assertNull(navigator.currentStep(ExperiencePosition(-1, 0)))
        assertNull(navigator.currentStep(ExperiencePosition(99, 0)))
        assertNull(navigator.currentStep(ExperiencePosition(0, -1)))
        assertNull(navigator.currentStep(ExperiencePosition(0, 99)))
        assertNull(navigator.next(ExperiencePosition(99, 0)))
        assertNull(navigator.previous(ExperiencePosition(99, 0)))
        assertEquals(0f, navigator.progress(ExperiencePosition(99, 0)))
    }

    @Test
    fun `progress is monotonic and reaches one on reveal`() {
        val definition = ExperienceDefinition(
            "demo",
            "Demo",
            listOf(
                ExperienceStep("choice", ExperienceStepKind.EITHER_OR),
                ExperienceStep("result", ExperienceStepKind.REVEAL)
            )
        )
        val navigator = ExperienceNavigator(definition) { stepId ->
            if (stepId == "choice") 3 else 1
        }

        val start = navigator.progress(ExperiencePosition(0, 0))
        val middle = navigator.progress(ExperiencePosition(0, 2))
        val reveal = navigator.progress(ExperiencePosition(1, 0))

        assertEquals(0f, start)
        assertTrue(middle > start)
        assertEquals(1f, reveal)
    }
}
