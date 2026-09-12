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

class RapidAnswerSubmissionGuard(
    private val windowMs: Long = 750L,
    private val nowMs: () -> Long = { System.nanoTime() / 1_000_000L }
) {
    private data class Recent(
        val answerText: String,
        val timestampMs: Long,
        val status: CoupleAnswerStatus
    )
    private val recentByQuestion = mutableMapOf<String, Recent>()

    @Synchronized
    fun recent(packId: String, questionIndex: Int, answerText: String): CoupleAnswerStatus? {
        val key = key(packId, questionIndex)
        val item = recentByQuestion[key] ?: return null
        if (nowMs() - item.timestampMs >= windowMs) {
            recentByQuestion.remove(key)
            return null
        }
        if (item.answerText != normalizeCoupleAnswerText(answerText)) return null
        return item.status
    }

    @Synchronized
    fun record(packId: String, questionIndex: Int, answerText: String, status: CoupleAnswerStatus) {
        recentByQuestion[key(packId, questionIndex)] = Recent(normalizeCoupleAnswerText(answerText), nowMs(), status)
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
    val partnerAnswerText: String?
) {
    val revealState: CoupleRevealState
        get() = coupleRevealState(
            myAnswered = !myAnswerText.isNullOrBlank(),
            partnerAnswered = partnerAnswered,
            partnerAnswerText = partnerAnswerText
        )
}

data class CouplePackAttemptStatus(
    val attemptId: String,
    val sequence: Int,
    val createdNew: Boolean
)

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

    suspend fun startPackAttempt(packId: String): CouplePackAttemptStatus {
        val normalizedPackId = packId.trim()
        if (normalizedPackId.isBlank()) throw CoupleQuestionException("invalid_pack_id")

        val response = postRpc(
            functionName = "start_pack_attempt",
            body = JSONObject().put("p_pack_id", normalizedPackId)
        )
        val rows = JSONArray(response)
        if (rows.length() != 1) throw CoupleQuestionException("pack_attempt_missing")
        val row = rows.getJSONObject(0)
        return CouplePackAttemptStatus(
            attemptId = row.getString("attempt_id"),
            sequence = row.getInt("attempt_sequence"),
            createdNew = row.optBoolean("created_new", false)
        )
    }

    suspend fun submitAnswer(packId: String, questionIndex: Int, answerText: String): CoupleAnswerStatus {
        if (packId.isBlank() || questionIndex < 0 || answerText.isBlank()) {
            throw CoupleQuestionException("invalid_answer_payload")
        }

        val normalizedAnswer = normalizeCoupleAnswerText(answerText)
        submitMutex.lock()
        try {
            submissionGuard.recent(packId, questionIndex, normalizedAnswer)?.let { return it }
            val response = postRpc(
                functionName = "submit_question_answer",
                body = JSONObject()
                    .put("p_pack_id", packId)
                    .put("p_question_index", questionIndex)
                    .put("p_answer_text", normalizedAnswer)
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
            // The RPC creates/locates the shared round. Persist the selected value again
            // through the row's authenticated UPDATE path so a changed choice always
            // replaces the previous value instead of leaving the first answer visible.
            replaceRoundAnswer(status.roundId, normalizedAnswer)
            submissionGuard.record(packId, questionIndex, normalizedAnswer, status)
            return status
        } finally {
            submitMutex.unlock()
        }
    }

    suspend fun getPackResults(packId: String): List<CouplePackQuestionResult> {
        if (packId.isBlank()) return emptyList()
        val response = postRpc("get_pack_question_results", JSONObject().put("p_pack_id", packId))
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
                        partnerAnswerText = row.nullableString("partner_answer_text")
                    )
                )
            }
        }
    }

    private suspend fun replaceRoundAnswer(roundId: String, answerText: String) = withContext(Dispatchers.IO) {
        val accessToken = accessTokenProvider()
        val request = Request.Builder()
            .url("${SupabaseConfig.SUPABASE_URL}/rest/v1/harmony_question_answers?round_id=eq.$roundId")
            .patch(JSONObject().put("answer_text", answerText).toString().toRequestBody(jsonMediaType))
            .header("apikey", SupabaseConfig.SUPABASE_PUBLISHABLE_KEY)
            .header("Authorization", "Bearer $accessToken")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("Prefer", "return=representation")
            .build()

        httpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val serverCode = runCatching { JSONObject(responseBody).optString("message") }
                    .getOrNull().orEmpty().ifBlank { "answer_replace_${response.code}" }
                throw CoupleQuestionException(serverCode)
            }
            val rows = JSONArray(responseBody.ifBlank { "[]" })
            val synced = (0 until rows.length()).any { index ->
                rows.getJSONObject(index).optString("answer_text") == answerText
            }
            if (!synced) throw CoupleQuestionException("latest_answer_not_synced")
        }
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
