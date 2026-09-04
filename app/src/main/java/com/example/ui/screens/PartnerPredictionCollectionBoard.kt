package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PredictionAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration

/**
 * Partner prediction flow for games that already have a final results screen.
 *
 * Both private choices are still collected and encoded, but no per-question hit/miss result is
 * shown. Once the partner chooses, the answer is submitted immediately so the runner can advance
 * to the next question. The final game reveal remains the single place where results are shown.
 */
@Composable
internal fun PartnerPredictionCollectionBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val restored = remember(selectedAnswer) { selectedAnswer?.let(PredictionAnswerCodec::decode) }
    var prediction by rememberSaveable(question, selectedAnswer) {
        mutableStateOf(restored?.prediction)
    }
    var actual by rememberSaveable(question, selectedAnswer) {
        mutableStateOf(restored?.actual)
    }
    var phase by rememberSaveable(question, selectedAnswer) { mutableStateOf(0) }

    FullscreenMechanicShell(
        kicker = tr("🔮 PARTNER-PROGNOSE", "🔮 PARTNER PREDICTION"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr(
                "Tippe heimlich, was ${profile.partnerName} wählen wird.",
                "Secretly predict what ${profile.partnerName} will choose."
            )
            1 -> tr("Dein Tipp ist sicher verdeckt.", "Your prediction is safely hidden.")
            else -> tr(
                "Jetzt entscheidet ${profile.partnerName} – dein Tipp bleibt unsichtbar.",
                "Now ${profile.partnerName} chooses – your prediction stays hidden."
            )
        },
        modifier = modifier.testTag("partner_prediction_board")
    ) {
        when (phase) {
            0 -> LargeOptionGrid(
                items = items,
                selectedRaw = prediction,
                onSelect = { item ->
                    triggerMiniVibration(context, 36L)
                    prediction = item.raw
                    phase = 1
                },
                tagPrefix = "prediction_option"
            )

            1 -> PredictionCollectionHandoffPane(
                name = profile.partnerName,
                title = tr("Jetzt ist ${profile.partnerName} dran", "Now it's ${profile.partnerName}'s turn"),
                body = tr(
                    "Dein Tipp wurde verdeckt. Übergib das Handy, ohne zurückzugehen.",
                    "Your prediction is hidden. Pass the phone over without going back."
                ),
                onReady = { phase = 2 }
            )

            else -> LargeOptionGrid(
                items = items,
                selectedRaw = actual,
                onSelect = { item ->
                    triggerMiniVibration(context, 42L)
                    val predicted = prediction
                    if (predicted != null) {
                        actual = item.raw
                        onPick(PredictionAnswerCodec.encode(predicted, item.raw))
                    } else {
                        phase = 0
                    }
                },
                tagPrefix = "prediction_actual_option"
            )
        }
    }
}

@Composable
private fun PredictionCollectionHandoffPane(
    name: String,
    title: String,
    body: String,
    onReady: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 86.dp else 108.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.trim().take(1).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = if (compact) 34.sp else 42.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(if (compact) 14.dp else 20.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = if (compact) 20.sp else 24.sp,
            lineHeight = if (compact) 25.sp else 30.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = body,
            color = HarmonyMuted,
            fontSize = if (compact) 13.sp else 15.sp,
            lineHeight = if (compact) 17.sp else 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(if (compact) 18.dp else 26.dp))
        PrimaryMechanicButton(
            text = tr("Ich bin bereit", "I'm ready"),
            onClick = onReady,
            testTag = "prediction_handoff_ready"
        )
    }
}
