package com.example.data.brain.gateway

import com.example.data.brain.model.BrainQuestionResult
import com.example.data.brain.model.BrainRecommendationResult
import com.example.data.brain.model.BrainResult
import com.example.data.brain.model.BrainSearchResult
import com.example.data.brain.model.HarmonyBrainContext

/**
 * Compatibility shell kept only until all legacy callers are removed.
 * Harmony Brain is archived and no Android network request may leave this class.
 */
class SupabaseHarmonyBrainGateway : HarmonyBrainGateway {

    companion object {
        private val INSTANCE = SupabaseHarmonyBrainGateway()
        fun getInstance(): SupabaseHarmonyBrainGateway = INSTANCE
    }

    override suspend fun chat(
        query: String,
        context: HarmonyBrainContext,
        useCurrentInfo: Boolean
    ): BrainResult = BrainResult(
        ok = false,
        answer = null,
        errorType = "feature_removed",
        errorMessage = "Harmony Brain wurde aus der aktiven App entfernt."
    )

    override suspend fun generateQuestions(
        query: String,
        context: HarmonyBrainContext
    ): BrainQuestionResult = BrainQuestionResult(
        ok = false,
        errorType = "feature_removed",
        errorMessage = "Harmony Brain wurde aus der aktiven App entfernt."
    )

    override suspend fun recommendations(
        query: String,
        context: HarmonyBrainContext
    ): BrainRecommendationResult = BrainRecommendationResult(
        ok = false,
        errorType = "feature_removed",
        errorMessage = "Harmony Brain wurde aus der aktiven App entfernt."
    )

    override suspend fun search(
        query: String,
        context: HarmonyBrainContext
    ): BrainSearchResult = BrainSearchResult(
        ok = false,
        errorType = "feature_removed",
        errorMessage = "Harmony Brain wurde aus der aktiven App entfernt."
    )
}
