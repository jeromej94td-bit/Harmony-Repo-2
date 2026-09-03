package com.example.ui.screens

import com.example.data.GeneratedContentRegistry
import com.example.data.HarmonyContentRepository
import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.FullscreenGameMechanicPolicy
import com.example.data.model.HarmonyPacksData
import com.example.data.model.LoveBalanceQuestionPolicy
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import com.example.data.model.SexIntimacyRevealPolicy

internal enum class HarmonyImageChoiceKind {
    EGG,
    STEAK,
    TRAVEL,
    TRAUMHAUS,
    AUTUMN_STORY,
    AUTUMN_DRINK,
    AUTUMN_SNACK,
    AUTUMN_NOOK,
    AUTUMN_SOUND,
    AUTUMN_SCENT,
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
    DEEP_TALK,
    INTIMACY_COMPACT,
    INTIMACY_PRIVATE_REVEAL
}

internal const val HAPPY_COUPLE_REVEAL_DURATION_MILLIS = 620

private const val EGG_PROMPT = "Wie möchtest du dein Ei am liebsten?"
private const val STEAK_PROMPT = "Wie willst du dein Steak?"
private const val TRAVEL_PROMPT = "Wie sieht deine Traumreise aus?"
private const val PROPOSAL_LOCATION_PROMPT = "Welche Umgebung würdest du dir für einen Antrag wünschen?"

private val autumnEveningKinds = listOf(
    HarmonyImageChoiceKind.AUTUMN_STORY,
    HarmonyImageChoiceKind.AUTUMN_DRINK,
    HarmonyImageChoiceKind.AUTUMN_SNACK,
    HarmonyImageChoiceKind.AUTUMN_NOOK,
    HarmonyImageChoiceKind.AUTUMN_SOUND,
    HarmonyImageChoiceKind.AUTUMN_SCENT
)

internal fun happyCoupleRevealDelayMillis(index: Int): Long = index.coerceAtLeast(0) * 700L

internal fun harmonyImageChoiceKind(packId: String, questionIndex: Int): HarmonyImageChoiceKind? {
    val qPack = HarmonyContentRepository.getPacks().firstOrNull { it.id == packId }
        ?: HarmonyPacksData.PACKS.firstOrNull { it.id == packId }
    if (qPack != null) {
        return harmonyImageChoiceKind(qPack, questionIndex)
    }

    val genPack = GeneratedContentRegistry.PACKS.firstOrNull { it.id == packId }
    if (genPack != null) {
        val mappedPack = QuestionPack(
            id = genPack.id,
            title = genPack.title,
            tags = genPack.tags,
            cat = genPack.cat,
            topic = genPack.topic,
            type = genPack.type,
            questions = genPack.questions.map { Question(q = it.q, options = it.options) },
            pairs = genPack.pairs,
            emoji = genPack.emoji
        )
        return harmonyImageChoiceKind(mappedPack, questionIndex)
    }

    // Compatibility fallback for callers that only know an id/index for a pack not loaded yet.
    if (PhotoQuestionPolicy.modeFor(packId, questionIndex) != null) {
        return HarmonyImageChoiceKind.MEMORY_MATCH
    }
    return when {
        packId == "aussen" -> HarmonyImageChoiceKind.TRAUMHAUS
        else -> null
    }
}

internal fun harmonyImageChoiceKind(pack: QuestionPack, questionIndex: Int): HarmonyImageChoiceKind? {
    if (pack.id == "herbstabend") {
        return autumnEveningKinds.getOrNull(questionIndex)
    }

    val q = pack.questions.getOrNull(questionIndex)
    if (pack.id == LoveBalanceQuestionPolicy.PACK_ID && questionIndex == 0) {
        return HarmonyImageChoiceKind.HAPPY_COUPLE
    }

    val rawQuestion = q?.q

    if (PhotoQuestionPolicy.modeFor(pack.id, questionIndex, rawQuestion) != null) {
        return HarmonyImageChoiceKind.MEMORY_MATCH
    }

    stableVisualQuestionKind(pack.id, rawQuestion)?.let { return it }

    if (pack.id == "aussen") {
        return HarmonyImageChoiceKind.TRAUMHAUS
    }

    if (SexIntimacyRevealPolicy.isSexIntimacyPack(pack.id, pack.topic)) {
        val questionText = rawQuestion ?: return HarmonyImageChoiceKind.INTIMACY_COMPACT
        return if (
            SexIntimacyRevealPolicy.usesPrivateCoupleReveal(
                pack.id,
                pack.topic,
                questionText
            )
        ) {
            HarmonyImageChoiceKind.INTIMACY_PRIVATE_REVEAL
        } else {
            HarmonyImageChoiceKind.INTIMACY_COMPACT
        }
    }

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

private fun stableVisualQuestionKind(packId: String, rawQuestion: String?): HarmonyImageChoiceKind? {
    val prompt = rawQuestion?.trim() ?: return null
    return when {
        packId == "essenreden" && prompt == EGG_PROMPT -> HarmonyImageChoiceKind.EGG
        packId == "essenreden" && prompt == STEAK_PROMPT -> HarmonyImageChoiceKind.STEAK
        packId == "reisevor" && prompt == TRAVEL_PROMPT -> HarmonyImageChoiceKind.TRAVEL
        packId == "antrag" && prompt == PROPOSAL_LOCATION_PROMPT -> HarmonyImageChoiceKind.PROPOSAL_LOCATION
        else -> null
    }
}

internal fun harmonyImageChoiceRevealDelayMillis(index: Int): Long {
    val row = index / 3
    val column = index % 3
    return row * 420L + column * 110L
}

internal fun autumnEveningRevealDelayMillis(index: Int): Long {
    val safeIndex = index.coerceAtLeast(0)
    val row = safeIndex / 2
    val column = safeIndex % 2
    return row * 420L + column * 110L
}
