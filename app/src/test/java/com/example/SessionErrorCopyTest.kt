package com.example

import com.example.ui.session.sessionErrorCopy
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionErrorCopyTest {
    @Test
    fun `pairing errors use stable user copy`() {
        assertEquals("Bitte prüfe den sechsstelligen Code.", sessionErrorCopy("invalid_invite_code"))
        assertEquals("Dieser Code ist ungültig, abgelaufen oder wurde bereits verwendet.", sessionErrorCopy("invite_not_available"))
        assertEquals("Einer von euch ist bereits mit einem Partner verbunden.", sessionErrorCopy("already_paired"))
        assertEquals("Deinen eigenen Code kannst du nicht verwenden.", sessionErrorCopy("cannot_pair_with_self"))
        assertEquals("Dein Partner ist inzwischen bereits verbunden. Bitte erstellt einen neuen Code.", sessionErrorCopy("inviter_already_paired"))
        assertEquals("Diese Verbindung ist bereits vollständig.", sessionErrorCopy("couple_full"))
    }

    @Test
    fun `auth and lifecycle errors do not leak backend text`() {
        assertEquals("Bitte melde dich erneut mit deinem Harmony-Konto an.", sessionErrorCopy("not_authenticated_or_anonymous"))
        assertEquals("Dein Konto konnte gerade nicht gelöscht werden. Bitte versuche es erneut.", sessionErrorCopy("account_deletion_failed"))
        assertEquals("Die Partner-Verbindung konnte vor dem Löschen nicht sauber getrennt werden.", sessionErrorCopy("couple_disconnect_failed"))
        assertEquals("Die Aktion konnte gerade nicht abgeschlossen werden. Bitte versuche es erneut.", sessionErrorCopy("some postgres detail that must stay hidden"))
    }
}