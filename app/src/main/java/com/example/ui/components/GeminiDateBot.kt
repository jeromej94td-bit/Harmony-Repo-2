package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.widget.Toast
import com.example.util.GeminiImageService
import com.example.util.GeneratedImageResult
import com.example.BuildConfig
import com.example.data.model.AnswerEntity
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyBlue
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyTeal
import com.example.ui.theme.HarmonyText
import com.example.util.AnalyzedAnswer
import com.example.util.DateCoachContextBuilder
import com.example.util.LanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// --- GEMINI API DATA CLASSES ---
@Serializable
data class GeminiPart(val text: String)

@Serializable
data class GeminiContent(val parts: List<GeminiPart>, val role: String? = null)

@Serializable
data class GeminiGenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@Serializable
data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
data class GeminiCandidate(val content: GeminiContent)

@Serializable
data class GeminiGenerateResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

// --- DATE COACH RESPONSE MODEL ---
@Serializable
data class DateIdeaDto(
    val title: String = "",
    val emoji: String = "✨",
    val vibe: String = "Romantisch",
    val inspiredBy: List<String> = emptyList(),
    val description: String = "",
    val steps: List<String> = emptyList(),
    val conversationPrompt: String = "",
    val duration: String = "ca. 2-3 Stunden"
)

@Serializable
data class DateCoachResponseDto(
    val summary: String = "",
    val ideas: List<DateIdeaDto> = emptyList()
)

interface GeminiDateApiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): GeminiGenerateResponse
}

object GeminiDateRetrofit {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    val service: GeminiDateApiService = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(
            OkHttpClient.Builder()
                .connectTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
                .build()
        )
        .build()
        .create(GeminiDateApiService::class.java)

