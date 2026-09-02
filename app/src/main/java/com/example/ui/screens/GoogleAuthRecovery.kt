package com.example.ui.screens

import android.app.Activity
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

internal enum class GoogleSignInOutcome {
    SESSION_CREATED,
    OAUTH_REDIRECT_STARTED
}

internal suspend fun performResilientGoogleSignIn(
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
    } catch (exception: GetCredentialException) {
        if (!isGoogleAccountReauthFailure(exception)) {
            throw exception
        }

        if (retryAfterCredentialReset) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (clearError: Exception) {
                Log.w(
                    "AuthScreen",
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

        // Error [16] can also be caused by an Android OAuth package/SHA mismatch.
        // Fall back to Supabase's browser OAuth flow so the Google button remains usable.
        Log.w(
            "AuthScreen",
            "Google native sign-in still reports [16] Account reauth failed; using OAuth fallback",
            exception
        )
        SupabaseConfig.client.auth.signInWith(Google)
        GoogleSignInOutcome.OAUTH_REDIRECT_STARTED
    }
}

internal fun isGoogleAccountReauthFailure(error: Throwable): Boolean {
    val details = generateSequence(error) { it.cause }
        .mapNotNull { it.message }
        .joinToString(separator = " ")

    return details.contains("Account reauth failed", ignoreCase = true) ||
        details.contains("[16]", ignoreCase = true)
}
