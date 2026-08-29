package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoQuestionPolicyTest {

    @Test
    fun `photo routing follows stable question identity instead of position`() {
        assertEquals(
            PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO,
            PhotoQuestionPolicy.modeFor(
                "gespraechsanreger",
                5,
                "Was ist dein Lieblingsfoto von uns? 📸"
            )
        )
        assertEquals(
            PhotoQuestionMode.PHOTO_ONLY,
            PhotoQuestionPolicy.modeFor(
                "schnapp",
                0,
                "Welches gemeinsame Foto ist dein Lieblingsfoto?"
            )
        )
    }

    @Test
    fun `ordinary question at former photo index does not inherit gallery behavior`() {
        assertNull(
            PhotoQuestionPolicy.modeFor(
                "gespraechsanreger",
                1,
                "Was möchtest du, dass dein Partner öfter tut?"
            )
        )
        assertNull(
            PhotoQuestionPolicy.modeFor(
                "schnapp",
                1,
                "Was war dein schönster Moment mit mir bisher?"
            )
        )
    }

    @Test
    fun `legacy position fallback exists only when raw question is unavailable`() {
        assertEquals(
            PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO,
            PhotoQuestionPolicy.modeFor("gespraechsanreger", 1, null)
        )
        assertEquals(
            PhotoQuestionMode.PHOTO_ONLY,
            PhotoQuestionPolicy.modeFor("schnapp", 1, null)
        )
        assertNull(PhotoQuestionPolicy.modeFor("gespraechsanreger", 0, null))
    }

    @Test
    fun `does not infer gallery behavior from the word photo alone`() {
        assertEquals(
            PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO,
            PhotoQuestionPolicy.modeForQuestion("Was ist dein Lieblingsfoto von uns? 📸")
        )
        assertEquals(
            PhotoQuestionMode.PHOTO_ONLY,
            PhotoQuestionPolicy.modeForQuestion("Welches gemeinsame Foto ist dein Lieblingsfoto?")
        )
        assertNull(
            PhotoQuestionPolicy.modeForQuestion("Wer würde eher das peinlichste Foto des Abends posten?")
        )
    }

    @Test
    fun `optional photo presentation asks a category question with three meaningful choices`() {
        val presentation = PhotoQuestionPolicy.presentation(PhotoQuestionMode.CHOICE_WITH_OPTIONAL_PHOTO)

        assertEquals("Welche Art von gemeinsamen Fotos magst du am liebsten? 📸", presentation.question)
        assertEquals(
            listOf("Lustige Schnappschüsse", "Romantische Fotos", "Urlaubsfotos"),
            presentation.options
        )
    }

    @Test
    fun `photo only presentation has no artificial multiple choice answers`() {
        val presentation = PhotoQuestionPolicy.presentation(PhotoQuestionMode.PHOTO_ONLY)

        assertEquals("Welches gemeinsame Foto bedeutet dir besonders viel? 📸", presentation.question)
        assertTrue(presentation.options.isEmpty())
    }

    @Test
    fun `legacy choice values migrate to the new copy`() {
        assertEquals("Lustige Schnappschüsse", PhotoQuestionPolicy.normalizeLegacyChoice("Ein lustiges Bild"))
        assertEquals("Romantische Fotos", PhotoQuestionPolicy.normalizeLegacyChoice("Ein romantisches Bild"))
        assertEquals("Urlaubsfotos", PhotoQuestionPolicy.normalizeLegacyChoice("Aus dem Urlaub"))
        assertEquals("Andere Antwort", PhotoQuestionPolicy.normalizeLegacyChoice("Andere Antwort"))
    }

    @Test
    fun `photo storage key is stable distinct and filesystem safe`() {
        val first = PhotoQuestionPolicy.storageFileName("Was ist dein Lieblingsfoto von uns? 📸")
        val again = PhotoQuestionPolicy.storageFileName("Was ist dein Lieblingsfoto von uns? 📸")
        val other = PhotoQuestionPolicy.storageFileName("Welches gemeinsame Foto ist dein Lieblingsfoto?")

        assertEquals(first, again)
        assertNotEquals(first, other)
        assertTrue(first.matches(Regex("photo_[0-9a-f]{32}\\.img")))
    }
}
