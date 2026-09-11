package com.example.ui.screens

internal data class ScenarioPlayLayoutMetrics(
    val showScene: Boolean,
    val showChapterLabel: Boolean,
    val sceneHeightDp: Int,
    val sceneEmojiSp: Int,
    val gapDp: Int
)

/**
 * Protects the interactive scenario choices from decorative chrome on short displays.
 * Very short landscape layouts drop the scene card entirely so the answer grid and primary
 * action remain usable; progressively taller layouts restore the current artwork proportions.
 */
internal object ScenarioPlayLayoutPolicy {
    fun metrics(screenHeightDp: Int, fontScale: Float): ScenarioPlayLayoutMetrics {
        val safeFontScale = fontScale.coerceAtLeast(1f)
        val veryShort = screenHeightDp < 480
        val veryCompact = screenHeightDp < 600 || safeFontScale >= 1.30f
        val compact = screenHeightDp < 700 || safeFontScale >= 1.15f

        return when {
            veryShort -> ScenarioPlayLayoutMetrics(
                showScene = false,
                showChapterLabel = false,
                sceneHeightDp = 0,
                sceneEmojiSp = 0,
                gapDp = 4
            )
            veryCompact -> ScenarioPlayLayoutMetrics(
                showScene = true,
                showChapterLabel = false,
                sceneHeightDp = 42,
                sceneEmojiSp = 24,
                gapDp = 5
            )
            compact -> ScenarioPlayLayoutMetrics(
                showScene = true,
                showChapterLabel = true,
                sceneHeightDp = 50,
                sceneEmojiSp = 26,
                gapDp = 6
            )
            else -> ScenarioPlayLayoutMetrics(
                showScene = true,
                showChapterLabel = true,
                sceneHeightDp = 58,
                sceneEmojiSp = 28,
                gapDp = 8
            )
        }
    }
}
