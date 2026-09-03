package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AutumnEveningPackContractTest {

    @Test
    fun `autumn evening ships the approved six round experience`() {
        val pack = HarmonyPacksData.DEFAULT_PACKS.single { it.id == "herbstabend" }

        assertEquals("Unser Herbstabend", pack.title)
        assertEquals("lieber", pack.cat)
        assertEquals("hobbys", pack.topic)
        assertEquals("quiz", pack.type)
        assertEquals("🍂", pack.emoji)
        assertEquals(listOf("herbst", "cozy", "fürpaare", "bildauswahl"), pack.tags)
        assertEquals(
            listOf(
                "Welche Geschichte zieht dich in den Herbst?" to
                    listOf("Mystery", "Thriller", "Dark Academia", "Cozy Fantasy"),
                "Was wärmt deinen Abend?" to
                    listOf("Chai Latte", "Heiße Schokolade", "Apfel-Zimt-Tee", "Pumpkin Spice"),
                "Welcher Snack gehört dazu?" to
                    listOf("Zimtschnecke", "Chocolate Cookie", "Kürbismuffin", "Apfelkuchen"),
                "Wo wird es richtig gemütlich?" to
                    listOf("Fensternest", "Kaminsofa", "Deckenhöhle", "Bibliotheksecke"),
                "Welcher Klang begleitet euch?" to
                    listOf("Regen am Fenster", "Kaminfeuer", "Herbstwind", "Völlige Ruhe"),
                "Welcher Duft macht es vollkommen?" to
                    listOf("Vanille & Holz", "Herbstlaub", "Kürbisgewürz", "Bratapfel")
            ),
            pack.questions.map { it.q to it.options }
        )
    }
}
