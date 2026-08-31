package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplicationIdContractTest {

    @Test
    fun `release package keeps stable Harmony application id`() {
        val gradle = source("app/build.gradle.kts")

        assertTrue(gradle.contains("namespace = \"com.example\""))
        assertTrue(gradle.contains("applicationId = \"com.aistudio.harmony.couples.xqvz\""))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
