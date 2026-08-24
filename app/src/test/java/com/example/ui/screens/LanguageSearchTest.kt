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

    @Test
    fun exactLocaleCodeWinsOverIncidentalTextMatches() {
        assertEquals(listOf(AppLanguage.POLISH), filterLanguages(languages, "pl"))
        assertEquals(listOf(AppLanguage.ITALIAN), filterLanguages(languages, "IT"))
        assertEquals(listOf(AppLanguage.PORTUGUESE_BRAZIL), filterLanguages(languages, "pt-BR"))
    }

    @Test
    fun searchMatchesCommonGermanLanguageNames() {
        assertEquals(listOf(AppLanguage.POLISH), filterLanguages(languages, "polnisch"))
        assertEquals(listOf(AppLanguage.ITALIAN), filterLanguages(languages, "italienisch"))
    }

    @Test
    fun searchIgnoresDiacritics() {
        assertEquals(listOf(AppLanguage.FRENCH), filterLanguages(languages, "francais"))
        assertTrue(AppLanguage.PORTUGUESE_BRAZIL in filterLanguages(languages, "portugues brasil"))
    }
}
