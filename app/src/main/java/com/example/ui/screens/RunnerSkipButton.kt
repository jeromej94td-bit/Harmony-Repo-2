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
import com.example.data.model.LoveBalanceQuestionPolicy
import com.example.ui.HarmonyViewModel
import com.example.ui.theme.HarmonyPinkSoft
import com.example.util.LanguageManager

@Composable
fun RunnerSkipButton(
    appLanguage: String,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeRun = viewModel<HarmonyViewModel>()
        .uiState
        .collectAsStateWithLifecycle()
        .value
        .activeRun

    if (activeRun?.pack?.id == LoveBalanceQuestionPolicy.PACK_ID && activeRun.currentIndex == 0) {
        return
    }

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .clip(CircleShape)
            .background(Color(0xFF160D1B).copy(alpha = 0.92f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
            .clickable(onClick = onSkip)
            .padding(horizontal = 13.dp, vertical = 9.dp)
            .testTag("runner_skip_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = LanguageManager.tr("Überspringen  →", appLanguage),
            color = HarmonyPinkSoft,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
