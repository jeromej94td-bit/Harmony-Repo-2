package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun IslandHoppingBookIntro(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().testTag("island_hopping_book_intro")) {
        FairyBookIntroOverlay(
            onFinished = onFinished,
            modifier = Modifier.fillMaxSize()
        )
    }
}
