package com.example.data.model

/**
 * Stable answers collected by the proposal runner before the final qualitative reveal.
 *
 * Values that already have stable IDs keep those IDs here. Free-text/binary choices keep their
 * exact selected text so the reveal can validate them against the existing proposal content.
 */
data class ProposalRevealInput(
    val eitherOrSelections: Map<String, String> = emptyMap(),
    val locationSelections: Map<String, String> = emptyMap(),
    val ringSelections: Map<String, String> = emptyMap(),
    val rankedPriorityIds: List<String> = emptyList(),
    val predictionMatches: Int? = null,
    val predictionTotal: Int = ProposalPartnerPrediction.rounds.size,
    val scenarioSelections: Map<String, String> = emptyMap(),
    val personalWishAnswers: Map<String, String> = emptyMap()
) {
    init {
        require(predictionTotal >= 0) { "Prediction total cannot be negative." }
        require(predictionMatches == null || predictionMatches in 0..predictionTotal) {
            "Prediction matches must fit inside the prediction total."
        }
    }
}

data class ProposalRevealSection(
    val id: String,
    val title: String,
    val values: List<String>
) {
    init {
        require(id.isNotBlank()) { "Reveal sections need a stable id." }
        require(title.isNotBlank()) { "Reveal sections need a title." }
        require(values.isNotEmpty()) { "Reveal sections need at least one value." }
        require(values.all(String::isNotBlank)) { "Reveal section values cannot be blank." }
    }
}

data class ProposalRevealResult(
    val title: String,
    val subtitle: String,
    val sections: List<ProposalRevealSection>,
    val closing: String
) {
    init {
        require(title.isNotBlank()) { "Proposal reveal needs a title." }
        require(subtitle.isNotBlank()) { "Proposal reveal needs a subtitle." }
        require(closing.isNotBlank()) { "Proposal reveal needs a closing line." }
    }
}

/**
 * Stage 02.9 result synthesis.
 *
 * This intentionally produces no point score. It resolves the stable choices made across the
 * proposal experience into a qualitative, human-readable picture that Stage 02.11 can render.
 */
object ProposalReveal {
    const val STEP_ID = "perfect_proposal_reveal"

    init {
        val finalStep = ProposalExperienceDefinitions.perfectProposal.steps.lastOrNull()
        require(finalStep?.id == STEP_ID && finalStep.kind == ProposalFlowStepKind.REVEAL) {
            "Perfect-proposal reveal must stay the final reveal step."
        }
    }

    fun build(input: ProposalRevealInput): ProposalRevealResult {
        val mood = resolveEitherOr("proposal_mood", input.eitherOrSelections)
        val details = resolveEitherOr("proposal_details", input.eitherOrSelections)
        val locations = resolveLocations(input.locationSelections)
        val rings = resolveRings(input.ringSelections)
        val priorities = resolvePriorities(input.rankedPriorityIds)
        val prediction = predictionNarrative(input.predictionMatches, input.predictionTotal)
        val scenarios = resolveScenarios(input.scenarioSelections)
        val personal = resolvePersonalWishes(input.personalWishAnswers)

        val sections = buildList {
            addSection("mood", "So soll es sich anfühlen", mood)
            addSection("details", "Eure persönlichen Details", details)
            addSection("location", "Eure Kulisse", locations)
            addSection("ring", "Euer Ringgefühl", rings)
            addSection("priorities", "Was wirklich zählt", priorities.take(3))
            prediction?.let { addSection("prediction", "Wie ihr euch gegenseitig lest", listOf(it)) }
            addSection("scenarios", "Wenn der Plan anders läuft", scenarios)
            addSection("personal_wishes", "Eure eigenen Worte", personal.map { it.second })
        }

        val topPriority = priorities.firstOrNull()
        val firstLocation = locations.firstOrNull()
        val subtitle = when {
            firstLocation != null && topPriority != null ->
                "Als Kulisse zieht es euch zu: $firstLocation. Im Mittelpunkt steht: $topPriority."
            topPriority != null -> "Im Mittelpunkt steht für euch: $topPriority."
            firstLocation != null -> "Als Kulisse zieht es euch zu: $firstLocation."
            else -> "Aus euren Entscheidungen entsteht ein Bild davon, was sich für euch wirklich richtig anfühlt."
        }

        val firstPersonalAnswer = personal.firstOrNull()?.second
        val closing = if (firstPersonalAnswer != null) {
            "Und die Worte, die bleiben sollen: „$firstPersonalAnswer“"
        } else {
            "Nicht perfekt nach Plan – sondern persönlich, ehrlich und ganz euer Moment."
        }

        return ProposalRevealResult(
            title = revealTitle(mood),
            subtitle = subtitle,
            sections = sections,
            closing = closing
        )
    }

