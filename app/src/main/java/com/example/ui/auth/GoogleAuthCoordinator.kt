package com.example.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.SupabaseConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken

enum class GoogleSignInOutcome {
    SESSION_CREATED,
    OAUTH_REDIRECT_STARTED
}

/**
 * Canonical Google authentication entry point for Harmony.
 *
 * Credential Manager is the preferred path because it creates the Supabase
 * session immediately from Google's ID token. Some Android / Google Play
 * Services states can nevertheless report that no native credential is
 * available even though Google OAuth itself is usable. In that case Harmony
 * falls back to Supabase browser OAuth instead of leaving the Google button
 * dead. A stale [16] re-auth state is cleared and retried once first.
 */
suspend fun performHarmonyGoogleSignIn(context: Context): GoogleSignInOutcome {
    val activity = context.findActivity()
        ?: throw IllegalStateException("Activity Context nicht gefunden")
    val credentialManager = CredentialManager.create(context)
    return performResilientGoogleSignIn(
        activity = activity,
        credentialManager = credentialManager
    )
}

private suspend fun performResilientGoogleSignIn(
    activity: Activity,
    credentialManager: CredentialManager,
    retryAfterCredentialReset: Boolean = true
): GoogleSignInOutcome {
    return try {
        val googleSignInOption = GetSignInWithGoogleOption.Builder(
            serverClientId = SupabaseConfig.GOOGLE_WEB_CLIENT_ID
        ).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleSignInOption)
            .build()

        val result = credentialManager.getCredential(
            context = activity,
            request = request
        )

        val credential = result.credential
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            throw IllegalStateException("Unerwarteter Anmeldetyp: ${credential.type}")
        }

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        SupabaseConfig.client.auth.signInWith(IDToken) {
            idToken = googleIdTokenCredential.idToken
            provider = Google
        }
        GoogleSignInOutcome.SESSION_CREATED
    } catch (exception: Exception) {
        val accountReauthFailure = isGoogleAccountReauthFailure(exception)

        if (accountReauthFailure && retryAfterCredentialReset) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (clearError: Exception) {
                Log.w(
                    "HarmonyGoogleAuth",
                    "Google credential state could not be cleared before retry",
                    clearError
                )
            }
            return performResilientGoogleSignIn(
                activity = activity,
                credentialManager = credentialManager,
                retryAfterCredentialReset = false
            )
        }

        if (accountReauthFailure) {
            Log.w(
                "HarmonyGoogleAuth",
                "Google native sign-in still reports [16] Account reauth failed; using OAuth fallback",
                exception
            )
            return startGoogleOAuthFallback()
        }

        if (exception is androidx.credentials.exceptions.GetCredentialCancellationException) {
            throw exception
        }

        Log.w(
            "HarmonyGoogleAuth",
            "Native Google credential failed (possibly SHA-1 mismatch or unavailable); using OAuth fallback",
            exception
        )
        return startGoogleOAuthFallback()
    }
}

private suspend fun startGoogleOAuthFallback(): GoogleSignInOutcome {
    SupabaseConfig.client.auth.signInWith(Google)
    return GoogleSignInOutcome.OAUTH_REDIRECT_STARTED
}

private fun isGoogleAccountReauthFailure(error: Throwable): Boolean {
    val details = generateSequence(error) { it.cause }
        .mapNotNull { it.message }
        .joinToString(separator = " ")

    return details.contains("Account reauth failed", ignoreCase = true) ||
        details.contains("[16]", ignoreCase = true)
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
