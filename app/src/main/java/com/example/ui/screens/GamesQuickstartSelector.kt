package com.example.ui.screens

import com.example.data.model.AnswerEntity
import com.example.data.model.LoveBalanceQuestionPolicy
import com.example.data.model.ProposalExperienceEntryPolicy
import com.example.data.model.QuestionPack
import kotlin.random.Random

internal data class GamesQuickstartCandidate(
    val packId: String,
    val categoryId: String,
    val unansweredIndexes: List<Int>
)

internal data class GamesQuickstartPool(
    val candidates: List<GamesQuickstartCandidate>
) {
    val openPackCount: Int
        get() = candidates.size

    val openQuestionCount: Int
        get() = candidates.sumOf { it.unansweredIndexes.size }

    fun pick(nextInt: (Int) -> Int = { bound -> Random.nextInt(bound) }): GamesQuickstartCandidate? {
        if (candidates.isEmpty()) return null

        val byCategory = candidates.groupBy { it.categoryId }
        val categories = byCategory.keys.sorted()
        val categoryIndex = nextInt(categories.size).coerceIn(0, categories.lastIndex)
        val categoryId = categories[categoryIndex]
        val categoryCandidates = byCategory.getValue(categoryId)
        val packIndex = nextInt(categoryCandidates.size).coerceIn(0, categoryCandidates.lastIndex)
        return categoryCandidates[packIndex]
    }
}

internal fun buildGamesQuickstartPool(
    packs: List<QuestionPack>,
    answers: List<AnswerEntity>
): GamesQuickstartPool {
    val answeredIndexesByPack = answers
        .groupBy { it.packId }
        .mapValues { (_, packAnswers) -> packAnswers.mapTo(hashSetOf()) { it.questionIndex } }

    val candidates = packs.mapNotNull { pack ->
        if (!isQuickstartCompatible(pack)) return@mapNotNull null

        val total = if (pack.type == "tot") pack.pairs.size else pack.questions.size
        if (total <= 0) return@mapNotNull null

        val answeredIndexes = answeredIndexesByPack[pack.id].orEmpty()
        val unansweredIndexes = (0 until total).filterNot(answeredIndexes::contains)
        if (unansweredIndexes.isEmpty()) return@mapNotNull null

        GamesQuickstartCandidate(
            packId = pack.id,
            categoryId = pack.cat,
            unansweredIndexes = unansweredIndexes
        )
    }

    return GamesQuickstartPool(candidates)
}

private fun isQuickstartCompatible(pack: QuestionPack): Boolean {
    if (pack.id == PANDA_EITHER_OR_PACK_ID) return false
    if (pack.id == LoveBalanceQuestionPolicy.PACK_ID) return false
    if (ProposalExperienceEntryPolicy.handlesPack(pack.id)) return false
    if (pack.cat == "unterbewusstsein") return false
    return true
}
