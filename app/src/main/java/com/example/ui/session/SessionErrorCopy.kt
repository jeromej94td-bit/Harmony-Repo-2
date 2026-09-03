package com.example.ui.session

internal fun sessionErrorCopy(rawReason: String?): String {
    val reason = rawReason.orEmpty().lowercase()
    return when {
        "invalid_invite_code" in reason -> "Bitte prüfe den sechsstelligen Code."
        "invite_not_available" in reason -> "Dieser Code ist ungültig, abgelaufen oder wurde bereits verwendet."
        "inviter_already_paired" in reason -> "Dein Partner ist inzwischen bereits verbunden. Bitte erstellt einen neuen Code."
        "cannot_pair_with_self" in reason -> "Deinen eigenen Code kannst du nicht verwenden."
        "already_paired" in reason -> "Einer von euch ist bereits mit einem Partner verbunden."
        "couple_full" in reason -> "Diese Verbindung ist bereits vollständig."
        "not_authenticated_or_anonymous" in reason || "not_authenticated" in reason ->
            "Bitte melde dich erneut mit deinem Harmony-Konto an."
        "couple_disconnect_failed" in reason ->
            "Die Partner-Verbindung konnte vor dem Löschen nicht sauber getrennt werden."
        "avatar_cleanup_failed" in reason || "account_deletion_failed" in reason ->
            "Dein Konto konnte gerade nicht gelöscht werden. Bitte versuche es erneut."
        "server_not_configured" in reason ->
            "Die Konto-Funktion ist gerade nicht verfügbar. Bitte versuche es später erneut."
        else -> "Die Aktion konnte gerade nicht abgeschlossen werden. Bitte versuche es erneut."
    }
}
