package com.example

import com.example.ui.screens.ScenarioPlayLayoutPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScenarioPlayLayoutPolicyTest {

    @Test
    fun `very short landscape hides decorative scene to protect answer space`() {
        val metrics = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 360, fontScale = 1f)

        assertFalse(metrics.showScene)
        assertFalse(metrics.showChapterLabel)
        assertEquals(4, metrics.gapDp)
        assertEquals(0, metrics.sceneHeightDp)
    }

    @Test
    fun `short and compact phones progressively restore scene artwork`() {
        val short = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 520, fontScale = 1f)
        assertTrue(short.showScene)
        assertFalse(short.showChapterLabel)
        assertEquals(56, short.sceneHeightDp)
        assertEquals(32, short.sceneEmojiSp)
        assertEquals(6, short.gapDp)

        val compact = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 640, fontScale = 1f)
        assertTrue(compact.showScene)
        assertTrue(compact.showChapterLabel)
        assertEquals(84, compact.sceneHeightDp)
        assertEquals(44, compact.sceneEmojiSp)
        assertEquals(8, compact.gapDp)
    }

    @Test
    fun `normal portrait keeps current visual proportions`() {
        val metrics = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 800, fontScale = 1f)

        assertTrue(metrics.showScene)
        assertTrue(metrics.showChapterLabel)
        assertEquals(112, metrics.sceneHeightDp)
        assertEquals(60, metrics.sceneEmojiSp)
        assertEquals(12, metrics.gapDp)
    }

    @Test
    fun `large font scale also releases decorative space`() {
        val metrics = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 800, fontScale = 1.35f)

        assertTrue(metrics.showScene)
        assertFalse(metrics.showChapterLabel)
        assertEquals(56, metrics.sceneHeightDp)
        assertEquals(32, metrics.sceneEmojiSp)
    }
}
