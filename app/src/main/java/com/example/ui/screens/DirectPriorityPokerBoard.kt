package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.ProfileEntity
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration

/**
 * Priority-poker variant used by the full-screen runner: a card tap is the answer.
 * There is deliberately no separate confirmation CTA, so the round advances in one step.
 */
@Composable
internal fun DirectPriorityPokerBoard(
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

    FullscreenMechanicShell(
        kicker = tr("🃏 PRIORITÄTEN-POKER", "🃏 PRIORITY POKER"),
        question = prompt,
        instruction = tr(
            "Tippe auf genau eine Karte. Deine Auswahl wird direkt übernommen.",
            "Tap exactly one card. Your choice is submitted immediately."
        ),
        modifier = modifier.testTag("priority_poker_board")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LargeOptionGrid(
                items = items,
                selectedRaw = selectedAnswer,
                onSelect = { item ->
                    triggerMiniVibration(context, 34L)
                    onPick(item.raw)
                },
                modifier = Modifier.fillMaxSize(),
                tagPrefix = "poker_card"
            )
        }
    }
}
