package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SoloChatContractTest {
    @Test
    fun `real solo user sees partner connection gate instead of fake chat`() {
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")

        assertTrue(chat.contains("liveSession?.partner"))
        assertTrue(chat.contains("PartnerRequiredScreen("))
        assertTrue(chat.contains("PartnerConnectionSheet("))
        assertTrue(chat.contains("sessionViewModel.createPartnerInvite()"))
        assertTrue(chat.contains("sessionViewModel.joinPartnerInvite(code)"))
    }

    @Test
    fun `paired chat renders authenticated partner avatar`() {
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")

        assertTrue(chat.contains("AuthenticatedAvatarImage("))
        assertTrue(chat.contains("livePartner.avatarUrl"))
        assertTrue(chat.contains("livePartner.displayName"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
