package com.example.ui.screens

import android.app.Activity
import androidx.credentials.CredentialManager
import com.example.data.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

internal enum class GoogleSignInOutcome {
    SESSION_CREATED,
    OAUTH_REDIRECT_STARTED
}

/**
 * Starts Google authentication through Supabase OAuth.
 *
 * Harmony deliberately avoids Android Credential Manager here because the current
 * installed app repeatedly receives Google error [16] before Supabase is reached.
 * Supabase opens the Google OAuth flow and returns through the configured
 * harmony://auth/callback deep link.
 */
@Suppress("UNUSED_PARAMETER")
internal suspend fun performResilientGoogleSignIn(
    activity: Activity,
    credentialManager: CredentialManager,
    retryAfterCredentialReset: Boolean = true
): GoogleSignInOutcome {
    SupabaseConfig.client.auth.signInWith(Google)
    return GoogleSignInOutcome.OAUTH_REDIRECT_STARTED
}
