package com.example.ui.introspection

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntrospectionNarrationFlowContractTest {
    @Test
    fun `intro gates narrator and revelation uses the stage based golden master route`() {
        val moduleRoot = sequenceOf(File("."), File(".."))
            .first { File(it, "src/main/java").exists() }
        val screen = File(
            moduleRoot,
            "src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt"
        ).readText()

        // The intro remains the gate: narrator must not start while the video overlay is active.
        assertTrue(screen.contains("if (showIntroVideo)"))
        assertTrue(screen.contains("mediaController.stopNarrator()"))
        assertTrue(screen.contains("onCompleted = {"))
        assertTrue(screen.contains("showIntroVideo = false"))

        // Question stages already use the stage router. Revelation must use it too so all four
        // narrator tracks resolve to the verified *_golden resources in one place.
        assertTrue(screen.contains("mediaController.playNarratorForStage(progress.stage)"))
        assertTrue(
            screen.contains(
                "mediaController.playNarratorForStage(IntrospectionStage.REVELATION)"
            )
        )
        assertFalse(
            "Revelation must not bypass the Golden-Master stage router",
            screen.contains("mediaController.playNarrator(com.example.R.raw.introspection_reveal)")
        )

        // Results must still be entered only from the real narrator completion callback.
        assertTrue(screen.contains("val completedProgress = progress.finishRevelation()"))
        assertTrue(screen.contains("screenStateName = ScreenState.RESULTS.name"))
    }
}
