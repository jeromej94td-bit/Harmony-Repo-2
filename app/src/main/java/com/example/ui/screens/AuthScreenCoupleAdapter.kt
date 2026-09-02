package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth

/**
 * Couple-session adapter for the current production AuthScreen.
 *
 * The one-argument AuthScreen remains the single owner of Google/email login,
 * registration and password recovery. Its demo button also calls the success
 * callback, so this overload distinguishes that local demo action from a real
 * authenticated Supabase session without forking the production auth UI.
 */
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onDemoRequested: () -> Unit
) {
    AuthScreen(
        onAuthSuccess = {
            val hasRealSession = runCatching {
                SupabaseConfig.client.auth.currentSessionOrNull() != null
            }.getOrDefault(false)

            if (hasRealSession) {
                onAuthSuccess()
            } else {
                onDemoRequested()
            }
        }
    )
}
