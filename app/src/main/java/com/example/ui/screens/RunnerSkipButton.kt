package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HarmonyViewModel
import com.example.ui.nextStep
import com.example.ui.theme.HarmonyPinkSoft
import com.example.util.LanguageManager

@Composable
fun RunnerSkipButton(
    appLanguage: String,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    // This composable lives under the same Activity ViewModelStore as HarmonyApp, so viewModel()
    // resolves the existing HarmonyViewModel instance rather than creating runner-local state.
    val runnerViewModel: HarmonyViewModel = viewModel()
    val uiState by runnerViewModel.uiState.collectAsStateWithLifecycle()
    val activeRun = uiState.activeRun
    val showContinue = activeRun?.let { run ->
        RunnerContinuePolicy.shouldShow(
            isFinished = run.isFinished,
            packType = run.pack.type,
            currentIndex = run.currentIndex,
            answeredIndexes = run.currentAnswers.keys
        )
    } == true

    val onClick: () -> Unit = if (showContinue && activeRun != null) {
        val expectedIndex = activeRun.currentIndex
        { runnerViewModel.nextStep(expectedIndex = expectedIndex) }
    } else {
        onSkip
    }

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .clip(CircleShape)
            .background(Color(0xFF160D1B).copy(alpha = 0.92f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp)
            .testTag(if (showContinue) "runner_continue_button" else "runner_skip_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (showContinue) {
                LanguageManager.tr("Weiter  →", appLanguage)
            } else {
                LanguageManager.tr("Überspringen  →", appLanguage)
            },
            color = HarmonyPinkSoft,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
