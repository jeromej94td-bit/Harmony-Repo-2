package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SilentSessionRefreshContractTest {
    @Test
    fun `invite polling refreshes session without entering loading phase`() {
        val source = source("app/src/main/java/com/example/ui/session/AppSessionViewModel.kt")
        assertTrue(source.contains("fun refreshSilently()"))
        val silentBody = source.substringAfter("fun refreshSilently()").substringBefore("fun createPartnerInvite()")
        assertTrue(silentBody.contains("repository.refresh()"))
        assertFalse(silentBody.contains("SessionPhase.LOADING"))
        assertTrue(silentBody.contains("if (session.isPaired) null else current.activeInvite"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
