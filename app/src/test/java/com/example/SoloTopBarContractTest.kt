package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SoloTopBarContractTest {
    @Test
    fun `topbar hides partner avatar for real solo accounts`() {
        val commonUi = source("app/src/main/java/com/example/ui/components/CommonUI.kt")

        assertTrue(commonUi.contains("val livePartner = liveSession?.partner"))
        assertTrue(commonUi.contains("val showPartnerAvatar = isDemoMode || livePartner != null"))
        assertTrue(commonUi.contains("if (showPartnerAvatar)"))
        assertTrue(commonUi.contains("AuthenticatedAvatarImage("))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
