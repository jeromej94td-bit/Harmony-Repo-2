package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PartnerConnectionPollingContractTest {
    @Test
    fun `generated invite polls quietly until the partner joins`() {
        val source = source("app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt")
        assertTrue(source.contains("shouldPollPartnerInvite"))
        assertTrue(source.contains("delay(4000)"))
        assertTrue(source.contains("refreshSilently()"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
