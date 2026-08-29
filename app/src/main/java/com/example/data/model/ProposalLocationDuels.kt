package com.example.data.model

/** A stable, user-facing location option for the proposal image-duel step. */
data class ProposalImageDuelOption(
    val id: String,
    val label: String
) {
    init {
        require(id.isNotBlank()) { "Proposal image-duel options need a stable id." }
        require(label.isNotBlank()) { "Proposal image-duel options need a label." }
    }
}

/** One visual choice between two proposal locations. */
data class ProposalImageDuelRound(
    val id: String,
    val prompt: String,
    val firstOption: ProposalImageDuelOption,
    val secondOption: ProposalImageDuelOption
) {
    init {
        require(id.isNotBlank()) { "Proposal image-duel rounds need a stable id." }
        require(prompt.isNotBlank()) { "Proposal image-duel rounds need a prompt." }
        require(firstOption.id != secondOption.id) {
            "Proposal image-duel rounds need two distinct options."
        }
    }
}

/** Content for Stage 02.3 of the proposal experience. */
object ProposalLocationDuels {
    private const val LOCATION_STEP_ID = "proposal_location"

    val rounds: List<ProposalImageDuelRound> = listOf(
        ProposalImageDuelRound(
            id = "location_home_or_lake",
            prompt = "Wo beginnt euer perfekter Moment?",
            firstOption = ProposalImageDuelOption("location_home", "Bei uns zu Hause"),
            secondOption = ProposalImageDuelOption("location_lake", "Am stillen See")
        ),
        ProposalImageDuelRound(
            id = "location_garden_or_view",
            prompt = "Welche Kulisse fühlt sich nach euch an?",
            firstOption = ProposalImageDuelOption("location_garden", "Im Lichtergarten"),
            secondOption = ProposalImageDuelOption("location_view", "Mit weiter Aussicht")
        ),
        ProposalImageDuelRound(
            id = "location_city_or_coast",
            prompt = "Wohin zieht es euch für diesen Augenblick?",
            firstOption = ProposalImageDuelOption("location_city", "Über den Dächern der Stadt"),
            secondOption = ProposalImageDuelOption("location_coast", "Am Meer bei Sonnenuntergang")
        )
    )

    init {
        val locationStep = ProposalExperienceDefinitions.perfectProposal.steps
            .singleOrNull { it.id == LOCATION_STEP_ID }
        require(locationStep?.kind == ProposalFlowStepKind.IMAGE_DUEL) {
            "Proposal location content must bind to the proposal image-duel step."
        }

        val roundIds = rounds.map(ProposalImageDuelRound::id)
        require(roundIds.distinct().size == roundIds.size) {
            "Proposal location-duel round ids must be unique."
        }

        val optionIds = rounds.flatMap { listOf(it.firstOption.id, it.secondOption.id) }
        require(optionIds.distinct().size == optionIds.size) {
            "Proposal location-duel option ids must be unique."
        }
    }
}
