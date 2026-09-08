package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class FairyBookFilamentContractTest {

    @Test
    fun `fairy book uses sceneview filament cinematic with glb and fallback`() {
        val renderer = source(
            "app/src/main/java/com/example/ui/screens/FairyBookFilamentIntro.kt"
        )
        val overlay = source(
            "app/src/main/java/com/example/ui/screens/FairyBookIntroOverlay.kt"
        )
        val versions = source("gradle/libs.versions.toml")
        val model = listOf(
            File("app/src/main/assets/models/harmony_magic_book.glb"),
            File("src/main/assets/models/harmony_magic_book.glb")
        ).firstOrNull(File::exists)

        assertTrue(versions.contains("sceneView = \"4.18.0\""))
        assertTrue(renderer.contains("SceneView("))
        assertTrue(renderer.contains("RenderQuality.Cinematic"))
        assertTrue(renderer.contains("rememberModelInstance"))
        assertTrue(renderer.contains("models/harmony_magic_book.glb"))
        assertTrue(renderer.contains("ModelNode("))
        assertTrue(renderer.contains("autoAnimate = true"))
        assertTrue(renderer.contains("bloomOptions"))
        assertTrue(overlay.contains("FairyBookCanvasFallback("))
        assertTrue(overlay.contains("FairyBookFilamentIntro("))
        assertTrue("Harmony magic-book GLB must exist", model != null)
        assertTrue(
            "Harmony magic-book GLB must not be empty",
            (model?.length() ?: 0L) > 1024L
        )
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path),
            File("../$path")
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
