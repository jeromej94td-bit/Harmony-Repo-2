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
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText

@Composable
fun HarmonyCoachPanelPublic(
    state: HarmonyCoachUiState,
    appLanguage: String,
    onAskCoach: (String, CoachLocation?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val copy = coachCopy(appLanguage)
    var input by remember { mutableStateOf("") }
    var pendingQuery by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        pendingQuery?.let { query -> onAskCoach(query, HarmonyLocationProvider.bestLastKnownLocation(context)) }
        pendingQuery = null
    }

    fun submit(text: String) {
        val query = text.trim()
        if (query.isEmpty() || state.isLoading) return
        input = ""
        if (HarmonyAiIntentRouter.needsLocationPermission(query) && !HarmonyLocationProvider.hasPermission(context)) {
            pendingQuery = query
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            val location = if (HarmonyAiIntentRouter.needsLocationPermission(query)) HarmonyLocationProvider.bestLastKnownLocation(context) else null
            onAskCoach(query, location)
        }
    }

    LaunchedEffect(state.messages.size, state.isLoading) {
        val itemCount = state.messages.size + if (state.isLoading) 1 else 0
        if (itemCount > 0) listState.animateScrollToItem(itemCount - 1)
    }

    Column(modifier = modifier.fillMaxSize().padding(bottom = 80.dp)) {
        if (state.messages.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.AutoAwesome, null, tint = Color.White) }
                Spacer(Modifier.height(10.dp))
                Text("Harmony Coach", color = HarmonyText, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text(copy.subtitle, color = HarmonyMuted, fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))
                val actions = listOf(
                    "🍣 ${copy.food}" to copy.foodPrompt,
                    "🎬 ${copy.watch}" to copy.watchPrompt,
                    "✨ ${copy.date}" to copy.datePrompt,
                    "📍 ${copy.nearby}" to copy.nearbyPrompt
                )
                actions.chunked(2).forEach { chunk ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        chunk.forEach { (label, prompt) ->
                            Box(
                                modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(HarmonySurface)
                                    .border(1.dp, HarmonyLine, RoundedCornerShape(14.dp)).clickable { submit(prompt) }
                                    .padding(horizontal = 9.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) { Text(label, color = HarmonyText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages, key = { it.id }) { CoachMessageBubblePublic(it, appLanguage) }
            if (state.isLoading) {
                item("loading") {
                    Row(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(HarmonySurface2)
                            .border(1.dp, HarmonyLine, RoundedCornerShape(16.dp)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = HarmonyPink)
                        Spacer(Modifier.width(8.dp))
                        Text(copy.thinking, color = HarmonyMuted, fontSize = 12.sp)
                    }
                }
            }
            state.errorMessage?.let { error -> item("error") { Text(error, color = Color(0xFFFF9E9E), fontSize = 12.sp) } }
        }

        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text(copy.placeholder, color = HarmonyMuted) },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HarmonyPink, unfocusedBorderColor = HarmonyLine,
                    focusedTextColor = HarmonyText, unfocusedTextColor = HarmonyText,
                    focusedContainerColor = HarmonySurface, unfocusedContainerColor = HarmonySurface
                ),
                shape = RoundedCornerShape(22.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { submit(input) }, enabled = input.isNotBlank() && !state.isLoading,
                modifier = Modifier.size(46.dp).clip(CircleShape).background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
            ) { Icon(Icons.AutoMirrored.Filled.Send, copy.send, tint = Color.White) }
        }
    }
}

@Composable
private fun CoachMessageBubblePublic(message: CoachMessage, appLanguage: String) {
    val context = LocalContext.current
    val mine = message.role == CoachRole.USER
    val shape = RoundedCornerShape(19.dp, 19.dp, if (mine) 19.dp else 4.dp, if (mine) 4.dp else 19.dp)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Column(Modifier.fillMaxWidth(0.88f)) {
            Box(
                Modifier.clip(shape)
                    .background(if (mine) Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)) else Brush.linearGradient(listOf(HarmonySurface2, HarmonySurface2)))
                    .border(if (mine) 0.dp else 1.dp, if (mine) Color.Transparent else HarmonyLine, shape).padding(13.dp)
            ) { Text(message.text, color = if (mine) Color.White else HarmonyText, fontSize = 14.sp, lineHeight = 20.sp) }
            if (!mine && (message.groundedByMaps || message.groundedBySearch || message.sources.isNotEmpty())) {
                Row(Modifier.padding(start = 4.dp, top = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (message.groundedByMaps) {
                        Icon(Icons.Default.LocationOn, null, tint = HarmonyMuted, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(3.dp)); Text("Google Maps", color = HarmonyMuted, fontSize = 10.sp)
                    } else if (message.groundedBySearch) Text(coachCopy(appLanguage).liveWeb, color = HarmonyMuted, fontSize = 10.sp)
                }
                message.sources.take(4).forEach { source ->
                    Text("↗ ${source.title}", color = HarmonyPink, fontSize = 10.5.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp).clickable {
                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.url))) }
                        })
                }
            }
        }
    }
}

private data class SimpleCoachCopy(
    val subtitle: String, val thinking: String, val placeholder: String, val send: String,
    val food: String, val watch: String, val date: String, val nearby: String, val liveWeb: String,
    val foodPrompt: String, val watchPrompt: String, val datePrompt: String, val nearbyPrompt: String
)

private fun coachCopy(lang: String): SimpleCoachCopy = when (lang.lowercase()) {
    "de" -> SimpleCoachCopy("Persönlich für euch · mit Live-Daten wenn nötig", "Harmony schaut nach …", "Frag Harmony …", "Senden", "Essen", "Was schauen?", "Date-Idee", "In der Nähe", "Aktuelle Webdaten", "Wo könnten wir heute gut essen gehen?", "Was könnten wir heute zusammen anschauen?", "Gib uns eine Date-Idee, die zu uns passt.", "Was können wir gerade in meiner Nähe machen?")
    "it" -> SimpleCoachCopy("Personale per voi · con dati live quando servono", "Harmony sta cercando …", "Chiedi a Harmony …", "Invia", "Mangiare", "Cosa guardare?", "Idea per un date", "Vicino a me", "Dati web aggiornati", "Dove potremmo mangiare bene oggi?", "Cosa potremmo guardare insieme oggi?", "Suggerisci un appuntamento adatto a noi.", "Cosa possiamo fare adesso vicino a me?")
    "pl" -> SimpleCoachCopy("Dopasowane do Was · z danymi live gdy trzeba", "Harmony sprawdza …", "Zapytaj Harmony …", "Wyślij", "Jedzenie", "Co obejrzeć?", "Pomysł na randkę", "W pobliżu", "Aktualne dane z sieci", "Gdzie moglibyśmy dziś dobrze zjeść?", "Co moglibyśmy dziś razem obejrzeć?", "Podaj pomysł na randkę pasujący do nas.", "Co możemy teraz zrobić w pobliżu mnie?")
    else -> SimpleCoachCopy("Personal to you · live data when needed", "Harmony is checking …", "Ask Harmony …", "Send", "Food", "What to watch?", "Date idea", "Nearby", "Current web data", "Where could we eat well today?", "What could we watch together today?", "Give us a date idea that fits us.", "What can we do near me right now?")
}
