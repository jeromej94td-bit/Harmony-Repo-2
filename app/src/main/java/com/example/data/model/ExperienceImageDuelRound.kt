package com.example.data.model

/** One reusable visual choice in a Harmony experience image duel. */
data class ExperienceImageDuelOption(
    val id: String,
    val label: String,
    val imageKey: String
) {
    init {
        require(id.isNotBlank()) { "Experience image-duel options need a stable id." }
        require(label.isNotBlank()) { "Experience image-duel options need a label." }
        require(imageKey.isNotBlank()) { "Experience image-duel options need an image key." }
    }
}

/** UI-independent reusable choice between two image-backed options. */
data class ExperienceImageDuelRound(
    val id: String,
    val prompt: String,
    val firstOption: ExperienceImageDuelOption,
    val secondOption: ExperienceImageDuelOption
) {
    init {
        require(id.isNotBlank()) { "Experience image-duel rounds need a stable id." }
        require(prompt.isNotBlank()) { "Experience image-duel rounds need a prompt." }
        require(firstOption.id != secondOption.id) {
            "Experience image-duel rounds need two distinct option ids."
        }
    }
}

fun ProposalImageDuelRound.toExperienceImageDuelRound(): ExperienceImageDuelRound =
    ExperienceImageDuelRound(
        id = id,
        prompt = prompt,
        firstOption = ExperienceImageDuelOption(
            id = firstOption.id,
            label = firstOption.label,
            imageKey = "proposal_${firstOption.id}"
        ),
        secondOption = ExperienceImageDuelOption(
            id = secondOption.id,
            label = secondOption.label,
            imageKey = "proposal_${secondOption.id}"
        )
    )

fun ProposalRingImageDuel.toExperienceImageDuelRound(): ExperienceImageDuelRound =
    ExperienceImageDuelRound(
        id = id,
        prompt = prompt,
        firstOption = ExperienceImageDuelOption(
            id = firstAssetKey,
            label = firstLabel,
            imageKey = firstAssetKey
        ),
        secondOption = ExperienceImageDuelOption(
            id = secondAssetKey,
            label = secondLabel,
            imageKey = secondAssetKey
        )
    )
