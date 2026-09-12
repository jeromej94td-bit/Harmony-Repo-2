package com.example.ui.components

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
