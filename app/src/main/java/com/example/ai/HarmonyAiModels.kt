package com.example.ai

enum class HarmonyAiIntent {
    GENERAL,
    RELATIONSHIP,
    FOOD,
    LOCAL_DISCOVERY,
    ENTERTAINMENT,
    TRAVEL,
    CURRENT_INFO,
    EVENT_SEARCH
}

enum class HarmonyGroundingMode {
    NONE,
    GOOGLE_SEARCH,
    GOOGLE_MAPS,
    SEARCH_AND_MAPS
}

data class HarmonyAiRoute(
    val intent: HarmonyAiIntent,
    val grounding: HarmonyGroundingMode,
    val needsUserLocation: Boolean = false
)

data class CoachLocation(
    val latitude: Double,
    val longitude: Double
)

data class CoachSource(
    val title: String,
    val url: String,
    val type: String
)

enum class CoachRole {
    USER,
    ASSISTANT
}

data class CoachMessage(
    val id: Long,
    val role: CoachRole,
    val text: String,
    val sources: List<CoachSource> = emptyList(),
    val groundedBySearch: Boolean = false,
    val groundedByMaps: Boolean = false
)

data class HarmonyCoachResponse(
    val text: String,
    val sources: List<CoachSource> = emptyList(),
    val groundedBySearch: Boolean = false,
    val groundedByMaps: Boolean = false
)

data class HarmonyCoachUiState(
    val messages: List<CoachMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
