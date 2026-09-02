package com.example.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions

object SupabaseConfig {
    const val SUPABASE_PROJECT_ID = "rspgnonlpkxdudbjxnrl"
    const val SUPABASE_URL = "https://$SUPABASE_PROJECT_ID.supabase.co"
    const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1"

    // Reuse the production callback already used by this Supabase project.
    const val AUTH_DEEP_LINK_SCHEME = "harmony"
    const val AUTH_DEEP_LINK_HOST = "auth"
    const val AUTH_DEEP_LINK_PATH = "/callback"
    const val PASSWORD_RECOVERY_REDIRECT_URL = "harmony://auth/callback"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_PUBLISHABLE_KEY
    ) {
        install(Auth) {
            scheme = AUTH_DEEP_LINK_SCHEME
            host = AUTH_DEEP_LINK_HOST
            defaultRedirectUrl = PASSWORD_RECOVERY_REDIRECT_URL
        }
        install(Functions)
    }

    // Configure your Google Web Client ID from Google Cloud Console (associated with Supabase)
    const val GOOGLE_WEB_CLIENT_ID = "1038373974684-lh5o0nhstljubgf76gfg62ifp302ulm6.apps.googleusercontent.com"
}
