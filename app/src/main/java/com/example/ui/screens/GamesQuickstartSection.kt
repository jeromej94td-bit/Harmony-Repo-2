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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.util.LanguageManager

@Composable
internal fun GamesQuickstartEntryDialog(
    appLanguage: String,
    onQuickstart: () -> Unit,
    onChooseBrowse: () -> Unit
) {
    Dialog(onDismissRequest = onChooseBrowse) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            HarmonySurface2.copy(alpha = 0.98f),
                            Color(0xFF140B1E).copy(alpha = 0.99f)
                        )
                    )
                )
                .border(1.dp, HarmonyPurpleLight.copy(alpha = 0.38f), RoundedCornerShape(26.dp))
                .padding(horizontal = 18.dp, vertical = 22.dp)
                .testTag("games_start_choice_dialog"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LanguageManager.tr("Wie möchtet ihr starten?", appLanguage),
                color = HarmonyText,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            GamesStartChoiceButton(
                icon = "⚡",
                title = LanguageManager.tr("Schnellstart", appLanguage),
                subtitle = LanguageManager.tr("Direkt zu einer noch offenen Frage", appLanguage),
                highlighted = true,
                testTag = "games_quickstart_button",
                onClick = onQuickstart
            )

            Spacer(Modifier.height(11.dp))

            GamesStartChoiceButton(
                icon = "▦",
                title = LanguageManager.tr("Fragen selbst auswählen", appLanguage),
                subtitle = LanguageManager.tr("Kategorien und Themen wie gewohnt", appLanguage),
                highlighted = false,
                testTag = "games_browse_questions_button",
                onClick = onChooseBrowse
            )
        }
    }
}

@Composable
private fun GamesStartChoiceButton(
    icon: String,
    title: String,
    subtitle: String,
    highlighted: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val borderColor = if (highlighted) HarmonyPink.copy(alpha = 0.72f) else HarmonyLine
    val background = if (highlighted) {
        Brush.linearGradient(
            listOf(
                HarmonyPink.copy(alpha = 0.18f),
                HarmonyPurple.copy(alpha = 0.22f),
                HarmonySurface2.copy(alpha = 0.96f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                HarmonySurface2.copy(alpha = 0.92f),
                HarmonySurface2.copy(alpha = 0.72f)
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (highlighted) HarmonyPink.copy(alpha = 0.16f)
                    else HarmonyPurple.copy(alpha = 0.14f)
                )
                .border(
                    1.dp,
                    if (highlighted) HarmonyPink.copy(alpha = 0.48f)
                    else HarmonyPurpleLight.copy(alpha = 0.30f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = if (highlighted) HarmonyPink else HarmonyPurpleLight,
                fontSize = if (icon == "⚡") 23.sp else 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = HarmonyText,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = HarmonyMuted,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }

        Text(
            text = "›",
            color = if (highlighted) HarmonyPink else HarmonyMuted,
            fontSize = 27.sp,
            fontWeight = FontWeight.Light
        )
    }
}
