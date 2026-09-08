package com.example.ui.screens

internal enum class SecretPlanMotionStage { LIFT, PAGE_TURN, SEAL, SETTLE, COMPLETE }

internal data class SecretPlanMotionFrame(
    val stage: SecretPlanMotionStage,
    val acceptsInput: Boolean
)

internal object SecretPlanMotionTimeline {
    const val LIFT_END_MS = 300
    const val PAGE_TURN_END_MS = 1_400
    const val SEAL_END_MS = 1_850
    const val TOTAL_DURATION_MS = 2_000

    fun frameAt(elapsedMillis: Int): SecretPlanMotionFrame {
        val elapsed = elapsedMillis.coerceAtLeast(0)
        val stage = when {
            elapsed < LIFT_END_MS -> SecretPlanMotionStage.LIFT
            elapsed < PAGE_TURN_END_MS -> SecretPlanMotionStage.PAGE_TURN
            elapsed < SEAL_END_MS -> SecretPlanMotionStage.SEAL
            elapsed < TOTAL_DURATION_MS -> SecretPlanMotionStage.SETTLE
            else -> SecretPlanMotionStage.COMPLETE
        }
        return SecretPlanMotionFrame(stage = stage, acceptsInput = stage == SecretPlanMotionStage.COMPLETE)
    }
}
