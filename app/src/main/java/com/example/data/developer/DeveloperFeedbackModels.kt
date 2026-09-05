package com.example.data.developer

import org.json.JSONObject
import java.util.UUID

enum class DeveloperFeedbackStatus {
    NEW,
    REVIEWED,
    IN_PROGRESS,
    FIXED,
    VERIFIED,
}

enum class DeveloperFeedbackType {
    BUG,
    UI,
    CHANGE,
    IDEA,
    QUESTION,
}

enum class DeveloperFeedbackPriority {
    BLOCKER,
    HIGH,
    MEDIUM,
    LOW,
}

enum class ExecutionMode {
    AUTO_SAFE,
    REVIEW_FIRST,
    IDEA_ONLY,
}

data class DeveloperReviewContext(
    val screen: String? = null,
    val route: String? = null,
    val gameId: String? = null,
    val part: Int? = null,
    val round: Int? = null,
    val questionId: String? = null,
    val questionText: String? = null,
    val elementId: String? = null,
)

data class DeveloperFeedbackDraft(
    val note: String,
    val type: DeveloperFeedbackType = DeveloperFeedbackType.CHANGE,
    val priority: DeveloperFeedbackPriority = DeveloperFeedbackPriority.MEDIUM,
    val executionMode: ExecutionMode = ExecutionMode.REVIEW_FIRST,
    val transcript: String? = null,
    val screenshotPath: String? = null,
    val audioPath: String? = null,
)

data class DeveloperFeedbackItem(
    val id: String,
    val createdAt: String,
    val status: DeveloperFeedbackStatus,
    val priority: DeveloperFeedbackPriority,
    val type: DeveloperFeedbackType,
    val executionMode: ExecutionMode,
    val repository: String,
    val context: DeveloperReviewContext,
    val note: String,
    val transcript: String? = null,
    val screenshotPath: String? = null,
    val audioPath: String? = null,
    val appVersion: String? = null,
    val buildNumber: String? = null,
    val gitCommit: String? = null,
    val githubPr: Int? = null,
    val githubBranch: String? = null,
    val fixedCommit: String? = null,
)

fun DeveloperFeedbackDraft.toRequestJson(
    clientFeedbackId: UUID,
    context: DeveloperReviewContext,
    appVersion: String,
    buildNumber: String,
    gitCommit: String,
    device: Map<String, String>,
): JSONObject = JSONObject().apply {
    put("client_feedback_id", clientFeedbackId.toString())
    put("note", note.trim())
    put("feedback_type", type.name)
    put("priority", priority.name)
    put("execution_mode", executionMode.name)
    putIfNotBlank("screen", context.screen)
    putIfNotBlank("route", context.route)
    putIfNotBlank("game_id", context.gameId)
    context.part?.let { put("part", it) }
    context.round?.let { put("round", it) }
    putIfNotBlank("question_id", context.questionId)
    putIfNotBlank("question_text", context.questionText)
    putIfNotBlank("element_id", context.elementId)
    putIfNotBlank("transcript", transcript)
    putIfNotBlank("screenshot_path", screenshotPath)
    putIfNotBlank("audio_path", audioPath)
    putIfNotBlank("app_version", appVersion)
    putIfNotBlank("build_number", buildNumber)
    putIfNotBlank("git_commit", gitCommit)
    put("device", JSONObject().apply { device.forEach { (key, value) -> put(key, value) } })
}

private fun JSONObject.putIfNotBlank(key: String, value: String?) {
    value?.trim()?.takeIf { it.isNotEmpty() }?.let { put(key, it) }
}
