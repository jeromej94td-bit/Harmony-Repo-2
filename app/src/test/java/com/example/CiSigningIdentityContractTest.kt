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
        assertTrue(workflow.contains("73:85:7C:7D:A2:C1:0A:29:79:14:6C:20:15:0C:AE:4E:7A:77:B3:92"))
        assertTrue(workflow.contains("Restore stable Harmony CI signing key"))
        assertTrue(workflow.contains("Verify installable APK signing certificate"))
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
