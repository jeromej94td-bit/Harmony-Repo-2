package com.example.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class AutumnEveningTranslationTest {

    @Test
    fun `all visible autumn evening copy has exact English translations`() {
        val expected = linkedMapOf(
            "Unser Herbstabend" to "Our Autumn Evening",
            "Welche Geschichte zieht dich in den Herbst?" to "Which story draws you into autumn?",
            "Mystery" to "Mystery",
            "Thriller" to "Thriller",
            "Dark Academia" to "Dark Academia",
            "Cozy Fantasy" to "Cozy Fantasy",
            "Was wärmt deinen Abend?" to "What warms your evening?",
            "Chai Latte" to "Chai Latte",
            "Heiße Schokolade" to "Hot Chocolate",
            "Apfel-Zimt-Tee" to "Apple Cinnamon Tea",
            "Pumpkin Spice" to "Pumpkin Spice",
            "Welcher Snack gehört dazu?" to "Which snack completes the evening?",
            "Zimtschnecke" to "Cinnamon Roll",
            "Chocolate Cookie" to "Chocolate Cookie",
            "Kürbismuffin" to "Pumpkin Muffin",
            "Apfelkuchen" to "Apple Pie",
            "Wo wird es richtig gemütlich?" to "Where does it get truly cozy?",
            "Fensternest" to "Window Nook",
            "Kaminsofa" to "Fireside Sofa",
            "Deckenhöhle" to "Blanket Fort",
            "Bibliotheksecke" to "Library Corner",
            "Welcher Klang begleitet euch?" to "Which sound sets the mood?",
            "Regen am Fenster" to "Rain on the Window",
            "Kaminfeuer" to "Crackling Fire",
            "Herbstwind" to "Autumn Wind",
            "Völlige Ruhe" to "Complete Silence",
            "Welcher Duft macht es vollkommen?" to "Which scent makes it complete?",
            "Vanille & Holz" to "Vanilla & Wood",
            "Herbstlaub" to "Autumn Leaves",
            "Kürbisgewürz" to "Pumpkin Spice",
            "Bratapfel" to "Baked Apple",
            "Wählt die Stimmung für eure Geschichte." to "Choose the mood for your story.",
            "Etwas Warmes für kalte Hände." to "Something warm for cold hands.",
            "Der süße Begleiter für euren Abend." to "A sweet companion for your evening.",
            "Findet euren gemütlichsten Rückzugsort." to "Find your coziest retreat.",
            "Welcher Klang macht den Moment komplett?" to "Which sound completes the moment?",
            "Der Duft, der noch lange bleibt." to "The scent that lingers long after."
        )

        expected.forEach { (german, english) ->
            assertEquals(
                "Missing or incorrect exact English translation for: $german",
                english,
                TranslationCatalog.exact(german, AppLanguage.ENGLISH)
            )
        }
    }
}
