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
    PERSON_ASSIGNMENT
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
        QuestionInteractionKind.STANDARD -> null
    }
}

internal fun harmonyImageChoiceRevealDelayMillis(index: Int): Long {
    val row = index / 3
    val column = index % 3
    return row * 420L + column * 110L
}
