package com.example.data.model

/** One stable item inside a reusable Harmony ranking round. */
data class ExperienceRankingItem(
    val id: String,
    val label: String
) {
    init {
        require(id.isNotBlank()) { "Ranking items need a stable id." }
        require(label.isNotBlank()) { "Ranking items need a label." }
    }
}

/** UI-independent ranking content reusable by any Harmony experience. */
class ExperienceRankingRound(
    val id: String,
    val prompt: String,
    items: List<ExperienceRankingItem>
) {
    val items: List<ExperienceRankingItem> = items.toList()
    val itemCount: Int get() = items.size

    init {
        require(id.isNotBlank()) { "Ranking rounds need a stable id." }
        require(prompt.isNotBlank()) { "Ranking rounds need a prompt." }
        require(this.items.size >= 2) { "Ranking rounds need at least two items." }
        require(this.items.map(ExperienceRankingItem::id).distinct().size == this.items.size) {
            "Ranking item ids must be unique."
        }
        require(this.items.map(ExperienceRankingItem::label).distinct().size == this.items.size) {
            "Ranking item labels must be unique for the existing ranking board."
        }
    }
}

/**
 * Bridges stable experience item IDs to the already shipped ranking answer format, whose payload
 * contains the visible labels. This keeps existing ranking persistence/restore semantics intact.
 */
object ExperienceRankingSelectionCodec {
    fun encodeOrNull(round: ExperienceRankingRound, orderedItemIds: List<String>): String? {
        if (orderedItemIds.size != round.items.size || orderedItemIds.distinct().size != orderedItemIds.size) {
            return null
        }
        val labelById = round.items.associate { it.id to it.label }
        val labels = orderedItemIds.map { id -> labelById[id] ?: return null }
        return RankingAnswerCodec.encode(labels)
    }

    fun encode(round: ExperienceRankingRound, orderedItemIds: List<String>): String =
        requireNotNull(encodeOrNull(round, orderedItemIds)) {
            "Ranking selection must contain every stable item id exactly once."
        }

    fun decode(round: ExperienceRankingRound, encoded: String): List<String>? {
        val labels = round.items.map(ExperienceRankingItem::label)
        val orderedLabels = RankingAnswerCodec.decode(encoded, labels) ?: return null
        val idByLabel = round.items.associate { it.label to it.id }
        return orderedLabels.map { label -> idByLabel[label] ?: return null }
    }
}

fun ProposalPriorityRanking.toExperienceRankingRound(): ExperienceRankingRound =
    ExperienceRankingRound(
        id = STEP_ID,
        prompt = "Was muss für euren Antrag am meisten stimmen?",
        items = priorities.map { priority ->
            ExperienceRankingItem(priority.id, priority.label)
        }
    )
