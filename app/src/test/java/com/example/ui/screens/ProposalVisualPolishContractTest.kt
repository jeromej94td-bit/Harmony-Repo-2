package com.example.ui.screens

import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalVisualPolishContractTest {

    @Test
    fun `proposal ring duel opts into a wide stacked layout while generic image duels stay unchanged`() {
        val board = source("src/main/java/com/example/ui/screens/ExperienceProductImageDuelBoard.kt")
        val proposal = source("src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")

        assertTrue(board.contains("enum class ExperienceProductImageDuelLayout"))
        assertTrue(board.contains("SIDE_BY_SIDE"))
        assertTrue(board.contains("WIDE_STACKED"))
        assertTrue(
            board.contains(
                "layout: ExperienceProductImageDuelLayout = ExperienceProductImageDuelLayout.SIDE_BY_SIDE"
            )
        )
        assertTrue(
            proposal.contains("layout = ExperienceProductImageDuelLayout.WIDE_STACKED")
        )
    }

    @Test
    fun `proposal text choices opt into glow and tilt without changing the reusable default`() {
        val board = source("src/main/java/com/example/ui/screens/ExperienceEitherOrBoard.kt")
        val proposal = source("src/main/java/com/example/ui/screens/ProposalExperienceScreen.kt")

        assertTrue(board.contains("enum class ExperienceEitherOrVisualStyle"))
        assertTrue(board.contains("STANDARD"))
        assertTrue(board.contains("PROPOSAL_GLOW_TILT"))
        assertTrue(
            board.contains(
                "visualStyle: ExperienceEitherOrVisualStyle = ExperienceEitherOrVisualStyle.STANDARD"
            )
        )
        assertTrue(
            proposal.contains("visualStyle = ExperienceEitherOrVisualStyle.PROPOSAL_GLOW_TILT")
        )
        assertTrue(board.contains("rotationY"))
        assertTrue(board.contains("shadowElevation"))
    }


    @Test
    fun `autumn story renderer uses a lighter image shade without changing other autumn rounds`() {
        val renderer = source("src/main/java/com/example/ui/screens/AutumnEveningQuestion.kt")
        assertTrue(
            renderer.contains(
                "val imageShadeAlpha = if (kind == HarmonyImageChoiceKind.AUTUMN_STORY) 0.14f else 0.32f"
            )
        )
        assertTrue(renderer.contains("AutumnBlackberry.copy(alpha = imageShadeAlpha)"))
    }
    @Test
    fun `autumn story images are lifted out of near black exposure`() {
        val imageDir = File(System.getProperty("user.dir"), "src/main/res/drawable-nodpi")
        val names = (1..4).map { "autumn_story_0$it.png" }

        names.forEach { name ->
            val image = ImageIO.read(File(imageDir, name))
            var lumaTotal = 0.0
            var samples = 0
            val stepX = maxOf(1, image.width / 96)
            val stepY = maxOf(1, image.height / 120)
            for (y in 0 until image.height step stepY) {
                for (x in 0 until image.width step stepX) {
                    val rgb = image.getRGB(x, y)
                    val red = rgb shr 16 and 0xFF
                    val green = rgb shr 8 and 0xFF
                    val blue = rgb and 0xFF
                    lumaTotal += 0.2126 * red + 0.7152 * green + 0.0722 * blue
                    samples++
                }
            }
            val averageLuma = lumaTotal / samples
            assertTrue("$name is still too dark: $averageLuma", averageLuma >= 45.0)
        }
    }

    private fun source(path: String): String = File(System.getProperty("user.dir"), path).readText()
}
