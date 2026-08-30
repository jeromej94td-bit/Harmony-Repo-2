package com.example.data

/** Non-mutating final quality gate for Stage 05.3: future, friends/family, money and work. */
object Harmony360Stage053CrossSectionAudit {
    private val sectionTags = setOf(
        "h360_section_03_zukunft_lebensplanung",
        "h360_section_08_freunde_familie",
        "h360_section_09_geld_finanzen",
        "h360_section_10_arbeit_karriere"
    )

    val archivedIds: Set<String> = setOf(
        "h500_052_in_fuenf_jahren_wer_eher",
        "h500_053_traumwohnung_skala",
        "h500_055_stadt_oder_land_prognose",
        "h500_057_karriereplaene_geheime_wahl",
        "h500_062_lebensstil_wer_eher",
        "h500_065_bucket_list_prognose",
        "h500_067_prioritaeten_geheime_wahl",
        "h500_069_selbststaendigkeit_prioritaet",
        "h500_212_ueberstunden_wer_eher",
        "h500_213_karriere_skala",
        "h500_218_erster_job_memory",
        "h500_222_chef_sein_wer_eher",
        "h500_223_weiterbildung_skala"
    )

    private val intentionalMechanicSets = setOf(
        normalizedOptions(listOf("{user}", "{partner}", "Beide", "Niemand"))
    )

    private val bannedQuartets = listOf(
        listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        listOf("Karriere", "Familie", "Ausgewogen", "Sehr unabhängig"),
        listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
        listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht"),
        listOf("Große Runde", "Kleine Runde", "Nur wir zwei", "Eher zurückhaltend"),
        listOf("Feste Traditionen", "Neue Wege", "Spontane Aktionen", "Genaue Planung"),
        listOf("Mehr Familie", "Mehr Freunde", "Ausgewogen", "Sehr unabhängig"),
        listOf("Integrieren", "Abgrenzen", "Kompromisse suchen", "Eigene Akzente setzen"),
        listOf("Alles teilen", "Getrennt halten", "Mischmodell", "Sehr unabhängig"),
        listOf("Ruhe", "Nähe", "Abenteuer", "Überraschung"),
        listOf("Mehr Zeit", "Mehr Aufmerksamkeit", "Mehr Komfort", "Mehr Freiheit")
    ).map(::normalizedOptions).toSet()

    private val englishFragments = listOf(
        "what decides whether",
        "what matters most",
        "what would you",
        "what would your",
        "which option would",
        "which answer would",
        "rank these",
        "who would be more likely"
    )

    fun stagePacks(packs: List<GenPack>): List<GenPack> = packs.filter { pack ->
        pack.tags.any(sectionTags::contains)
    }

    fun audit(packs: List<GenPack>): List<String> {
        val target = stagePacks(packs)
        val violations = mutableListOf<String>()

        val visibleIds = target.map { it.id }.toSet()
        archivedIds.filter(visibleIds::contains).forEach { id ->
            violations += "$id: archived pack is visible again"
        }

        target.forEach { pack ->
            pack.questions.forEachIndexed { index, question ->
                val location = "${pack.id}#${index + 1}"
                val normalized = normalizedOptions(question.options)

                if (question.options.size == 4 && normalized in bannedQuartets) {
                    violations += "$location: generic quartet survived: ${question.options.joinToString(" / ")}"
                }

                val lower = question.q.lowercase()
                if (englishFragments.any(lower::contains) || lower.contains("rank:") || lower.contains("rankt:")) {
                    violations += "$location: generator wording survived: ${question.q}"
                }
            }
        }

        val usage = mutableMapOf<List<String>, MutableSet<String>>()
        target.forEach { pack ->
            pack.questions.forEach questionLoop@{ question ->
                if (question.options.size != 4) return@questionLoop
                val normalized = normalizedOptions(question.options)
                if (normalized in intentionalMechanicSets || normalized in bannedQuartets) return@questionLoop
                usage.getOrPut(normalized) { linkedSetOf() }.add(pack.id)
            }
        }

        usage.filterValues { it.size >= 3 }.forEach { (options, packIds) ->
            violations += "reused 4-option set across ${packIds.size} packs (${packIds.joinToString()}): ${options.joinToString(" / ")}"
        }

        return violations.sorted()
    }

    private fun normalizedOptions(options: List<String>): List<String> =
        options.map { it.trim().lowercase() }.sorted()
}
