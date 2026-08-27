package com.example.data

import android.net.Uri
import android.util.Log
import com.example.data.model.BrainChatSuggestionItem
import com.example.data.model.BrainInterestEntity
import com.example.data.model.ProfileEntity
import com.example.util.GeminiImageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class GeminiBrainResult(
    val ok: Boolean,
    val answer: String,
    val sources: List<HarmonyBrainSource> = emptyList(),
    val suggestions: List<BrainChatSuggestionItem> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val errorMessage: String? = null
)

object GeminiBrainGateway {
    private const val TAG = "GeminiBrainGateway"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    suspend fun queryBrain(
        userQuery: String,
        profile: ProfileEntity,
        interests: List<BrainInterestEntity>,
        recentNotes: List<String> = emptyList(),
        appLanguage: String = "de"
    ): GeminiBrainResult = withContext(Dispatchers.IO) {
        val apiKey = GeminiImageService.resolveGeminiApiKey()
        if (apiKey.isBlank()) {
            return@withContext fallbackOfflineReply(userQuery, profile, interests, "Kein Gemini API-Schlüssel hinterlegt.")
        }

        try {
            val systemInstruction = buildString {
                append("Du bist Harmony Brain 🧠, der KI-Beziehungscoach und Date-Planer für ${profile.userName} & ${profile.partnerName}.\n")
                append("Du hilfst dem Paar mit kreativen, personalisierten Date-Ideen, Restaurant-Vorschlägen, Aktivitäten, Gesprächsanstößen und Analysen ihrer Gemeinsamkeiten.\n\n")
                append("Paar-Kontext:\n")
                append("- Partner A: ${profile.userName}\n")
                append("- Partner B: ${profile.partnerName}\n")
                if (interests.isNotEmpty()) {
                    append("- Erkannte gemeinsame Interessen:\n")
                    interests.take(10).forEach {
                        append("  * ${it.name} (Kategorie: ${it.category}, Status: ${it.confidence})\n")
                    }
                }
                if (recentNotes.isNotEmpty()) {
                    append("- Notizen & Bucket List des Paares:\n")
                    recentNotes.take(5).forEach {
                        append("  * $it\n")
                    }
                }
                append("\nRichtlinien für deine Antwort:\n")
                append("1. Antworte immer herzlich, inspirierend und auf Deutsch.\n")
                append("2. Wenn nach Date-Ideen, Ausflügen oder Restaurants gefragt wird, nenne 1 bis 3 konkrete, lebendige Vorschläge.\n")
                append("3. Wenn du einen bestimmten Ort, Restaurant oder Ausflug empfiehlst, nenne immer den genauen Namen (z.B. **Trattoria Da Luigi**) und gib einen passenden Google Maps Suchlink an (Format: [Auf Google Maps suchen](https://www.google.com/maps/search/?api=1&query=NAME_UND_STADT)).\n")
                append("4. Nutze Such-Grounding, um aktuelle und reale Informationen bereitzustellen.\n")
            }

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        put("role", "user")
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$systemInstruction\n\nNutzer-Anfrage von ${profile.userName}:\n$userQuery")
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                // Tools: Google Search grounding for accurate real-time information
                val toolsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("googleSearch", JSONObject())
                    })
                }
                put("tools", toolsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 2048)
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(mediaTypeJson))
                .build()

            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini call failed with code ${response.code}: $bodyString")
                return@withContext fallbackOfflineReply(userQuery, profile, interests, "Gemini API Fehler (${response.code})")
            }

            val resJson = JSONObject(bodyString)
            val candidates = resJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext fallbackOfflineReply(userQuery, profile, interests, "Keine Antwort erhalten")
            }

            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawAnswer = parts?.optJSONObject(0)?.optString("text")?.trim().orEmpty()

            // Grounding sources extraction
            val sources = mutableListOf<HarmonyBrainSource>()
            val searchQueries = mutableListOf<String>()

            val groundingMetadata = candidate.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
                if (webSearchQueries != null) {
                    for (i in 0 until webSearchQueries.length()) {
                        searchQueries.add(webSearchQueries.optString(i))
                    }
                }

                val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
                if (groundingChunks != null) {
                    for (i in 0 until groundingChunks.length()) {
                        val chunk = groundingChunks.optJSONObject(i)
                        val web = chunk?.optJSONObject("web")
                        if (web != null) {
                            val title = web.optString("title", "Quelle ${i + 1}")
                            val uri = web.optString("uri", "")
                            if (uri.isNotBlank()) {
                                sources.add(HarmonyBrainSource(title = title, url = uri))
                            }
                        }
                    }
                }
            }

            // Extract structured suggestions with rich image URLs
            val suggestions = extractSuggestionsFromAnswer(rawAnswer, userQuery)

            GeminiBrainResult(
                ok = true,
                answer = rawAnswer,
                sources = sources.distinctBy { it.url }.take(6),
                suggestions = suggestions,
                searchQueries = searchQueries
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception querying Gemini: ${e.message}", e)
            fallbackOfflineReply(userQuery, profile, interests, e.localizedMessage)
        }
    }

    /**
     * Extracts rich suggestion cards with beautiful image URLs and Google Maps links.
     */
    fun extractSuggestionsFromAnswer(
        answer: String,
        userQuery: String
    ): List<BrainChatSuggestionItem> {
        val items = mutableListOf<BrainChatSuggestionItem>()

        // 1. Look for bold headers or bullet points in the markdown
        // e.g. **1. Romantisches Picknick im Schlosspark**
        val itemPattern = Pattern.compile("(?:^|\\n)(?:\\d+\\.\\s*|\\*\\s*|-\\s*)?\\*\\*([^*\\n]+)\\*\\*[:\\s]*([^\\n]+(?:\\n(?!\\d+\\.|\\*|-|\\*\\*)[^\\n]+)*)", Pattern.MULTILINE)
        val matcher = itemPattern.matcher(answer)

        while (matcher.find() && items.size < 4) {
            val titleRaw = matcher.group(1)?.trim().orEmpty()
            val descRaw = matcher.group(2)?.trim().orEmpty()

            if (titleRaw.length in 3..60 && !titleRaw.startsWith("Hinweis", ignoreCase = true) && !titleRaw.startsWith("Fazit", ignoreCase = true)) {
                val cleanTitle = titleRaw.replace(Regex("^[0-9]+[.):]\\s*"), "").trim()
                val (category, imageUrl) = resolveSuggestionImageAndCategory(cleanTitle, descRaw)
                val mapsUrl = extractOrGenerateMapsUrl(cleanTitle, descRaw)

                items.add(
                    BrainChatSuggestionItem(
                        id = UUID.randomUUID().toString(),
                        title = cleanTitle,
                        description = descRaw.take(240).replace(Regex("\\[([^\\]]+)\\]\\([^)]+\\)"), "$1").trim(),
                        imageUrl = imageUrl,
                        linkUrl = mapsUrl,
                        category = category
                    )
                )
            }
        }

        // If no structured markdown items matched, but the query is about dates or food, provide smart curated cards
        if (items.isEmpty() && isIdeaOrRecommendationQuery(userQuery)) {
            val fallbackSuggestions = generateSmartThematicSuggestions(userQuery)
            items.addAll(fallbackSuggestions)
        }

        return items
    }

    private fun extractOrGenerateMapsUrl(title: String, desc: String): String {
        // Check if there is an explicit Google Maps link in the text
        val linkMatcher = Pattern.compile("https://www\\.google\\.com/maps/search/\\?api=1&query=([^)\\s]+)").matcher(desc)
        if (linkMatcher.find()) {
            return linkMatcher.group(0) ?: ""
        }

        // Otherwise generate query URL for Google Maps
        val encodedQuery = try {
            URLEncoder.encode(title, "UTF-8")
        } catch (_: Exception) {
            title.replace(" ", "+")
        }
        return "https://www.google.com/maps/search/?api=1&query=$encodedQuery"
    }

    fun resolveSuggestionImageAndCategory(title: String, desc: String): Pair<String, String> {
        val combined = "$title $desc".lowercase()
        return when {
            combined.contains("pizza") || combined.contains("trattoria") || combined.contains("italien") || combined.contains("pasta") -> {
                Pair("Essen", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80")
            }
            combined.contains("sushi") || combined.contains("japan") || combined.contains("ramen") || combined.contains("asiatisch") -> {
                Pair("Essen", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=800&q=80")
            }
            combined.contains("restaurant") || combined.contains("dinner") || combined.contains("candle") || combined.contains("kerzenschein") || combined.contains("romantisch") -> {
                Pair("Date", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80")
            }
            combined.contains("café") || combined.contains("cafe") || combined.contains("kaffee") || combined.contains("brunch") || combined.contains("frühstück") -> {
                Pair("Essen", "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&q=80")
            }
            combined.contains("picknick") || combined.contains("park") || combined.contains("wiese") -> {
                Pair("Aktivität", "https://images.unsplash.com/photo-1590483736622-39da8677c7b8?w=800&q=80")
            }
            combined.contains("wellness") || combined.contains("therme") || combined.contains("sauna") || combined.contains("spa") || combined.contains("massage") -> {
                Pair("Wellness", "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800&q=80")
            }
            combined.contains("stern") || combined.contains("planetarium") || combined.contains("himmel") || combined.contains("nacht") -> {
                Pair("Date", "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800&q=80")
            }
            combined.contains("kino") || combined.contains("film") || combined.contains("popcorn") || combined.contains("serie") -> {
                Pair("Date", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&q=80")
            }
            combined.contains("wandern") || combined.contains("berg") || combined.contains("natur") || combined.contains("wald") -> {
                Pair("Aktivität", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=800&q=80")
            }
            combined.contains("see") || combined.contains("kanu") || combined.contains("boot") || combined.contains("strand") || combined.contains("meer") -> {
                Pair("Ausflug", "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800&q=80")
            }
            combined.contains("museum") || combined.contains("kunst") || combined.contains("galerie") || combined.contains("theater") || combined.contains("kultur") -> {
                Pair("Kultur", "https://images.unsplash.com/photo-1565008447742-97f6f38c985c?w=800&q=80")
            }
            combined.contains("kochen") || combined.contains("küche") || combined.contains("rezept") -> {
                Pair("Date", "https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=800&q=80")
            }
            combined.contains("wein") || combined.contains("cocktail") || combined.contains("bar") || combined.contains("tasting") -> {
                Pair("Date", "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=800&q=80")
            }
            combined.contains("reise") || combined.contains("urlaub") || combined.contains("hotel") || combined.contains("städtetrip") -> {
                Pair("Reisen", "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&q=80")
            }
            else -> {
                Pair("Date", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&q=80")
            }
        }
    }

    private fun isIdeaOrRecommendationQuery(q: String): Boolean {
        val lower = q.lowercase()
        return lower.contains("date") || lower.contains("idee") || lower.contains("essen") ||
                lower.contains("restaurant") || lower.contains("ausflug") || lower.contains("urlaub") ||
                lower.contains("aktivität") || lower.contains("vorschlag") || lower.contains("tipp") ||
                lower.contains("machen") || lower.contains("unternehmen")
    }

    private fun generateSmartThematicSuggestions(q: String): List<BrainChatSuggestionItem> {
        val lower = q.lowercase()
        return when {
            lower.contains("essen") || lower.contains("restaurant") || lower.contains("hunger") -> {
                listOf(
                    BrainChatSuggestionItem(
                        title = "Italienisches Candle-Light Dinner 🍝",
                        description = "Frische hausgemachte Pasta, guter Wein und intime Stimmung.",
                        imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&q=80",
                        linkUrl = "https://www.google.com/maps/search/?api=1&query=Italienisches+Restaurant",
                        category = "Essen"
                    ),
                    BrainChatSuggestionItem(
                        title = "Gemütliches Sonntags-Café & Brunch ☕",
                        description = "Ausgiebiges Frühstück mit frischem Gebäck und Zeit für Gespräche.",
                        imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=800&q=80",
                        linkUrl = "https://www.google.com/maps/search/?api=1&query=Cafe+Brunch",
                        category = "Essen"
                    )
                )
            }
            lower.contains("ausflug") || lower.contains("natur") || lower.contains("draußen") -> {
                listOf(
                    BrainChatSuggestionItem(
                        title = "Romantisches Sonnenuntergangs-Picknick 🧺",
                        description = "Decke, Lieblingssnacks und den Abendhimmel zu zweit genießen.",
                        imageUrl = "https://images.unsplash.com/photo-1590483736622-39da8677c7b8?w=800&q=80",
                        linkUrl = "https://www.google.com/maps/search/?api=1&query=Park+Aussichtspunkt",
                        category = "Aktivität"
                    ),
                    BrainChatSuggestionItem(
                        title = "Ausflug an den See mit Bootsfahrt 🛶",
                        description = "Frische Seeluft, Entspannung und gemeinsame Paddel-Abenteuer.",
                        imageUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800&q=80",
                        linkUrl = "https://www.google.com/maps/search/?api=1&query=See+Bootsverleih",
                        category = "Ausflug"
                    )
                )
            }
            else -> {
                listOf(
                    BrainChatSuggestionItem(
                        title = "Sternschnuppen & Nachtspaziergang ✨",
                        description = "Heiße Schokolade in der Thermoskanne und gemeinsam Sterne zählen.",
                        imageUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800&q=80",
                        linkUrl = "https://www.google.com/maps/search/?api=1&query=Aussichtspunkt+Sternwarte",
                        category = "Date"
                    ),
                    BrainChatSuggestionItem(
                        title = "Kuscheliger Filmabend mit Deckenburg 🎬",
                        description = "Eure Lieblingsfilme, Popcorn und die ultimative Festung aus Decken.",
                        imageUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=800&q=80",
                        linkUrl = "https://www.google.com/maps/search/?api=1&query=Kino",
                        category = "Date"
                    )
                )
            }
        }
    }

    private fun fallbackOfflineReply(
        query: String,
        profile: ProfileEntity,
        interests: List<BrainInterestEntity>,
        reason: String?
    ): GeminiBrainResult {
        val suggestions = generateSmartThematicSuggestions(query)
        val reply = buildString {
            append("Hier sind schöne Vorschläge für euch zwei (${profile.userName} & ${profile.partnerName}):\n\n")
            if (interests.isNotEmpty()) {
                append("Passend zu euren gemeinsamen Interessen (z.B. ${interests.firstOrNull()?.name ?: "Zweisamkeit"}):\n")
            }
            suggestions.forEachIndexed { i, s ->
                append("${i + 1}. **${s.title}**\n${s.description}\n\n")
            }
            if (!reason.isNullOrBlank()) {
                append("_(Hinweis: $reason · Offline-Vorschläge geladen)_")
            }
        }

        return GeminiBrainResult(
            ok = true,
            answer = reply,
            suggestions = suggestions,
            errorMessage = reason
        )
    }
}
