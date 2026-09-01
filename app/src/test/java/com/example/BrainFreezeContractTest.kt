package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class BrainFreezeContractTest {
    @Test
    fun `production app does not start brain or create anonymous auth users`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val vm = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")
        val brainAuth = source("app/src/main/java/com/example/data/brain/gateway/SupabaseBrainAuthSession.kt")

        assertFalse(main.contains("attachAutoGeneration(this)"))
        assertFalse(vm.contains("HarmonyBrainEngine.analyzeAnswers"))
        assertFalse(vm.contains("ForegroundGameGenerator("))
        assertFalse(brainAuth.contains("/auth/v1/signup"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
