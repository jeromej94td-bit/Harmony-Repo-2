package com.example.data

import com.example.R
import com.example.data.model.HarmonyPacksData
import com.example.data.model.TravelDestinationCatalog
import com.example.ui.components.TotImageProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TravelDestinationCatalogTest {
    @Test
    fun `Unsere Reiseziele is the single curated 11 round pack`() {
        val pack = HarmonyPacksData.DEFAULT_PACKS.single { it.id == TravelDestinationCatalog.PACK_ID }

        assertEquals("Unsere Reiseziele", pack.title)
        assertEquals(11, pack.pairs.size)
        assertEquals(TravelDestinationCatalog.displayPairs, pack.pairs)
        assertFalse(GeneratedHarmonyContent.PACKS.any { it.id == TravelDestinationCatalog.PACK_ID })
    }

    @Test
    fun `round five is Tokyo versus Dubai by stable destination id`() {
        val round = TravelDestinationCatalog.rounds[4]

        assertEquals("tokyo", round.leftId)
        assertEquals("dubai", round.rightId)
        assertEquals("travel:tokyo", TravelDestinationCatalog.assetKeyForDestinationId(round.leftId))
        assertEquals("travel:dubai", TravelDestinationCatalog.assetKeyForDestinationId(round.rightId))
    }

    @Test
    fun `image lookup uses stable destination key not translated label`() {
        val stableKey = TravelDestinationCatalog.assetKeyForDestinationId("tokyo")
        val germanLabel = "Tokyo, Japan"
        val translatedLabel = "Tokyo, Giappone"

        assertNotEquals(stableKey, germanLabel)
        assertNotEquals(stableKey, translatedLabel)
        assertEquals(
            R.drawable.travel_tokyo,
            TotImageProvider.getImageUrl(assetKey = stableKey, legacyAssetKey = translatedLabel)
        )
    }
}
