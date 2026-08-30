package com.example.data

/**
 * Non-mutating quality gate for the Stage 05.5 sections that were already explicitly curated
 * by the relationship/topic cleanup: personality & values, faith, society and fantasy.
 */
object Harmony360Stage055ExistingCurationAudit {
    private val sectionTags = setOf(
        "h360_section_13_persoenlichkeit_werte",
        "h360_section_15_glaube_religion",
        "h360_section_16_politik_gesellschaft",
        "h360_section_19_fantasie_was_waere_wenn"
    )

    val expectedVisibleIds: Set<String> = linkedSetOf(
        // 13 · Persönlichkeit & Werte — 8 survivors
        "h500_271_werte_im_alltag_entweder_oder",
        "h500_272_charaktereigenschaften_wer_eher",
        "h500_274_lebensmotto_ranking",
        "h500_275_staerken_und_schwaechen_prognose",
        "h500_278_praegende_momente_memory",
        "h500_284_moral_ranking",
        "h500_285_vorbilder_prognose",
        "h500_290_sinn_des_lebens_offene_runde",
        // 15 · Glaube & Religion — 4 survivors
        "h500_313_glaube_skala",
        "h500_318_religioese_erziehung_memory",
        "h500_320_tod_und_danach_offene_runde",
        "h500_330_gemeinsamer_glaube_offene_runde",
        // 16 · Politik & Gesellschaft — reduced to values conversations — 4 survivors
        "h500_333_nachhaltigkeit_skala",
        "h500_339_gesellschaftliche_werte_prioritaet",
        "h500_340_gerechtigkeit_offene_runde",
        "h500_350_gemeinsames_weltbild_offene_runde",
        // 19 · Fantasie / Was wäre wenn — 13 survivors
        "h500_391_zeitreise_entweder_oder",
        "h500_392_superkraefte_wer_eher",
        "h500_394_lottogewinn_ranking",
        "h500_395_unsichtbarkeit_prognose",
        "h500_396_einsame_insel_szenario",
        "h500_398_kindheitstraum_memory",
        "h500_399_drei_wuensche_prioritaet",
        "h500_400_ewige_jugend_offene_runde",
        "h500_402_telepathie_wer_eher",
        "h500_403_zukunftsvision_skala",
        "h500_405_koerpertausch_prognose",
        "h500_407_geheime_fantasie_geheime_wahl",
        "h500_410_unsere_traumwelt_offene_runde"
    )

    private val visibleTopicIds = setOf(
        "aufwaermen", "beziehung", "sex", "moral", "geld", "kennen",
        "reisen", "familie", "hobbys", "filme_serien", "essen"
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
        " is deinem partner",
        "plot-twist",
        "komplett anders zu erleben"
    )

    fun targetPacks(packs: List<GenPack>): List<GenPack> = packs.filter { pack ->
        pack.tags.any(sectionTags::contains)
    }

    fun audit(rawPacks: List<GenPack>, runtimePacks: List<GenPack>): List<String> {
        val raw = targetPacks(rawPacks)
        val runtime = targetPacks(runtimePacks)
        val violations = mutableListOf<String>()

        if (raw.size != 72) violations += "expected 72 raw packs but found ${raw.size}"
        if (raw.map { it.id }.toSet().size != raw.size) violations += "raw Stage 05.5 ids are not unique"

        val runtimeIds = runtime.map { it.id }.toSet()
        if (runtimeIds != expectedVisibleIds) {
            val missing = expectedVisibleIds - runtimeIds
            val unexpected = runtimeIds - expectedVisibleIds
            if (missing.isNotEmpty()) violations += "missing curated ids: ${missing.sorted()}"
            if (unexpected.isNotEmpty()) violations += "archived/unexpected ids visible again: ${unexpected.sorted()}"
        }

        runtime.forEach { pack ->
            if (pack.topic !in visibleTopicIds) {
                violations += "${pack.id}: invisible/legacy topic '${pack.topic}'"
            }
            if (pack.questions.size < 6) {
                violations += "${pack.id}: only ${pack.questions.size} curated questions"
            }
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            if (isScenario && pack.questions.size != 8) {
                violations += "${pack.id}: scenario has ${pack.questions.size} decisions instead of 8"
            }
            auditQuestions(pack, violations)
        }

        return violations.sorted()
    }

    private fun auditQuestions(pack: GenPack, violations: MutableList<String>) {
        pack.questions.forEachIndexed { index, question ->
            val location = "${pack.id}#${index + 1}"
            if (question.options.size == 4 && normalizedOptions(question.options) in bannedQuartets) {
                violations += "$location: generic quartet survived: ${question.options.joinToString(" / ")}"
            }
            val searchable = (question.q + " " + question.options.joinToString(" ")).lowercase()
            forbiddenTextFragments.firstOrNull(searchable::contains)?.let { fragment ->
                violations += "$location: generator residue '$fragment' survived"
            }
        }
    }

    private fun normalizedOptions(options: List<String>): List<String> =
        options.map { it.trim().lowercase() }.sorted()
}
