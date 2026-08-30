package com.example.ui.screens

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.example.data.model.ExperienceImageDuelOption
import com.example.data.model.ExperienceImageDuelRound
import com.example.ui.theme.HarmonyTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ExperienceProductImageDuelBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val round = ExperienceImageDuelRound(
        id = "ring_demo",
        prompt = "Welcher Ring passt besser?",
        firstOption = ExperienceImageDuelOption("first", "Klassisch", "asset_first"),
        secondOption = ExperienceImageDuelOption("second", "Modern", "asset_second")
    )

    @Test
    fun `product board keeps cards flexible instead of fixed cinematic height`() {
        composeRule.setContent {
            HarmonyTheme {
                ExperienceProductImageDuelBoard(
                    round = round,
                    selectedOptionId = null,
                    imageResolver = { android.R.drawable.ic_menu_gallery },
                    onPick = {},
                    modifier = Modifier.width(720.dp).height(600.dp)
                )
            }
        }

        val rootHeight = composeRule.onNodeWithTag("experience_product_image_duel")
            .assertExists().fetchSemanticsNode().boundsInRoot.height
        val cardHeight = composeRule.onNodeWithTag("experience_product_image_duel_first")
            .assertExists().fetchSemanticsNode().boundsInRoot.height

        assertTrue("Cards should consume the flexible remaining height", cardHeight / rootHeight > 0.65f)
        composeRule.onNodeWithText(round.prompt).assertExists()
        composeRule.onNodeWithText("Klassisch").assertExists()
        composeRule.onNodeWithText("Modern").assertExists()
    }
}
