package com.example.data.couple

import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

enum class CoupleRevealState {
    NEEDS_OWN_ANSWER,
    WAITING_FOR_PARTNER,
    READY
}

fun coupleRevealState(
    myAnswered: Boolean,
    partnerAnswered: Boolean,
    partnerAnswerText: String?
): CoupleRevealState = when {
    !myAnswered -> CoupleRevealState.NEEDS_OWN_ANSWER
    !partnerAnswered -> CoupleRevealState.WAITING_FOR_PARTNER
    partnerAnswerText.isNullOrBlank() -> CoupleRevealState.WAITING_FOR_PARTNER
    else -> CoupleRevealState.READY
}

data class CoupleAnswerStatus(
    val roundId: String,
    val myAnswered: Boolean,
    val partnerAnswered: Boolean,
    val readyToReveal: Boolean
)

data class PartnerPackCompletionStatus(
    val myCompleted: Boolean,
    val partnerCompleted: Boolean,
    val readyToReveal: Boolean,
    val notificationCreated: Boolean
)

data class PartnerNotification(
    val notificationId: String,
    val packId: String,
    val body: String,
    val actorUserId: String?,
    val actorDisplayName: String?,
    val actorAvatarUrl: String?,
    val createdAt: String?
)

class RapidAnswerSubmissionGuard(
    private val windowMs: Long = 750L,
    private val nowMs: () -> Long = { System.nanoTime() / 1_000_000L }
) {
    private data class Recent(val timestampMs: Long, val status: CoupleAnswerStatus)
    private val recentByQuestion = mutableMapOf<String, Recent>()

    @Synchronized
    fun recent(packId: String, questionIndex: Int): CoupleAnswerStatus? {
        val key = key(packId, questionIndex)
        val item = recentByQuestion[key] ?: return null
        if (nowMs() - item.timestampMs >= windowMs) {
            recentByQuestion.remove(key)
            return null
        }
        return item.status
    }

    @Synchronized
    fun record(packId: String, questionIndex: Int, status: CoupleAnswerStatus) {
        recentByQuestion[key(packId, questionIndex)] = Recent(nowMs(), status)
    }

    private fun key(packId: String, questionIndex: Int): String = "$packId#$questionIndex"
}

data class CouplePackQuestionResult(
    val questionIndex: Int,
    val myAnswerText: String?,
    val partnerAnswered: Boolean,
    val readyToReveal: Boolean,
    val partnerUserId: String?,
    val partnerDisplayName: String?,
    val partnerAvatarUrl: String?,
    val partnerAnswerText: String?,
    val myPackCompleted: Boolean = false,
    val partnerPackCompleted: Boolean = false
) {
    val revealState: CoupleRevealState
        get() = coupleRevealState(
            myAnswered = !myAnswerText.isNullOrBlank(),
            partnerAnswered = partnerAnswered,
            partnerAnswerText = partnerAnswerText
        )
}

