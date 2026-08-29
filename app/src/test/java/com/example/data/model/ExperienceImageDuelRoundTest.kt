package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceImageDuelRoundTest {

    @Test
    fun `round keeps stable id prompt and two distinct image options`() {
        val round = ExperienceImageDuelRound(
            id = "trip_mountains_or_sea",
            prompt = "Welche Kulisse zieht euch mehr an?",
            firstOption = ExperienceImageDuelOption("mountains", "Berge", "trip_mountains"),
            secondOption = ExperienceImageDuelOption("sea", "Meer", "trip_sea")
        )

        assertEquals("trip_mountains_or_sea", round.id)
        assertEquals("Welche Kulisse zieht euch mehr an?", round.prompt)
        assertEquals("mountains", round.firstOption.id)
        assertEquals("trip_mountains", round.firstOption.imageKey)
        assertEquals("sea", round.secondOption.id)
        assertEquals("trip_sea", round.secondOption.imageKey)
    }

    @Test
    fun `malformed options and rounds fail fast`() {
        assertIllegalArgument { ExperienceImageDuelOption("", "A", "asset_a") }
        assertIllegalArgument { ExperienceImageDuelOption("a", "", "asset_a") }
        assertIllegalArgument { ExperienceImageDuelOption("a", "A", "") }
        assertIllegalArgument {
            ExperienceImageDuelRound(
                id = "",
                prompt = "Frage",
                firstOption = ExperienceImageDuelOption("a", "A", "asset_a"),
                secondOption = ExperienceImageDuelOption("b", "B", "asset_b")
            )
        }
        assertIllegalArgument {
            ExperienceImageDuelRound(
                id = "round",
                prompt = "",
                firstOption = ExperienceImageDuelOption("a", "A", "asset_a"),
                secondOption = ExperienceImageDuelOption("b", "B", "asset_b")
            )
        }
        assertIllegalArgument {
            ExperienceImageDuelRound(
                id = "round",
                prompt = "Frage",
                firstOption = ExperienceImageDuelOption("same", "A", "asset_a"),
                secondOption = ExperienceImageDuelOption("same", "B", "asset_b")
            )
        }
    }

    @Test
    fun `proposal location duels adapt without changing ids labels or image mapping`() {
        ProposalLocationDuels.rounds.forEach { proposalRound ->
            val generic = proposalRound.toExperienceImageDuelRound()

            assertEquals(proposalRound.id, generic.id)
            assertEquals(proposalRound.prompt, generic.prompt)
            assertEquals(proposalRound.firstOption.id, generic.firstOption.id)
            assertEquals(proposalRound.firstOption.label, generic.firstOption.label)
            assertEquals("proposal_${proposalRound.firstOption.id}", generic.firstOption.imageKey)
            assertEquals(proposalRound.secondOption.id, generic.secondOption.id)
            assertEquals(proposalRound.secondOption.label, generic.secondOption.label)
            assertEquals("proposal_${proposalRound.secondOption.id}", generic.secondOption.imageKey)
        }
    }

    @Test
    fun `proposal ring duels adapt without changing asset keys or labels`() {
        ProposalRingImageDuels.rounds.forEach { proposalRound ->
            val generic = proposalRound.toExperienceImageDuelRound()

            assertEquals(proposalRound.id, generic.id)
            assertEquals(proposalRound.prompt, generic.prompt)
            assertEquals(proposalRound.firstAssetKey, generic.firstOption.id)
            assertEquals(proposalRound.firstLabel, generic.firstOption.label)
            assertEquals(proposalRound.firstAssetKey, generic.firstOption.imageKey)
            assertEquals(proposalRound.secondAssetKey, generic.secondOption.id)
            assertEquals(proposalRound.secondLabel, generic.secondOption.label)
            assertEquals(proposalRound.secondAssetKey, generic.secondOption.imageKey)
        }
    }

    private fun assertIllegalArgument(block: () -> Unit) {
        var failed = false
        try {
            block()
        } catch (_: IllegalArgumentException) {
            failed = true
        }
        assertTrue("Expected IllegalArgumentException", failed)
    }
}
