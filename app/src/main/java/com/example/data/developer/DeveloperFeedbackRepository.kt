package com.example.data.developer

import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

class DeveloperFeedbackRepository(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build(),
    private val accessTokenProvider: suspend () -> String = {
        SupabaseConfig.client.auth.currentSessionOrNull()?.accessToken
            ?: throw DeveloperFeedbackException("not_authenticated")
    },
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun isCurrentUserAdmin(): Boolean = withContext(Dispatchers.IO) {
        val body = request(
            method = "POST",
            url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/rpc/is_ai_admin",
            body = JSONObject(),
        )
        body.trim().removeSurrounding("\"").toBooleanStrictOrNull() ?: false
    }

    suspend fun enrollAdmin(code: String): Boolean {
        val normalized = code.trim()
        if (normalized.length < 8) throw DeveloperFeedbackException("invalid_enrollment_code")
        request(
            method = "POST",
            url = "${SupabaseConfig.SUPABASE_URL}/functions/v1/harmony-ai-control-enroll",
            body = JSONObject().put("code", normalized),
        )
        return isCurrentUserAdmin()
    }

    suspend fun submitFeedback(
        draft: DeveloperFeedbackDraft,
        context: DeveloperReviewContext,
        appVersion: String,
        buildNumber: String,
        gitCommit: String,
        device: Map<String, String>,
        clientFeedbackId: UUID = UUID.randomUUID(),
    ): String = withContext(Dispatchers.IO) {
        if (draft.note.isBlank()) throw DeveloperFeedbackException("note_required")
        val payload = draft.toRequestJson(
            clientFeedbackId = clientFeedbackId,
            context = context,
            appVersion = appVersion,
            buildNumber = buildNumber,
            gitCommit = gitCommit,
            device = device,
        )
        val response = request(
            method = "POST",
            url = "${SupabaseConfig.SUPABASE_URL}/functions/v1/harmony-developer-feedback",
            body = payload,
        )
        val json = JSONObject(response)
        if (!json.optBoolean("ok")) {
            throw DeveloperFeedbackException(json.optString("error", "feedback_write_failed"))
        }
        json.optString("id").takeIf { it.isNotBlank() }
            ?: clientFeedbackId.toString()
    }

    suspend fun loadFeedback(limit: Int = 100): List<DeveloperFeedbackItem> = withContext(Dispatchers.IO) {
        val safeLimit = limit.coerceIn(1, 250)
        val response = request(
            method = "GET",
            url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/developer_feedback?select=*&order=created_at.desc&limit=$safeLimit",
        )
        val array = JSONArray(response)
        buildList {
            for (index in 0 until array.length()) {
                add(array.getJSONObject(index).toFeedbackItem())
            }
        }
    }

    suspend fun updateStatus(id: String, status: DeveloperFeedbackStatus) = withContext(Dispatchers.IO) {
        val safeId = id.trim()
        if (!UUID_PATTERN.matches(safeId)) throw DeveloperFeedbackException("invalid_feedback_id")
        request(
            method = "PATCH",
            url = "${SupabaseConfig.SUPABASE_URL}/rest/v1/developer_feedback?id=eq.$safeId",
            body = JSONObject().put("status", status.name),
            prefer = "return=minimal",
        )
        Unit
    }

    private suspend fun request(
        method: String,
        url: String,
        body: JSONObject? = null,
        prefer: String? = null,
    ): String = withContext(Dispatchers.IO) {
        val accessToken = accessTokenProvider()
        val builder = Request.Builder()
            .url(url)
            .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "application/json")

        prefer?.let { builder.header("Prefer", it) }
        when (method) {
            "GET" -> builder.get()
            "POST" -> builder.post((body ?: JSONObject()).toString().toRequestBody(jsonMediaType))
            "PATCH" -> builder.patch((body ?: JSONObject()).toString().toRequestBody(jsonMediaType))
            else -> throw IllegalArgumentException("Unsupported method: $method")
        }

        httpClient.newCall(builder.build()).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val code = runCatching {
                    JSONObject(responseBody).optString("error")
                        .ifBlank { JSONObject(responseBody).optString("message") }
                }.getOrNull().orEmpty().ifBlank { "developer_feedback_${response.code}" }
                throw DeveloperFeedbackException(code)
            }
            responseBody
        }
    }

    private fun JSONObject.toFeedbackItem(): DeveloperFeedbackItem = DeveloperFeedbackItem(
        id = getString("id"),
        createdAt = optString("created_at"),
        status = enumValueOrDefault(optString("status"), DeveloperFeedbackStatus.NEW),
        priority = enumValueOrDefault(optString("priority"), DeveloperFeedbackPriority.MEDIUM),
        type = enumValueOrDefault(optString("feedback_type"), DeveloperFeedbackType.CHANGE),
        executionMode = enumValueOrDefault(optString("execution_mode"), ExecutionMode.REVIEW_FIRST),
        repository = optString("repository", "Harmony-Repo-2"),
        context = DeveloperReviewContext(
            screen = nullableString("screen"),
            route = nullableString("route"),
            gameId = nullableString("game_id"),
            part = nullableInt("part"),
            round = nullableInt("round"),
            questionId = nullableString("question_id"),
            questionText = nullableString("question_text"),
            elementId = nullableString("element_id"),
        ),
        note = optString("note"),
        transcript = nullableString("transcript"),
        screenshotPath = nullableString("screenshot_path"),
        audioPath = nullableString("audio_path"),
        appVersion = nullableString("app_version"),
        buildNumber = nullableString("build_number"),
        gitCommit = nullableString("git_commit"),
        githubPr = nullableInt("github_pr"),
        githubBranch = nullableString("github_branch"),
        fixedCommit = nullableString("fixed_commit"),
    )

    private fun JSONObject.nullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return optString(key).takeIf { it.isNotBlank() && it != "null" }
    }

    private fun JSONObject.nullableInt(key: String): Int? {
        if (!has(key) || isNull(key)) return null
        return optInt(key)
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String, fallback: T): T =
        enumValues<T>().firstOrNull { it.name == value } ?: fallback

    private companion object {
        val UUID_PATTERN = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    }
}

class DeveloperFeedbackException(val reason: String) : IOException(reason)
