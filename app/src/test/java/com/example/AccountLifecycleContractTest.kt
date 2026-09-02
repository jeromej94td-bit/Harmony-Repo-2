package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountLifecycleContractTest {
    @Test
    fun `reset keeps login but removes harmony relationship data`() {
        val source = source("app/src/main/java/com/example/ui/screens/AccountLifecycleScreen.kt")

        assertTrue(source.contains("Harmony zurücksetzen"))
        assertTrue(source.contains("Dein Login bleibt bestehen"))
        assertTrue(source.contains("Verbindung zu deinem Partner wird getrennt"))
        assertTrue(source.contains("Fortschritt"))
        assertTrue(source.contains("nicht rückgängig"))
    }

    @Test
    fun `account deletion explains automatic disconnect`() {
        val source = source("app/src/main/java/com/example/ui/screens/AccountLifecycleScreen.kt")

        assertTrue(source.contains("Konto löschen"))
        assertTrue(source.contains("automatisch entkoppelt"))
        assertTrue(source.contains("unwiderruflich"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
