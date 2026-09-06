package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DrivePlaceholderAssetsContractTest {

    @Test
    fun `drive backed tot cards no longer use loremflickr placeholders`() {
        val provider = source("app/src/main/java/com/example/ui/components/TotImageProvider.kt")

        assertFalse(provider.contains("loremflickr.com"))
    }

    @Test
    fun `drive backed drinks animals and hobbies use local drawable resources`() {
        val provider = source("app/src/main/java/com/example/ui/components/TotImageProvider.kt")
        val expectedResources = listOf(
            "drive_drink_cappuccino", "drive_drink_matcha_latte", "drive_drink_heisse_schokolade",
            "drive_drink_eistee", "drive_drink_minzlimonade", "drive_drink_fruchtpunsch",
            "drive_drink_bier", "drive_drink_rote_bete_saft", "drive_drink_coca_cola",
            "drive_drink_fanta", "drive_drink_orangensaft", "drive_drink_apfelsaft",
            "drive_drink_kaffee", "drive_drink_tee",
            "drive_animal_hund", "drive_animal_katze", "drive_animal_singvogel",
            "drive_animal_pinguin", "drive_animal_kaninchen", "drive_animal_otter",
            "drive_animal_roter_panda", "drive_animal_fuchs", "drive_animal_meerschweinchen",
            "drive_animal_giraffe", "drive_animal_loewe", "drive_animal_gorilla",
            "drive_animal_meeresschildkroete", "drive_animal_igel", "drive_animal_tiger",
            "drive_animal_wolf", "drive_animal_adler", "drive_animal_delfin",
            "drive_hobby_toepfern", "drive_hobby_klavier", "drive_hobby_malen",
            "drive_hobby_zeichnen", "drive_hobby_badminton", "drive_hobby_fahrrad",
            "drive_hobby_bowling", "drive_hobby_diy", "drive_hobby_gitarre",
            "drive_hobby_tennis", "drive_hobby_brettspiel", "drive_hobby_dart"
        )

        expectedResources.forEach { name ->
            assertTrue("missing local mapping for $name", provider.contains("R.drawable.$name"))
        }
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
