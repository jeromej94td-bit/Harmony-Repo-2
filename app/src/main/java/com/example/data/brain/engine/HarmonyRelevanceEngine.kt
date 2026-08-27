package com.example.data.brain.engine

import com.example.data.brain.db.BrainAnswerHistoryEntity
import com.example.data.brain.db.BrainMemoryFactEntity
import com.example.data.brain.db.BrainPreferenceEntity
import kotlin.math.max
import kotlin.math.min

data class ScoredAnswer(
    val answer: BrainAnswerHistoryEntity,
    val score: Double
)

data class ScoredFact(
    val fact: BrainMemoryFactEntity,
    val score: Double
)

data class ScoredPreference(
    val preference: BrainPreferenceEntity,
    val score: Double
)

object HarmonyRelevanceEngine {

    /**
     * Ranks answers according to relevance for the given category, query, or task.
     */
    fun rankAnswers(
        answers: List<BrainAnswerHistoryEntity>,
        category: String?,
        query: String?,
        limit: Int = 16
    ): List<BrainAnswerHistoryEntity> {
        if (answers.isEmpty()) return emptyList()

        val queryTags = HarmonyLocalSignalExtractor.extractTagsFromText("${category.orEmpty()} ${query.orEmpty()}")
        val catNorm = HarmonyLocalSignalExtractor.normalizeText(category.orEmpty())
        val queryNorm = HarmonyLocalSignalExtractor.normalizeText(query.orEmpty())

        val scored = answers.map { ans ->
            var score = 0.0

            // 1. Exact Category / Topic match
            val ansCatNorm = HarmonyLocalSignalExtractor.normalizeText(ans.category)
            val ansTopicNorm = HarmonyLocalSignalExtractor.normalizeText(ans.topic.orEmpty())
            if (catNorm.isNotBlank() && (ansCatNorm.contains(catNorm) || catNorm.contains(ansCatNorm))) {
                score += 0.40
            }
            if (catNorm.isNotBlank() && ansTopicNorm.isNotBlank() && (ansTopicNorm.contains(catNorm) || catNorm.contains(ansTopicNorm))) {
                score += 0.25
            }

            // 2. Tag & Keyword Overlap
            val ansText = "${ans.questionText} ${ans.answerPersonA.orEmpty()} ${ans.answerPersonB.orEmpty()}"
            val ansTags = HarmonyLocalSignalExtractor.extractTagsFromText(ansText)
            val commonTags = queryTags intersect ansTags
            score += min(0.35, commonTags.size * 0.15)

            // 3. Direct Token intersection
            if (queryNorm.isNotBlank()) {
                val jaccard = HarmonyDuplicateDetector.tokenJaccardSimilarity(queryNorm, ansText)
                score += min(0.30, jaccard * 0.5)
            }

            // 4. Content Quality & Engagement boost
            if (ans.liked) score += 0.15
            if (ans.disliked || ans.skipped) score -= 0.20
            if (!ans.answerPersonA.isNullOrBlank() && !ans.answerPersonB.isNullOrBlank()) {
                score += 0.10 // Both answered
            }

            // 5. Mild recency tie-breaker
            val ageDays = max(0L, (System.currentTimeMillis() - ans.createdAt) / (1000 * 3600 * 24))
            val recencyBonus = (1.0 / (1.0 + (ageDays / 30.0))) * 0.05
            score += recencyBonus

            ScoredAnswer(ans, score)
        }

        return scored.sortedByDescending { it.score }
            .take(limit)
            .map { it.answer }
    }

    /**
     * Ranks memory facts based on relevance to category and query.
     */
    fun rankMemoryFacts(
        facts: List<BrainMemoryFactEntity>,
        category: String?,
        query: String?,
        limit: Int = 8
    ): List<BrainMemoryFactEntity> {
        if (facts.isEmpty()) return emptyList()

        val queryTags = HarmonyLocalSignalExtractor.extractTagsFromText("${category.orEmpty()} ${query.orEmpty()}")
        val catNorm = HarmonyLocalSignalExtractor.normalizeText(category.orEmpty())

        val scored = facts.map { fact ->
            var score = fact.importance * fact.confidence

            val factCatNorm = HarmonyLocalSignalExtractor.normalizeText(fact.category.orEmpty())
            if (catNorm.isNotBlank() && (factCatNorm.contains(catNorm) || catNorm.contains(factCatNorm))) {
                score += 0.35
            }

            val factTags = HarmonyLocalSignalExtractor.extractTagsFromText(fact.factText)
            val commonTags = queryTags intersect factTags
            score += min(0.30, commonTags.size * 0.15)

            ScoredFact(fact, score)
        }

        return scored.sortedByDescending { it.score }
            .take(limit)
            .map { it.fact }
    }

    /**
     * Ranks preferences based on relevance to category and query.
     */
    fun rankPreferences(
        preferences: List<BrainPreferenceEntity>,
        category: String?,
        query: String?
    ): List<BrainPreferenceEntity> {
        if (preferences.isEmpty()) return emptyList()

        val queryTags = HarmonyLocalSignalExtractor.extractTagsFromText("${category.orEmpty()} ${query.orEmpty()}")

        return preferences.sortedWith(
            compareByDescending<BrainPreferenceEntity> { if (it.tag in queryTags) 1 else 0 }
                .thenByDescending { it.score * it.confidence }
        )
    }
}
