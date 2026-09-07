package com.example

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CiSigningIdentityContractTest {
    @Test
    fun `installable main apks cannot silently rotate signing identity`() {
        val workflow = source(".github/workflows/android-apk-build.yml")

        assertTrue(workflow.contains("HARMONY_CI_DEBUG_KEYSTORE_B64"))
        assertTrue(workflow.contains("63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F"))
        assertTrue(workflow.contains("Restore stable Harmony CI signing key"))
        assertTrue(workflow.contains("Verify installable APK signing certificate"))
        assertTrue(workflow.contains("APKSIGNER_OUTPUT="))
        assertTrue(workflow.contains("2>&1"))
        assertTrue(workflow.contains("certificate SHA-1 digest:"))
        assertFalse(workflow.contains("sed -n 's/^Signer #1 certificate SHA-1 digest: //p'"))
        assertTrue(workflow.contains("if: github.event_name != 'pull_request'"))
        assertFalse(workflow.contains("if [ ! -f debug.keystore ]"))
        assertFalse(workflow.contains("- name: Create debug keystore"))

        val keyGenerationCount = Regex("keytool -genkeypair").findAll(workflow).count()
        assertEquals("Only the explicitly PR-only build may generate an ephemeral key", 1, keyGenerationCount)

        val prKeyStep = workflow.indexOf("Create ephemeral PR-only debug keystore")
        val keyGeneration = workflow.indexOf("keytool -genkeypair")
        val buildStep = workflow.indexOf("Build Harmony debug APK")
        assertTrue(prKeyStep >= 0)
        assertTrue(keyGeneration > prKeyStep)
        assertTrue(buildStep > keyGeneration)
    }

    private fun source(path: String): String =
        listOf(File(path), File("../$path")).first(File::exists).readText()
}
