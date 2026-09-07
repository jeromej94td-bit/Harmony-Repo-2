package com.example.ui.screens

import com.example.data.model.WeekendEchoMotif

internal enum class WeekendEchoPhase {
    PERSON_A,
    HANDOFF,
    PERSON_B,
    READY_TO_REVEAL,
    REVEALED
}

internal data class WeekendEchoRoundState(
    val phase: WeekendEchoPhase,
    val firstMotifKey: String? = null,
    val secondMotifKey: String? = null
) {
    val exposesAnswers: Boolean
        get() = phase == WeekendEchoPhase.REVEALED

    val canContinue: Boolean
        get() = phase == WeekendEchoPhase.REVEALED

    fun pick(key: String): WeekendEchoRoundState {
        if (WeekendEchoMotif.fromKey(key) == null) return this
        return when (phase) {
            WeekendEchoPhase.PERSON_A -> copy(
                phase = WeekendEchoPhase.HANDOFF,
                firstMotifKey = key
            )

            WeekendEchoPhase.PERSON_B -> copy(
                phase = WeekendEchoPhase.READY_TO_REVEAL,
                secondMotifKey = key
            )

            else -> this
        }
    }

    fun handoffReady(): WeekendEchoRoundState =
        if (phase == WeekendEchoPhase.HANDOFF) copy(phase = WeekendEchoPhase.PERSON_B) else this

    fun reveal(): WeekendEchoRoundState =
        if (phase == WeekendEchoPhase.READY_TO_REVEAL) copy(phase = WeekendEchoPhase.REVEALED) else this

    companion object {
        fun initial(): WeekendEchoRoundState = WeekendEchoRoundState(WeekendEchoPhase.PERSON_A)
    }
}
