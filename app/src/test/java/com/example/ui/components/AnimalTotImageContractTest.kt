package com.example.ui.components

import org.junit.Assert.assertNotNull
import org.junit.Test

class AnimalTotImageContractTest {

    @Test
    fun `animal this or that uses bundled artwork for every choice`() {
        val animalOptions = listOf(
            "Hund", "Katze", "Singvogel", "Pinguin", "Kaninchen", "Otter",
            "Roter Panda", "Fuchs", "Meerschweinchen", "Giraffe", "Löwe", "Gorilla",
            "Meeresschildkröte", "Igel", "Tiger", "Wolf", "Adler", "Delfin"
        )

        animalOptions.forEach { option ->
            assertNotNull(
                "Animal choice '$option' must use bundled artwork instead of a remote placeholder",
                TotImageProvider.getBundledImageResId(option)
            )
        }
    }
}
