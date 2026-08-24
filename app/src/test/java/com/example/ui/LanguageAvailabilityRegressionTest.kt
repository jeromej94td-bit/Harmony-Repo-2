package com.example.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageAvailabilityRegressionTest {

    @Test
    fun allRegisteredProductionLanguagesRemainSelectable() {
        val expectedCodes = setOf(
            "de", "en", "it", "fr", "ja", "pl", "es-419", "es-ES", "pt-BR", "pt-PT",
            "da", "no", "nl", "sv", "is", "ko", "zh-CN", "zh-TW",
            "hu", "ro", "bg", "uk", "ru", "el", "tr", "ar", "he", "fa", "hi", "bn",
            "ur", "ta", "te", "mr", "gu", "kn", "ml", "th", "vi", "id", "ms", "fil",
            "my", "km", "lo", "sw", "af", "am", "yo", "ig", "ha", "zu", "xh", "so",
            "et", "lv", "lt", "sl", "sr"
        )

        assertEquals(59, AppLanguage.entries.size)
        assertEquals(expectedCodes, AppLanguage.entries.map { it.code }.toSet())
        assertTrue(
            "A registered language must never silently disappear from the selector",
            AppLanguage.entries.all(TranslationCatalog::hasCompletePack)
        )
    }
}
