package com.example.data

import com.example.data.model.ExperienceEitherOrRound
import com.example.data.model.ExperienceOpenPromptRound
import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProposalLegacyContentReuse
import com.example.data.model.ProposalRingImageDuels

data class MigratedEitherOrRound(
    val sourcePackId: String,
    val sourceIndex: Int,
    val round: ExperienceEitherOrRound
) {
    init {
        require(sourcePackId.isNotBlank()) { "Migrated Either-Or content needs a source pack id." }
        require(sourceIndex >= 0) { "Migrated Either-Or content needs a non-negative source index." }
    }
}

data class MigratedOpenPromptRound(
    val sourcePackId: String,
    val sourceIndex: Int,
    val round: ExperienceOpenPromptRound
) {
    init {
        require(sourcePackId.isNotBlank()) { "Migrated open-prompt content needs a source pack id." }
        require(sourceIndex >= 0) { "Migrated open-prompt content needs a non-negative source index." }
    }
}

data class MigratedRingAssetReuse(
    val sourcePackId: String,
    val sourceLabel: String,
    val assetKey: String
) {
    init {
        require(sourcePackId.isNotBlank()) { "Migrated ring content needs a source pack id." }
        require(sourceLabel.isNotBlank()) { "Migrated ring content needs a source label." }
        require(assetKey.isNotBlank()) { "Migrated ring content needs an asset key." }
    }
}

/**
 * Stage 04.2 compatibility layer from the standalone proposal/ring/wedding packs into the
 * reusable Stage-03 Experience models.
 *
 * Source choices and curated open-prompt text are read from the shipped packs instead of copied.
 * This keeps the old source ids traceable while Stage 04.3/04.4 later remove duplicate catalogue
 * presentation and archive the standalone packs non-destructively.
 */
object ProposalLegacyExperienceMigration {
    private const val BOUQUET_PACK_ID = "straeusse"
    private const val WEDDING_STYLE_PACK_ID = "traumhochzeit"
    private const val RING_PACK_ID = "ringe"
    const val WEDDING_OPEN_PACK_ID = "h500_060_hochzeit_offene_runde"

    private val bouquetPrompts = listOf(
        "Welche Blumenrichtung passt eher zu eurer Hochzeit?",
        "Welche Wirkung soll der Strauß eher haben?",
        "Welche Größe fühlt sich für euch stimmiger an?",
        "Welche Farbwelt passt eher zu eurem Hochzeitstag?"
    )

    private val weddingStylePrompts = listOf(
        "Wie soll sich eure Feier eher anfühlen?",
        "Welche Kulisse passt eher zu eurem Hochzeitstag?",
        "Welche Form der Trauung fühlt sich eher nach euch an?",
        "Welche Jahreszeit passt eher zu eurer Hochzeit?"
    )

    val bouquetPreferences: List<MigratedEitherOrRound> by lazy {
        migrateDefaultPairs(
            packId = BOUQUET_PACK_ID,
            roundIdPrefix = "legacy_bouquet",
            prompts = bouquetPrompts
        )
    }

    val weddingStylePreferences: List<MigratedEitherOrRound> by lazy {
        migrateDefaultPairs(
            packId = WEDDING_STYLE_PACK_ID,
            roundIdPrefix = "legacy_wedding_style",
            prompts = weddingStylePrompts
        )
    }

    val weddingOpenPrompts: List<MigratedOpenPromptRound> by lazy {
        val source = GeneratedContentRegistry.PACKS.single { it.id == WEDDING_OPEN_PACK_ID }
        require(source.questions.isNotEmpty()) {
            "The curated Harmony-360 wedding source must keep at least one open prompt."
        }

        source.questions.mapIndexed { index, question ->
            MigratedOpenPromptRound(
                sourcePackId = source.id,
                sourceIndex = index,
                round = ExperienceOpenPromptRound(
                    id = "legacy_wedding_open_${index + 1}",
                    prompt = question.q
                )
            )
        }
    }

    /**
     * Explicit semantic bridge from the ten Stage-02 legacy ring concepts to the ten refreshed
     * proposal assets. The source labels are validated against the effective generated `ringe`
     * pack and the asset set is validated against the actual ProposalRingImageDuels.
     */
    val ringAssetReuses: List<MigratedRingAssetReuse> by lazy {
        val sourceToAsset = linkedMapOf(
            "Klassisch Solitär" to "ring_klassisch_solitaer",
            "Modern geometrisch" to "ring_modern_geometrisch",
            "Vintage Art déco" to "ring_art_deco",
            "Vintage verspielt" to "ring_vintage_verspielt",
            "Schmal & zart" to "ring_schmal_zart",
            "Markant & breit" to "ring_markant_breit",
            "Moderner Solitär" to "ring_moderner_solitaer",
            "Großer Stein" to "ring_grosser_stein",
            "Ohne Stein" to "ring_ohne_stein",
            "Diamanten im Band" to "ring_diamanten_band"
        )

        val effectiveRingPack = GeneratedContentRegistry.PACKS
            .singleOrNull { it.id == RING_PACK_ID }
            ?: error("The generated runtime ring override is required for Stage 04.2 migration.")
        val effectiveSourceLabels = effectiveRingPack.pairs
            .flatMap { (first, second) -> listOf(first, second) }
            .toSet()
        val proposalAssetKeys = ProposalRingImageDuels.rounds
            .flatMap { round -> listOf(round.firstAssetKey, round.secondAssetKey) }
            .toSet()

        require(sourceToAsset.keys == ProposalLegacyContentReuse.alreadyReusedRingLabels) {
            "Stage 04.2 ring migration must cover exactly the Stage-02 reuse manifest."
        }
        require(sourceToAsset.keys.all { it in effectiveSourceLabels }) {
            "Every migrated ring concept must still exist in the effective runtime ring pack."
        }
        require(sourceToAsset.values.toSet() == proposalAssetKeys) {
            "Stage 04.2 ring migration must cover exactly the proposal ring assets."
        }

        sourceToAsset.map { (sourceLabel, assetKey) ->
            MigratedRingAssetReuse(
                sourcePackId = RING_PACK_ID,
                sourceLabel = sourceLabel,
                assetKey = assetKey
            )
        }
    }

    val migratedSourcePackIds: Set<String>
        get() = setOf(
            RING_PACK_ID,
            BOUQUET_PACK_ID,
            WEDDING_STYLE_PACK_ID,
            WEDDING_OPEN_PACK_ID
        )

    private fun migrateDefaultPairs(
        packId: String,
        roundIdPrefix: String,
        prompts: List<String>
    ): List<MigratedEitherOrRound> {
        val source = HarmonyPacksData.DEFAULT_PACKS.single { it.id == packId }
        require(source.pairs.isNotEmpty()) { "Legacy pair source $packId must not be empty." }
        require(source.pairs.size == prompts.size) {
            "Legacy pair source $packId changed size; review the Stage 04.2 migration prompts."
        }

        return source.pairs.mapIndexed { index, (first, second) ->
            MigratedEitherOrRound(
                sourcePackId = source.id,
                sourceIndex = index,
                round = ExperienceEitherOrRound(
                    id = "${roundIdPrefix}_${index + 1}",
                    prompt = prompts[index],
                    firstChoice = first,
                    secondChoice = second
                )
            )
        }
    }
}
