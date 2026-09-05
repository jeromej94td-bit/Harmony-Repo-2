package com.example

import android.util.Base64
import com.example.data.DriveTotAssetInstaller
import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class OutdoorAreaImageContractTest {

    @Test
    fun outdoorAreaUsesShortLabelsThatMatchBundledImageKeys() {
        val pack = GeneratedContentRegistry.PACKS.first { it.id == "aussen" }
        assertEquals("Außenpool" to "Whirlpool", pack.pairs.first())

        val mapField = DriveTotAssetInstaller::class.java.getDeclaredField("driveOptionToFile")
        mapField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val optionFiles = mapField.get(DriveTotAssetInstaller) as Map<String, String>

        assertEquals("outdoor_aussenpool.webp", optionFiles["Außenpool"])
        assertEquals("outdoor_whirlpool.webp", optionFiles["Whirlpool"])
    }

    @Test
    fun exactDriveOutdoorImagesAreBundledAsValidWebpPayloads() {
        listOf(
            "outdoor_aussenpool.b64",
            "outdoor_whirlpool.b64"
        ).forEach { assetName ->
            val asset = sequenceOf(
                File("app/src/main/assets/$assetName"),
                File("src/main/assets/$assetName")
            ).firstOrNull { it.isFile }

            assertTrue("Missing bundled asset $assetName", asset != null)
            val bytes = Base64.decode(asset!!.readText(), Base64.DEFAULT)
            assertTrue("Bundled asset $assetName is unexpectedly small", bytes.size > 50_000)
            assertEquals("RIFF", bytes.copyOfRange(0, 4).toString(Charsets.US_ASCII))
            assertEquals("WEBP", bytes.copyOfRange(8, 12).toString(Charsets.US_ASCII))
        }
    }
}
