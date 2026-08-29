package com.example.ui

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse

class GamesScreenPerformanceContractTest {

    @Test
    fun `topic cards do not run continuous per-item animations`() {
        val source = gamesScreenSource()

        assertFalse(
            source.contains("label = \"topic_power_"),
            "Topic cards must not create their own infinite transition while the Games screen uses a non-lazy vertical scroll container."
        )
        assertFalse(
            source.contains("label = \"topic_energy_"),
            "Topic cards must not run a continuous travelling-energy animation for every topic at once."
        )
        assertFalse(
            source.contains("label = \"topic_breathe_"),
            "Topic cards must not continuously rescale every topic card while scrolling."
        )
    }

    private fun gamesScreenSource(): String {
        val candidates = listOf(
            File("src/main/java/com/example/ui/screens/GamesScreen.kt"),
            File("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("GamesScreen.kt not found from test working directory ${File(".").absolutePath}")
    }
}
