package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.util.LanguageManager

@Composable
internal fun GamesQuickstartSection(
    pool: GamesQuickstartPool,
    browseMode: Boolean,
    appLanguage: String,
    onBrowseModeChange: (Boolean) -> Unit,
    onStartPack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEverythingDone by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = LanguageManager.tr("Wie möchtet ihr spielen?", appLanguage),
            color = HarmonyText,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = LanguageManager.tr(
                "Direkt loslegen oder selbst durch Kategorien und Themen stöbern.",
                appLanguage
            ),
            color = HarmonyMuted,
            fontSize = 12.5.sp,
            lineHeight = 18.sp
        )
        Spacer(Modifier.height(12.dp))

        QuickstartHeroCard(
            openQuestionCount = pool.openQuestionCount,
            openPackCount = pool.openPackCount,
            appLanguage = appLanguage,
            onClick = {
                val candidate = pool.pick()
                if (candidate == null) {
                    showEverythingDone = true
                } else {
                    onStartPack(candidate.packId)
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        BrowseModeCard(
            selected = browseMode,
            appLanguage = appLanguage,
            onClick = { onBrowseModeChange(!browseMode) }
        )
    }

    if (showEverythingDone) {
        AlertDialog(
            onDismissRequest = { showEverythingDone = false },
            title = {
                Text(
                    text = LanguageManager.tr("Alles geschafft", appLanguage),
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(
                    text = LanguageManager.tr(
                        "Ihr habt aktuell alle für Quickstart verfügbaren Fragen beantwortet. Ihr könnt weiterhin Kategorien öffnen und Spiele erneut spielen.",
                        appLanguage
                    ),
                    color = HarmonyMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEverythingDone = false
                        onBrowseModeChange(true)
                    }
                ) {
                    Text(LanguageManager.tr("Kategorien öffnen", appLanguage), color = HarmonyPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEverythingDone = false }) {
                    Text(LanguageManager.tr("Schließen", appLanguage), color = HarmonyMuted)
                }
            }
        )
    }
}

@Composable
private fun QuickstartHeroCard(
    openQuestionCount: Int,
    openPackCount: Int,
    appLanguage: String,
    onClick: () -> Unit
) {
    val countText = if (openQuestionCount > 0) {
        LanguageManager.tr("{questions} offene Fragen · {games} Spiele", appLanguage)
            .replace("{questions}", openQuestionCount.toString())
            .replace("{games}", openPackCount.toString())
    } else {
        LanguageManager.tr("Aktuell alles beantwortet", appLanguage)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        HarmonyPink.copy(alpha = 0.88f),
                        HarmonyPurple.copy(alpha = 0.88f),
                        Color(0xFF6941C6).copy(alpha = 0.90f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 17.dp)
            .testTag("games_quickstart_button")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(1.dp, Color.White.copy(alpha = 0.24f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 26.sp)
            }

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = LanguageManager.tr("Quickstart", appLanguage),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = LanguageManager.tr("EMPFOHLEN", appLanguage),
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = LanguageManager.tr(
                        "Harmony wählt zufällig etwas aus, das ihr noch nicht beantwortet habt.",
                        appLanguage
                    ),
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = countText,
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BrowseModeCard(
    selected: Boolean,
    appLanguage: String,
    onClick: () -> Unit
) {
    val border = if (selected) HarmonyPink.copy(alpha = 0.70f) else HarmonyLine
    val background = if (selected) {
        Brush.linearGradient(
            listOf(HarmonyPink.copy(alpha = 0.16f), HarmonyPurple.copy(alpha = 0.12f), HarmonySurface2)
        )
    } else {
        Brush.linearGradient(listOf(HarmonySurface2.copy(alpha = 0.86f), HarmonySurface2.copy(alpha = 0.66f)))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("games_browse_categories_button"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(HarmonyPurple.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🗂️", fontSize = 21.sp)
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = LanguageManager.tr("Kategorien selbst wählen", appLanguage),
                color = HarmonyText,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (selected) {
                    LanguageManager.tr("Kategorien und Themen sind geöffnet", appLanguage)
                } else {
                    LanguageManager.tr("Wie bisher selbst stöbern und auswählen", appLanguage)
                },
                color = HarmonyMuted,
                fontSize = 11.5.sp
            )
        }

        Text(
            text = if (selected) "⌃" else "⌄",
            color = if (selected) HarmonyPink else HarmonyMuted,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
