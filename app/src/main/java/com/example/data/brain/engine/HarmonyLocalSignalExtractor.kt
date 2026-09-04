package com.example.data.brain.engine

import com.example.data.brain.model.BrainScope
import java.util.Locale

data class ExtractedSignal(
    val tag: String,
    val scope: BrainScope,
    val category: String,
    val isPositive: Boolean = true,
    val weight: Double = 0.10
)

object HarmonyLocalSignalExtractor {

    // Central Topic to Keywords/Synonyms mapping
    private val TAG_DICTIONARY = mapOf(
        // REISEN
        "strand" to setOf("strand", "beach", "meer", "ozean", "küste", "bucht", "sonne", "sand"),
        "berge" to setOf("berge", "berg", "alpen", "gipfel", "wandern", "klettern", "wanderurlaub"),
        "italien" to setOf("italien", "rom", "toskana", "venedig", "amalfi", "dolomiten", "florenz"),
        "japan" to setOf("japan", "tokio", "kyoto", "osaka", "hokkaido"),
        "camping" to setOf("camping", "zelt", "zelten", "wohnmobil", "outdoor", "lagerfeuer"),
        "hotel_luxus" to setOf("hotel", "5-sterne", "luxushotel", "resort", "all-inclusive"),
        "roadtrip" to setOf("roadtrip", "rundreise", "abenteuer", "auto", "entdecken"),
        "wellness" to setOf("wellness", "spa", "sauna", "massage", "therme", "entspannung"),
        "staedtetrip" to setOf("stadt", "städte", "staedtetrip", "metropole", "sightseeing"),
        "natur" to setOf("natur", "wald", "see", "landschaft", "ruhe"),
        "auswandern" to setOf("auswandern", "ausland", "neuanfang", "insel"),

        // WOHNEN & ZUKUNFT
        "haus" to setOf("haus", "eigenheim", "einfamilienhaus", "villa"),
        "wohnung" to setOf("wohnung", "apartment", "loft", "altbau"),
        "garten" to setOf("garten", "rasen", "terrasse", "balkon", "pflanzen"),
        "landleben" to setOf("land", "dorf", "ruhe", "bauernhof", "natur"),
        "stadtleben" to setOf("stadt", "zentral", "city", "trubel"),
        "familie_kinder" to setOf("kinder", "familie", "nachwuchs", "baby"),
        "haustiere" to setOf("haustier", "hund", "katze", "tiere"),

        // ESSEN & TRINKEN
        "pizza" to setOf("pizza", "pizzeria", "italienisch"),
        "pasta" to setOf("pasta", "nudeln", "spaghetti", "lasagne"),
        "sushi" to setOf("sushi", "ramen", "asiatisch", "japanisch", "maki", "nigiri"),
        "kochen" to setOf("kochen", "selberkochen", "kueche", "rezept", "herd"),
        "bestellen" to setOf("bestellen", "lieferdienst", "takeaway", "lieferservice"),
        "restaurant" to setOf("restaurant", "essen_gehen", "feinschmecker", "dinner"),
        "kaffee" to setOf("kaffee", "cappuccino", "espresso", "latte", "cafe"),
        "tee" to setOf("tee", "matcha", "kraeutertee"),
        "dessert" to setOf("dessert", "nachtisch", "eis", "schokolade", "kuchen", "suesses"),
        "grillen" to setOf("grillen", "bbq", "barbecue"),

        // ROMANTIK & BEZIEHUNG
        "romantik" to setOf("romantik", "romantisch", "liebe", "gefuehle", "herz"),
        "kuscheln" to setOf("kuscheln", "umarmung", "zweisamkeit", "naehe"),
        "date_night" to setOf("date", "datenight", "ausgehen", "zeit_zu_zweit"),
        "hochzeit" to setOf("hochzeit", "heiraten", "antrag", "verlobung", "ehe"),
        "geschenke" to setOf("geschenk", "ueberraschung", "aufmerksamkeit"),

        // FREIZEIT & ENTERTAINMENT
        "couch_abend" to setOf("couch", "sofa", "zuhause", "gemuetlich", "entspannt"),
        "kino_filme" to setOf("kino", "film", "filme", "movie", "blockbuster"),
        "serien" to setOf("serie", "serien", "netflix", "bingewatching"),
        "gaming" to setOf("gaming", "videospiele", "zocken", "playstation", "xbox", "pc", "switch"),
        "brettspiele" to setOf("brettspiel", "gesellschaftsspiel", "karten", "spielabend"),
        "musik_konzerte" to setOf("musik", "konzert", "festival", "live"),
        "sport_fitness" to setOf("sport", "fitness", "gym", "laufen", "joggen", "training"),
        "lesen" to setOf("lesen", "buch", "buecher", "roman"),
        "party_ausgehen" to setOf("party", "club", "feiern", "tanzen", "bar", "drinks")
    )

