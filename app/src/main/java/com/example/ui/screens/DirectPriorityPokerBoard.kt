package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.data.model.ProfileEntity
import com.example.ui.util.triggerMiniVibration

/**
 * Priority poker is deliberately one-step: the question and every card stay visible at once.
 * A card tap is the final answer and advances the runner immediately.
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

    QuestionOnlyMechanicShell(
        question = prompt,
        modifier = modifier.testTag("priority_poker_board")
    ) {
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
