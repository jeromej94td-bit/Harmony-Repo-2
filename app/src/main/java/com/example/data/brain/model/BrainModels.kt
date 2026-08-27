package com.example.data.brain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BrainScope(val scopeKey: String) {
    PERSON_A("PERSON_A"),
    PERSON_B("PERSON_B"),
    COUPLE("COUPLE");

    companion object {
        fun fromKey(key: String): BrainScope = entries.firstOrNull { it.scopeKey.equals(key, ignoreCase = true) } ?: COUPLE
    }
}

enum class BrainInteractionAction {
    PLAYED,
    ANSWERED,
    SKIPPED,
    LIKED,
    DISLIKED,
    OPENED_CATEGORY,
    FINISHED_PACK,
    GENERATED_SHOWN,
    GENERATED_PLAYED
}

enum class BrainContentType {
    STATIC,
    GENERATED,
    QUESTION,
    CATEGORY,
    DUEL,
    RECOMMENDATION,
    PERSONAL_ANSWER
}

enum class BrainContentStatus {
    DRAFT,
    QUEUED,
    PUBLISHED,
    ARCHIVED,
    REJECTED
}

enum class BrainPendingStatus {
    WAITING,
    IN_FLIGHT,
    DONE,
    FAILED
}

@Serializable
data class BrainProfileTag(
    val tag: String,
    val score: Double,
    val confidence: Double
)

@Serializable
data class BrainLowInterestTag(
    val tag: String,
    val score: Double
)

@Serializable
data class BrainMemoryFactItem(
    val text: String,
    val scope: String = "COUPLE",
    val confidence: Double = 0.8
)

@Serializable
data class BrainRelevantAnswerItem(
    val question: String,
    @SerialName("personA")
    val answerA: String? = null,
    @SerialName("personB")
    val answerB: String? = null,
    val category: String? = null,
    val topic: String? = null
)

@Serializable
data class BrainContextProfile(
    val personA: List<BrainProfileTag> = emptyList(),
    val personB: List<BrainProfileTag> = emptyList(),
    val couple: List<BrainProfileTag> = emptyList()
)

@Serializable
data class BrainContextSummary(
    val totalAnswers: Int,
    val totalInteractions: Int
)

@Serializable
data class HarmonyBrainContext(
    val schemaVersion: Int = 1,
    val answerCount: Int = 0,
    val task: String = "questions",
    val category: String? = null,
    val profile: BrainContextProfile = BrainContextProfile(),
    val lowInterests: List<BrainLowInterestTag> = emptyList(),
    @SerialName("memoryFacts")
    val importantMemories: List<BrainMemoryFactItem> = emptyList(),
    val relevantAnswers: List<BrainRelevantAnswerItem> = emptyList(),
    val avoidTopics: List<String> = emptyList(),
    val summary: BrainContextSummary = BrainContextSummary(0, 0)
)

data class GeneratedBrainQuestion(
    val text: String,
    val category: String,
    val difficulty: String = "medium",
    val topic: String? = null
)

data class BrainResult(
    val ok: Boolean,
    val answer: String?,
    val model: String? = null,
    val latencyMs: Long? = null,
    val errorType: String? = null,
    val errorMessage: String? = null,
    val isOfflineQueued: Boolean = false
)

data class BrainQuestionResult(
    val ok: Boolean,
    val questions: List<GeneratedBrainQuestion> = emptyList(),
    val rawAnswer: String? = null,
    val model: String? = null,
    val latencyMs: Long? = null,
    val errorType: String? = null,
    val errorMessage: String? = null,
    val isOfflineQueued: Boolean = false
)

data class BrainRecommendationResult(
    val ok: Boolean,
    val recommendations: List<String> = emptyList(),
    val rawAnswer: String? = null,
    val model: String? = null,
    val latencyMs: Long? = null,
    val errorType: String? = null,
    val errorMessage: String? = null,
    val isOfflineQueued: Boolean = false
)

data class BrainSearchResult(
    val ok: Boolean,
    val grounded: Boolean = false,
    val answer: String? = null,
    val sources: List<com.example.data.HarmonyBrainSource> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val model: String? = null,
    val latencyMs: Long? = null,
    val errorType: String? = null,
    val errorMessage: String? = null,
    val isOfflineQueued: Boolean = false
)
