package com.example.ai

object HarmonyAiIntentRouter {
    private val foodTerms = listOf(
        "restaurant", "essen", "sushi", "burger", "pizza", "ramen", "café", "cafe", "bar", "frühstück",
        "ristorante", "mangiare", "sushi", "cibo", "restauracja", "jedzenie", "restaurante", "comer",
        "food", "dinner", "lunch", "breakfast"
    )

    private val localTerms = listOf(
        "in der nähe", "bei mir", "umgebung", "hier in", "wo können wir", "wo kann man", "tiergarten", "date-ort",
        "near me", "nearby", "around me", "where can we go", "in my area", "close to me",
        "vicino a me", "nelle vicinanze", "dove possiamo", "w pobliżu", "blisko mnie", "dónde podemos", "cerca de mí",
        "museum", "park", "kino", "cinema", "theater", "sehenswürdigkeit", "attraktion", "activity", "aktivität"
    )

    private val entertainmentTerms = listOf(
        "film", "movie", "serie", "series", "anime", "stream", "netflix", "disney", "prime video", "hbo", "pokemon",
        "pokémon", "game of thrones", "watch", "anschauen", "schauen", "guardare", "oglądać"
    )

    private val currentTerms = listOf(
        "aktuell", "heute", "jetzt", "neu", "neue", "neueste", "dieses wochenende", "diese woche", "gerade",
        "current", "today", "now", "new", "latest", "this weekend", "this week", "currently",
        "oggi", "adesso", "nuovo", "nuova", "ultim", "dzisiaj", "teraz", "nowy", "najnowsz"
    )

    private val eventTerms = listOf(
        "event", "veranstaltung", "konzert", "festival", "ausstellung", "eventi", "concerto", "wydarzenie", "koncert"
    )

    private val travelTerms = listOf(
        "reise", "urlaub", "ausflug", "trip", "travel", "vacation", "hotel", "reiseziel", "viaggio", "vacanza", "podróż"
    )

    private val relationshipTerms = listOf(
        "beziehung", "streit", "partner", "partnerin", "kommunikation", "eifersucht", "relationship", "argument",
        "couple", "relazione", "coppia", "związek", "para"
    )

    private val userLocationTerms = listOf(
        "bei mir", "in meiner nähe", "meine nähe", "um mich", "hier in der nähe", "near me", "nearby", "around me",
        "my area", "close to me", "vicino a me", "qui vicino", "w pobliżu mnie", "blisko mnie", "cerca de mí"
    )

    private val explicitLocationPattern = Regex(
        pattern = "\\b(in|bei|at|around|near|a|à|en|w)\\s+[\\p{L}][\\p{L}\\p{M}'’-]{2,}",
        option = RegexOption.IGNORE_CASE
    )

    fun route(query: String): HarmonyAiRoute {
        val q = query.lowercase()
        val isFood = foodTerms.any(q::contains)
        val isLocal = isFood || localTerms.any(q::contains)
        val isEntertainment = entertainmentTerms.any(q::contains)
        val isCurrent = currentTerms.any(q::contains)
        val isEvent = eventTerms.any(q::contains)
        val isTravel = travelTerms.any(q::contains)
        val isRelationship = relationshipTerms.any(q::contains)
        val explicitlyAsksForOwnLocation = userLocationTerms.any(q::contains)
        val hasExplicitPlace = !explicitlyAsksForOwnLocation && explicitLocationPattern.containsMatchIn(query)
        val needsUserLocation = isLocal && !hasExplicitPlace

        val intent = when {
            isEvent -> HarmonyAiIntent.EVENT_SEARCH
            isFood -> HarmonyAiIntent.FOOD
            isLocal -> HarmonyAiIntent.LOCAL_DISCOVERY
            isTravel -> HarmonyAiIntent.TRAVEL
            isEntertainment -> HarmonyAiIntent.ENTERTAINMENT
            isRelationship -> HarmonyAiIntent.RELATIONSHIP
            isCurrent -> HarmonyAiIntent.CURRENT_INFO
            else -> HarmonyAiIntent.GENERAL
        }

        val grounding = when {
            isLocal && (isCurrent || isEvent) -> HarmonyGroundingMode.SEARCH_AND_MAPS
            isLocal -> HarmonyGroundingMode.GOOGLE_MAPS
            isEvent -> HarmonyGroundingMode.GOOGLE_SEARCH
            isEntertainment && isCurrent -> HarmonyGroundingMode.GOOGLE_SEARCH
            isTravel && isCurrent -> HarmonyGroundingMode.GOOGLE_SEARCH
            intent == HarmonyAiIntent.CURRENT_INFO -> HarmonyGroundingMode.GOOGLE_SEARCH
            else -> HarmonyGroundingMode.NONE
        }

        return HarmonyAiRoute(intent, grounding, needsUserLocation)
    }

    fun needsLocationPermission(query: String): Boolean = route(query).needsUserLocation

    fun allowsSensitiveContext(query: String): Boolean {
        val q = query.lowercase()
        return listOf("sex", "intim", "nähe", "sexual", "intimacy", "intimità", "seks", "intym").any(q::contains)
    }
}
