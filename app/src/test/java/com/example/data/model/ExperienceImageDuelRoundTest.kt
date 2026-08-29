package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExperienceImageDuelRoundTest {

    private fun option(
        id: String = "first",
        label: String = "Erste Wahl",
        assetKey: String = "asset_first"
    ) = ExperienceImageDuelOption(id = id, label = label, assetKey = assetKey)

    @Test
    fun `generic image duel round keeps stable option identity and assets`() {
        val first = option()
        val second = option("second", "Zweite Wahl", "asset_second")
        val round = ExperienceImageDuelRound(
            id = "duel",
            prompt = "Was passt besser?",
            firstOption = first,
            secondOption = second
        )

        assertEquals("duel", round.id)
        assertEquals("Was passt besser?", round.prompt)
        assertEquals(first, round.firstOption)
        assertEquals(second, round.secondOption)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank option id is rejected`() {
        option(id = " ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank option label is rejected`() {
        option(label = " ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank asset key is rejected`() {
        option(assetKey = " ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank round id is rejected`() {
        ExperienceImageDuelRound(" ", "Prompt", option(), option("b", "B", "asset_b"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `blank prompt is rejected`() {
        ExperienceImageDuelRound("round", " ", option(), option("b", "B", "asset_b"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `duplicate option ids are rejected`() {
        ExperienceImageDuelRound(
            "round",
            "Prompt",
            option(id = "same", assetKey = "asset_a"),
            option(id = "same", label = "B", assetKey = "asset_b")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `duplicate asset keys are rejected`() {
        ExperienceImageDuelRound(
            "round",
            "Prompt",
            option(id = "a", assetKey = "same_asset"),
            option(id = "b", label = "B", assetKey = "same_asset")
        )
    }

    @Test
    fun `proposal image duel adapter preserves location and ring content exactly`() {
        assertEquals(
            listOf("proposal_location", "ring_style"),
            ProposalImageDuelAdapter.byStepId.keys.toList()
        )
        assertEquals(3, ProposalImageDuelAdapter.roundsFor("proposal_location").size)
        assertEquals(5, ProposalImageDuelAdapter.roundsFor("ring_style").size)
        assertEquals(emptyList<ExperienceImageDuelRound>(), ProposalImageDuelAdapter.roundsFor("missing"))

        ProposalLocationDuels.rounds.zip(ProposalImageDuelAdapter.roundsFor("proposal_location"))
            .forEach { (proposal, generic) ->
                assertEquals(proposal.id, generic.id)
                assertEquals(proposal.prompt, generic.prompt)
                assertEquals(proposal.firstOption.id, generic.firstOption.id)
                assertEquals(proposal.firstOption.label, generic.firstOption.label)
                assertEquals(proposal.firstOption.id, generic.firstOption.assetKey)
                assertEquals(proposal.secondOption.id, generic.secondOption.id)
                assertEquals(proposal.secondOption.label, generic.secondOption.label)
                assertEquals(proposal.secondOption.id, generic.secondOption.assetKey)
            }

        ProposalRingImageDuels.rounds.zip(ProposalImageDuelAdapter.roundsFor("ring_style"))
            .forEach { (proposal, generic) ->
                assertEquals(proposal.id, generic.id)
                assertEquals(proposal.prompt, generic.prompt)
                assertEquals(proposal.firstAssetKey, generic.firstOption.id)
                assertEquals(proposal.firstLabel, generic.firstOption.label)
                assertEquals(proposal.firstAssetKey, generic.firstOption.assetKey)
                assertEquals(proposal.secondAssetKey, generic.secondOption.id)
                assertEquals(proposal.secondLabel, generic.secondOption.label)
                assertEquals(proposal.secondAssetKey, generic.secondOption.assetKey)
            }
    }

    @Test
    fun `proposal runner keeps three location and five ring image duels`() {
        assertEquals(3, ProposalExperienceRunnerPolicy.itemCount("proposal_location"))
        assertEquals(5, ProposalExperienceRunnerPolicy.itemCount("ring_style"))
        assertEquals(8, ProposalImageDuelAdapter.roundsFor("proposal_location").size +
            ProposalImageDuelAdapter.roundsFor("ring_style").size)
        assertNull(ProposalExperienceRunnerPolicy.next(ProposalRunnerPosition(8, 0)))
    }
}