    private val STOP_WORDS = setOf(
        "der", "die", "das", "und", "oder", "ein", "eine", "einer", "eines", "einem",
        "in", "im", "am", "auf", "mit", "fuer", "für", "von", "zu", "zur", "zum",
        "ist", "sind", "war", "waren", "wir", "ich", "du", "ihr", "sie", "es",
        "nicht", "sehr", "mehr", "immer", "gerne", "lieber", "beide", "uns", "unsere"
    )

    fun normalizeText(text: String): String {
        return text.lowercase(Locale.GERMAN)
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Extracts recognized interest tags from text.
     */
    fun extractTagsFromText(text: String): Set<String> {
        val normalized = normalizeText(text)
        val words = normalized.split(" ").filter { it.isNotBlank() && it !in STOP_WORDS }.toSet()
        val foundTags = mutableSetOf<String>()

        for ((tag, synonyms) in TAG_DICTIONARY) {
            for (syn in synonyms) {
                val synNorm = normalizeText(syn)
                if (words.contains(synNorm) || normalized.contains(" $synNorm ") || normalized.startsWith("$synNorm ") || normalized.endsWith(" $synNorm") || normalized == synNorm) {
                    foundTags.add(tag)
                    break
                }
            }
        }

        return foundTags
    }

    /**
     * Extracts signals for a single answer record.
     */
    fun extractSignals(
        category: String,
        topic: String?,
        questionText: String,
        answerPersonA: String?,
        answerPersonB: String?,
        isSkipped: Boolean = false,
        isDisliked: Boolean = false,
        isLiked: Boolean = false
    ): List<ExtractedSignal> {
        val signals = mutableListOf<ExtractedSignal>()
        val catNorm = normalizeText(category)

        // General Category Signal
        val defaultCategoryTag = when {
            catNorm.contains("reis") || catNorm.contains("urlaub") -> "reisen"
            catNorm.contains("ess") || catNorm.contains("kuech") -> "essen"
            catNorm.contains("wohn") || catNorm.contains("zukunft") -> "wohnen_zukunft"
            catNorm.contains("romant") || catNorm.contains("liebe") -> "romantik"
            catNorm.contains("freizeit") || catNorm.contains("spiel") -> "freizeit"
            else -> category.lowercase(Locale.GERMAN).replace(" ", "_")
        }

        if (isDisliked) {
            signals.add(ExtractedSignal(defaultCategoryTag, BrainScope.COUPLE, category, isPositive = false, weight = 0.20))
            return signals
        }

        if (isSkipped) {
            signals.add(ExtractedSignal(defaultCategoryTag, BrainScope.COUPLE, category, isPositive = false, weight = 0.08))
            return signals
        }

        if (isLiked) {
            signals.add(ExtractedSignal(defaultCategoryTag, BrainScope.COUPLE, category, isPositive = true, weight = 0.18))
        }

        // Tags from Question
        val questionTags = extractTagsFromText(questionText)

        // Person A Signals
        if (!answerPersonA.isNullOrBlank()) {
            val tagsA = extractTagsFromText(answerPersonA) + questionTags
            for (tag in tagsA) {
                signals.add(ExtractedSignal(tag, BrainScope.PERSON_A, category, isPositive = true, weight = 0.12))
            }
        }

        // Person B Signals
        if (!answerPersonB.isNullOrBlank()) {
            val tagsB = extractTagsFromText(answerPersonB) + questionTags
            for (tag in tagsB) {
                signals.add(ExtractedSignal(tag, BrainScope.PERSON_B, category, isPositive = true, weight = 0.12))
            }
        }

        // Couple Signals when both answers are known
        if (!answerPersonA.isNullOrBlank() && !answerPersonB.isNullOrBlank()) {
            val tagsA = extractTagsFromText(answerPersonA)
            val tagsB = extractTagsFromText(answerPersonB)
            val commonTags = (tagsA intersect tagsB) + questionTags

            for (tag in commonTags) {
                signals.add(ExtractedSignal(tag, BrainScope.COUPLE, category, isPositive = true, weight = 0.15))
            }

            // Differences: A has tag, B doesn't
            val diffA = tagsA - tagsB
            for (tag in diffA) {
                signals.add(ExtractedSignal(tag, BrainScope.PERSON_A, category, isPositive = true, weight = 0.10))
            }
            val diffB = tagsB - tagsA
            for (tag in diffB) {
                signals.add(ExtractedSignal(tag, BrainScope.PERSON_B, category, isPositive = true, weight = 0.10))
            }
        } else if (!answerPersonA.isNullOrBlank()) {
            // Single answer mode (free text or quiz)
            val tags = extractTagsFromText(answerPersonA) + questionTags
            for (tag in tags) {
                signals.add(ExtractedSignal(tag, BrainScope.COUPLE, category, isPositive = true, weight = 0.10))
            }
        }

        return signals
    }
}
