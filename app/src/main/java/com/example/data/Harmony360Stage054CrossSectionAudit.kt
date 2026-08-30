package com.example.data

/** Final non-mutating quality gate for Stage 05.4 health, psychology/feelings and intimacy. */
object Harmony360Stage054CrossSectionAudit {
    private val sectionTags = setOf(
        "h360_section_11_gesundheit_fitness",
        "h360_section_17_psychologie_gefuehle"
    )

    private val intimacyIds = listOf("naehe", "intimleben")

    val archivedIds: Set<String> = setOf(
        // Health & fitness
        "h500_232_schlafgewohnheiten_wer_eher",
        "h500_233_mental_health_skala",
        "h500_234_arztbesuche_ranking",
        "h500_235_stressbewaeltigung_prognose",
        "h500_237_wellness_und_spa_geheime_wahl",
        "h500_240_koerpergefuehl_offene_runde",
        "h500_243_routinen_skala",
        "h500_244_vorsorge_ranking",
        "h500_245_suchtmittel_prognose",
        "h500_246_regeneration_szenario",
        // Psychology & feelings
        "h500_351_selbstreflexion_entweder_oder",
        "h500_354_bindungsmuster_ranking",
        "h500_356_aengste_szenario",
        "h500_359_emotionale_sicherheit_prioritaet",
        "h500_360_liebeserklaerung_offene_runde",
        "h500_363_stimmungsschwankungen_skala",
        "h500_364_vertrauen_ranking",
        "h500_365_vergebung_prognose",
        "h500_370_gemeinsame_psychohygiene_offene_runde"
    )

    private val bannedQuartets = listOf(
        listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        listOf("Aktiv", "Passiv", "Etwas lernen", "Einfach abschalten"),
        listOf("Kopf", "Herz", "Bauch", "Erfahrung"),
        listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
        listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
        listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung")
    ).map(::normalizedOptions).toSet()

    private val forbiddenTextFragments = listOf(
        "what decides",
        "rank:",
        "rankt:",
        "diagnose stellen",
        "medikament absetzen",
        "behandlung ersetzen",
        "du hast eine störung",
        "dein partner ist toxisch"
    )

    fun harmony360Packs(packs: List<GenPack>): List<GenPack> = packs.filter { pack ->
        pack.tags.any(sectionTags::contains)
    }

    fun intimacyPacks(packs: List<GenPack>): List<GenPack> = intimacyIds.mapNotNull { id ->
        packs.singleOrNull { it.id == id }
    }

    fun audit(harmony360: List<GenPack>, runtimeRegistry: List<GenPack>): List<String> {
        val target360 = harmony360Packs(harmony360)
        val intimacy = intimacyPacks(runtimeRegistry)
        val violations = mutableListOf<String>()

        val visibleIds = target360.map { it.id }.toSet()
        archivedIds.filter(visibleIds::contains).forEach { id ->
            violations += "$id: archived pack is visible again"
        }

        target360.forEach { pack ->
            val isScenario = pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
            if (isScenario && pack.questions.size != 8) {
                violations += "${pack.id}: scenario has ${pack.questions.size} decisions instead of 8"
            }
            auditQuestions(pack, violations)
        }

        intimacy.forEach { pack -> auditQuestions(pack, violations) }

        if (intimacy.map { it.id } != intimacyIds) {
            violations += "intimacy runtime ids are incomplete or out of order: ${intimacy.map { it.id }}"
        }
        val expectedCounts = mapOf("naehe" to 12, "intimleben" to 18)
        intimacy.forEach { pack ->
            val expected = expectedCounts[pack.id]
            if (pack.topic != "sex") violations += "${pack.id}: intimacy topic changed to ${pack.topic}"
            if (expected != null && pack.questions.size != expected) {
                violations += "${pack.id}: expected $expected curated questions but found ${pack.questions.size}"
            }
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
                violations += "$location: forbidden wording '$fragment' survived"
            }
        }
    }

    private fun normalizedOptions(options: List<String>): List<String> =
        options.map { it.trim().lowercase() }.sorted()
}
