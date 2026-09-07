package com.example.developer

import com.example.data.developer.DeveloperFeedbackItem
import com.example.data.developer.DeveloperFeedbackPriority
import com.example.data.developer.DeveloperFeedbackStatus
import com.example.data.developer.DeveloperFeedbackType
import com.example.data.developer.DeveloperReviewContext
import com.example.data.developer.ExecutionMode
import com.example.ui.developer.DeveloperReviewInboxFilter
import com.example.ui.developer.DeveloperReviewInboxPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class DeveloperReviewInboxPolicyTest {

    @Test
    fun `new blocker stays visible and items group by game or screen`() {
        val items = listOf(
            feedback(
                id = "1",
                gameId = "christmas",
                priority = DeveloperFeedbackPriority.BLOCKER,
                status = DeveloperFeedbackStatus.NEW,
            ),
            feedback(
                id = "2",
                screen = "GamesScreen",
                priority = DeveloperFeedbackPriority.MEDIUM,
                status = DeveloperFeedbackStatus.FIXED,
            ),
        )

        val groups = DeveloperReviewInboxPolicy.apply(
            items = items,
            filter = DeveloperReviewInboxFilter(
                statuses = setOf(DeveloperFeedbackStatus.NEW),
                query = "",
            ),
        )

        assertEquals(listOf("christmas"), groups.map { it.key })
        assertEquals("1", groups.single().items.single().id)
    }

    @Test
    fun `query matches note transcript question and element context`() {
        val item = feedback(
            id = "3",
            gameId = "christmas",
            note = "Karten höher verteilen",
            transcript = "Der untere Button überlappt",
            questionText = "Welche Farbe passt?",
            elementId = "christmas_option_2",
        )

        listOf("karten", "überlappt", "farbe", "option_2").forEach { query ->
            val groups = DeveloperReviewInboxPolicy.apply(
                items = listOf(item),
                filter = DeveloperReviewInboxFilter(
                    statuses = emptySet(),
                    query = query,
                ),
            )
            assertEquals("query=$query", listOf("3"), groups.flatMap { it.items }.map { it.id })
        }
    }

    private fun feedback(
        id: String,
        gameId: String? = null,
        screen: String? = null,
        priority: DeveloperFeedbackPriority = DeveloperFeedbackPriority.MEDIUM,
        status: DeveloperFeedbackStatus = DeveloperFeedbackStatus.NEW,
        note: String = "Testnotiz",
        transcript: String? = null,
        questionText: String? = null,
        elementId: String? = null,
    ) = DeveloperFeedbackItem(
        id = id,
        createdAt = "2026-09-06T12:00:00Z",
        status = status,
        priority = priority,
        type = DeveloperFeedbackType.UI,
        executionMode = ExecutionMode.REVIEW_FIRST,
        repository = "Harmony-Repo-2",
        context = DeveloperReviewContext(
            screen = screen,
            gameId = gameId,
            questionText = questionText,
            elementId = elementId,
        ),
        note = note,
        transcript = transcript,
    )
}
