package com.example.data.brain.engine

import com.example.data.brain.db.BrainAnswerHistoryEntity
import com.example.data.brain.db.BrainMemoryFactEntity
import com.example.data.brain.db.BrainPreferenceEntity
import com.example.data.brain.model.BrainContextProfile
import com.example.data.brain.model.BrainContextSummary
import com.example.data.brain.model.BrainLowInterestTag
import com.example.data.brain.model.BrainMemoryFactItem
import com.example.data.brain.model.BrainProfileTag
import com.example.data.brain.model.BrainRelevantAnswerItem
import com.example.data.brain.model.BrainScope
import com.example.data.brain.model.HarmonyBrainContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object HarmonyContextBuilder {

    private const val MAX_CONTEXT_JSON_CHARS = 5500
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    /**
     * Builds a structured, privacy-filtered HarmonyBrainContext ready for transmission.
     */
    fun buildContext(
        allAnswers: List<BrainAnswerHistoryEntity>,
        allPreferences: List<BrainPreferenceEntity>,
        allMemoryFacts: List<BrainMemoryFactEntity>,
        totalInteractions: Int,
        task: String = "questions",
        category: String? = null,
        query: String? = null,
        userName: String? = null,
        partnerName: String? = null
    ): HarmonyBrainContext {
        val totalAnswerCount = allAnswers.size

        // 1. Filter and rank preferences
        val rankedPrefs = HarmonyRelevanceEngine.rankPreferences(allPreferences, category, query)
        val prefsA = rankedPrefs.filter { it.scope == BrainScope.PERSON_A.scopeKey }
        val prefsB = rankedPrefs.filter { it.scope == BrainScope.PERSON_B.scopeKey }
        val prefsCouple = rankedPrefs.filter { it.scope == BrainScope.COUPLE.scopeKey }

        val topTagsA = prefsA.filter { it.score >= 0.55 }.take(10).map {
            BrainProfileTag(it.tag, it.score, it.confidence)
        }
        val topTagsB = prefsB.filter { it.score >= 0.55 }.take(10).map {
            BrainProfileTag(it.tag, it.score, it.confidence)
        }
        val topTagsCouple = prefsCouple.filter { it.score >= 0.55 }.take(12).map {
            BrainProfileTag(it.tag, it.score, it.confidence)
        }

        // Low interests & Avoid topics
        val lowInterests = allPreferences
            .filter { it.score <= 0.30 || it.negativeSignals >= 2 }
            .distinctBy { it.tag }
            .take(8)
            .map { BrainLowInterestTag(it.tag, it.score) }

        val avoidTopics = (lowInterests.map { it.tag } +
                allPreferences.filter { it.negativeSignals >= 2 }.map { it.tag }).distinct().take(10)

        // 2. Ranked Memory Facts (Sanitized)
        val rankedFacts = HarmonyRelevanceEngine.rankMemoryFacts(allMemoryFacts, category, query, limit = 8)
        val sanitizedFacts = rankedFacts.map { fact ->
            BrainMemoryFactItem(
                text = HarmonyPrivacyFilter.sanitizeText(fact.factText, userName, partnerName),
                scope = fact.personScope,
                confidence = fact.confidence
            )
        }

        // 3. Ranked Past Relevant Answers (Sanitized)
        val rankedAnswers = HarmonyRelevanceEngine.rankAnswers(allAnswers, category, query, limit = 14)
        val sanitizedAnswers = rankedAnswers.map { ans ->
            BrainRelevantAnswerItem(
                question = HarmonyPrivacyFilter.sanitizeText(ans.questionText, userName, partnerName),
                answerA = HarmonyPrivacyFilter.sanitizeText(ans.answerPersonA, userName, partnerName).ifBlank { null },
                answerB = HarmonyPrivacyFilter.sanitizeText(ans.answerPersonB, userName, partnerName).ifBlank { null },
                category = ans.category,
                topic = ans.topic
            )
        }

        var candidateContext = HarmonyBrainContext(
            schemaVersion = 1,
            answerCount = totalAnswerCount,
            task = task,
            category = category,
            profile = BrainContextProfile(
                personA = topTagsA,
                personB = topTagsB,
                couple = topTagsCouple
            ),
            lowInterests = lowInterests,
            importantMemories = sanitizedFacts,
            relevantAnswers = sanitizedAnswers,
            avoidTopics = avoidTopics,
            summary = BrainContextSummary(
                totalAnswers = totalAnswerCount,
                totalInteractions = totalInteractions
            )
        )

        // 4. Pruning loop if candidate exceeds character budget
        var jsonStr = serializeCompact(candidateContext)
        if (jsonStr.length > MAX_CONTEXT_JSON_CHARS) {
            // Trim low interests first
            candidateContext = candidateContext.copy(lowInterests = emptyList())
            jsonStr = serializeCompact(candidateContext)
        }
        if (jsonStr.length > MAX_CONTEXT_JSON_CHARS) {
            // Trim relevant answers down
            candidateContext = candidateContext.copy(relevantAnswers = candidateContext.relevantAnswers.take(8))
            jsonStr = serializeCompact(candidateContext)
        }
        if (jsonStr.length > MAX_CONTEXT_JSON_CHARS) {
            // Trim profile tags down
            candidateContext = candidateContext.copy(
                profile = BrainContextProfile(
                    personA = candidateContext.profile.personA.take(5),
                    personB = candidateContext.profile.personB.take(5),
                    couple = candidateContext.profile.couple.take(6)
                ),
                relevantAnswers = candidateContext.relevantAnswers.take(5)
            )
        }

        return candidateContext
    }

    fun serializeCompact(context: HarmonyBrainContext): String {
        return json.encodeToString(context)
    }
}
