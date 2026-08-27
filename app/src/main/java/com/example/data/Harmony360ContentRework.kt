package com.example.data

/**
 * Quality pass for generated Harmony 360 content.
 *
 * Older generated packs reused a small number of generic answer quartets across unrelated
 * subjects. The original generated files stay untouched, while obvious filler is replaced
 * deterministically at load time with section-specific choices and shorter, coherent prompts.
 */
object Harmony360ContentRework {

    private val overusedOptionSets = listOf(
        setOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        setOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        setOf("Karriere", "Familie", "Ausgewogen", "Sehr unabhängig"),
        setOf("Sofort ansprechen", "Erst fühlen", "Nähe suchen", "Raum geben"),
        setOf("Nähe", "Freiheit", "Humor", "Sicherheit"),
        setOf("Eine Umarmung", "Ein ehrliches Gespräch", "Gemeinsame Zeit", "Eine Überraschung"),
        setOf("Spontan", "Ritual", "Große Geste", "Kleine Geste"),
        setOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
        setOf("Spontan", "Geplant", "Vertraut", "Etwas völlig Neues"),
        setOf("Mehr Mut", "Mehr Gefühl", "Mehr Humor", "Mehr Konsequenz"),
        setOf("Druck", "Desinteresse", "Unklarheit", "Zu viel Kontrolle"),
        setOf("Zeit", "Persönliche Geste", "Überraschung", "Volle Aufmerksamkeit"),
        setOf("Gelassener als gedacht", "Mutiger als gedacht", "Sensibler als gedacht", "Spontaner als gedacht"),
        setOf("Vorfreude", "Nähe", "Neugier", "Anspannung"),
        setOf("Sehr unsicher", "Eher unsicher", "Ziemlich sicher", "Fast sicher"),
        setOf("Ruhe", "Nähe", "Abenteuer", "Überraschung"),
        setOf("Die sichere Wahl", "Die mutige Wahl", "Die romantische Wahl", "Die völlig verrückte Wahl"),
        setOf("Mehr Zeit", "Mehr Aufmerksamkeit", "Mehr Komfort", "Mehr Freiheit"),
        setOf("Kaum", "Ein bisschen", "Deutlich", "Extrem"),
        setOf("Planung", "Initiative", "Entscheidung", "Überraschung"),
        setOf("Etwas Neues", "Etwas Mutigeres", "Etwas Persönlicheres", "Etwas Ungeplanteres"),
        setOf("Routine", "Perfektion", "Erwartungen anderer", "Zu viel Planung"),
        setOf("Eine kleine persönliche Geste", "Ein großer unerwarteter Plan", "Ein mutiger erster Schritt", "Etwas nur für euch zwei"),
        setOf("Mehr Zeit", "Mehr Energie", "Mehr Freiheit", "Mehr Besonderheit"),
        setOf("Wir-Gefühl", "Persönlicher Wunsch", "Leichtigkeit", "Verlässlichkeit")
    )

    fun apply(pack: GenPack): GenPack {
        if ("harmony360" !in pack.tags) return pack

        if (pack.id == "h500_224_arbeitsweg_ranking") {
            return pack.copy(questions = arbeitswegQuestions)
        }

        val section = sectionNumber(pack) ?: return pack
        val vocabulary = sectionVocabulary[section] ?: return pack
        val subject = pack.title.substringBefore(" – ").trim()
        val offset = positiveHash(pack.id) % vocabulary.size
        var changed = false

        val questions = pack.questions.mapIndexed { index, question ->
            if (!shouldContextualize(pack, question.options)) {
                question
            } else {
                changed = true
                GenQuestion(
                    q = contextualPrompt(pack.cat, subject, index),
                    options = contextualOptions(vocabulary, offset, index),
                    defaultMine = question.defaultMine
                )
            }
        }

        return if (changed) pack.copy(questions = questions) else pack
    }

    private fun shouldContextualize(pack: GenPack, options: List<String>): Boolean {
        if (options.size != 4 || overusedOptionSets.none { it == options.toSet() }) return false
        return pack.cat in setOf("h360_ranking", "h360_prognose", "h360_geheim", "h360_prioritaet", "tot")
    }

    private fun sectionNumber(pack: GenPack): Int? {
        val tag = pack.tags.firstOrNull { it.startsWith("h360_section_") } ?: return null
        return Regex("h360_section_(\\d{2})_").find(tag)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun positiveHash(value: String): Int = value.hashCode().let {
        if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it)
    }

    private fun contextualOptions(vocabulary: List<String>, offset: Int, questionIndex: Int): List<String> {
        val start = (offset + questionIndex * 3) % vocabulary.size
        return (0 until 4).map { vocabulary[(start + it) % vocabulary.size] }
    }

    private fun contextualPrompt(cat: String, subject: String, index: Int): String = when (cat) {
        "h360_prognose" -> when (index % 4) {
            0 -> "Was wäre deinem Partner bei „$subject“ vermutlich am wichtigsten?"
            1 -> "Welche Seite von „$subject“ passt am ehesten zu deinem Partner?"
            2 -> "Womit könnte dein Partner dich bei „$subject“ überraschen?"
            else -> "Was würde dein Partner bei „$subject“ wahrscheinlich zuerst wählen?"
        }
        "h360_geheim" -> when (index % 4) {
            0 -> "Was reizt dich bei „$subject“ heimlich am meisten?"
            1 -> "Was würdest du bei „$subject“ wählen, wenn niemand urteilt?"
            2 -> "Wovon hättest du bei „$subject“ insgeheim gern mehr?"
            else -> "Welche Seite von „$subject“ würdest du gern öfter ausleben?"
        }
        "h360_prioritaet" -> when (index % 4) {
            0 -> "Was hat bei „$subject“ für dich Vorrang?"
            1 -> "Was darf bei „$subject“ niemals zu kurz kommen?"
            2 -> "Was würdest du bei „$subject“ zuerst schützen?"
            else -> "Was ist bei „$subject“ für dich am wenigsten verhandelbar?"
        }
        "tot" -> when (index % 4) {
            0 -> "Was passt bei „$subject“ eher zu dir?"
            1 -> "Was würdest du bei „$subject“ spontan wählen?"
            2 -> "Welche Seite von „$subject“ spricht dich stärker an?"
            else -> "Was gewinnt bei „$subject“ aus dem Bauch heraus?"
        }
        else -> rankingPrompt(subject, index)
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
