package com.example.data.model

/**
 * UI-independent ring-image duel content for Stage 02.4.
 *
 * The refreshed drawable assets are referenced by stable resource keys so the later experience
 * runner can render them without changing the deterministic proposal flow contract.
 */
data class ProposalRingImageDuel(
    val id: String,
    val prompt: String,
    val firstAssetKey: String,
    val firstLabel: String,
    val secondAssetKey: String,
    val secondLabel: String
) {
    init {
        require(id.isNotBlank()) { "Ring duels need a stable id." }
        require(prompt.isNotBlank()) { "Ring duels need a prompt." }
        require(firstAssetKey.isNotBlank() && secondAssetKey.isNotBlank()) {
            "Ring duels need two asset keys."
        }
        require(firstAssetKey != secondAssetKey) { "Ring duel asset keys must differ." }
        require(firstLabel.isNotBlank() && secondLabel.isNotBlank()) {
            "Ring duels need two labels."
        }
    }
}

/**
 * The five deterministic ring choices used by the perfect-proposal experience.
 *
 * Asset keys intentionally match the refreshed drawable names from Stage 01. No UI, navigation,
 * legacy deletion, or image downloading belongs in this package.
 */
object ProposalRingImageDuels {
    const val STEP_ID = "ring_style"

    val rounds: List<ProposalRingImageDuel> = listOf(
        ProposalRingImageDuel(
            id = "ring_classic_or_geometric",
            prompt = "Welche Ringsprache fühlt sich mehr nach euch an?",
            firstAssetKey = "ring_klassisch_solitaer",
            firstLabel = "Klassisch & zeitlos",
            secondAssetKey = "ring_modern_geometrisch",
            secondLabel = "Modern & geometrisch"
        ),
        ProposalRingImageDuel(
            id = "ring_art_deco_or_vintage",
            prompt = "Welcher Stil passt zu eurem gemeinsamen Moment?",
            firstAssetKey = "ring_art_deco",
            firstLabel = "Art déco & elegant",
            secondAssetKey = "ring_vintage_verspielt",
            secondLabel = "Vintage & verspielt"
        ),
        ProposalRingImageDuel(
            id = "ring_delicate_or_statement",
            prompt = "Wie präsent darf der Ring an eurer Hand sein?",
            firstAssetKey = "ring_schmal_zart",
            firstLabel = "Schmal & zart",
            secondAssetKey = "ring_markant_breit",
            secondLabel = "Markant & ausdrucksstark"
        ),
        ProposalRingImageDuel(
            id = "ring_solitaire_or_centerpiece",
            prompt = "Was soll den Blick zuerst auf sich ziehen?",
            firstAssetKey = "ring_moderner_solitaer",
            firstLabel = "Ein klarer Solitär",
            secondAssetKey = "ring_grosser_stein",
            secondLabel = "Ein großer Mittelpunkt"
        ),
        ProposalRingImageDuel(
            id = "ring_minimal_or_banded",
            prompt = "Welche Form wirkt für euch am stimmigsten?",
            firstAssetKey = "ring_ohne_stein",
            firstLabel = "Schlicht & ohne Stein",
            secondAssetKey = "ring_diamanten_band",
            secondLabel = "Band mit Diamanten"
        )
    )

    init {
        val imageDuelStep = ProposalExperienceDefinitions.perfectProposal.steps
            .firstOrNull { it.id == STEP_ID }
        require(imageDuelStep?.kind == ProposalFlowStepKind.IMAGE_DUEL) {
            "Ring duels must attach to the ring_style image-duel step."
        }

        val assetKeys = rounds.flatMap { listOf(it.firstAssetKey, it.secondAssetKey) }
        require(assetKeys.distinct().size == assetKeys.size) {
            "Ring duel asset keys must be unique."
        }
        require(rounds.map(ProposalRingImageDuel::id).distinct().size == rounds.size) {
            "Ring duel ids must be unique."
        }
    }
}
