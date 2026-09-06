package com.example

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.data.model.ProfileEntity
import com.example.ui.screens.MemoryMatchBoard
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class MemoryMatchFlowContractTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val profile = ProfileEntity(userName = "Jerome", partnerName = "Alex")

    @Test
    fun memoryMatchCollectsTwoPrivateMemoriesBeforeSharedReveal() {
        var picked: String? = null

        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                MemoryMatchBoard(
                    question = "Woran erinnert ihr euch bei eurem ersten gemeinsamen Abenteuer?",
                    options = emptyList(),
                    selectedAnswer = null,
                    profile = profile,
                    onPick = { picked = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("memory_match_text")
            .performTextInput("Der Regen und unser Lachen")
        composeTestRule.onNodeWithTag("memory_match_first_lock").performClick()
        assertNull(picked)

        composeTestRule.onNodeWithTag("memory_match_handoff_ready").performClick()
        composeTestRule.onNodeWithTag("memory_match_partner_text")
            .performTextInput("Die nassen Schuhe und der Kaffee danach")
        composeTestRule.onNodeWithTag("memory_match_partner_lock").performClick()
        assertNull(picked)

        composeTestRule.onNodeWithTag("memory_match_reveal_ready").fetchSemanticsNode()
        composeTestRule.onNodeWithTag("memory_match_reveal_button").performClick()

        composeTestRule.onNodeWithTag("memory_match_reveal_first", useUnmergedTree = true)
            .assertTextContains("Der Regen und unser Lachen", substring = true)
        composeTestRule.onNodeWithTag("memory_match_reveal_second", useUnmergedTree = true)
            .assertTextContains("Die nassen Schuhe und der Kaffee danach", substring = true)
        composeTestRule.onNodeWithTag("memory_match_keep").fetchSemanticsNode()
        assertNull(picked)
    }
}
