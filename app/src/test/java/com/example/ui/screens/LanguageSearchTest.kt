package com.example.ui.screens

import com.example.ui.AppLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageSearchTest {

    private val languages = AppLanguage.entries.toList()

    @Test
    fun blankQueryKeepsAllLanguagesVisible() {
        assertEquals(languages, filterLanguages(languages, "   "))
    }

    @Test
    fun searchMatchesNativeNameEnglishNameAndLocaleCodeIgnoringCase() {
        assertEquals(listOf(AppLanguage.POLISH), filterLanguages(languages, "polski"))
        assertEquals(listOf(AppLanguage.ITALIAN), filterLanguages(languages, "ITALIAN"))
        assertTrue(AppLanguage.PORTUGUESE_BRAZIL in filterLanguages(languages, "pt-br"))
    }
}
