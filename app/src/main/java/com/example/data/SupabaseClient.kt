package com.example.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.functions.Functions

object SupabaseConfig {
    val client = createSupabaseClient(
        supabaseUrl = "https://rspgnonlpkxdudbjxnrl.supabase.co",
        supabaseKey = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1"
    ) {
        install(Auth)
        install(Functions)
    }

    // Configure your Google Web Client ID from Google Cloud Console (associated with Supabase)
    const val GOOGLE_WEB_CLIENT_ID = "1038373974684-lh5o0nhstljubgf76gfg62ifp302ulm6.apps.googleusercontent.com"
}
