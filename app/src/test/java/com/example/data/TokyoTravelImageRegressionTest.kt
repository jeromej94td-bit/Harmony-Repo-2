package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TokyoTravelImageRegressionTest {

    @Test
    fun `Tokyo Japan keeps short label and installs a real WebP image`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = DriveTotAssetInstaller.install(context)

        val path = result["Tokyo, Japan"]
        assertNotNull("Tokyo, Japan must resolve to the bundled Drive image", path)

        val image = File(path!!)
        assertEquals("travel_tokyo.webp", image.name)
        assertTrue("Tokyo image must be a non-placeholder image", image.isFile && image.length() > 1_024L)

        val header = image.inputStream().use { input ->
            ByteArray(12).also { bytes ->
                val read = input.read(bytes)
                assertEquals(12, read)
            }
        }
        assertEquals("RIFF", String(header, 0, 4, Charsets.US_ASCII))
        assertEquals("WEBP", String(header, 8, 4, Charsets.US_ASCII))
    }
}
