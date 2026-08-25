package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CoachLocation
import com.example.ai.CoachMessage
import com.example.ai.CoachRole
import com.example.ai.HarmonyAiIntentRouter
import com.example.ai.HarmonyCoachUiState
import com.example.ai.HarmonyLocationProvider
import com.example.data.model.ChatMessageEntity
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText

@Composable
fun ChatHubScreen(
    messages: List<ChatMessageEntity>,
    partnerName: String,
    partnerAvatarPath: String?,
    appLanguage: String,
    coachState: HarmonyCoachUiState,
    onSendMessage: (String) -> Unit,
    onSendImage: (Uri) -> Unit,
    onReportUser: () -> Unit,
    onAskCoach: (String, CoachLocation?) -> Unit,
    modifier: Modifier = Modifier
) {
    var coachMode by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(HarmonySurface)
                .border(1.dp, HarmonyLine, RoundedCornerShape(18.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HubModeButton(
                label = copy(appLanguage).partnerChat,
                selected = !coachMode,
                onClick = { coachMode = false },
                modifier = Modifier.weight(1f)
            )
            HubModeButton(
                label = "✨ Harmony Coach",
                selected = coachMode,
                onClick = { coachMode = true },
                modifier = Modifier.weight(1f)
            )
        }

        if (coachMode) {
            HarmonyCoachPanel(
                state = coachState,
                appLanguage = appLanguage,
                onAskCoach = onAskCoach,
                modifier = Modifier.weight(1f)
            )
        } else {
            ChatScreen(
                messages = messages,
                partnerName = partnerName,
                partnerAvatarPath = partnerAvatarPath,
                appLanguage = appLanguage,
                onSendMessage = onSendMessage,
                onSendImage = onSendImage,
                onReportUser = onReportUser,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HubModeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (selected) Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))
                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) Color.White else HarmonyMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
private fun HarmonyCoachPanel(
    state: HarmonyCoachUiState,
    appLanguage: String,
    onAskCoach: (String, CoachLocation?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = copy(appLanguage)
    var input by remember { mutableStateOf("") }
    var pendingLocationQuery by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        pendingLocationQuery?.let { query ->
            onAskCoach(query, HarmonyLocationProvider.bestLastKnownLocation(context))
        }
        pendingLocationQuery = null
    }

    fun submit(raw: String) {
        val query = raw.trim()
        if (query.isEmpty() || state.isLoading) return
        input = ""
        if (HarmonyAiIntentRouter.needsLocationPermission(query) && !HarmonyLocationProvider.hasPermission(context)) {
            pendingLocationQuery = query
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            )
        } else {
            val location = if (HarmonyAiIntentRouter.needsLocationPermission(query)) {
                HarmonyLocationProvider.bestLastKnownLocation(context)
            } else null
            onAskCoach(query, location)
        }
    }

    LaunchedEffect(state.messages.size, state.isLoading) {
        val target = state.messages.size + if (state.isLoading) 1 else 0
        if (target > 0) listState.animateScrollToItem(target - 1)
    }

    Column(modifier = modifier.fillMaxSize().padding(bottom = 80.dp)) {
        if (state.messages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                Text("Harmony Coach", color = HarmonyText, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text(strings.subtitle, color = HarmonyMuted, fontSize = 12.sp)
                Spacer(Modifier.height(18.dp))
                QuickActions(strings = strings, onPick = ::submit)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages, key = { it.id }) { message ->
                CoachBubble(message = message, appLanguage = appLanguage)
            }
            if (state.isLoading) {
                item(key = "coach_loading") {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(HarmonySurface2)
                            .border(1.dp, HarmonyLine, RoundedCornerShape(18.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = HarmonyPink)
                        Spacer(Modifier.width(9.dp))
                        Text(strings.thinking, color = HarmonyMuted, fontSize = 12.sp)
                    }
                }
            }
            state.errorMessage?.let { error ->
                item(key = "coach_error") {
                    Text(error, color = Color(0xFFFF9E9E), fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text(strings.placeholder, color = HarmonyMuted) },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HarmonyPink,
                    unfocusedBorderColor = HarmonyLine,
                    focusedTextColor = HarmonyText,
                    unfocusedTextColor = HarmonyText,
                    focusedContainerColor = HarmonySurface,
                    unfocusedContainerColor = HarmonySurface
                ),
                shape = RoundedCornerShape(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { submit(input) },
                enabled = input.isNotBlank() && !state.isLoading,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = strings.send, tint = Color.White)
            }
        }
    }
}

