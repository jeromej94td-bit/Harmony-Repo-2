package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SoloTopBarContractTest {
    @Test
    fun `topbar hides partner avatar for real solo accounts`() {
        val commonUi = source("app/src/main/java/com/example/ui/components/CommonUI.kt")
        val identity = source("app/src/main/java/com/example/ui/components/HarmonyTopBarIdentity.kt")

        assertTrue(commonUi.contains("SessionAwareTopBarAvatars("))
        assertTrue(identity.contains("val livePartner = liveSession?.partner"))
        assertTrue(identity.contains("val showPartnerAvatar = isDemoMode || livePartner != null"))
        assertTrue(identity.contains("if (showPartnerAvatar)"))
        assertTrue(identity.contains("AuthenticatedAvatarImage("))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
