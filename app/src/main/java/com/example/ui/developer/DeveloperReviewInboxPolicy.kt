package com.example.ui.developer

import com.example.data.developer.DeveloperFeedbackItem
import com.example.data.developer.DeveloperFeedbackPriority
import com.example.data.developer.DeveloperFeedbackStatus
import com.example.data.developer.DeveloperFeedbackType

data class DeveloperReviewInboxFilter(
    val statuses: Set<DeveloperFeedbackStatus> = setOf(DeveloperFeedbackStatus.NEW),
    val priorities: Set<DeveloperFeedbackPriority> = emptySet(),
    val types: Set<DeveloperFeedbackType> = emptySet(),
    val query: String = "",
)

data class DeveloperReviewInboxGroup(
    val key: String,
    val items: List<DeveloperFeedbackItem>,
)

object DeveloperReviewInboxPolicy {
    fun apply(
        items: List<DeveloperFeedbackItem>,
        filter: DeveloperReviewInboxFilter,
    ): List<DeveloperReviewInboxGroup> {
        val needle = filter.query.trim().lowercase()
        return items.asSequence()
            .filter { filter.statuses.isEmpty() || it.status in filter.statuses }
            .filter { filter.priorities.isEmpty() || it.priority in filter.priorities }
            .filter { filter.types.isEmpty() || it.type in filter.types }
            .filter { item ->
                needle.isEmpty() || listOfNotNull(
                    item.note,
                    item.transcript,
                    item.context.gameId,
                    item.context.screen,
                    item.context.questionText,
                    item.context.elementId,
                ).any { value -> value.lowercase().contains(needle) }
            }
            .groupBy { item -> item.context.gameId ?: item.context.screen ?: "Harmony" }
            .map { (key, values) ->
                DeveloperReviewInboxGroup(
                    key = key,
                    items = values.sortedByDescending { it.createdAt },
                )
            }
            .sortedByDescending { group ->
                group.items.maxOfOrNull { it.createdAt }.orEmpty()
            }
            .toList()
    }
}
