package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SoloHomeContractTest {
    @Test
    fun `solo home keeps games but hides couple-only surfaces`() {
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")

        assertTrue(home.contains("liveSession?.isPaired == true"))
        assertTrue(home.contains("if (isPaired) {\n            PicShareHomeCard("))
        assertTrue(home.contains("Dein Harmony-Fortschritt"))
        assertTrue(home.contains("PartnerConnectionSheet("))
        assertTrue(home.contains("sessionViewModel.createPartnerInvite()"))
        assertTrue(home.contains("sessionViewModel.joinPartnerInvite(code)"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
