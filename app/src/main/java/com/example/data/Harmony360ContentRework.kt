package com.example.data

/**
 * Quality pass for generated Harmony 360 content.
 *
 * Older generated ranking packs reused a handful of generic answer quartets across unrelated
 * subjects (for example Sicherheit/Freiheit/Abenteuer/Komfort). Those packs remain the source of
 * truth, but obviously generic ranking filler is replaced deterministically at load time so the
 * app can ship a coherent catalogue without destructively rewriting all generated section files.
 */
object Harmony360ContentRework {

    private val overusedRankingSets = listOf(
        setOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        setOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        setOf("Karriere", "Familie", "Ausgewogen", "Sehr unabhängig"),
        setOf("Sofort ansprechen", "Erst fühlen", "Nähe suchen", "Raum geben"),
        setOf("Nähe", "Freiheit", "Humor", "Sicherheit"),
        setOf("Eine Umarmung", "Ein ehrliches Gespräch", "Gemeinsame Zeit", "Eine Überraschung"),
        setOf("Spontan", "Ritual", "Große Geste", "Kleine Geste")
    )

    fun apply(pack: GenPack): GenPack {
        if ("harmony360" !in pack.tags || pack.cat != "h360_ranking") return pack

        if (pack.id == "h500_224_arbeitsweg_ranking") {
            return pack.copy(questions = arbeitswegQuestions)
        }

        val section = sectionNumber(pack) ?: return pack
        val vocabulary = sectionVocabulary[section] ?: return pack
        val subject = pack.title.substringBefore(" – ").trim()
        val offset = positiveHash(pack.id) % vocabulary.size
        var changed = false

        val questions = pack.questions.mapIndexed { index, question ->
            if (!isOverusedRankingSet(question.options)) {
                question
            } else {
                changed = true
                GenQuestion(
                    q = rankingPrompt(subject, index),
                    options = contextualOptions(vocabulary, offset, index),
                    defaultMine = question.defaultMine
                )
            }
        }

        return if (changed) pack.copy(questions = questions) else pack
    }

    private fun isOverusedRankingSet(options: List<String>): Boolean {
        if (options.size != 4) return false
        val set = options.toSet()
        return overusedRankingSets.any { it == set }
    }

