package com.example.data.brain

data class AutoGenerationState(
    val enabled: Boolean = false,
    val generatedToday: Int = 0,
    val dailyLimit: Int = 10,
    val startupBatchRemaining: Int = 2,
    val isGenerating: Boolean = false,
    val lastGenerationAt: Long? = null,
    val dayKey: String = ""
) {
    val limitReached: Boolean
        get() = generatedToday >= dailyLimit
}
