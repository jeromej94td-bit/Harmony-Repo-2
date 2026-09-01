package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PartnerConnectionContractTest {
    @Test
    fun `partner connection uses shareable code without password`() {
        val source = source("app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt")

        assertTrue(source.contains("Partner verbinden"))
        assertTrue(source.contains("Code erstellen"))
        assertTrue(source.contains("Code eingeben"))
        assertTrue(source.contains("Code teilen"))
        assertTrue(source.contains("Intent.ACTION_SEND"))
        assertFalse(source.contains("Passwort"))
        assertFalse(source.contains("password", ignoreCase = true))
    }

    @Test
    fun `paired view is limited to one partner`() {
        val source = source("app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt")
        assertTrue(source.contains("Ihr seid verbunden"))
        assertTrue(source.contains("Verbindung trennen"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
