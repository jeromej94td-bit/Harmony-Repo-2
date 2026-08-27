package com.example.data.brain.usecase

import com.example.data.brain.gateway.HarmonyBrainGateway
import com.example.data.brain.model.BrainQuestionResult
import com.example.data.brain.model.BrainRecommendationResult
import com.example.data.brain.model.BrainResult
import com.example.data.brain.repository.BrainRepository

class PersonalHarmonyUseCase(
    private val repository: BrainRepository,
    private val gateway: HarmonyBrainGateway
) {
    suspend operator fun invoke(
        query: String,
        category: String? = null,
        userName: String? = null,
        partnerName: String? = null
    ): BrainResult {
        val context = repository.buildBrainContext(
            task = "chat",
            category = category,
            query = query,
            userName = userName,
            partnerName = partnerName
        )

        val result = gateway.chat(query, context, useCurrentInfo = false)
        if (!result.ok && result.errorType == "network_error") {
            repository.queuePendingGeneration("chat", query, context)
            return result.copy(isOfflineQueued = true)
        }
        return result
    }
}

class GenerateQuestionsUseCase(
    private val repository: BrainRepository,
    private val gateway: HarmonyBrainGateway
) {
    suspend operator fun invoke(
        category: String? = null,
        promptQuery: String = "Erstelle 5 neue, tiefgründige Fragen, die perfekt zu unseren Interessen und unserer Beziehungsphase passen.",
        userName: String? = null,
        partnerName: String? = null
    ): BrainQuestionResult {
        val context = repository.buildBrainContext(
            task = "questions",
            category = category,
            query = promptQuery,
            userName = userName,
            partnerName = partnerName
        )

        val result = gateway.generateQuestions(promptQuery, context)
        if (result.ok && result.questions.isNotEmpty()) {
            repository.storeGeneratedQuestions(result.questions, category)
        } else if (!result.ok && result.errorType == "network_error") {
            repository.queuePendingGeneration("questions", promptQuery, context)
            return result.copy(isOfflineQueued = true)
        }
        return result
    }
}

class GenerateRecommendationsUseCase(
    private val repository: BrainRepository,
    private val gateway: HarmonyBrainGateway
) {
    suspend operator fun invoke(
        query: String,
        category: String? = null,
        userName: String? = null,
        partnerName: String? = null
    ): BrainRecommendationResult {
        val context = repository.buildBrainContext(
            task = "recommendations",
            category = category,
            query = query,
            userName = userName,
            partnerName = partnerName
        )

        val result = gateway.recommendations(query, context)
        if (!result.ok && result.errorType == "network_error") {
            repository.queuePendingGeneration("recommendations", query, context)
            return result.copy(isOfflineQueued = true)
        }
        return result
    }
}
