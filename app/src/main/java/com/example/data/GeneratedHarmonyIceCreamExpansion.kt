package com.example.data

/**
 * Additive Erweiterung des bestehenden Gourmet-Eis-Spiels.
 *
 * Die sieben bisherigen Paare bleiben unverändert und werden um vier neue,
 * zusammengehörige Bildpaare aus dem kuratierten Eiscreme-Ordner ergänzt.
 */
object GeneratedHarmonyIceCreamExpansion {
    const val VERSION: Long = 1789249000000L

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

    /**
     * Die Bilder liegen bereits als optimierte WebP-Ressourcen im APK. Der spezielle
     * @drawable/-Wert wird beim Generated-Content-Install in eine interne Arbeitskopie
     * überführt, sodass TotImageProvider dieselbe Pipeline wie bei anderen Bildern nutzt.
     */
    val IMAGES: Map<String, String> = mapOf(
        "Triple Chocolate Brownie Fudge – Premium Pint" to "@drawable/ice_triple_brownie_pint",
        "Triple Chocolate Brownie Fudge im Dessertglas" to "@drawable/ice_triple_brownie_glass",
        "Peanut Butter Chocolate Crunch im Dessertglas" to "@drawable/ice_peanut_butter_crunch_glass",
        "Peanut Butter Chocolate Crunch – zweite Variante" to "@drawable/ice_peanut_butter_crunch_alt",
        "Mint Chocolate Chip im Dessertglas" to "@drawable/ice_mint_choc_chip_glass",
        "Mint Chocolate Chip – zweite Variante" to "@drawable/ice_mint_choc_chip_alt",
        "Mocha Tiramisu Eis im Dessertglas" to "@drawable/ice_mocha_tiramisu_glass",
        "Mocha Tiramisu Eis – zweite Variante" to "@drawable/ice_mocha_tiramisu_alt"
    )
}
