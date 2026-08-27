package com.example.data.brain.engine

import java.util.Locale

object HarmonyDuplicateDetector {

    private const val SIMILARITY_THRESHOLD = 0.82

    private val STOP_WORDS = setOf(
        "der", "die", "das", "und", "oder", "ein", "eine", "einer", "eines", "einem",
        "in", "im", "am", "auf", "mit", "fuer", "für", "von", "zu", "zur", "zum",
        "ist", "sind", "war", "waren", "wir", "ich", "du", "ihr", "sie", "es",
        "was", "wie", "wo", "wann", "warum", "welche", "welcher", "welches"
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

    private fun extractTokens(text: String): Set<String> {
        val normalized = normalizeText(text)
        return normalized.split(" ")
            .map { it.trim() }
            .filter { it.isNotBlank() && it !in STOP_WORDS }
            .toSet()
    }

    private fun generateTrigrams(text: String): Set<String> {
        val normalized = normalizeText(text).replace(" ", "_")
        if (normalized.length < 3) return setOf(normalized)
        val trigrams = mutableSetOf<String>()
        for (i in 0..normalized.length - 3) {
            trigrams.add(normalized.substring(i, i + 3))
        }
        return trigrams
    }

    /**
     * Computes Jaccard similarity between two token sets.
     */
    fun tokenJaccardSimilarity(text1: String, text2: String): Double {
        val tokens1 = extractTokens(text1)
        val tokens2 = extractTokens(text2)
        if (tokens1.isEmpty() && tokens2.isEmpty()) return 1.0
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0

        val intersection = tokens1 intersect tokens2
        val union = tokens1 union tokens2
        return intersection.size.toDouble() / union.size.toDouble()
    }

    /**
     * Computes Trigram similarity between two strings.
     */
    fun trigramSimilarity(text1: String, text2: String): Double {
        val tri1 = generateTrigrams(text1)
        val tri2 = generateTrigrams(text2)
        if (tri1.isEmpty() && tri2.isEmpty()) return 1.0
        if (tri1.isEmpty() || tri2.isEmpty()) return 0.0

        val intersection = tri1 intersect tri2
        val union = tri1 union tri2
        return intersection.size.toDouble() / union.size.toDouble()
    }

    /**
     * Computes Overlap coefficient (containment) between two token sets.
     */
    fun overlapCoefficient(text1: String, text2: String): Double {
        val tokens1 = extractTokens(text1)
        val tokens2 = extractTokens(text2)
        if (tokens1.isEmpty() && tokens2.isEmpty()) return 1.0
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0

        val intersection = tokens1 intersect tokens2
        return intersection.size.toDouble() / minOf(tokens1.size, tokens2.size).toDouble()
    }

    /**
     * Checks whether a new text is a duplicate of any item in an existing list of texts.
     */
    fun isDuplicate(newText: String, existingTexts: Collection<String>): Boolean {
        val normalizedNew = normalizeText(newText)
        if (normalizedNew.isBlank()) return true

        for (existing in existingTexts) {
            val normalizedExisting = normalizeText(existing)
            if (normalizedNew == normalizedExisting) {
                return true
            }

            val jaccard = tokenJaccardSimilarity(newText, existing)
            if (jaccard >= SIMILARITY_THRESHOLD) {
                return true
            }

            val trigram = trigramSimilarity(newText, existing)
            if (trigram >= SIMILARITY_THRESHOLD) {
                return true
            }

            val overlap = overlapCoefficient(newText, existing)
            val minTokens = minOf(extractTokens(newText).size, extractTokens(existing).size)
            if (overlap >= 0.90 && minTokens >= 2) {
                return true
            }
        }

        return false
    }
}
