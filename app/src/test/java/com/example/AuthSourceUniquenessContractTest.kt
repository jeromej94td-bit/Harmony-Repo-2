package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthSourceUniquenessContractTest {

    @Test
    fun `only canonical Android auth implementation remains`() {
        assertTrue(sourceExists("app/src/main/java/com/example/ui/screens/AuthScreen.kt"))
        assertFalse(sourceExists("app/applet/app/src/main/java/com/example/ui/screens/AuthScreen.kt"))
        assertFalse(sourceExists("app/applet/src/supabaseClient.js"))
    }

    private fun sourceExists(path: String): Boolean =
        listOf(File(path.removePrefix("app/")), File(path)).any(File::exists)
}
