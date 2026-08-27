package com.example.data.brain.engine

import com.example.data.brain.db.BrainPreferenceEntity
import com.example.data.brain.model.BrainScope
import kotlin.math.max
import kotlin.math.min

object HarmonyPreferenceEngine {

    const val WEIGHT_FREE_ANSWER = 0.15
    const val WEIGHT_CHOICE_DECISION = 0.10
    const val WEIGHT_LIKE = 0.18
    const val WEIGHT_CATEGORY_REPEATED = 0.05
    const val WEIGHT_SKIP = 0.08
    const val WEIGHT_DISLIKE = 0.20
    const val WEIGHT_REPEATED_SKIP = 0.05

    /**
     * Updates an existing preference entity or creates a new one with deterministic bounded maths.
     */
    fun updatePreference(
        existing: BrainPreferenceEntity?,
        scope: BrainScope,
        tag: String,
        isPositive: Boolean,
        weight: Double,
        now: Long = System.currentTimeMillis()
    ): BrainPreferenceEntity {
        val currentScore = existing?.score ?: 0.5
        val currentConfidence = existing?.confidence ?: 0.15
        val currentEngagement = existing?.engagement ?: 0.0
        val currentSaturation = existing?.saturation ?: 0.0
        val posCount = (existing?.positiveSignals ?: 0) + if (isPositive) 1 else 0
        val negCount = (existing?.negativeSignals ?: 0) + if (!isPositive) 1 else 0

        val newScore = if (isPositive) {
            min(1.0, currentScore + weight * (1.0 - currentScore))
        } else {
            val actualWeight = if (negCount > 2) weight + WEIGHT_REPEATED_SKIP else weight
            max(0.0, currentScore - actualWeight * currentScore)
        }

        // Confidence increases with more signals
        val confidenceStep = if (isPositive) 0.18 else 0.12
        val newConfidence = min(1.0, currentConfidence + confidenceStep * (1.0 - currentConfidence))

        // Engagement increases with interaction
        val newEngagement = min(1.0, currentEngagement + 0.08)

        // Saturation tracks recent content usage
        val newSaturation = min(1.0, currentSaturation + 0.05)

        return BrainPreferenceEntity(
            scope = scope.scopeKey,
            tag = tag,
            score = (newScore * 1000).toInt() / 1000.0,
            confidence = (newConfidence * 1000).toInt() / 1000.0,
            engagement = (newEngagement * 1000).toInt() / 1000.0,
            positiveSignals = posCount,
            negativeSignals = negCount,
            saturation = (newSaturation * 1000).toInt() / 1000.0,
            lastSeenAt = now,
            lastUsedForContentAt = existing?.lastUsedForContentAt,
            updatedAt = now
        )
    }

    /**
     * Applies a batch of extracted signals to a list of existing preferences.
     */
    fun applySignals(
        existingPreferences: Map<Pair<String, String>, BrainPreferenceEntity>,
        signals: List<ExtractedSignal>,
        now: Long = System.currentTimeMillis()
    ): List<BrainPreferenceEntity> {
        val result = existingPreferences.toMutableMap()

        for (signal in signals) {
            val key = Pair(signal.scope.scopeKey, signal.tag)
            val current = result[key]
            val updated = updatePreference(
                existing = current,
                scope = signal.scope,
                tag = signal.tag,
                isPositive = signal.isPositive,
                weight = signal.weight,
                now = now
            )
            result[key] = updated
        }

        return result.values.toList()
    }
}
