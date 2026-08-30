package com.example.data

/** Non-mutating final quality gate for Stage 05.5. */
object Harmony360Stage055FinalAudit {
    private val sectionTags = setOf(
        "h360_section_13_persoenlichkeit_werte",
        "h360_section_15_glaube_religion",
        "h360_section_16_politik_gesellschaft",
        "h360_section_18_humor_lachen",
        "h360_section_19_fantasie_was_waere_wenn",
        "h360_section_20_teamwork_challenge"
    )

    private val visibleTopicIds = setOf(
        "aufwaermen", "beziehung", "sex", "moral", "geld", "kennen",
        "reisen", "familie", "hobbys", "filme_serien", "essen"
    )

    val expectedVisibleIds: Set<String> = linkedSetOf<String>().apply {
        addAll(Harmony360Stage055ExistingCurationAudit.expectedVisibleIds)
        addAll(Harmony360HumorSectionCuration.decisions.filterValues {
            it != Harmony360HumorSectionCuration.CurationDecision.ARCHIVE
        }.keys)
        addAll(Harmony360TeamworkSectionCuration.decisions.filterValues {
            it != Harmony360TeamworkSectionCuration.CurationDecision.ARCHIVE
        }.keys)
    }

    private val allRawIds: Set<String> by lazy {
        buildSet {
            addAll(GeneratedHarmonyAdrenaline360Section13PersoenlichkeitWerte.PACKS.map { it.id })
            addAll(GeneratedHarmonyAdrenaline360Section15GlaubeReligion.PACKS.map { it.id })
            addAll(GeneratedHarmonyAdrenaline360Section16PolitikGesellschaft.PACKS.map { it.id })
            addAll(GeneratedHarmonyAdrenaline360Section18HumorLachen.PACKS.map { it.id })
            addAll(GeneratedHarmonyAdrenaline360Section19FantasieWasWaereWenn.PACKS.map { it.id })
            addAll(GeneratedHarmonyAdrenaline360Section20TeamworkChallenge.PACKS.map { it.id })
        }
    }

    val expectedArchivedIds: Set<String> by lazy { allRawIds - expectedVisibleIds }

    private val intentionalMechanicSets = setOf(
        normalizedOptions(listOf("{user}", "{partner}", "Beide", "Niemand"))
    )

    private val bannedQuartets = listOf(
        listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        listOf("Aktiv", "Passiv", "Etwas lernen", "Einfach abschalten"),
        listOf("Kopf", "Herz", "Bauch", "Erfahrung"),
        listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
        listOf("Spontan", "Geplant", "Vertraut", "Etwas völlig Neues"),
        listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
        listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung")
    ).map(::normalizedOptions).toSet()

    private val forbiddenTextFragments = listOf(
        "what decides",
        " rank:",
        "rankt:",
        "plot-twist",
        "sommerhaus der stars",
        "user mathe",
        "partner mathe",
        "user bleibt",
        "150%",
        "perfekte einheit"
    )

    fun targetPacks(packs: List<GenPack>): List<GenPack> = packs.filter { pack ->
        pack.tags.any(sectionTags::contains)
    }

    fun audit(rawPacks: List<GenPack>, runtimePacks: List<GenPack>): List<String> {
        val raw = targetPacks(rawPacks)
        val runtime = targetPacks(runtimePacks)
        val violations = mutableListOf<String>()

        if (raw.size != 108) violations += "expected 108 raw packs but found ${raw.size}"
        if (raw.map { it.id }.toSet().size != raw.size) violations += "raw Stage 05.5 ids are not unique"
        if (expectedVisibleIds.size != 53) violations += "expected 53 visible ids but ledger has ${expectedVisibleIds.size}"
        if (expectedArchivedIds.size != 55) violations += "expected 55 archived ids but ledger has ${expectedArchivedIds.size}"

        val rawIds = raw.map { it.id }.toSet()
        if (rawIds != allRawIds) violations += "raw Stage 05.5 inventory differs from canonical section inventory"

        val runtimeIds = runtime.map { it.id }.toSet()
        val missing = expectedVisibleIds - runtimeIds
        val unexpected = runtimeIds - expectedVisibleIds
        if (missing.isNotEmpty()) violations += "missing curated ids: ${missing.sorted()}"
        if (unexpected.isNotEmpty()) violations += "archived/unexpected ids visible again: ${unexpected.sorted()}"
        val resurrected = runtimeIds intersect expectedArchivedIds
        if (resurrected.isNotEmpty()) violations += "archived ids resurrected: ${resurrected.sorted()}"

        violations += Harmony360Stage055ExistingCurationAudit.audit(rawPacks, runtimePacks)
            .map { "existing-sections: $it" }

        runtime.forEach { pack ->
            if (pack.topic !in visibleTopicIds) violations += "${pack.id}: legacy topic '${pack.topic}'"
            val isHumorOrTeamwork =
                "h360_section_18_humor_lachen" in pack.tags ||
                    "h360_section_20_teamwork_challenge" in pack.tags
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            if (isHumorOrTeamwork) {
                val expectedQuestions = if (isScenario) 8 else 6
                if (pack.questions.size != expectedQuestions) {
                    violations += "${pack.id}: expected $expectedQuestions questions but found ${pack.questions.size}"
                }
            } else if (pack.questions.size < 6) {
                violations += "${pack.id}: only ${pack.questions.size} curated questions"
            }
            if (isScenario && pack.questions.size != 8) {
                violations += "${pack.id}: scenario has ${pack.questions.size} decisions instead of 8"
            }
            auditQuestions(pack, violations)
        }

        val optionUsage = mutableMapOf<List<String>, MutableSet<String>>()
        runtime.forEach { pack ->
            pack.questions.forEach questionLoop@{ question ->
                if (question.options.size != 4) return@questionLoop
                val normalized = normalizedOptions(question.options)
                if (normalized in intentionalMechanicSets || normalized in bannedQuartets) return@questionLoop
                optionUsage.getOrPut(normalized) { linkedSetOf() }.add(pack.id)
            }
        }
        optionUsage.filterValues { it.size >= 3 }.forEach { (options, packIds) ->
            violations += "reused 4-option set across ${packIds.size} packs (${packIds.joinToString()}): ${options.joinToString(" / ")}"
        }

        return violations.distinct().sorted()
    }

    private fun auditQuestions(pack: GenPack, violations: MutableList<String>) {
        pack.questions.forEachIndexed { index, question ->
            val location = "${pack.id}#${index + 1}"
            if (question.options.size == 4 && normalizedOptions(question.options) in bannedQuartets) {
                violations += "$location: generic quartet survived"
            }
            val searchable = (question.q + " " + question.options.joinToString(" ")).lowercase()
            forbiddenTextFragments.firstOrNull(searchable::contains)?.let { fragment ->
                violations += "$location: forbidden residue '$fragment' survived"
            }
        }
    }

    private fun normalizedOptions(options: List<String>): List<String> =
        options.map { it.trim().lowercase() }.sorted()
}
