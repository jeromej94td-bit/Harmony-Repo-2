package com.example.ui.components

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class DriveTotRebuildContractTest {

    @Test
    fun `drink pack uses the seven repaired pairs and bundled Drive artwork`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "getraenke" }
        val expected = listOf(
            "Cappuccino" to "Matcha-Latte",
            "Heiße Schokolade" to "Eistee",
            "Minzlimonade" to "Fruchtpunsch",
            "Bier" to "Rote-Bete-Saft",
            "Cola" to "Orangenlimonade",
            "Orangensaft" to "Apfelsaft",
            "Kaffee" to "Tee"
        )

        assertEquals(expected, pack.pairs)
        expected.flatMap { listOf(it.first, it.second) }.forEach { option ->
            assertNotNull(
                "Drink choice '$option' must resolve to bundled Drive artwork",
                TotImageProvider.getBundledImageResId(option)
            )
        }
        assertNotNull("Cola must use its local drawable", TotImageProvider.getBundledImageResId("Cola"))
        assertNotNull(
            "Orangenlimonade must use its local drawable",
            TotImageProvider.getBundledImageResId("Orangenlimonade")
        )
    }

    @Test
    fun `dream house contains only the ten Drive backed pairs`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "traumhaus" }
        val expected = listOf(
            "Altbau mit Charme" to "Neubau mit Smart Home",
            "Offene Wohnküche" to "Separate Küche",
            "Stadtvilla" to "Landhaus",
            "Glasfassade" to "Natursteinfassade",
            "Penthouse mit Ausblick" to "Haus am See",
            "Minimalistisches Interieur" to "Landhausstil",
            "Bibliothek" to "Heimkino",
            "Innenpool" to "Wellnessbad",
            "Große Fensterfront" to "Privater Innenhof",
            "Tiny House" to "Mehrgenerationenhaus"
        )

        assertEquals(expected, pack.pairs)
        expected.flatMap { listOf(it.first, it.second) }.forEach { option ->
            assertNotNull(
                "Dream-house choice '$option' must not fall back to a remote URL",
                TotImageProvider.getBundledImageResId(option)
            )
        }
        assertFalse(("Prasselnder Kamin" to "Fußbodenheizung") in pack.pairs)
        assertFalse(("Großer Garten" to "Sonnige Dachterrasse") in pack.pairs)
    }

    @Test
    fun `outdoor dream house contains only the nine unique Drive backed pairs`() {
        val pack = GeneratedContentRegistry.PACKS.single { it.id == "aussen" }
        val expected = listOf(
            "Großer Außenpool" to "Outdoor-Whirlpool",
            "Eigenes Gemüsebeet" to "Bunte Blumenwiese",
            "Entspannte Hängematte" to "Stilvolles Outdoor-Sofa",
            "Infinity-Pool" to "Naturteich",
            "Outdoor-Küche" to "Pizzaofen",
            "Pergola mit Lounge" to "Wintergarten",
            "Dachgarten mit Lounge" to "Mediterraner Innenhof",
            "Feuerstelle" to "Außenkamin",
            "Gewächshaus" to "Saunahaus"
        )

        assertEquals(expected, pack.pairs)
        expected.flatMap { listOf(it.first, it.second) }.forEach { option ->
            assertNotNull(
                "Outdoor choice '$option' must not fall back to a remote URL",
                TotImageProvider.getBundledImageResId(option)
            )
        }
        assertFalse(("Moderne Grillstation" to "Gemütliche Feuerstelle") in pack.pairs)
        assertFalse(("Kräuterbeet" to "Obstgarten") in pack.pairs)
        assertFalse(("Spielbereich für Kinder" to "Sportplatz") in pack.pairs)
    }
}