package com.example.data

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GeneratedHarmonyIceCreamExpansionTest {

    @Test
    fun keepsSevenExistingPairsAndAppendsFourNewPairs() {
        val pack = GeneratedHarmonyIceCreamExpansion.PACKS.single()

        assertEquals("custom_gourmet_eissorten", pack.id)
        assertEquals(11, pack.pairs.size)
        assertEquals("Vanille" to "Schokolade", pack.pairs.first())
        assertEquals(
            "Triple Chocolate Brownie Fudge – Premium Pint" to
                "Triple Chocolate Brownie Fudge im Dessertglas",
            pack.pairs[7]
        )
        assertEquals(
            "Mocha Tiramisu Eis im Dessertglas" to
                "Mocha Tiramisu Eis – zweite Variante",
            pack.pairs.last()
        )
    }

    @Test
    fun exposesArtworkForEveryNewChoice() {
        val newChoices = GeneratedHarmonyIceCreamExpansion.PACKS.single()
            .pairs
            .drop(7)
            .flatMap { listOf(it.first, it.second) }

        assertEquals(8, newChoices.size)
        assertEquals(newChoices.toSet(), GeneratedHarmonyIceCreamExpansion.IMAGES.keys)
        assertTrue(GeneratedHarmonyIceCreamExpansion.IMAGES.values.all { it.startsWith("@drawable/ice_") })
    }

    @Test
    fun bundledDrawableSourceCanBeInstalledIntoGeneratedImageStore() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val key = "Triple Chocolate Brownie Fudge – Premium Pint"
        DevAssetStore.deleteImage(context, key)

        val path = DevAssetStore.writeBase64(
            context,
            key,
            GeneratedHarmonyIceCreamExpansion.IMAGES.getValue(key)
        )

        assertNotNull(path)
        assertTrue(DevAssetStore.hasImage(context, key))
        assertNotNull(BitmapFactory.decodeFile(path))
    }
}
