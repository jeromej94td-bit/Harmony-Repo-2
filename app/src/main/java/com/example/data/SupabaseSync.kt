package com.example.data

import android.content.Context
import android.util.Log

object SupabaseSync {
    suspend fun fetchAndSync(context: Context) {
        try {
            Log.d("SupabaseSync", "Delegating fetchAndSync to HarmonyContentRepository...")
            val success = HarmonyContentRepository.initAndSync(context)
            Log.d("SupabaseSync", "Sync completed. Success: $success")
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Error during fetchAndSync delegation", e)
        }
    }

    // Deprecated / Compatibility overload
    suspend fun fetchAndSync() {
        Log.w("SupabaseSync", "fetchAndSync called without context. Dynamic content sync skipped.")
    }
}
