package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class RealProfileEditContractTest {
    @Test
    fun `production profile saves own name through AppSession`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        assertTrue(source.contains("AppSessionViewModel"))
        assertTrue(source.contains("sessionViewModel.updateProfileDisplayName(userEdit)"))
        assertTrue(source.contains("if (isDemoMode) {\n                        OutlinedTextField(\n                            value = partnerEdit"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
