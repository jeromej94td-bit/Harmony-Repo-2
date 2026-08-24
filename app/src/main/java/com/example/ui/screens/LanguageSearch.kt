package com.example.ui.screens

import com.example.ui.AppLanguage
import java.text.Normalizer
import java.util.Locale

internal fun filterLanguages(
    languages: List<AppLanguage>,
    query: String
): List<AppLanguage> {
    val normalizedQuery = normalizeLanguageSearchTerm(query)
    if (normalizedQuery.isBlank()) return languages

    return languages.filter { language ->
        languageSearchTerms(language).any { term ->
            normalizeLanguageSearchTerm(term).contains(normalizedQuery)
        }
    }
}

private fun languageSearchTerms(language: AppLanguage): List<String> = buildList {
    add(language.nativeName)
    add(language.englishName)
    add(language.code)
    add(language.name)
    addAll(germanLanguageSearchAliases[language].orEmpty())
}

private fun normalizeLanguageSearchTerm(value: String): String =
    Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.ROOT)
        .replace("[^\\p{L}\\p{N}]+".toRegex(), " ")
        .trim()

private val germanLanguageSearchAliases: Map<AppLanguage, List<String>> = mapOf(
    AppLanguage.GERMAN to listOf("Deutsch"),
    AppLanguage.ENGLISH to listOf("Englisch"),
    AppLanguage.ITALIAN to listOf("Italienisch"),
    AppLanguage.FRENCH to listOf("Französisch", "Franzoesisch"),
    AppLanguage.JAPANESE to listOf("Japanisch"),
    AppLanguage.POLISH to listOf("Polnisch"),
    AppLanguage.SPANISH_LATIN_AMERICA to listOf("Spanisch Lateinamerika", "Lateinamerikanisches Spanisch"),
    AppLanguage.SPANISH_SPAIN to listOf("Spanisch Spanien", "Spanisch"),
    AppLanguage.PORTUGUESE_BRAZIL to listOf("Portugiesisch Brasilien", "Brasilianisches Portugiesisch"),
    AppLanguage.PORTUGUESE_PORTUGAL to listOf("Portugiesisch Portugal", "Europäisches Portugiesisch"),
    AppLanguage.DANISH to listOf("Dänisch", "Daenisch"),
    AppLanguage.NORWEGIAN to listOf("Norwegisch"),
    AppLanguage.DUTCH to listOf("Niederländisch", "Niederlaendisch"),
    AppLanguage.SWEDISH to listOf("Schwedisch"),
    AppLanguage.ICELANDIC to listOf("Isländisch", "Islaendisch"),
    AppLanguage.KOREAN to listOf("Koreanisch"),
    AppLanguage.CHINESE_SIMPLIFIED to listOf("Chinesisch vereinfacht", "Vereinfachtes Chinesisch"),
    AppLanguage.CHINESE_TRADITIONAL to listOf("Chinesisch traditionell", "Traditionelles Chinesisch"),
    AppLanguage.HUNGARIAN to listOf("Ungarisch"),
    AppLanguage.ROMANIAN to listOf("Rumänisch", "Rumaenisch"),
    AppLanguage.BULGARIAN to listOf("Bulgarisch"),
    AppLanguage.UKRAINIAN to listOf("Ukrainisch"),
    AppLanguage.RUSSIAN to listOf("Russisch"),
    AppLanguage.GREEK to listOf("Griechisch"),
    AppLanguage.TURKISH to listOf("Türkisch", "Tuerkisch"),
    AppLanguage.ARABIC to listOf("Arabisch"),
    AppLanguage.HEBREW to listOf("Hebräisch", "Hebraeisch"),
    AppLanguage.PERSIAN to listOf("Persisch"),
    AppLanguage.HINDI to listOf("Hindi"),
    AppLanguage.BENGALI to listOf("Bengalisch"),
    AppLanguage.URDU to listOf("Urdu"),
    AppLanguage.TAMIL to listOf("Tamil"),
    AppLanguage.TELUGU to listOf("Telugu"),
    AppLanguage.MARATHI to listOf("Marathi"),
    AppLanguage.GUJARATI to listOf("Gujarati"),
    AppLanguage.KANNADA to listOf("Kannada"),
    AppLanguage.MALAYALAM to listOf("Malayalam"),
    AppLanguage.THAI to listOf("Thai", "Thailändisch", "Thailaendisch"),
    AppLanguage.VIETNAMESE to listOf("Vietnamesisch"),
    AppLanguage.INDONESIAN to listOf("Indonesisch"),
    AppLanguage.MALAY to listOf("Malaiisch", "Malaysisch"),
    AppLanguage.FILIPINO to listOf("Filipino", "Philippinisch"),
    AppLanguage.BURMESE to listOf("Burmesisch", "Birmanisch"),
    AppLanguage.KHMER to listOf("Khmer"),
    AppLanguage.LAO to listOf("Laotisch", "Lao"),
    AppLanguage.SWAHILI to listOf("Swahili"),
    AppLanguage.AFRIKAANS to listOf("Afrikaans"),
    AppLanguage.AMHARIC to listOf("Amharisch"),
    AppLanguage.YORUBA to listOf("Yoruba"),
    AppLanguage.IGBO to listOf("Igbo"),
    AppLanguage.HAUSA to listOf("Hausa"),
    AppLanguage.ZULU to listOf("Zulu"),
    AppLanguage.XHOSA to listOf("Xhosa"),
    AppLanguage.SOMALI to listOf("Somali"),
    AppLanguage.ESTONIAN to listOf("Estnisch"),
    AppLanguage.LATVIAN to listOf("Lettisch"),
    AppLanguage.LITHUANIAN to listOf("Litauisch"),
    AppLanguage.SLOVENIAN to listOf("Slowenisch"),
    AppLanguage.SERBIAN to listOf("Serbisch")
)