    fun parseResponse(rawJson: String): DateCoachResponseDto {
        val clean = rawJson.trim()
            .removePrefix("```json")
            .removePrefix("```JSON")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            json.decodeFromString<DateCoachResponseDto>(clean)
        } catch (e: Exception) {
            try {
                // If it was returned as a list of ideas directly
                val ideasList = json.decodeFromString<List<DateIdeaDto>>(clean)
                DateCoachResponseDto(summary = "Individuelle Date-Ideen basierend auf euren Antworten", ideas = ideasList)
            } catch (e2: Exception) {
                // Fallback: create a single card with raw text
                DateCoachResponseDto(
                    summary = "Hier sind eure Date-Vorschläge:",
                    ideas = listOf(
                        DateIdeaDto(
                            title = "Persönliches Paar-Date",
                            emoji = "✨",
                            vibe = "Individuell",
                            inspiredBy = listOf("Eure Spielantworten"),
                            description = clean.take(400),
                            steps = listOf("Gemeinsam vorbereiten", "Zeit zu zweit genießen", "Über eure Antworten sprechen"),
                            conversationPrompt = "Welche der heutigen Fragen hat dich am meisten überrascht?",
                            duration = "ca. 2 Stunden"
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun GeminiDateBot(
    profile: ProfileEntity,
    answers: List<AnswerEntity>,
    appLanguage: String = "de",
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resultData by remember { mutableStateOf<DateCoachResponseDto?>(null) }
    var showAnswersPreview by remember { mutableStateOf(false) }
    var selectedVibe by remember { mutableStateOf("🎲 Bester Mix") }
    var customWishText by remember { mutableStateOf("") }
    val savedIdeaTitles = remember { mutableStateListOf<String>() }

    // Analyze current answers
    val analyzedAnswers = remember(answers, profile, appLanguage) {
        DateCoachContextBuilder.analyzeAnswers(answers, profile, appLanguage)
    }
    val customNotesCount = remember(analyzedAnswers) {
        analyzedAnswers.count { it.isCustomText }
    }

    val vibeOptions = listOf(
        "🎲 Bester Mix",
        "🍿 Film & Kino",
        "🍽️ Essen & Genuss",
        "🌲 Outdoor & Natur",
        "🛋️ Gemütlich Daheim",
        "⚡ Spontan heute"
    )

    // Animated loading messages
    val loadingStatusMessages = listOf(
        "Analysiere eure Spielantworten...",
        "Prüfe Kino-, Food- & Aktivitäts-Vorlieben...",
        "Beziehe eigene Freitext-Notizen ein...",
        "Kreiere 3 maßgeschneiderte Date-Erlebnisse..."
    )
    var currentLoadingMessageIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(isLoading) {
        if (isLoading) {
            currentLoadingMessageIndex = 0
            while (true) {
                delay(2200)
                currentLoadingMessageIndex = (currentLoadingMessageIndex + 1) % loadingStatusMessages.size
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        // Main Container Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            HarmonyPurple.copy(alpha = 0.35f),
                            HarmonySurface2.copy(alpha = 0.95f),
                            HarmonyPink.copy(alpha = 0.20f)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(HarmonyPinkSoft.copy(alpha = 0.7f), HarmonyPurpleLight.copy(alpha = 0.7f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "KI Date Coach",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(HarmonyPink.copy(alpha = 0.25f))
                                        .border(1.dp, HarmonyPink.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Gemini",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HarmonyPinkSoft
                                    )
                                }
                            }
                            Text(
                                text = "Dates verknüpft mit euren Spielantworten",
                                fontSize = 11.5.sp,
                                color = HarmonyMuted
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Answers Context & Transparency Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .border(1.dp, HarmonyLine, RoundedCornerShape(14.dp))
                        .clickable { showAnswersPreview = !showAnswersPreview }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = HarmonyTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Basis: ${analyzedAnswers.size} Antworten" +
                                            if (customNotesCount > 0) " · ✍️ $customNotesCount eigene Freitext-Notizen" else "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (showAnswersPreview) "Tippen zum Einklappen" else "Tippen, um analysierte Antworten anzusehen",
                                    fontSize = 10.5.sp,
                                    color = HarmonyMuted
                                )
                            }
                        }
                        Icon(
                            imageVector = if (showAnswersPreview) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = HarmonyMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Expandable Answers Preview
                AnimatedVisibility(
                    visible = showAnswersPreview,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(HarmonySurface.copy(alpha = 0.9f))
                            .border(1.dp, HarmonyLine, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Vom Coach analysierte Antworten & Notizen:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarmonyText
                        )
                        Spacer(Modifier.height(8.dp))

                        if (analyzedAnswers.isEmpty()) {
                            Text(
                                text = "Noch keine Spiel-Fragen beantwortet. Beantwortet ein paar Runden 'Entweder oder' oder 'Wer würde eher', um den Coach mit euren Vorlieben zu füttern!",
                                fontSize = 11.5.sp,
                                color = HarmonyMuted
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                analyzedAnswers.take(12).forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (item.isCustomText) HarmonyPink.copy(alpha = 0.12f)
                                                else Color.White.copy(alpha = 0.04f)
                                            )
                                            .border(
                                                1.dp,
                                                if (item.isCustomText) HarmonyPinkSoft.copy(alpha = 0.4f) else HarmonyLine,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = item.categoryIcon, fontSize = 12.sp)
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = item.packTitle,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = HarmonyPinkSoft
                                                )
                                                if (item.isCustomText) {
                                                    Spacer(Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(HarmonyPink)
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("Freitext", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                if (item.isMatch) {
                                                    Spacer(Modifier.width(6.dp))
                                                    Text("✓ Match", color = HarmonyTeal, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Text(
                                                text = item.questionOrPair,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = HarmonyText,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (item.isCustomText) "✍️ \"${item.userChoice}\""
                                                else "Wahl: ${item.userChoice}" + if (item.partnerChoice != null) " · ${profile.partnerName}: ${item.partnerChoice}" else "",
                                                fontSize = 11.sp,
                                                color = if (item.isCustomText) HarmonyPinkSoft else HarmonyMuted,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                if (analyzedAnswers.size > 12) {
                                    Text(
                                        text = "+ ${analyzedAnswers.size - 12} weitere Antworten werden einbezogen...",
                                        fontSize = 10.5.sp,
                                        color = HarmonyMuted,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Vibe Filter Chips
                Text(
                    text = "Vibe / Fokus wählen:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarmonyText
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    vibeOptions.forEach { vibe ->
                        val isSelected = selectedVibe == vibe
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))
                                    else Brush.linearGradient(listOf(Color.White.copy(alpha = 0.07f), Color.White.copy(alpha = 0.07f)))
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) HarmonyPinkSoft else HarmonyLine,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedVibe = vibe }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = vibe,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else HarmonyMuted
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Optional Custom Wish Input
                OutlinedTextField(
                    value = customWishText,
                    onValueChange = { customWishText = it },
                    placeholder = {
                        Text("Zusätzlicher Wunsch (z. B. Regentag, unter 20€...)", color = HarmonyMuted, fontSize = 12.sp)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HarmonyPinkSoft,
                        unfocusedBorderColor = HarmonyLine,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.04f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(14.dp))

                // Generate Button
                Button(
                    onClick = {
                        if (isLoading) return@Button
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                val systemPrompt = DateCoachContextBuilder.buildSystemInstruction(profile)
                                val userPrompt = DateCoachContextBuilder.buildUserPrompt(
                                    profile = profile,
                                    analyzedAnswers = analyzedAnswers,
                                    moodFilter = if (selectedVibe != "🎲 Bester Mix") selectedVibe else null,
                                    customWish = customWishText.takeIf { it.isNotBlank() }
                                )

                                val request = GeminiGenerateRequest(
                                    contents = listOf(
                                        GeminiContent(parts = listOf(GeminiPart(userPrompt)))
                                    ),
                                    systemInstruction = GeminiContent(
                                        parts = listOf(GeminiPart(systemPrompt))
                                    ),
                                    generationConfig = GeminiGenerationConfig(
                                        responseMimeType = "application/json",
                                        temperature = 0.7f
                                    )
                                )

                                val rawText = withContext(Dispatchers.IO) {
                                    val response = GeminiDateRetrofit.service.generateContent(
                                        apiKey = BuildConfig.GEMINI_API_KEY,
                                        request = request
                                    )
                                    response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                                        ?: throw IllegalStateException("Keine Antwort von Gemini erhalten.")
                                }

                                val parsed = GeminiDateRetrofit.parseResponse(rawText)
                                resultData = parsed
                            } catch (e: Exception) {
                                errorMessage = e.localizedMessage ?: "Verbindungsfehler bei der Date-Generierung"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(HarmonyPink, HarmonyPurple, HarmonyPurpleLight)
                            )
                        )
                        .testTag("generate_date_ideas_button")
                ) {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = loadingStatusMessages[currentLoadingMessageIndex],
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (resultData == null) "Date-Ideen generieren ✨" else "Neue Date-Ideen erstellen ✨",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Error Message if any
                if (errorMessage != null) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF4A1521))
                            .border(1.dp, HarmonyPink, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Hinweis: $errorMessage (Bitte prüfe, ob der GEMINI_API_KEY im Secrets-Panel eingetragen ist)",
                            color = HarmonyPinkSoft,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        // Generated Results Cards
        if (resultData != null && resultData!!.ideas.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))

            if (resultData!!.summary.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, HarmonyLine, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = resultData!!.summary,
                            color = HarmonyText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                resultData!!.ideas.forEachIndexed { index, idea ->
                    val isSaved = savedIdeaTitles.contains(idea.title)
                    DateIdeaCard(
                        idea = idea,
                        profile = profile,
                        index = index + 1,
                        isSaved = isSaved,
                        onToggleSave = {
                            if (isSaved) {
                                savedIdeaTitles.remove(idea.title)
                            } else {
                                savedIdeaTitles.add(idea.title)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateIdeaCard(
    idea: DateIdeaDto,
    profile: ProfileEntity,
    index: Int,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isGeneratingAnime by remember { mutableStateOf(false) }
    var animeResult by remember { mutableStateOf<GeneratedImageResult?>(null) }
    var animeError by remember { mutableStateOf<String?>(null) }
    var isFullscreenOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HarmonySurface2.copy(alpha = 0.98f),
                        HarmonySurface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(HarmonyPurpleLight.copy(alpha = 0.5f), HarmonyPinkSoft.copy(alpha = 0.5f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = idea.emoji.ifBlank { "✨" }, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = idea.title,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (idea.vibe.isNotBlank()) {
                                Text(
                                    text = idea.vibe,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarmonyPinkSoft
                                )
                                Text(" · ", color = HarmonyMuted, fontSize = 11.sp)
                            }
                            Text(
                                text = idea.duration.ifBlank { "ca. 2-3h" },
                                fontSize = 11.sp,
                                color = HarmonyMuted
                            )
                        }
                    }
                }

                // Bookmark / Save Action
                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSaved) HarmonyPink.copy(alpha = 0.2f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Idee merken",
                        tint = if (isSaved) HarmonyPink else HarmonyMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Inspired By Section (Explicitly connecting to answers)
            if (idea.inspiredBy.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = HarmonyGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Inspiriert von euren Antworten & Notizen:",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarmonyGold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        idea.inspiredBy.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HarmonyPurple.copy(alpha = 0.25f))
                                    .border(1.dp, HarmonyPurpleLight.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 10.5.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // Description
            Text(
                text = idea.description,
                fontSize = 12.5.sp,
                color = HarmonyText,
                lineHeight = 17.5.sp
            )

            // Steps
            if (idea.steps.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    idea.steps.forEachIndexed { sIdx, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "${sIdx + 1}.",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarmonyPinkSoft,
                                modifier = Modifier.width(18.dp)
                            )
                            Text(
                                text = step,
                                fontSize = 11.5.sp,
                                color = HarmonyText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Conversation Prompt Box
            if (idea.conversationPrompt.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HarmonyPink.copy(alpha = 0.10f))
                        .border(1.dp, HarmonyPinkSoft.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "💬", fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Gesprächs-Impuls für das Date:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarmonyPinkSoft
                            )
                            Text(
                                text = "\"${idea.conversationPrompt}\"",
                                fontSize = 11.5.sp,
                                color = HarmonyText,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- ANIME DATE VISUALIZATION SECTION ---
            if (animeResult == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    HarmonyPurple.copy(alpha = 0.25f),
                                    HarmonyPink.copy(alpha = 0.25f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    HarmonyPurpleLight.copy(alpha = 0.4f),
                                    HarmonyPinkSoft.copy(alpha = 0.4f)
                                )
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = !isGeneratingAnime) {
                            isGeneratingAnime = true
                            animeError = null
                            scope.launch {
                                val res = GeminiImageService.generateAnimeDateVisual(
                                    context = context,
                                    userAvatarPath = profile.userAvatarPath,
                                    partnerAvatarPath = profile.partnerAvatarPath,
                                    userName = profile.userName,
                                    partnerName = profile.partnerName,
                                    activityTitle = idea.title,
                                    activityDescription = idea.description
                                )
                                isGeneratingAnime = false
                                res.onSuccess {
                                    animeResult = it
                                    Toast.makeText(context, "Anime-Visualisierung erstellt! ✨", Toast.LENGTH_SHORT).show()
                                }.onFailure { err ->
                                    animeError = err.localizedMessage ?: "Konnte Bild nicht generieren"
                                }
                            }
                        }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isGeneratingAnime) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = HarmonyPinkSoft,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Erschaffe Anime-Szene aus euren Profilbildern...",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = HarmonyPinkSoft,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "🎨 Als Anime-Szene visualisieren (mit Profilbildern)",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Generated Anime Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, HarmonyPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨", fontSize = 13.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Anime-Visualisierung",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarmonyPinkSoft
                                )
                            }
                            IconButton(
                                onClick = {
                                    val uri = GeminiImageService.saveToDeviceGallery(
                                        context,
                                        animeResult!!.bitmap,
                                        "AnimeDate_${idea.title}"
                                    )
                                    if (uri != null) {
                                        Toast.makeText(context, "In Galerie gespeichert! 💾", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "In Galerie speichern",
                                    tint = HarmonyGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isFullscreenOpen = true }
                        ) {
                            Image(
                                bitmap = animeResult!!.bitmap.asImageBitmap(),
                                contentDescription = "Anime Date",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Vollbild",
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        if (animeResult!!.aiDescription.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = animeResult!!.aiDescription,
                                fontSize = 11.sp,
                                color = HarmonyMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            if (animeError != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = animeError ?: "",
                    color = Color(0xFFFF8FA3),
                    fontSize = 11.sp
                )
            }

            // Saved indicator feedback
            if (isSaved) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = HarmonyTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Für eure Date-Liste vorgemerkt",
                        fontSize = 10.5.sp,
                        color = HarmonyTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Fullscreen Dialog
    if (isFullscreenOpen && animeResult != null) {
        Dialog(
            onDismissRequest = { isFullscreenOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { isFullscreenOpen = false }
            ) {
                Image(
                    bitmap = animeResult!!.bitmap.asImageBitmap(),
                    contentDescription = "Anime Date Vollbild",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                IconButton(
                    onClick = { isFullscreenOpen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Schließen",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
