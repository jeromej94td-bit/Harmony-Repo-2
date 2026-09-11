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
        assertEquals(42, short.sceneHeightDp)
        assertEquals(24, short.sceneEmojiSp)
        assertEquals(5, short.gapDp)

        val compact = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 640, fontScale = 1f)
        assertTrue(compact.showScene)
        assertTrue(compact.showChapterLabel)
        assertEquals(50, compact.sceneHeightDp)
        assertEquals(26, compact.sceneEmojiSp)
        assertEquals(6, compact.gapDp)
    }

    @Test
    fun `normal portrait keeps the scene compact to prioritize answer readability`() {
        val metrics = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 800, fontScale = 1f)

        assertTrue(metrics.showScene)
        assertTrue(metrics.showChapterLabel)
        assertEquals(58, metrics.sceneHeightDp)
        assertEquals(28, metrics.sceneEmojiSp)
        assertEquals(8, metrics.gapDp)
    }

    @Test
    fun `large font scale also releases decorative space`() {
        val metrics = ScenarioPlayLayoutPolicy.metrics(screenHeightDp = 800, fontScale = 1.35f)

        assertTrue(metrics.showScene)
        assertFalse(metrics.showChapterLabel)
        assertEquals(42, metrics.sceneHeightDp)
        assertEquals(24, metrics.sceneEmojiSp)
    }
}
