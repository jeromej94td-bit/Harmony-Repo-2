package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.R
import com.example.data.model.TravelDestinationCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TokyoTravelImageRegressionTest {

    @Test
    fun `Tokyo uses the compiled Drive WebP behind the stable destination key`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        assertEquals(
            "travel:tokyo",
            TravelDestinationCatalog.assetKeyForCanonicalLabel("Tokyo, Japan")
        )

        val bytes = context.resources.openRawResource(R.drawable.travel_tokyo).use { input ->
            input.readBytes()
        }
        assertTrue("Tokyo image must be a real non-placeholder image", bytes.size > 1_024)
        assertEquals("RIFF", String(bytes, 0, 4, Charsets.US_ASCII))
        assertEquals("WEBP", String(bytes, 8, 4, Charsets.US_ASCII))
    }
}
