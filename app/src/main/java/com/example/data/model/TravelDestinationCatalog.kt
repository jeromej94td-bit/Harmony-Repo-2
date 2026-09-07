package com.example.data.model

/**
 * Curated, translation-safe source of truth for "Unsere Reiseziele".
 * Destination ids and image asset keys never depend on localized display text.
 */
object TravelDestinationCatalog {
    const val PACK_ID = "reiseziele"
    const val TITLE = "Unsere Reiseziele"

    data class Destination(
        val id: String,
        val canonicalLabel: String
    ) {
        val assetKey: String get() = "travel:$id"
    }

    data class Round(
        val leftId: String,
        val rightId: String
    )

    private val destinations = listOf(
        Destination("paris", "Paris, Frankreich"),
        Destination("rome", "Rom, Italien"),
        Destination("bali", "Bali, Indonesien"),
        Destination("santorini", "Santorini, Griechenland"),
        Destination("london", "London, England"),
        Destination("new_york", "New York, USA"),
        Destination("maldives", "Malediven"),
        Destination("seychelles", "Seychellen"),
        Destination("tokyo", "Tokyo, Japan"),
        Destination("dubai", "Dubai, VAE"),
        Destination("venice", "Venedig, Italien"),
        Destination("amsterdam", "Amsterdam, Niederlande"),
        Destination("lapland", "Lappland, Finnland"),
        Destination("iceland", "Island"),
        Destination("miami", "Miami, USA"),
        Destination("bangkok", "Bangkok, Thailand"),
        Destination("chicago", "Chicago, USA"),
        Destination("barcelona", "Barcelona, Spanien"),
        Destination("lisbon", "Lissabon, Portugal"),
        Destination("copenhagen", "Kopenhagen, Dänemark"),
        Destination("prague", "Prag, Tschechien"),
        Destination("budapest", "Budapest, Ungarn")
    )

    private val byId = destinations.associateBy { it.id }
    private val byCanonicalLabel = destinations.associateBy { it.canonicalLabel }

    val rounds = listOf(
        Round("paris", "rome"),
        Round("bali", "santorini"),
        Round("london", "new_york"),
        Round("maldives", "seychelles"),
        Round("tokyo", "dubai"),
        Round("venice", "amsterdam"),
        Round("lapland", "iceland"),
        Round("miami", "bangkok"),
        Round("chicago", "barcelona"),
        Round("lisbon", "copenhagen"),
        Round("prague", "budapest")
    )

    val displayPairs: List<Pair<String, String>> = rounds.map { round ->
        labelFor(round.leftId) to labelFor(round.rightId)
    }

    fun labelFor(destinationId: String): String =
        requireNotNull(byId[destinationId]) { "Unknown travel destination id: $destinationId" }.canonicalLabel

    fun assetKeyForDestinationId(destinationId: String): String =
        requireNotNull(byId[destinationId]) { "Unknown travel destination id: $destinationId" }.assetKey

    fun assetKeyForCanonicalLabel(label: String): String? =
        byCanonicalLabel[label]?.assetKey

    fun assetKeyFor(packId: String, canonicalOptionLabel: String): String? =
        if (packId == PACK_ID) assetKeyForCanonicalLabel(canonicalOptionLabel) else null
}
