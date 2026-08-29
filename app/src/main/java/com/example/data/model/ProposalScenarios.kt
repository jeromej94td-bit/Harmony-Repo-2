package com.example.data.model

/**
 * One concrete pressure-point decision for Stage 02.7 of the perfect-proposal experience.
 *
 * Rendering stays with the existing scenario mechanic. This model only owns deterministic
 * proposal-specific content and its binding to the stable proposal flow step.
 */
data class ProposalScenarioRound(
    val id: String,
    val prompt: String,
    val options: List<String>
) {
    init {
        require(id.isNotBlank()) { "Proposal scenarios need a stable id." }
        require(prompt.isNotBlank()) { "Proposal scenarios need a prompt." }
        require(options.size == 4) { "Proposal scenarios need exactly four decisions." }
        require(options.all(String::isNotBlank)) { "Proposal scenario decisions cannot be blank." }
        require(options.distinct().size == options.size) {
            "Proposal scenario decisions must be unique inside a round."
        }
    }
}

object ProposalScenarios {
    const val STEP_ID = "proposal_scenarios"

    val rounds: List<ProposalScenarioRound> = listOf(
        ProposalScenarioRound(
            id = "scenario_weather_breaks_plan",
            prompt = "Eine Stunde vor dem Antrag zieht ein Gewitter auf und euer geplanter Ort draußen fällt aus. Was wäre jetzt am meisten euer Weg?",
            options = listOf(
                "Spontan einen neuen Ort suchen und den Moment trotzdem heute erleben",
                "Nach Hause wechseln und aus dem Plan einen ganz intimen Abend machen",
                "Auf eine Wetterlücke warten, weil genau dieser Ort Teil der Geschichte ist",
                "Den Antrag verschieben, bis sich Ort und Moment wieder wirklich richtig anfühlen"
            )
        ),
        ProposalScenarioRound(
            id = "scenario_privacy_vs_loved_ones",
            prompt = "Eure Lieblingsmenschen warten heimlich in der Nähe, aber dein Partner sagt an diesem Abend, dass er heute nur Zeit zu zweit braucht. Wie gehst du damit um?",
            options = listOf(
                "Alle absagen und den Antrag bewusst nur zu zweit erleben",
                "Den Antrag privat machen und die anderen erst danach zum Feiern dazuholen",
                "Offen fragen, ob später noch ein kleiner gemeinsamer Moment okay wäre",
                "Den ganzen Plan auf einen anderen Tag verschieben, ohne Druck zu machen"
            )
        ),
        ProposalScenarioRound(
            id = "scenario_partner_senses_surprise",
            prompt = "Kurz bevor es losgeht, merkt dein Partner deine Nervosität und fragt direkt: „Hast du heute etwas Besonderes vor?“ Wie reagierst du?",
            options = listOf(
                "Den geplanten Ablauf loslassen und genau jetzt aus dem Gefühl heraus fragen",
                "Charmant ablenken und die Überraschung noch ein kleines Stück weitertragen",
                "Ehrlich sagen, dass etwas Besonderes kommt, ohne den eigentlichen Moment zu verraten",
                "Die Vorbereitung beiseitelegen und erst einmal gemeinsam im Augenblick ankommen"
            )
        ),
        ProposalScenarioRound(
            id = "scenario_ring_missing",
            prompt = "Der perfekte Moment ist da, aber plötzlich merkst du: Der Ring ist nicht bei dir. Was soll jetzt wichtiger sein als der ursprüngliche Plan?",
            options = listOf(
                "Trotzdem fragen, weil die Worte wichtiger sind als das Schmuckstück",
                "Ein spontanes Symbol aus dem Moment nehmen und den Ring später überreichen",
                "Kurz unterbrechen, den Ring holen und dann bewusst an diesen Ort zurückkehren",
                "Den Moment nicht erzwingen und den Antrag auf später verschieben"
            )
        ),
        ProposalScenarioRound(
            id = "scenario_emotional_pause",
            prompt = "Während du anfängst zu sprechen, wird dein Partner sehr emotional und sagt: „Warte kurz, ich muss einmal durchatmen.“ Was tust du?",
            options = listOf(
                "Sofort pausieren, Nähe geben und überhaupt nichts weiter erwarten",
                "Die Hand halten und erst fragen, ob dein Partner hören möchte, was du sagen wolltest",
                "Die vorbereiteten Worte weglegen und nur noch ehrlich aus dem Moment sprechen",
                "Den Antrag für heute stoppen und den emotionalen Moment einfach gemeinsam tragen"
            )
        ),
        ProposalScenarioRound(
            id = "scenario_public_attention",
            prompt = "Mitten in eurem eigentlich persönlichen Moment bemerken Fremde, was passiert, filmen und applaudieren. Wie schützt ihr das, was gerade zwischen euch passiert?",
            options = listOf(
                "Alles ausblenden und den Blick nur beim Partner lassen",
                "Kurz um Ruhe bitten, damit der eigentliche Moment wieder euch gehört",
                "Gemeinsam darüber lachen und die unerwartete Energie in den Moment aufnehmen",
                "Dem Partner die Entscheidung lassen, ob ihr bleibt oder an einen ruhigeren Ort geht"
            )
        )
    )

    init {
        val scenarioStep = ProposalExperienceDefinitions.perfectProposal.steps
            .singleOrNull { it.id == STEP_ID }
        require(scenarioStep?.kind == ProposalFlowStepKind.SCENARIO) {
            "Proposal scenarios must attach to the proposal_scenarios scenario step."
        }

        require(rounds.map(ProposalScenarioRound::id).distinct().size == rounds.size) {
            "Proposal scenario round ids must be unique."
        }
    }
}