    private fun sectionNumber(pack: GenPack): Int? {
        val tag = pack.tags.firstOrNull { it.startsWith("h360_section_") } ?: return null
        return Regex("h360_section_(\\d{2})_").find(tag)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun positiveHash(value: String): Int = value.hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) }

    private fun contextualOptions(vocabulary: List<String>, offset: Int, questionIndex: Int): List<String> {
        val start = (offset + questionIndex * 3) % vocabulary.size
        return (0 until 4).map { vocabulary[(start + it) % vocabulary.size] }
    }

    private fun rankingPrompt(subject: String, index: Int): String = when (index % 8) {
        0 -> "Was zählt für dich bei „$subject“ am meisten?"
        1 -> "Was darf bei „$subject“ auf keinen Fall zu kurz kommen?"
        2 -> "Worauf würdest du bei „$subject“ zuerst achten?"
        3 -> "Was macht „$subject“ für dich wirklich gut?"
        4 -> "Was wäre bei „$subject“ dein stärkster Pluspunkt?"
        5 -> "Was sollte dein Partner über deine Prioritäten bei „$subject“ wissen?"
        6 -> "Was würdest du bei „$subject“ am wenigsten opfern wollen?"
        else -> "Was hat bei „$subject“ langfristig den höchsten Stellenwert?"
    }

    private val arbeitswegQuestions = listOf(
        GenQuestion(
            q = "Was ist dir auf dem Arbeitsweg am wichtigsten?",
            options = listOf("Kurze Fahrzeit", "Wenig Umstiege", "Geringe Kosten", "Bequemer Weg")
        ),
        GenQuestion(
            q = "Wie würdest du am liebsten zur Arbeit kommen?",
            options = listOf("Zu Fuß", "Mit dem Fahrrad", "Mit Bus & Bahn", "Mit dem Auto")
        ),
        GenQuestion(
            q = "Was nervt dich auf dem Arbeitsweg am meisten?",
            options = listOf("Stau", "Verspätungen", "Volle Verkehrsmittel", "Parkplatzsuche")
        ),
        GenQuestion(
            q = "Was macht einen guten Arbeitsweg für dich aus?",
            options = listOf("Zuverlässigkeit", "Direkte Verbindung", "Flexibilität", "Ruhe unterwegs")
        ),
        GenQuestion(
            q = "Was würdest du für einen deutlich kürzeren Arbeitsweg am ehesten ändern?",
            options = listOf("Verkehrsmittel", "Arbeitszeiten", "Wohnort", "Arbeitsplatz")
        ),
        GenQuestion(
            q = "Was ist dir morgens auf dem Weg zur Arbeit wichtiger?",
            options = listOf("Mehr Schlaf", "Kein Umsteigen", "Frische Luft", "Planbare Ankunft")
        ),
        GenQuestion(
            q = "Welcher kleine Luxus würde deinen Arbeitsweg am meisten verbessern?",
            options = listOf("Sicherer Sitzplatz", "Gute Musik oder Podcast", "Kaffee unterwegs", "Kein Zeitdruck")
        ),
        GenQuestion(
            q = "Was sollte euer Alltag trotz Arbeitsweg am meisten schützen?",
            options = listOf("Zeit zu zweit", "Energie nach Feierabend", "Pünktlichkeit", "Spontane Pläne")
        )
    )

    private val sectionVocabulary: Map<Int, List<String>> = mapOf(
        1 to listOf("Zärtlichkeit", "Vertrauen", "Gemeinsame Zeit", "Aufmerksamkeit", "Geborgenheit", "Humor", "Freiraum", "Offenheit", "Spontanität", "Rituale", "Körpernähe", "Verlässlichkeit"),
        2 to listOf("Direktheit", "Zuhören", "Geduld", "Humor", "Klare Worte", "Nachfragen", "Ruhe", "Ehrlichkeit", "Timing", "Empathie", "Lösungen", "Offenheit"),
        3 to listOf("Gemeinsame Ziele", "Wohnort", "Familie", "Karriere", "Finanzielle Sicherheit", "Freiheit", "Abenteuer", "Planbarkeit", "Flexibilität", "Eigenes Zuhause", "Zeit zu zweit", "Persönliche Entwicklung"),
        4 to listOf("Natur", "Kultur", "Gutes Essen", "Abenteuer", "Entspannung", "Komfort", "Spontanität", "Planung", "Lokale Erlebnisse", "Meer", "Berge", "Neue Städte"),
        5 to listOf("Geschmack", "Qualität", "Atmosphäre", "Preis", "Abwechslung", "Gemeinsam kochen", "Neue Küchen", "Lieblingsgerichte", "Frische Zutaten", "Dessert", "Gemütlichkeit", "Spontane Genussmomente"),
        6 to listOf("Ordnung", "Ruhe", "Gemeinsame Zeit", "Rückzug", "Aufgabenteilung", "Spontanität", "Sauberkeit", "Gemütlichkeit", "Feste Routinen", "Flexibilität", "Privatsphäre", "Kleine Rituale"),
        7 to listOf("Bewegung", "Kreativität", "Entspannung", "Neue Erlebnisse", "Freunde", "Natur", "Musik", "Gaming", "Lernen", "Sport", "Kultur", "Zeit zu zweit"),
        8 to listOf("Loyalität", "Nähe", "Klare Grenzen", "Gemeinsame Zeit", "Unterstützung", "Ehrlichkeit", "Traditionen", "Freiraum", "Verlässlichkeit", "Humor", "Respekt", "Zusammenhalt"),
        9 to listOf("Rücklagen", "Genuss", "Investieren", "Schuldenfreiheit", "Gemeinsame Ziele", "Eigenes Budget", "Sicherheit", "Spontane Ausgaben", "Transparenz", "Vorsorge", "Große Wünsche", "Unabhängigkeit"),
        10 to listOf("Gutes Gehalt", "Flexible Arbeitszeit", "Sinnvolle Aufgaben", "Entwicklung", "Stabilität", "Eigenständigkeit", "Gutes Team", "Anerkennung", "Work-Life-Balance", "Verantwortung", "Kurzer Arbeitsweg", "Planbarkeit"),
        11 to listOf("Schlaf", "Bewegung", "Ernährung", "Entspannung", "Vorsorge", "Mentale Ruhe", "Alltagsbewegung", "Erholung", "Routine", "Motivation", "Gemeinsame Aktivität", "Zeit für sich"),
        12 to listOf("Zuhören", "Respekt", "Klare Worte", "Pause machen", "Nähe danach", "Entschuldigung", "Kompromiss", "Grenzen", "Ruhe", "Verständnis", "Lösung", "Vergebung"),
        13 to listOf("Ehrlichkeit", "Loyalität", "Freiheit", "Verantwortung", "Mitgefühl", "Mut", "Verlässlichkeit", "Neugier", "Gerechtigkeit", "Respekt", "Authentizität", "Bescheidenheit"),
        14 to listOf("Starke Geschichten", "Musik", "Atmosphäre", "Humor", "Spannung", "Emotionen", "Visuelle Welt", "Originalität", "Nostalgie", "Live-Erlebnis", "Gemeinsames Entdecken", "Lieblingsfiguren"),
        15 to listOf("Persönlicher Sinn", "Gemeinschaft", "Tradition", "Rituale", "Offene Fragen", "Toleranz", "Spiritualität", "Familie", "Freiheit", "Hoffnung", "Dankbarkeit", "Werte"),
        16 to listOf("Freiheit", "Fairness", "Sicherheit", "Verantwortung", "Chancengleichheit", "Solidarität", "Privatsphäre", "Bildung", "Umweltschutz", "Mitbestimmung", "Respekt", "Zusammenhalt"),
        17 to listOf("Verstanden werden", "Nähe", "Ruhe", "Offenheit", "Bestätigung", "Freiraum", "Zuhören", "Körperkontakt", "Humor", "Geduld", "Ehrlichkeit", "Sicherheit"),
        18 to listOf("Wortwitz", "Situationskomik", "Albernheit", "Selbstironie", "Trockener Humor", "Insider", "Memes", "Schwarzer Humor", "Spontane Sprüche", "Peinliche Momente", "Running Gags", "Lachanfälle"),
        19 to listOf("Magie", "Entdeckung", "Abenteuer", "Neue Welten", "Zeitreisen", "Superkräfte", "Geheimnisse", "Weltraum", "Parallelwelten", "Erfindungen", "Unbekannte Wesen", "Grenzenlose Freiheit"),
        20 to listOf("Ideen", "Planung", "Umsetzung", "Qualitätscheck", "Tempo", "Kommunikation", "Improvisation", "Verantwortung", "Motivation", "Entscheidungen", "Problemlösung", "Zusammenhalt")
    )
}
