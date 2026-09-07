package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HarmonySigningContractTest {
    private val expectedSha1 =
        "63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F"

    @Test
    fun `installable workflow is pinned to installed Harmony certificate`() {
        val workflow = source(".github/workflows/android-apk-build.yml")
        assertTrue(workflow.contains("HARMONY_CI_SIGNING_SHA1: $expectedSha1"))
        assertFalse(workflow.contains("7F:F5:D5:66:BB:0F:6E:AB"))
    }

    @Test
    fun `local debug signing prefers stable user profile keystore`() {
        val gradle = source("app/build.gradle.kts")
        assertTrue(gradle.contains("HARMONY_DEBUG_KEYSTORE_PATH"))
        assertTrue(gradle.contains(".harmony-build-tools/signing/harmony-debug.keystore"))
    }

    private fun source(path: String): String =
        listOf(File(path), File("../$path"), File(path.removePrefix("app/")))
            .first(File::exists)
            .readText()
}
