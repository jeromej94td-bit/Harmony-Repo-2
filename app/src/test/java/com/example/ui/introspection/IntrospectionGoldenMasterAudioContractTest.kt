package com.example.ui.introspection

import java.io.File
import java.security.MessageDigest
import org.junit.Assert.assertEquals
import org.junit.Test

class IntrospectionGoldenMasterAudioContractTest {
    private data class ExpectedAsset(
        val name: String,
        val size: Long,
        val gitBlobSha1: String
    )

    private val expectedAssets = listOf(
        ExpectedAsset("introspection_animal.mp3", 2_320_970L, "193b8b9701f50c5bba0248211cb209d79161a871"),
        ExpectedAsset("introspection_color.mp3", 1_949_822L, "2325462013787473ed31d2c7871d7e785e3ebefd"),
        ExpectedAsset("introspection_reveal.mp3", 2_159_220L, "bbf652cf2f7fc708774015005643bb6a0fa010ba"),
        ExpectedAsset("introspection_water.mp3", 2_923_458L, "30253797743f4e9c9dc40975a9efde9752e9d5c6")
    )

    @Test
    fun `introspection narrator assets stay byte-identical to working old main`() {
        val moduleRoot = sequenceOf(File("."), File(".."))
            .first { File(it, "src/main/res/raw").exists() }
        val rawDir = File(moduleRoot, "src/main/res/raw")

        expectedAssets.forEach { expected ->
            val asset = File(rawDir, expected.name)
            assertEquals("${expected.name} size changed", expected.size, asset.length())
            assertEquals(
                "${expected.name} no longer matches the working Harmony-App/main blob",
                expected.gitBlobSha1,
                gitBlobSha1(asset)
            )
        }
    }

    private fun gitBlobSha1(file: File): String {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update("blob ${file.length()}\u0000".toByteArray(Charsets.UTF_8))
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
