package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.ProfileEntity

/**
 * Temporary compatibility route while the unfinished Kid Generator is parked on the
 * `unfinished-ideas` branch. The production app uses the established EureMischung flow instead.
 *
 * Keep this wrapper only until MainActivity routing is consolidated; the experimental generator
 * implementation itself must not be restored to main from here.
 */
@Composable
fun KidGeneratorScreen(
    profile: ProfileEntity,
    appLanguage: String = "de",
    onClose: () -> Unit,
    onAddMoment: (title: String, content: String, emoji: String) -> Unit,
    modifier: Modifier = Modifier
) {
    EureMischungScreen(
        profile = profile,
        appLanguage = appLanguage,
        onClose = onClose,
        onAddMoment = onAddMoment,
        modifier = modifier
    )
}
