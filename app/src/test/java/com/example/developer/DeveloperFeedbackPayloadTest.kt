package com.example.developer

import com.example.data.developer.DeveloperFeedbackDraft
import com.example.data.developer.DeveloperFeedbackPriority
import com.example.data.developer.DeveloperFeedbackType
import com.example.data.developer.DeveloperReviewContext
import com.example.data.developer.ExecutionMode
import com.example.data.developer.toRequestJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class DeveloperFeedbackPayloadTest {

    @Test
    fun `payload carries exact game and question context`() {
        val clientId = UUID.fromString("11111111-2222-3333-4444-555555555555")
        val draft = DeveloperFeedbackDraft(
            note = "Antwortkarte muss höher stehen",
            type = DeveloperFeedbackType.UI,
            priority = DeveloperFeedbackPriority.HIGH,
            executionMode = ExecutionMode.AUTO_SAFE,
        )
        val context = DeveloperReviewContext(
            screen = "QuizRunnerScreen",
            route = "games/runner",
            gameId = "weihnachten",
            round = 4,
            questionId = "weihnachten:3",
            questionText = "Welcher Look macht vor dem Öffnen Freude?",
            elementId = "christmas_option_2",
        )

        val json = draft.toRequestJson(
            clientFeedbackId = clientId,
            context = context,
            appVersion = "1.2",
            buildNumber = "3",
            gitCommit = "abc123",
            device = mapOf("model" to "SM-S926B"),
        )

        assertEquals(clientId.toString(), json.getString("client_feedback_id"))
        assertEquals("UI", json.getString("feedback_type"))
        assertEquals("HIGH", json.getString("priority"))
        assertEquals("AUTO_SAFE", json.getString("execution_mode"))
        assertEquals("weihnachten", json.getString("game_id"))
        assertEquals(4, json.getInt("round"))
        assertEquals("christmas_option_2", json.getString("element_id"))
        assertEquals("abc123", json.getString("git_commit"))
        assertEquals("SM-S926B", json.getJSONObject("device").getString("model"))
    }

    @Test
    fun `blank optional context fields are omitted`() {
        val json = DeveloperFeedbackDraft(note = "Nur eine Notiz").toRequestJson(
            clientFeedbackId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
            context = DeveloperReviewContext(screen = "GamesScreen"),
            appVersion = "1.2",
            buildNumber = "3",
            gitCommit = "local",
            device = emptyMap(),
        )

        assertTrue(json.has("screen"))
        assertFalse(json.has("game_id"))
        assertFalse(json.has("question_id"))
        assertFalse(json.has("element_id"))
    }
}
