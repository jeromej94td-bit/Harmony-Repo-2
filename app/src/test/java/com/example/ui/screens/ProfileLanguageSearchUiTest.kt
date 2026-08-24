package com.example.ui.screens

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProfileLanguageSearchUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchIconOpensInputAndFiltersLanguageOptions() {
        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                ProfileSheet(
                    profile = ProfileEntity(userName = "Ralf", partnerName = "J"),
                    isEditProfileOpen = false,
                    onDismiss = {},
                    onToggleSimulator = {},
                    onOpenEditProfile = {},
                    onCloseEditProfile = {},
                    onSaveEditProfile = { _, _, _ -> },
                    onUpdateAvatar = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithTag("language_search_toggle").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("language_search_input").assertIsDisplayed().performTextInput("polnisch")
        composeTestRule.onNodeWithTag("language_option_POLISH").assertIsDisplayed()
        composeTestRule.onNodeWithTag("language_option_ITALIAN").assertDoesNotExist()
    }
}
