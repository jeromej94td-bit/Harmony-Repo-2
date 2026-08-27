package com.example.data.brain

object AutoGenerationPolicy {
    const val DAILY_LIMIT: Int = 20
    const val INTERVAL_MS: Long = 60_000L

    fun canGenerate(successfulGamesToday: Int): Boolean = successfulGamesToday < DAILY_LIMIT
}
