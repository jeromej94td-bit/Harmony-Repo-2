package com.example.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.theme.HarmonyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LiveChangeLauncherUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun launcherShowsDismissControl() {
        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                LiveChangeLauncher(onStart = {})
            }
        }

        composeTestRule.onNodeWithTag("live_change_dismiss_button")
            .assertIsDisplayed()
    }
}
