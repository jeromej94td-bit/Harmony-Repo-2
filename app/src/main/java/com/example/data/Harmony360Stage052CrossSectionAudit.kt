package com.example.data

/**
 * Final Stage 05.2 quality gate for the four curated Harmony-360 sections.
 *
 * This does not mutate runtime content. It gives tests and future maintenance work one stable
 * place to detect the exact generator regressions that Stage 05.2 removed: generic answer
 * quartets, English template residue, known source typos and repeated unrelated 4-option sets.
 */
object Harmony360Stage052CrossSectionAudit {
    private val sectionTags: Set<String> = setOf(
        "h360_section_04_reisen_abenteuer",
        "h360_section_05_essen_genuss",
        "h360_section_07_freizeit_hobbys",
        "h360_section_14_kultur_medien"
    )

    private val intentionalMechanicSets: Set<List<String>> = setOf(
        normalizedOptions(listOf("{user}", "{partner}", "Beide", "Niemand"))
    )

    private val bannedGenericQuartets: Set<List<String>> = listOf(
        listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        listOf("Karriere", "Familie", "Ausgewogen", "Sehr unabhängig"),
        listOf("Sofort ansprechen", "Erst fühlen", "Nähe suchen", "Raum geben"),
        listOf("Nähe", "Freiheit", "Humor", "Sicherheit"),
        listOf("Eine Umarmung", "Ein ehrliches Gespräch", "Gemeinsame Zeit", "Eine Überraschung"),
        listOf("Spontan", "Ritual", "Große Geste", "Kleine Geste"),
        listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
        listOf("Spontan", "Geplant", "Vertraut", "Etwas völlig Neues"),
        listOf("Mehr Mut", "Mehr Gefühl", "Mehr Humor", "Mehr Konsequenz"),
        listOf("Druck", "Desinteresse", "Unklarheit", "Zu viel Kontrolle"),
        listOf("Zeit", "Persönliche Geste", "Überraschung", "Volle Aufmerksamkeit"),
        listOf("Gelassener als gedacht", "Mutiger als gedacht", "Sensibler als gedacht", "Spontaner als gedacht"),
        listOf("Vorfreude", "Nähe", "Neugier", "Anspannung"),
        listOf("Sehr unsicher", "Eher unsicher", "Ziemlich sicher", "Fast sicher"),
        listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung"),
        listOf("Die sichere Wahl", "Die mutige Wahl", "Die romantische Wahl", "Die völlig verrückte Wahl"),
        listOf("Mehr Zeit", "Mehr Aufmerksamkeit", "Mehr Komfort", "Mehr Freiheit"),
        listOf("Kaum", "Ein bisschen", "Deutlich", "Extrem"),
        listOf("Planung", "Initiative", "Entscheidung", "Überraschung"),
        listOf("Etwas Neues", "Etwas Mutigeres", "Etwas Persönlicheres", "Etwas Ungeplanteres"),
        listOf("Routine", "Perfektion", "Erwartungen anderer", "Zu viel Planung"),
        listOf("Eine kleine persönliche Geste", "Ein großer unerwarteter Plan", "Ein mutiger erster Schritt", "Etwas nur für euch zwei"),
        listOf("Mehr Zeit", "Mehr Energie", "Mehr Freiheit", "Mehr Besonderheit"),
        listOf("Wir-Gefühl", "Persönlicher Wunsch", "Leichtigkeit", "Verlässlichkeit"),
        listOf("Kopf", "Herz", "Bauch", "Erfahrung")
    ).map(::normalizedOptions).toSet()

    private val englishGeneratorFragments: List<String> = listOf(
        "what decides whether",
        "what matters most",
        "what would you",
        "what would your",
        "which option would",
        "which answer would",
        "how would you",
        "how likely",
        "rank these",
        "choose the",
        "who would be more likely"
    )

    private val knownTypos: List<String> = listOf(
        "givst",
        "fnnf"
    )

    fun stagePacks(packs: List<GenPack>): List<GenPack> = packs.filter { pack ->
        pack.tags.any(sectionTags::contains)
    }

    fun audit(packs: List<GenPack>): List<String> {
        val target = stagePacks(packs)
        val violations = mutableListOf<String>()

        for (pack in target) {
            for ((index, question) in pack.questions.withIndex()) {
                val location = "${pack.id}#${index + 1}"
                val normalized = normalizedOptions(question.options)

                if (question.options.size == 4 && normalized in bannedGenericQuartets) {
                    violations += "$location: generic quartet survived: ${question.options.joinToString(" / ")}"
                }

                val lowerQuestion = question.q.lowercase()
                if (englishGeneratorFragments.any(lowerQuestion::contains)) {
                    violations += "$location: English generator residue: ${question.q}"
                }

                val searchable = buildString {
                    append(question.q)
                    question.options.forEach { option -> append(' ').append(option) }
                }.lowercase()
                knownTypos.firstOrNull(searchable::contains)?.let { typo ->
                    violations += "$location: known typo '$typo' survived"
                }
            }
        }

        val fourOptionUsage = linkedMapOf<List<String>, MutableSet<String>>()
        for (pack in target) {
            for (question in pack.questions) {
                if (question.options.size != 4) continue
                val normalized = normalizedOptions(question.options)
                if (normalized in intentionalMechanicSets || normalized in bannedGenericQuartets) continue
                fourOptionUsage.getOrPut(normalized) { linkedSetOf() }.add(pack.id)
            }
        }

        fourOptionUsage
            .filterValues { packIds -> packIds.size >= 3 }
            .forEach { (options, packIds) ->
                violations += "reused 4-option set across ${packIds.size} packs (${packIds.joinToString()}): ${options.joinToString(" / ")}"
            }

        return violations.sorted()
    }

    private fun normalizedOptions(options: List<String>): List<String> =
        options.map { option -> option.trim().lowercase() }.sorted()
}
