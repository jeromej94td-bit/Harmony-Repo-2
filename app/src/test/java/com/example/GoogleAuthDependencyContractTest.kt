package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAuthDependencyContractTest {
    @Test
    fun `uses current stable credential manager components`() {
        val versions = rootSource("gradle/libs.versions.toml")
        assertTrue(versions.contains("credentials = \"1.6.0\""))
        assertTrue(versions.contains("googleid = \"1.2.0\""))
    }

    private fun rootSource(path: String): String =
        listOf(File(path), File("../$path"))
            .first(File::exists)
            .readText()
}
