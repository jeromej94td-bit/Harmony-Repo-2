package com.example.data.session

import android.content.Context
import com.example.data.db.HarmonyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AccountCacheBoundary(
    private val readOwner: () -> String?,
    private val writeOwner: (String) -> Unit,
    private val clearLocalData: suspend () -> Unit
) {
    suspend fun ensureOwner(userId: String) {
        require(userId.isNotBlank()) { "Authenticated user id must not be blank" }

        val previousOwner = readOwner()
        when {
            previousOwner == userId -> Unit
            previousOwner == null -> {
                clearLocalData()
                writeOwner(userId)
            }
            else -> {
                clearLocalData()
                writeOwner(userId)
            }
        }
    }

    suspend fun clearForReset(userId: String) {
        require(userId.isNotBlank()) { "Authenticated user id must not be blank" }
        clearLocalData()
        writeOwner(userId)
    }

    companion object {
        private const val PREFS_NAME = "harmony_account_cache_owner"
        private const val OWNER_KEY = "owner_user_id"
        private val RELATIONSHIP_MEDIA_DIRS = listOf("chat", "avatars", "picshare", "moments")

        fun forApplication(context: Context): AccountCacheBoundary {
            val appContext = context.applicationContext
            val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val database = HarmonyDatabase.getInstance(appContext)

            return AccountCacheBoundary(
                readOwner = { prefs.getString(OWNER_KEY, null) },
                writeOwner = { userId -> prefs.edit().putString(OWNER_KEY, userId).apply() },
                clearLocalData = {
                    withContext(Dispatchers.IO) {
                        database.clearAllTables()
                        RELATIONSHIP_MEDIA_DIRS.forEach { directoryName ->
                            File(appContext.filesDir, directoryName).deleteRecursively()
                        }
                    }
                }
            )
        }
    }
}
