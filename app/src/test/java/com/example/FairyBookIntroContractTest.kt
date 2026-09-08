package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FairyBookIntroContractTest {

    @Test
    fun `animation fantasy pack opens through the fairy book intro before quiz start`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(main.contains("cj_disney_quiz"))
        assertTrue(main.contains("FairyBookIntroOverlay"))
        assertTrue(main.contains("pendingFairyBookIntro"))
        assertTrue(main.contains("skipFairyBookIntro"))
    }

    @Test
    fun `fairy book is code rendered with perspective pages glow and particles`() {
        val intro = source("app/src/main/java/com/example/ui/screens/FairyBookIntroOverlay.kt")

        assertTrue(intro.contains("rotationY"))
        assertTrue(intro.contains("TransformOrigin"))
        assertTrue(intro.contains("Canvas"))
        assertTrue(intro.contains("Brush.radialGradient"))
        assertTrue(intro.contains("drawCircle"))
        assertTrue(intro.contains("FairyDustParticle"))
        assertTrue(intro.contains("List(72)"))
        assertTrue(intro.contains("FairyBurstRays"))
        assertTrue(intro.contains("cameraPullback"))
        assertTrue(intro.contains("frontFaceAlpha"))
        assertTrue(intro.contains("segment(openProgress, 0.76f, 0.96f)"))
        assertTrue(intro.contains("tween(3100, easing = LinearEasing)"))
        assertTrue(intro.contains("segment(progress.value, 0.95f, 1f)"))
        assertTrue(intro.contains("text = \"HARMONY\""))
        assertFalse(intro.contains("Animationswelten, Abenteuer und Feenstaub"))
        assertFalse(intro.contains("Eine Welt öffnet sich für euch"))
        assertFalse(intro.contains("VideoView"))
        assertFalse(intro.contains("AndroidView"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
