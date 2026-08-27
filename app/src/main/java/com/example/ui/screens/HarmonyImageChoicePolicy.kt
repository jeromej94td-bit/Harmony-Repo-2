package com.example.ui.screens

import com.example.data.model.HarmonyPacksData
import com.example.data.model.QuestionInteractionKind
import com.example.data.model.QuestionInteractionPolicy

internal enum class HarmonyImageChoiceKind {
    EGG,
    STEAK,
    TRAVEL,
    TRAUMHAUS,
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

internal fun harmonyImageChoiceKind(packId: String, questionIndex: Int): HarmonyImageChoiceKind? {
    when {
        packId == "essenreden" && questionIndex == 3 -> return HarmonyImageChoiceKind.EGG
        packId == "essenreden" && questionIndex == 4 -> return HarmonyImageChoiceKind.STEAK
        packId == "reisevor" && questionIndex == 4 -> return HarmonyImageChoiceKind.TRAVEL
        packId == "aussen" -> return HarmonyImageChoiceKind.TRAUMHAUS
    }

    val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId } ?: return null
    return when (QuestionInteractionPolicy.resolve(pack, questionIndex)) {
        QuestionInteractionKind.PERSON_ASSIGNMENT -> HarmonyImageChoiceKind.PERSON_ASSIGNMENT
        QuestionInteractionKind.RANK_ORDER -> HarmonyImageChoiceKind.RANK_ORDER
        QuestionInteractionKind.PARTNER_PREDICTION -> HarmonyImageChoiceKind.PARTNER_PREDICTION
        QuestionInteractionKind.SECRET_CHOICE -> HarmonyImageChoiceKind.SECRET_CHOICE
        QuestionInteractionKind.SCALE_MATCH -> HarmonyImageChoiceKind.SCALE_MATCH
        QuestionInteractionKind.WHO_WOULD -> HarmonyImageChoiceKind.WHO_WOULD
        QuestionInteractionKind.MEMORY_MATCH -> HarmonyImageChoiceKind.MEMORY_MATCH
        QuestionInteractionKind.SCENARIO -> HarmonyImageChoiceKind.SCENARIO
        QuestionInteractionKind.PRIORITY_POKER -> HarmonyImageChoiceKind.PRIORITY_POKER
        QuestionInteractionKind.MATCH_TOURNAMENT -> HarmonyImageChoiceKind.MATCH_TOURNAMENT
        QuestionInteractionKind.DEEP_TALK -> HarmonyImageChoiceKind.DEEP_TALK
        QuestionInteractionKind.STANDARD -> null
    }
}

internal fun harmonyImageChoiceRevealDelayMillis(index: Int): Long {
    val row = index / 3
    val column = index % 3
    return row * 420L + column * 110L
}
