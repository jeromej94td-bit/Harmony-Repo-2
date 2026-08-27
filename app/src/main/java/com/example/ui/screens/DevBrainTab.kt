package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.brain.db.BrainAnswerHistoryEntity
import com.example.data.brain.db.BrainMemoryFactEntity
import com.example.data.brain.db.BrainPendingGenerationEntity
import com.example.data.brain.db.BrainPreferenceEntity
import com.example.data.brain.engine.HarmonyContextBuilder
import com.example.data.brain.gateway.SupabaseHarmonyBrainGateway
import com.example.data.brain.model.BrainScope
import com.example.data.brain.model.HarmonyBrainContext
import com.example.data.brain.repository.BrainRepository
import com.example.data.db.HarmonyDatabase
import com.example.data.model.ProfileEntity
import com.example.ui.components.HarmonyCard
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonyTeal
import com.example.ui.theme.HarmonyText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DevBrainTab(
    profile: ProfileEntity,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { HarmonyDatabase.getInstance(context) }
    val brainRepo = remember { BrainRepository(db.brainRoomDao(), context) }

    val answerCount by brainRepo.answerCountFlow.collectAsState(initial = 0)
    val interactionCount by brainRepo.interactionCountFlow.collectAsState(initial = 0)
    val allPreferences by brainRepo.allPreferencesFlow.collectAsState(initial = emptyList())
    val allFacts by brainRepo.memoryFactsFlow.collectAsState(initial = emptyList())
    val pendingGenerations by brainRepo.pendingGenerationsFlow.collectAsState(initial = emptyList())

    // Playground state
    val modes = listOf("questions", "chat", "recommendations", "search")
    var selectedMode by remember { mutableStateOf("questions") }
    var selectedCategory by remember { mutableStateOf("Reisen") }
    var inputQuery by remember { mutableStateOf("Erstelle 3 neue Fragen über Traumreiseziele und Urlaubsstile für uns.") }

    var builtContext by remember { mutableStateOf<HarmonyBrainContext?>(null) }
    var builtContextJson by remember { mutableStateOf<String?>(null) }
    var isCallingEdge by remember { mutableStateOf(false) }
    var edgeResponseText by remember { mutableStateOf<String?>(null) }
    var edgeResponseModel by remember { mutableStateOf<String?>(null) }
    var edgeLatencyMs by remember { mutableStateOf<Long?>(null) }
    var edgeError by remember { mutableStateOf<String?>(null) }
    var isErrorDetailsExpanded by remember { mutableStateOf(false) }

    fun refreshBuiltContext() {
        scope.launch {
            val ctx = brainRepo.buildBrainContext(
                task = selectedMode,
                category = selectedCategory.ifBlank { null },
                query = inputQuery,
                userName = profile.userName,
                partnerName = profile.partnerName
            )
            builtContext = ctx
            builtContextJson = try {
                val raw = HarmonyContextBuilder.serializeCompact(ctx)
                JSONObject(raw).toString(2)
            } catch (e: Exception) {
                HarmonyContextBuilder.serializeCompact(ctx)
            }
        }
    }

    LaunchedEffect(selectedMode, selectedCategory, inputQuery, answerCount) {
        refreshBuiltContext()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Overview Card
        item {
            HarmonyCard(modifier = Modifier.fillMaxWidth()) {
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
                                .background(HarmonyPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = HarmonyPurpleLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Harmony Brain Core",
                                color = HarmonyText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lokale Wissensbasis & KI-Präferenzen",
                                color = HarmonyMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = {
                        scope.launch {
                            val answers = db.answerDao().getAllAnswersDirect()
                            brainRepo.performInitialBackfillIfNeeded(answers)
                            refreshBuiltContext()
                            onShowToast("Brain synchronisiert")
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = HarmonyPurpleLight)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = HarmonyLine)
                Spacer(modifier = Modifier.height(14.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BrainStatItem(
                        value = answerCount.toString(),
                        label = "Antworten",
                        color = HarmonyTeal
                    )
                    BrainStatItem(
                        value = interactionCount.toString(),
                        label = "Interaktionen",
                        color = HarmonyGold
                    )
                    BrainStatItem(
                        value = allFacts.size.toString(),
                        label = "Fakten",
                        color = HarmonyPurpleLight
                    )
                    BrainStatItem(
                        value = allPreferences.size.toString(),
                        label = "Tags",
                        color = HarmonyPink
                    )
                    BrainStatItem(
                        value = pendingGenerations.size.toString(),
                        label = "Warteschlange",
                        color = if (pendingGenerations.isEmpty()) HarmonyMuted else HarmonyGold
                    )
                }
            }
        }

        // 2. Preferences Profiles Section
        item {
            HarmonyCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "📊 Erkannte Vorlieben & Scores",
                    color = HarmonyText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val prefsA = allPreferences.filter { it.scope == BrainScope.PERSON_A.scopeKey }
                val prefsB = allPreferences.filter { it.scope == BrainScope.PERSON_B.scopeKey }
                val prefsCouple = allPreferences.filter { it.scope == BrainScope.COUPLE.scopeKey }

                // Couple
                Text(
                    text = "💑 Gemeinsam (Couple):",
                    color = HarmonyPurpleLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (prefsCouple.isEmpty()) {
                    Text("Noch keine gemeinsamen Präferenzen abgeleitet", color = HarmonyMuted, fontSize = 12.sp)
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        prefsCouple.take(8).forEach { pref ->
                            BrainTagBadge(
                                tag = pref.tag,
                                score = pref.score,
                                confidence = pref.confidence,
                                color = HarmonyPurpleLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Person A
                Text(
                    text = "👤 ${profile.userName} (Person A):",
                    color = HarmonyTeal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (prefsA.isEmpty()) {
                    Text("Keine spezifischen Tags", color = HarmonyMuted, fontSize = 12.sp)
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        prefsA.take(6).forEach { pref ->
                            BrainTagBadge(
                                tag = pref.tag,
                                score = pref.score,
                                confidence = pref.confidence,
                                color = HarmonyTeal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Person B
                Text(
                    text = "👤 ${profile.partnerName} (Person B):",
                    color = HarmonyPink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (prefsB.isEmpty()) {
                    Text("Keine spezifischen Tags", color = HarmonyMuted, fontSize = 12.sp)
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        prefsB.take(6).forEach { pref ->
                            BrainTagBadge(
                                tag = pref.tag,
                                score = pref.score,
                                confidence = pref.confidence,
                                color = HarmonyPink
                            )
                        }
                    }
                }
            }
        }

        // 3. Memory Facts Section
        item {
            HarmonyCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🧠 Gesicherte Erinnerungen (${allFacts.size})",
                        color = HarmonyText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verifizierte Fakten",
                        color = HarmonyGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (allFacts.isEmpty()) {
                    Text(
                        text = "Es werden automatisch Fakten abgeleitet, sobald mehr Fragen beantwortet werden.",
                        color = HarmonyMuted,
                        fontSize = 12.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        allFacts.take(6).forEach { fact ->
                            Surface(
                                color = HarmonySurface.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyLine),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = HarmonyGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = fact.factText,
                                            color = HarmonyText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Bereich: ${fact.category ?: fact.personScope}",
                                                color = HarmonyMuted,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "Konfidenz: ${(fact.confidence * 100).toInt()}%",
                                                color = HarmonyTeal,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Interactive Edge Function & Context Playground
        item {
            HarmonyCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = HarmonyGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edge Function & Context Tester",
                        color = HarmonyText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    modes.forEach { mode ->
                        val isSel = selectedMode == mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) HarmonyPurpleLight else HarmonySurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSel) HarmonyPurpleLight else HarmonyLine
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) HarmonyPurpleLight else Color.Transparent)
                                .clickable {
                                    selectedMode = mode
                                    inputQuery = when (mode) {
                                        "questions" -> "Erstelle 3 tiefgründige Beziehungsfragen passend zu unserem Profil."
                                        "chat" -> "Welche gemeinsame Aktivität passt heute Abend perfekt zu uns?"
                                        "recommendations" -> "Gib uns 3 Date-Ideen für das Wochenende."
                                        "search" -> "Die 3 schönsten italienischen Städte für einen romantischen Kurztrip"
                                        else -> inputQuery
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode,
                                    color = if (isSel) Color.White else HarmonyMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category & Query Fields
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    label = { Text("Kategorie", color = HarmonyMuted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HarmonyText,
                        unfocusedTextColor = HarmonyText,
                        focusedBorderColor = HarmonyPurpleLight,
                        unfocusedBorderColor = HarmonyLine
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    label = { Text("Query / User Prompt", color = HarmonyMuted, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HarmonyText,
                        unfocusedTextColor = HarmonyText,
                        focusedBorderColor = HarmonyPurpleLight,
                        unfocusedBorderColor = HarmonyLine
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Privacy Indicator Card
                Surface(
                    color = HarmonyBg.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyTeal.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = HarmonyTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🛡️ Privacy Filter aktiv: Reale Namen, Adressen & private Details werden vor dem Senden anonymisiert.",
                            color = HarmonyTeal,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Context JSON Preview with Character Budget
                val jsonLength = builtContextJson?.length ?: 0
                val isBudgetOk = jsonLength <= 5500

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📦 Generierter Context Payload:",
                        color = HarmonyMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$jsonLength / 5.500 Zeichen",
                        color = if (isBudgetOk) HarmonyTeal else HarmonyPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyLine),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = builtContextJson ?: "Context wird aufgebaut...",
                            color = Color(0xFF80CBC4),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Button to Execute Edge Function
                Button(
                    onClick = {
                        scope.launch {
                            isCallingEdge = true
                            edgeError = null
                            edgeResponseText = null
                            edgeResponseModel = null
                            edgeLatencyMs = null

                            val gateway = SupabaseHarmonyBrainGateway.getInstance()
                            val ctx = builtContext ?: brainRepo.buildBrainContext(
                                task = selectedMode,
                                category = selectedCategory.ifBlank { null },
                                query = inputQuery,
                                userName = profile.userName,
                                partnerName = profile.partnerName
                            )

                            when (selectedMode) {
                                "questions" -> {
                                    val res = gateway.generateQuestions(inputQuery, ctx)
                                    isCallingEdge = false
                                    edgeLatencyMs = res.latencyMs
                                    edgeResponseModel = res.model
                                    if (res.ok) {
                                        edgeResponseText = if (res.questions.isNotEmpty()) {
                                            res.questions.mapIndexed { idx, q ->
                                                "${idx + 1}. ${q.text} [${q.category}]"
                                            }.joinToString("\n\n")
                                        } else {
                                            res.rawAnswer
                                        }
                                        onShowToast("Fragen erfolgreich generiert!")
                                    } else {
                                        edgeError = res.errorMessage ?: "Fehler bei der Generierung"
                                    }
                                }
                                "chat" -> {
                                    val res = gateway.chat(inputQuery, ctx)
                                    isCallingEdge = false
                                    edgeLatencyMs = res.latencyMs
                                    edgeResponseModel = res.model
                                    if (res.ok) {
                                        edgeResponseText = res.answer
                                        onShowToast("Antwort erhalten!")
                                    } else {
                                        edgeError = res.errorMessage ?: "Fehler bei Chat"
                                    }
                                }
                                "recommendations" -> {
                                    val res = gateway.recommendations(inputQuery, ctx)
                                    isCallingEdge = false
                                    edgeLatencyMs = res.latencyMs
                                    edgeResponseModel = res.model
                                    if (res.ok) {
                                        edgeResponseText = res.recommendations.joinToString("\n• ")
                                        onShowToast("Empfehlungen geladen!")
                                    } else {
                                        edgeError = res.errorMessage ?: "Fehler"
                                    }
                                }
                                "search" -> {
                                    val res = gateway.search(inputQuery, ctx)
                                    isCallingEdge = false
                                    edgeLatencyMs = res.latencyMs
                                    edgeResponseModel = res.model
                                    if (res.ok) {
                                        val sources = res.sources.joinToString("\n") { "• ${it.title}: ${it.url}" }
                                        edgeResponseText = "${res.answer.orEmpty()}\n\nQuellen:\n$sources"
                                        onShowToast("Suchergebnisse geladen!")
                                    } else {
                                        edgeError = res.errorMessage ?: "Fehler bei Suche"
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurpleLight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCallingEdge
                ) {
                    if (isCallingEdge) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edge Function lädt...", color = Color.White, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edge Function testen (Online)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Response Card
                if (edgeResponseText != null || edgeError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = if (edgeError != null) HarmonyPink.copy(alpha = 0.12f) else HarmonyPurple.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (edgeError != null) HarmonyPink.copy(alpha = 0.6f) else HarmonyPurpleLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (edgeError != null) "❌ Edge Fehler" else "✨ Edge Ergebnis ($selectedMode):",
                                    color = if (edgeError != null) HarmonyPink else HarmonyText,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (edgeLatencyMs != null) {
                                    Text(
                                        text = "${edgeLatencyMs}ms · ${edgeResponseModel ?: "Supabase V4"}",
                                        color = HarmonyMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (edgeError != null) {
                                val summary = edgeError?.substringBefore(":")?.ifBlank { "Fehler bei der Kommunikation" } ?: "Fehler"
                                Text(
                                    text = summary,
                                    color = HarmonyPink,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { isErrorDetailsExpanded = !isErrorDetailsExpanded }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isErrorDetailsExpanded) "Fehlerdetails einklappen ▲" else "Fehlerdetails anzeigen ▼",
                                        color = HarmonyPurpleLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (isErrorDetailsExpanded) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = edgeError.orEmpty(),
                                            color = HarmonyText,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = edgeResponseText.orEmpty(),
                                    color = HarmonyText,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun BrainStatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = HarmonyMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun BrainTagBadge(
    tag: String,
    score: Double,
    confidence: Double,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${(score * 100).toInt()}%",
                color = HarmonyMuted,
                fontSize = 9.sp
            )
        }
    }
}
