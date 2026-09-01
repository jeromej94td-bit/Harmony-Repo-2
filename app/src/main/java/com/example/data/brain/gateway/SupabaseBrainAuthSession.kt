package com.example.data.brain.gateway

import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import java.io.IOException

/**
 * Provides Harmony Brain with the canonical signed-in Harmony access token.
 *
 * Brain must never create its own Supabase Auth user. If there is no real
 * signed-in session (for example in local demo mode), Brain calls fail closed
 * instead of creating an anonymous account.
 */
class SupabaseBrainAuthSession {
    @Suppress("UNUSED_PARAMETER")
    suspend fun getOrFetchToken(forceRefresh: Boolean = false): String {
        return SupabaseConfig.client.auth.currentSessionOrNull()?.accessToken
            ?: throw IOException("Harmony Brain requires a signed-in Supabase account")
    }
}
