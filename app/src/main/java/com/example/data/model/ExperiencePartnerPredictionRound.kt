package com.example.data.model

/**
 * One reusable partner-prediction round for a Harmony experience.
 *
 * The prediction and actual choice remain separate so the A → B → Reveal semantics are preserved.
 */
data class ExperiencePartnerPredictionRound(
    val id: String,
    val prompt: String,
    val options: List<String>
) {
    init {
        require(id.isNotBlank()) { "Partner-prediction rounds need a stable id." }
        require(prompt.isNotBlank()) { "Partner-prediction rounds need a prompt." }
        require(options.size >= 2) { "Partner-prediction rounds need at least two options." }
        require(options.all(String::isNotBlank)) { "Partner-prediction options cannot be blank." }
        require(options.distinct().size == options.size) {
            "Partner-prediction options must be unique."
        }
    }
}

data class ExperiencePartnerPredictionSelection(
    val prediction: String,
    val actual: String
) {
    val isHit: Boolean get() = prediction == actual
}

object ExperiencePartnerPredictionSelectionCodec {
    fun encode(selection: ExperiencePartnerPredictionSelection): String =
        PredictionAnswerCodec.encode(selection.prediction, selection.actual)

    fun decode(encoded: String): ExperiencePartnerPredictionSelection? =
        PredictionAnswerCodec.decode(encoded)?.let {
            ExperiencePartnerPredictionSelection(it.prediction, it.actual)
        }
}

/** Compatibility adapter: proposal content stays feature-owned. */
fun ProposalPartnerPredictionRound.toExperiencePartnerPredictionRound(): ExperiencePartnerPredictionRound =
    ExperiencePartnerPredictionRound(id = id, prompt = prompt, options = options)
