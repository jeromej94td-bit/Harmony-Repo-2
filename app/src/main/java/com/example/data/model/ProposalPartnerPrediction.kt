package com.example.data.model

/**
 * Deterministic partner-prediction rounds for Stage 02.6.
 *
 * The existing PartnerPredictionBoard can render the prompt and options. This model keeps the
 * A → B → Reveal contract independent from navigation and persistence details.
 */
data class ProposalPartnerPredictionRound(
    val id: String,
    val prompt: String,
    val options: List<String>
) {
    init {
        require(id.isNotBlank()) { "Prediction rounds need a stable id." }
        require(prompt.isNotBlank()) { "Prediction rounds need a prompt." }
        require(options.size >= 2) { "Prediction rounds need at least two options." }
        require(options.all(String::isNotBlank)) { "Prediction options cannot be blank." }
        require(options.distinct().size == options.size) { "Prediction options must be unique." }
    }
}

object ProposalPartnerPrediction {
    const val STEP_ID = "partner_prediction"

    val rounds: List<ProposalPartnerPredictionRound> = listOf(
        ProposalPartnerPredictionRound(
            id = "prediction_ideal_moment",
            prompt = "Welcher Moment würde dein Partner für den Antrag wählen?",
            options = listOf("Bei Sonnenaufgang", "Bei Sonnenuntergang", "Ganz spontan")
        ),
        ProposalPartnerPredictionRound(
            id = "prediction_ideal_place",
            prompt = "Welcher Ort würde deinem Partner am meisten bedeuten?",
            options = listOf("Ein Ort mit eurer Geschichte", "Ein besonderer Ausblick", "Ganz privat zu Hause")
        ),
        ProposalPartnerPredictionRound(
            id = "prediction_ideal_afterward",
            prompt = "Was wäre deinem Partner direkt danach am wichtigsten?",
            options = listOf("Zeit zu zweit", "Mit Lieblingsmenschen feiern", "Den Moment festhalten")
        )
    )

    init {
        val predictionStep = ProposalExperienceDefinitions.perfectProposal.steps
            .firstOrNull { it.id == STEP_ID }
        require(predictionStep?.kind == ProposalFlowStepKind.PARTNER_PREDICTION) {
            "Partner prediction must attach to the partner_prediction step."
        }
        require(rounds.map(ProposalPartnerPredictionRound::id).distinct().size == rounds.size) {
            "Prediction round ids must be unique."
        }
    }
}