class CoupleQuestionRepository(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build(),
    private val accessTokenProvider: suspend () -> String = {
        SupabaseConfig.client.auth.currentSessionOrNull()?.accessToken
            ?: throw CoupleQuestionException("not_authenticated")
    },
    private val submissionGuard: RapidAnswerSubmissionGuard = RapidAnswerSubmissionGuard()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val submitMutex = Mutex()

    suspend fun submitAnswer(packId: String, questionIndex: Int, answerText: String): CoupleAnswerStatus {
        if (packId.isBlank() || questionIndex < 0 || answerText.isBlank()) {
            throw CoupleQuestionException("invalid_answer_payload")
        }

        submitMutex.lock()
        try {
            submissionGuard.recent(packId, questionIndex)?.let { return it }
            val response = postRpc(
                functionName = "submit_question_answer",
                body = JSONObject()
                    .put("p_pack_id", packId)
                    .put("p_question_index", questionIndex)
                    .put("p_answer_text", answerText.trim())
            )
            val rows = JSONArray(response)
            if (rows.length() != 1) throw CoupleQuestionException("answer_status_missing")
            val row = rows.getJSONObject(0)
            val status = CoupleAnswerStatus(
                roundId = row.getString("round_id"),
                myAnswered = row.optBoolean("my_answered", false),
                partnerAnswered = row.optBoolean("partner_answered", false),
                readyToReveal = row.optBoolean("ready_to_reveal", false)
            )
            submissionGuard.record(packId, questionIndex, status)
            return status
        } finally {
            submitMutex.unlock()
        }
    }

    suspend fun completePartnerPack(packId: String): PartnerPackCompletionStatus {
        if (!PartnerPackRevealPolicy.isWholePackRevealEnabled(packId)) {
            throw CoupleQuestionException("partner_pack_not_enabled")
        }
        val response = postRpc("complete_partner_pack", JSONObject().put("p_pack_id", packId))
        val rows = JSONArray(response)
        if (rows.length() != 1) throw CoupleQuestionException("partner_pack_completion_missing")
        val row = rows.getJSONObject(0)
        return PartnerPackCompletionStatus(
            myCompleted = row.optBoolean("my_completed", false),
            partnerCompleted = row.optBoolean("partner_completed", false),
            readyToReveal = row.optBoolean("ready_to_reveal", false),
            notificationCreated = row.optBoolean("notification_created", false)
        )
    }

    suspend fun getPackResults(packId: String): List<CouplePackQuestionResult> {
        if (packId.isBlank()) return emptyList()
        val wholePackReveal = PartnerPackRevealPolicy.isWholePackRevealEnabled(packId)
        val functionName = if (wholePackReveal) "get_partner_pack_results" else "get_pack_question_results"
        val response = postRpc(functionName, JSONObject().put("p_pack_id", packId))
        val rows = JSONArray(response)
        return buildList {
            for (index in 0 until rows.length()) {
                val row = rows.getJSONObject(index)
                add(
                    CouplePackQuestionResult(
                        questionIndex = row.getInt("question_index"),
                        myAnswerText = row.nullableString("my_answer_text"),
                        partnerAnswered = row.optBoolean("partner_answered", false),
                        readyToReveal = row.optBoolean("ready_to_reveal", false),
                        partnerUserId = row.nullableString("partner_user_id"),
                        partnerDisplayName = row.nullableString("partner_display_name"),
                        partnerAvatarUrl = row.nullableString("partner_avatar_url"),
                        partnerAnswerText = row.nullableString("partner_answer_text"),
                        myPackCompleted = if (wholePackReveal) row.optBoolean("my_completed", false) else false,
                        partnerPackCompleted = if (wholePackReveal) row.optBoolean("partner_completed", false) else false
                    )
                )
            }
        }
    }

    suspend fun getPartnerNotifications(): List<PartnerNotification> {
        val response = postRpc("get_partner_notifications", JSONObject())
        val rows = JSONArray(response)
        return buildList {
            for (index in 0 until rows.length()) {
                val row = rows.getJSONObject(index)
                add(
                    PartnerNotification(
                        notificationId = row.getString("notification_id"),
                        packId = row.getString("pack_id"),
                        body = row.getString("body"),
                        actorUserId = row.nullableString("actor_user_id"),
                        actorDisplayName = row.nullableString("actor_display_name"),
                        actorAvatarUrl = row.nullableString("actor_avatar_url"),
                        createdAt = row.nullableString("created_at")
                    )
                )
            }
        }
    }

    suspend fun markPartnerNotificationRead(notificationId: String): Boolean {
        if (notificationId.isBlank()) return false
        val response = postRpc(
            "mark_partner_notification_read",
            JSONObject().put("p_notification_id", notificationId)
        )
        return response.trim().equals("true", ignoreCase = true)
    }

    private suspend fun postRpc(functionName: String, body: JSONObject): String = withContext(Dispatchers.IO) {
        val accessToken = accessTokenProvider()
        val request = Request.Builder()
            .url("${SupabaseConfig.SUPABASE_URL}/rest/v1/rpc/$functionName")
            .post(body.toString().toRequestBody(jsonMediaType))
            .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()

        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val serverCode = runCatching { JSONObject(responseBody).optString("message") }
                    .getOrNull().orEmpty().ifBlank { "rpc_${response.code}" }
                throw CoupleQuestionException(serverCode)
            }
            responseBody
        }
    }

    private fun JSONObject.nullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return optString(key).takeIf { it.isNotBlank() && it != "null" }
    }
}

class CoupleQuestionException(val reason: String) : IOException(reason)
