package com.example.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.data.SupabaseConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken

enum class GoogleSignInOutcome {
    SESSION_CREATED
}

/**
 * Canonical native Google authentication entry point for Harmony.
 *
 * Google receives only SHA-256(rawNonce). Supabase receives the Google ID
 * token together with the original raw nonce and keeps nonce validation on.
 * Browser OAuth is intentionally not used as a fallback for the Google button.
 */
suspend fun performHarmonyGoogleSignIn(context: Context): GoogleSignInOutcome {
    val activity = context.findActivity()
        ?: throw IllegalStateException("Activity Context nicht gefunden")
    return performNativeGoogleSignIn(
        activity = activity,
        credentialManager = CredentialManager.create(context)
    )
}

private suspend fun performNativeGoogleSignIn(
    activity: Activity,
    credentialManager: CredentialManager,
    retryAfterCredentialReset: Boolean = true
): GoogleSignInOutcome {
    return try {
        val rawNonce = GoogleNativeAuthConfig.generateRawNonce()
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(GoogleNativeAuthConfig.WEB_CLIENT_ID)
            .setNonce(GoogleNativeAuthConfig.sha256Hex(rawNonce))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            context = activity,
            request = request
        )
        val credential = result.credential
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            throw IllegalStateException("Unerwarteter Anmeldetyp: ${credential.type}")
        }

        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        SupabaseConfig.client.auth.signInWith(IDToken) {
            idToken = googleCredential.idToken
            provider = Google
            nonce = rawNonce
        }
        GoogleSignInOutcome.SESSION_CREATED
    } catch (exception: Exception) {
        val accountReauthFailure = isGoogleAccountReauthFailure(exception)
        if (accountReauthFailure && retryAfterCredentialReset) {
            runCatching {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            }.onFailure { clearError ->
                Log.w(
                    "HarmonyGoogleAuth",
                    "Google credential state could not be cleared before native retry",
                    clearError
                )
            }
            return performNativeGoogleSignIn(
                activity = activity,
                credentialManager = credentialManager,
                retryAfterCredentialReset = false
            )
        }

        if (exception is GetCredentialCancellationException) {
            throw exception
        }

        Log.e(
            "HarmonyGoogleAuth",
            "Native Google sign-in failed; browser OAuth fallback is disabled",
            exception
        )
        throw exception
    }
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
