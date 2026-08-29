package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ProfileEntity
import com.example.ui.HarmonyViewModel
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.tr

@Composable
internal fun IntimacyCompactChoiceBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val harmonyViewModel: HarmonyViewModel = viewModel()
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)

    FullscreenMechanicShell(
        kicker = tr("💞 NUR FÜR EUCH ZWEI", "💞 JUST FOR YOU TWO"),
        question = prompt,
        instruction = tr(
            "Wähle, was sich für dich am ehesten richtig anfühlt.",
            "Choose what feels most true for you."
        ),
        modifier = modifier.testTag("intimacy_compact_board")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LargeOptionGrid(
                items = items,
                selectedRaw = selectedAnswer,
                onSelect = { item -> onPick(item.raw) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 46.dp),
                tagPrefix = "intimacy_option"
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { harmonyViewModel.openOwnAnswerDialog() },
                    modifier = Modifier.testTag("intimacy_own_answer")
                ) {
                    Text(
                        text = tr("Eigene Antwort", "Own answer"),
                        color = HarmonyMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(
                    onClick = { harmonyViewModel.nextStep() },
                    modifier = Modifier.testTag("intimacy_skip")
                ) {
                    Text(
                        text = tr("Überspringen", "Skip"),
                        color = HarmonyPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun IntimacyPrivateRevealBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val harmonyViewModel: HarmonyViewModel = viewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("intimacy_private_reveal_board")
    ) {
        SecretChoiceBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp)
        )

        TextButton(
            onClick = { harmonyViewModel.nextStep() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(16.dp))
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.20f))
                .testTag("intimacy_private_skip")
        ) {
            Text(
                text = tr("Diese Frage überspringen", "Skip this question"),
                color = HarmonyMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
