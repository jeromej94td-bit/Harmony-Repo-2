package com.example.data.brain.gateway

import com.example.data.brain.model.BrainQuestionResult
import com.example.data.brain.model.BrainRecommendationResult
import com.example.data.brain.model.BrainResult
import com.example.data.brain.model.BrainSearchResult
import com.example.data.brain.model.HarmonyBrainContext

interface HarmonyBrainGateway {
    suspend fun chat(
        query: String,
        context: HarmonyBrainContext,
        useCurrentInfo: Boolean = false
    ): BrainResult

    suspend fun generateQuestions(
        query: String,
        context: HarmonyBrainContext
    ): BrainQuestionResult

    suspend fun recommendations(
        query: String,
        context: HarmonyBrainContext
    ): BrainRecommendationResult

    suspend fun search(
        query: String,
        context: HarmonyBrainContext
    ): BrainSearchResult
}
