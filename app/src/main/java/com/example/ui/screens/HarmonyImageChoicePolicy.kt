package com.example.ui.screens

import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.FullscreenGameMechanicPolicy
import com.example.data.model.HarmonyPacksData

internal enum class HarmonyImageChoiceKind {
    EGG,
    STEAK,
    TRAVEL,
    TRAUMHAUS,
    PROPOSAL_LOCATION,
    HAPPY_COUPLE,
    RANK_ORDER,
    PERSON_ASSIGNMENT,
    PARTNER_PREDICTION,
    SECRET_CHOICE,
    SCALE_MATCH,
    WHO_WOULD,
    MEMORY_MATCH,
    SCENARIO,
    PRIORITY_POKER,
    MATCH_TOURNAMENT,
    DEEP_TALK
}

internal const val HAPPY_COUPLE_REVEAL_DURATION_MILLIS = 620

internal fun happyCoupleRevealDelayMillis(index: Int): Long = index.coerceAtLeast(0) * 700L

internal fun harmonyImageChoiceKind(packId: String, questionIndex: Int): HarmonyImageChoiceKind? {
    when {
        packId == "liebegleichgewicht" && questionIndex == 0 -> return HarmonyImageChoiceKind.HAPPY_COUPLE
        packId == "essenreden" && questionIndex == 3 -> return HarmonyImageChoiceKind.EGG
        packId == "essenreden" && questionIndex == 4 -> return HarmonyImageChoiceKind.STEAK
        packId == "reisevor" && questionIndex == 4 -> return HarmonyImageChoiceKind.TRAVEL
        packId == "aussen" -> return HarmonyImageChoiceKind.TRAUMHAUS
        packId == "antrag" && questionIndex == 0 -> return HarmonyImageChoiceKind.PROPOSAL_LOCATION
    }

    val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId } ?: return null
    return when (FullscreenGameMechanicPolicy.resolve(pack, questionIndex)) {
        FullscreenGameMechanicKind.PERSON_ASSIGNMENT -> HarmonyImageChoiceKind.PERSON_ASSIGNMENT
        FullscreenGameMechanicKind.RANK_ORDER -> HarmonyImageChoiceKind.RANK_ORDER
        FullscreenGameMechanicKind.PARTNER_PREDICTION -> HarmonyImageChoiceKind.PARTNER_PREDICTION
        FullscreenGameMechanicKind.SECRET_CHOICE -> HarmonyImageChoiceKind.SECRET_CHOICE
        FullscreenGameMechanicKind.SCALE_MATCH -> HarmonyImageChoiceKind.SCALE_MATCH
        FullscreenGameMechanicKind.WHO_WOULD -> HarmonyImageChoiceKind.WHO_WOULD
        FullscreenGameMechanicKind.MEMORY_MATCH -> HarmonyImageChoiceKind.MEMORY_MATCH
        FullscreenGameMechanicKind.SCENARIO -> HarmonyImageChoiceKind.SCENARIO
        FullscreenGameMechanicKind.PRIORITY_POKER -> HarmonyImageChoiceKind.PRIORITY_POKER
        FullscreenGameMechanicKind.MATCH_TOURNAMENT -> HarmonyImageChoiceKind.MATCH_TOURNAMENT
        FullscreenGameMechanicKind.DEEP_TALK -> HarmonyImageChoiceKind.DEEP_TALK
        null -> null
    }
}

internal fun harmonyImageChoiceRevealDelayMillis(index: Int): Long {
    val row = index / 3
    val column = index % 3
    return row * 420L + column * 110L
}
