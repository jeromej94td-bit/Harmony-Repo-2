package com.example.data.repository

import com.example.data.db.HarmonyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Removes only the durable answer for the question the user explicitly skips.
 *
 * The delete runs through Room's transaction boundary so answer-flow invalidation stays
 * consistent while leaving every other answer in the pack untouched.
 */
suspend fun HarmonyDatabase.deleteAnswerForSkip(
    packId: String,
    questionIndex: Int
) = withContext(Dispatchers.IO) {
    runInTransaction(
        Runnable {
            openHelper.writableDatabase.execSQL(
                "DELETE FROM answers WHERE packId = ? AND questionIndex = ?",
                arrayOf<Any?>(packId, questionIndex)
            )
        }
    )
}