    private fun MutableList<ProposalRevealSection>.addSection(
        id: String,
        title: String,
        values: List<String>
    ) {
        val clean = values.map(String::trim).filter(String::isNotBlank).distinct()
        if (clean.isNotEmpty()) add(ProposalRevealSection(id, title, clean))
    }

    private fun resolveEitherOr(
        stepId: String,
        selections: Map<String, String>
    ): List<String> = ProposalEitherOrRounds.roundsFor(stepId).mapNotNull { round ->
        selections[round.id]?.takeIf { selected ->
            selected == round.firstChoice || selected == round.secondChoice
        }
    }

    private fun resolveLocations(selections: Map<String, String>): List<String> =
        ProposalLocationDuels.rounds.mapNotNull { round ->
            when (selections[round.id]) {
                round.firstOption.id -> round.firstOption.label
                round.secondOption.id -> round.secondOption.label
                else -> null
            }
        }

    private fun resolveRings(selections: Map<String, String>): List<String> =
        ProposalRingImageDuels.rounds.mapNotNull { round ->
            when (selections[round.id]) {
                round.firstAssetKey -> round.firstLabel
                round.secondAssetKey -> round.secondLabel
                else -> null
            }
        }

    private fun resolvePriorities(priorityIds: List<String>): List<String> {
        val byId = ProposalPriorityRanking.priorities.associateBy(ProposalPriority::id)
        return priorityIds.distinct().mapNotNull { id -> byId[id]?.label }
    }

    private fun resolveScenarios(selections: Map<String, String>): List<String> =
        ProposalScenarios.rounds.mapNotNull { round ->
            selections[round.id]?.takeIf { it in round.options }
        }

    private fun resolvePersonalWishes(answers: Map<String, String>): List<Pair<String, String>> =
        ProposalOpenPrompts.prompts.mapNotNull { prompt ->
            answers[prompt.id]?.trim()?.takeIf(String::isNotBlank)?.let { prompt.prompt to it }
        }

    private fun predictionNarrative(matches: Int?, total: Int): String? {
        if (matches == null || total <= 0) return null
        return when {
            matches == total ->
                "Ihr habt ein sehr feines Gespür dafür, was dem anderen in diesem Moment wichtig ist."
            matches * 2 >= total ->
                "Ihr lest euch schon ziemlich gut – und lasst trotzdem Raum für kleine Überraschungen."
            matches == 0 ->
                "Gerade die Unterschiede zeigen, wo zwischen euch noch echte Überraschungen stecken."
            else ->
                "Ein Teil war sofort klar, ein anderer hat euch überrascht – genau das macht euren Blick aufeinander spannend."
        }
    }

    private fun revealTitle(mood: List<String>): String = when {
        "Leise & intim" in mood && "Nur wir zwei" in mood ->
            "Ein stiller Moment, der nur euch gehört"
        "Groß & feierlich" in mood && "Mit unseren Lieblingsmenschen" in mood ->
            "Ein großer Moment, den ihr gemeinsam feiert"
        "Spontan im Augenblick" in mood || "Überraschend" in mood ->
            "Ein Antrag, der aus dem Augenblick lebt"
        "Bis ins Detail vorbereitet" in mood ->
            "Ein liebevoll vorbereiteter Moment mit eurer Handschrift"
        else -> "Ein persönlicher Antrag mit eurer eigenen Handschrift"
    }
}
