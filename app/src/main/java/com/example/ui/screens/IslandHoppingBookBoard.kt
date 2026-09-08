package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.ProfileEntity

@Composable
internal fun IslandHoppingBookBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DirectPriorityPokerBoard(
        question = question,
        options = options,
        selectedAnswer = selectedAnswer,
        profile = profile,
        onPick = onPick,
        modifier = modifier
    )
}
