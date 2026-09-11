package com.example

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ScenarioShortLandscapeSourceContractTest {

    @Test
    fun `scenario play uses responsive scene metrics instead of fixed hero height`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenChoiceMechanics.kt").readText()

        assertTrue(source.contains("val playMetrics = ScenarioPlayLayoutPolicy.metrics("))
        assertTrue(source.contains("if (playMetrics.showScene)"))
        assertTrue(source.contains(".height(playMetrics.sceneHeightDp.dp)"))
        assertTrue(source.contains("Text(sceneEmoji, fontSize = playMetrics.sceneEmojiSp.sp)"))
        assertTrue(source.contains("Spacer(Modifier.height(playMetrics.gapDp.dp))"))
        assertFalse(source.contains(".height(112.dp)"))
    }

    @Test
    fun `scenario play prioritizes readable answers over helper chrome`() {
        val scenarioSource = File("src/main/java/com/example/ui/screens/FullscreenChoiceMechanics.kt").readText()
        val commonSource = File("src/main/java/com/example/ui/screens/FullscreenMechanicCommon.kt").readText()

        assertFalse(scenarioSource.contains("Entscheidet euch – die Geschichte geht mit eurer Wahl weiter."))
        assertTrue(scenarioSource.contains("emphasizeLabels = true"))
        assertTrue(commonSource.contains("instruction.isNotBlank()"))
        assertTrue(commonSource.contains("emphasizeLabel = emphasizeLabels"))
    }

    @Test
    fun `large option cards derive padding from their actual constrained height`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenMechanicCommon.kt").readText()

        assertTrue(source.contains("LargeOptionCardLayoutPolicy.metrics(maxHeight.value.toInt())"))
        assertTrue(source.contains("horizontal = cardMetrics.horizontalPaddingDp.dp"))
        assertTrue(source.contains("vertical = cardMetrics.verticalPaddingDp.dp"))
        assertFalse(source.contains(".padding(horizontal = 12.dp, vertical = 12.dp)"))
    }
}
