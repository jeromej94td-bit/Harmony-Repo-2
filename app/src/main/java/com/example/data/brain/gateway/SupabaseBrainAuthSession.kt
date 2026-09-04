package com.example.data.brain.gateway

/**
 * Harmony Brain is intentionally frozen for the real-user/couple foundation.
 *
 * The old implementation created an anonymous Supabase Auth user by calling
 * `/auth/v1/signup` whenever Brain needed a token. That polluted the real user
 * pool and bypassed the authenticated Harmony session. Brain must use the
 * canonical signed-in Harmony session when it is re-enabled in a later phase.
 */
class SupabaseBrainAuthSession {
    @Suppress("UNUSED_PARAMETER")
    suspend fun getOrFetchToken(forceRefresh: Boolean = false): String {
        error("Harmony Brain is disabled. Use the authenticated Harmony session when Brain is re-enabled.")
    }
}
