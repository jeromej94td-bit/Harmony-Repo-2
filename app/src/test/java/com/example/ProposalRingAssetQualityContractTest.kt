package com.example

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalRingAssetQualityContractTest {

    @Test
    fun `centerpiece proposal ring is a real packaged webp instead of transparent placeholder`() {
        val webp = repoFile("app/src/main/res/drawable/ring_grosser_stein.webp")
        val placeholder = repoFile("app/src/main/res/drawable/ring_grosser_stein.xml")

        assertTrue("ring_grosser_stein.webp is missing", webp.isFile)
        assertTrue("centerpiece ring asset is suspiciously tiny", webp.length() > 1_000L)
        assertFalse("transparent XML placeholder must be removed", placeholder.exists())

        val header = webp.readBytes().take(12).toByteArray()
        assertEquals("RIFF", header.copyOfRange(0, 4).toString(Charsets.US_ASCII))
        assertEquals("WEBP", header.copyOfRange(8, 12).toString(Charsets.US_ASCII))
    }

    private fun repoFile(path: String): File {
        val candidates = listOf(File(path), File(path.removePrefix("app/")))
        return candidates.firstOrNull { it.exists() } ?: candidates.first()
    }
}
