package com.example.data.model

import java.util.Locale

enum class ContentRepetitionIssueKind {
    EXACT_PROMPT,
    PROMPT_TEMPLATE,
    OPTION_QUARTET
}

data class ContentRepetitionIssue(
    val kind: ContentRepetitionIssueKind,
    val signature: String,
    val packIds: Set<String>,
    val occurrences: Int
)

/**
 * Stage 06.4 non-mutating audit for high-confidence cross-pack repetition in the final runtime
 * catalogue.
 *
 * A threshold of three distinct packs keeps deliberate pairs out of the gate. Option quartets are
 * only considered when they match a curated generic Harmony relationship set; repeated domain
 * choices such as transport modes are not defects by themselves.
 */
object ContentRepetitionAudit {
    private val whitespace = Regex("\\s+")
    private val quotedSubject = Regex("„[^“]+“")

    private val intentionalOptionSets = setOf(
        normalizedOptions(listOf("{user}", "{partner}", "Beide", "Niemand"))
    )

    private val genericOptionSets: Set<List<String>> = listOf(
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
        listOf("Kopf", "Herz", "Bauch", "Erfahrung"),
        listOf("Mehr Nähe", "Mehr Freiheit", "Mehr Sicherheit", "Mehr Abenteuer")
    ).map(::normalizedOptions).toSet()

    fun audit(
        packs: List<QuestionPack>,
        minDistinctPacks: Int = 3
    ): List<ContentRepetitionIssue> {
        require(minDistinctPacks >= 2) { "minDistinctPacks must be at least 2" }

        val exactPrompts = linkedMapOf<String, MutableSet<String>>()
        val promptTemplates = linkedMapOf<String, MutableSet<String>>()
        val optionQuartets = linkedMapOf<List<String>, MutableSet<String>>()

        packs.forEach { pack ->
            pack.questions.forEach { question ->
                val exact = normalizeText(question.q)
                if (exact.isNotBlank()) {
                    exactPrompts.getOrPut(exact) { linkedSetOf() }.add(pack.id)

                    val template = normalizeTemplate(question.q)
                    if (template != exact) {
                        promptTemplates.getOrPut(template) { linkedSetOf() }.add(pack.id)
                    }
                }

                if (question.options.size == 4) {
                    val options = normalizedOptions(question.options)
                    if (options in genericOptionSets && options !in intentionalOptionSets) {
                        optionQuartets.getOrPut(options) { linkedSetOf() }.add(pack.id)
                    }
                }
            }
        }

        return buildList {
            addIssues(exactPrompts, ContentRepetitionIssueKind.EXACT_PROMPT, minDistinctPacks) {
                it
            }
            addIssues(promptTemplates, ContentRepetitionIssueKind.PROMPT_TEMPLATE, minDistinctPacks) {
                it
            }
            addIssues(optionQuartets, ContentRepetitionIssueKind.OPTION_QUARTET, minDistinctPacks) {
                it.joinToString(" | ")
            }
        }.sortedWith(compareBy({ it.kind.name }, { it.signature }))
    }

    private fun <T> MutableList<ContentRepetitionIssue>.addIssues(
        groups: Map<T, Set<String>>,
        kind: ContentRepetitionIssueKind,
        minDistinctPacks: Int,
        signature: (T) -> String
    ) {
        groups
            .filterValues { it.size >= minDistinctPacks }
            .forEach { (key, packIds) ->
                add(
                    ContentRepetitionIssue(
                        kind = kind,
                        signature = signature(key),
                        packIds = packIds.toSet(),
                        occurrences = packIds.size
                    )
                )
            }
    }

    private fun normalizeTemplate(raw: String): String =
        normalizeText(quotedSubject.replace(raw, "„<subject>“"))

    private fun normalizeText(raw: String): String =
        raw.trim().replace(whitespace, " ").lowercase(Locale.ROOT)

    private fun normalizedOptions(options: List<String>): List<String> =
        options.map(::normalizeText).sorted()
}
