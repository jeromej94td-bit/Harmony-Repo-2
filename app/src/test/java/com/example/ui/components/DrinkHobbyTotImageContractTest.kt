package com.example.ui.components

import com.example.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DrinkHobbyTotImageContractTest {

    @Test
    fun `drink this or that uses bundled artwork for every choice`() {
        val drinkOptions = listOf(
            "Cappuccino", "Matcha-Latte", "Heiße Schokolade", "Eistee",
            "Minzlimonade", "Fruchtpunsch", "Bier", "Rote-Bete-Saft",
            "Coca-Cola", "Fanta", "Orangensaft", "Apfelsaft", "Kaffee", "Tee"
        )

        drinkOptions.forEach { option ->
            assertNotNull(
                "Drink choice '$option' must use bundled artwork instead of a remote placeholder",
                TotImageProvider.getBundledImageResId(option)
            )
        }
    }

    @Test
    fun `hot chocolate resolves to the dedicated cocoa drawable`() {
        assertEquals(
            "Heiße Schokolade must not be captured by the generic chocolate ice-cream heuristic",
            R.drawable.tot_drink_hot_chocolate,
            TotImageProvider.getBundledImageResId("Heiße Schokolade")
        )
    }

    @Test
    fun `pack scoped image override wins and can be replaced independently`() {
        val packId = "getraenke"
        val option = "Heiße Schokolade"
        val scopedKey = TotImageProvider.totAssetKey(packId, option)
        val replacement = "file:///replacement/hot-cocoa.webp"

        try {
            TotImageProvider.setTotCustomImage(packId, option, replacement)
            assertEquals(
                replacement,
                TotImageProvider.getImageUrl(assetKey = scopedKey, legacyAssetKey = option)
            )
        } finally {
            TotImageProvider.removeTotCustomImage(packId, option)
        }

        assertEquals(
            R.drawable.tot_drink_hot_chocolate,
            TotImageProvider.getBundledImageResId(option)
        )
    }

    @Test
    fun `drive backed hobby choices use bundled artwork`() {
        val hobbyOptions = listOf(
            "Wandern", "Töpfern", "Klavier spielen", "Malen", "Zeichnen",
            "Badminton", "Mountainbike", "Bowling", "Holzwerken", "Gitarre spielen",
            "Tennis", "Brettspiele", "Darts"
        )

        hobbyOptions.forEach { option ->
            assertNotNull(
                "Hobby choice '$option' must use bundled artwork instead of a remote placeholder",
                TotImageProvider.getBundledImageResId(option)
            )
        }
    }
}
