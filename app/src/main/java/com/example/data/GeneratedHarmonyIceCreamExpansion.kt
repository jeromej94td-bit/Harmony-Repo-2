package com.example.data

/**
 * Additive Erweiterung des bestehenden Gourmet-Eis-Spiels.
 *
 * Die sieben bisherigen Paare bleiben unverändert und werden um vier neue,
 * zusammengehörige Bildpaare aus dem kuratierten Eiscreme-Ordner ergänzt.
 */
object GeneratedHarmonyIceCreamExpansion {
    const val VERSION: Long = 1789248000000L

    val PACKS: List<GenPack> = listOf(
        GenPack(
            id = "custom_gourmet_eissorten",
            title = "Gourmet Eis-Sorten",
            cat = "tot",
            topic = "unterhaltung",
            type = "tot",
            tags = listOf("dasoderdas", "unterhaltung"),
            pairs = listOf(
                // Bestehende sieben Paare – Reihenfolge bewusst unverändert.
                "Vanille" to "Schokolade",
                "Erdbeere" to "Zitrone",
                "Stracciatella" to "Pistazie",
                "Mango Sorbet" to "Himbeere",
                "Salted Caramel" to "Cookie Dough",
                "Hazelnut" to "White Chocolate",
                "Walnuss" to "Banane",

                // Neue Paare – ausschließlich hinten angehängt.
                "Triple Chocolate Brownie Fudge – Premium Pint" to "Triple Chocolate Brownie Fudge im Dessertglas",
                "Peanut Butter Chocolate Crunch im Dessertglas" to "Peanut Butter Chocolate Crunch – zweite Variante",
                "Mint Chocolate Chip im Dessertglas" to "Mint Chocolate Chip – zweite Variante",
                "Mocha Tiramisu Eis im Dessertglas" to "Mocha Tiramisu Eis – zweite Variante"
            ),
            questions = emptyList()
        )
    )
}
