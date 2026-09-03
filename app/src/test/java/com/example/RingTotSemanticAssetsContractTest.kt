package com.example

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RingTotSemanticAssetsContractTest {

    private val semanticRingIds = listOf(
        "ring_diamant",
        "ring_farbedelstein",
        "ring_gelbgold",
        "ring_weissgold",
        "ring_ovaler_diamant",
        "ring_runder_diamant"
    )

    @Test
    fun `semantic ring ids use real webp artwork instead of transparent xml placeholders`() {
        semanticRingIds.forEach { id ->
            val webp = projectFile("app/src/main/res/drawable/$id.webp")
            val xml = projectFile("app/src/main/res/drawable/$id.xml")

            assertTrue("Missing WebP for $id", webp.exists())
            assertFalse("Transparent XML placeholder still shadows $id", xml.exists())

            val bytes = webp.readBytes()
            assertTrue("WebP for $id is suspiciously small", bytes.size > 128)
            assertEquals("RIFF", bytes.copyOfRange(0, 4).toString(Charsets.US_ASCII))
            assertEquals("WEBP", bytes.copyOfRange(8, 12).toString(Charsets.US_ASCII))
        }
    }

    private fun projectFile(path: String): File =
        listOf(File(path.removePrefix("app/")), File(path)).firstOrNull(File::exists)
            ?: File(path)
}
