package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalVisualPolishContractTest {

    @Test
    fun `only proposal ring duels switch to wide stacked image cards`() {
        val source = source("src/main/java/com/example/ui/screens/ExperienceProductImageDuelBoard.kt")

        assertTrue(source.contains("ExperienceProductImageDuelLayout.SIDE_BY_SIDE"))
        assertTrue(source.contains("rootTestTag == \"proposal_ring_duel\""))
        assertTrue(source.contains("ExperienceProductImageDuelLayout.WIDE_STACKED"))
        assertTrue(source.contains("verticalArrangement = Arrangement.spacedBy(14.dp)"))
        assertFalse(source.contains("proposal_location"))
    }

    @Test
    fun `proposal text choices have glow depth and a short tilt interaction`() {
        val source = source("src/main/java/com/example/ui/screens/ExperienceEitherOrBoard.kt")

        assertTrue(source.contains("Animatable(0f)"))
        assertTrue(source.contains("rotationZ = tilt.value"))
        assertTrue(source.contains("shadowElevation = 18f * density"))
        assertTrue(source.contains("${'$'}{testTag}_glow"))
        assertTrue(source.contains("tilt.animateTo"))
    }

    @Test
    fun `autumn story alone gets a lighter warm image treatment`() {
        val source = source("src/main/java/com/example/ui/screens/AutumnEveningQuestion.kt")

        assertTrue(source.contains("storyImageTreatment = kind == HarmonyImageChoiceKind.AUTUMN_STORY"))
        assertTrue(source.contains("if (storyImageTreatment) 0.08f else 0.32f"))
        assertTrue(source.contains("BlendMode.Screen"))
        assertTrue(source.contains("warmStoryImage = storyImageTreatment"))
    }

    private fun source(path: String): String {
        val cwd = File(System.getProperty("user.dir"))
        val direct = File(cwd, path)
        if (direct.isFile) return direct.readText()
        val fromRepoRoot = File(cwd.parentFile ?: cwd, "app/$path")
        check(fromRepoRoot.isFile) { "Missing source file: $path from ${cwd.absolutePath}" }
        return fromRepoRoot.readText()
    }
}
