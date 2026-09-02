package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth

/**
 * Keeps the current production AuthScreen intact while allowing the real-user
 * session layer to distinguish an authenticated login from the explicit local
 * demo entry point.
 */
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onDemoRequested: () -> Unit
) {
    AuthScreen(
        onAuthSuccess = {
            val hasAuthenticatedSession = runCatching {
                SupabaseConfig.client.auth.currentSessionOrNull()
            }.getOrNull() != null

            if (hasAuthenticatedSession) {
                onAuthSuccess()
            } else {
                onDemoRequested()
            }
        }
    )
}