@Composable
private fun QuickActions(strings: CoachCopy, onPick: (String) -> Unit) {
    val actions = listOf(
        "🍣 ${strings.food}" to strings.foodPrompt,
        "🎬 ${strings.watch}" to strings.watchPrompt,
        "✨ ${strings.date}" to strings.datePrompt,
        "📍 ${strings.nearby}" to strings.nearbyPrompt
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        actions.chunked(2).forEach { rowActions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                rowActions.forEach { (label, prompt) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(HarmonySurface)
                            .border(1.dp, HarmonyLine, RoundedCornerShape(14.dp))
                            .clickable { onPick(prompt) }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = HarmonyText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachBubble(message: CoachMessage, appLanguage: String) {
    val context = LocalContext.current
    val isUser = message.role == CoachRole.USER
    val bubbleShape = RoundedCornerShape(
        topStart = 19.dp,
        topEnd = 19.dp,
        bottomStart = if (isUser) 19.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 19.dp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.88f)) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(
                        if (isUser) Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))
                        else Brush.linearGradient(listOf(HarmonySurface2, HarmonySurface2))
                    )
                    .border(if (isUser) 0.dp else 1.dp, if (isUser) Color.Transparent else HarmonyLine, bubbleShape)
                    .padding(13.dp)
            ) {
                Text(message.text, color = if (isUser) Color.White else HarmonyText, fontSize = 14.sp, lineHeight = 20.sp)
            }
            if (!isUser && (message.groundedByMaps || message.groundedBySearch || message.sources.isNotEmpty())) {
                Row(
                    modifier = Modifier.padding(top = 5.dp, start = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.groundedByMaps) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = HarmonyMuted, modifier = Modifier.size(13.dp))
                        Text("Google Maps", color = HarmonyMuted, fontSize = 10.sp)
                    } else if (message.groundedBySearch) {
                        Text(copy(appLanguage).liveWeb, color = HarmonyMuted, fontSize = 10.sp)
                    }
                }
                message.sources.take(4).forEach { source ->
                    Text(
                        text = "↗ ${source.title}",
                        color = HarmonyPink,
                        fontSize = 10.5.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, top = 4.dp)
                            .clickable {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.url)))
                                }
                            }
                    )
                }
            }
        }
    }
}

private data class CoachCopy(
    val partnerChat: String,
    val subtitle: String,
    val thinking: String,
    val placeholder: String,
    val send: String,
    val food: String,
    val watch: String,
    val date: String,
    val nearby: String,
    val liveWeb: String,
    val foodPrompt: String,
    val watchPrompt: String,
    val datePrompt: String,
    val nearbyPrompt: String
)

private fun copy(language: String): CoachCopy = when (language.lowercase()) {
    "de" -> CoachCopy("Partner-Chat", "Ideen, die zu euch passen – mit Live-Daten, wenn nötig", "Harmony schaut nach …", "Frag Harmony …", "Senden", "Essen", "Was schauen?", "Date-Idee", "In der Nähe", "Aktuelle Webdaten", "Wo könnten wir heute gut essen gehen?", "Was könnten wir heute zusammen anschauen?", "Gib uns eine Date-Idee, die zu uns passt.", "Was können wir gerade in meiner Nähe machen?")
    "it" -> CoachCopy("Chat di coppia", "Idee su misura per voi, con dati live quando servono", "Harmony sta cercando …", "Chiedi a Harmony …", "Invia", "Mangiare", "Cosa guardare?", "Idea per un date", "Vicino a me", "Dati web aggiornati", "Dove potremmo mangiare bene oggi?", "Cosa potremmo guardare insieme oggi?", "Suggerisci un appuntamento adatto a noi.", "Cosa possiamo fare adesso vicino a me?")
    "pl" -> CoachCopy("Czat pary", "Pomysły dopasowane do Was, z danymi na żywo gdy trzeba", "Harmony sprawdza …", "Zapytaj Harmony …", "Wyślij", "Jedzenie", "Co obejrzeć?", "Pomysł na randkę", "W pobliżu", "Aktualne dane z sieci", "Gdzie moglibyśmy dziś dobrze zjeść?", "Co moglibyśmy dziś razem obejrzeć?", "Podaj pomysł na randkę pasujący do nas.", "Co możemy teraz zrobić w pobliżu mnie?")
    "es" -> CoachCopy("Chat de pareja", "Ideas pensadas para vosotros, con datos en vivo cuando haga falta", "Harmony está buscando …", "Pregunta a Harmony …", "Enviar", "Comer", "¿Qué vemos?", "Idea de cita", "Cerca", "Datos web actuales", "¿Dónde podríamos comer bien hoy?", "¿Qué podríamos ver juntos hoy?", "Danos una idea de cita que encaje con nosotros.", "¿Qué podemos hacer ahora cerca de mí?")
    "fr" -> CoachCopy("Chat du couple", "Des idées adaptées à vous, avec des données en direct si nécessaire", "Harmony cherche …", "Demande à Harmony …", "Envoyer", "Manger", "Que regarder ?", "Idée de date", "À proximité", "Données web actuelles", "Où pourrions-nous bien manger aujourd'hui ?", "Que pourrions-nous regarder ensemble aujourd'hui ?", "Propose-nous une idée de rendez-vous qui nous correspond.", "Que pouvons-nous faire près de moi maintenant ?")
    else -> CoachCopy("Partner chat", "Ideas tailored to you, using live data when needed", "Harmony is checking …", "Ask Harmony …", "Send", "Food", "What to watch?", "Date idea", "Nearby", "Current web data", "Where could we eat well today?", "What could we watch together today?", "Give us a date idea that fits us.", "What can we do near me right now?")
}
