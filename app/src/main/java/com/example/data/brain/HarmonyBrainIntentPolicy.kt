package com.example.data.brain

import java.util.Locale

/** Pure routing policy so local/current-information questions never fall into offline fantasy replies. */
object HarmonyBrainIntentPolicy {
    private val liveTerms = listOf(
        "restaurant", "restaurants", "essen in", "food in", "sushi in", "pizza in",
        "hotel", "hotels", "übernachten", "uebernachten", "stay in",
        "café", "cafe", "cafes", "kaffee in", "bar", "bars",
        "aktivität", "aktivitäten", "aktivitaet", "aktivitaeten", "activity", "activities",
        "ausflug", "ausflüge", "ausfluege", "things to do", "unternehmen in",
        "museum", "museen", "sehenswürdigkeit", "sehenswürdigkeiten", "sehenswuerdigkeit",
        "attraction", "attractions", "kino", "cinema", "veranstaltung", "veranstaltungen",
        "event", "events", "öffnungszeiten", "oeffnungszeiten", "opening hours",
        "adresse", "address", "google maps", "maps", "in der nähe", "in meiner nähe",
        "in der naehe", "near me", "nearby", "heute in", "today in", "tonight in",
        "jetzt in", "aktuell in", "wo können wir", "wo koennen wir", "wo kann man"
    )

    fun needsLiveSearch(query: String): Boolean {
        val normalized = query.lowercase(Locale.GERMAN).trim()
        if (normalized.isBlank()) return false
        return liveTerms.any(normalized::contains)
    }
}
