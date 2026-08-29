package com.example.data.model

/** One image-backed choice inside a reusable Harmony image duel. */
data class ExperienceImageDuelOption(
    val id: String,
    val label: String,
    val assetKey: String
) {
    init {
        require(id.isNotBlank()) { "Image-duel options need a stable id." }
        require(label.isNotBlank()) { "Image-duel options need a label." }
        require(assetKey.isNotBlank()) { "Image-duel options need an asset key." }
    }
}

/** UI-independent two-image round reusable by any Harmony experience. */
data class ExperienceImageDuelRound(
    val id: String,
    val prompt: String,
    val firstOption: ExperienceImageDuelOption,
    val secondOption: ExperienceImageDuelOption
) {
    init {
        require(id.isNotBlank()) { "Image-duel rounds need a stable id." }
        require(prompt.isNotBlank()) { "Image-duel rounds need a prompt." }
        require(firstOption.id != secondOption.id) {
            "Image-duel rounds need two distinct option ids."
        }
        require(firstOption.assetKey != secondOption.assetKey) {
            "Image-duel rounds need two distinct assets."
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
            assetKey = firstOption.id
        ),
        secondOption = ExperienceImageDuelOption(
            id = secondOption.id,
            label = secondOption.label,
            assetKey = secondOption.id
        )
    )

fun ProposalRingImageDuel.toExperienceImageDuelRound(): ExperienceImageDuelRound =
    ExperienceImageDuelRound(
        id = id,
        prompt = prompt,
        firstOption = ExperienceImageDuelOption(
            id = firstAssetKey,
            label = firstLabel,
            assetKey = firstAssetKey
        ),
        secondOption = ExperienceImageDuelOption(
            id = secondAssetKey,
            label = secondLabel,
            assetKey = secondAssetKey
        )
    )
