package com.example.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CoupleResultAutoRevealContractTest {

    @Test
    fun `paired results reveal automatically in stages and expose replay action`() {
        val source = File("src/main/java/com/example/ui/screens/CouplePackRevealScreen.kt").readText()
        val main = File("src/main/java/com/example/MainActivity.kt").readText()
        val packResults = File("src/main/java/com/example/ui/screens/PackResultsScreen.kt").readText()

        assertFalse(source.contains("Ergebnis ansehen"))
        assertTrue(source.contains("delay(110)"))
        assertTrue(source.contains("ownVisible = true"))
        assertTrue(source.contains("delay(230)"))
        assertTrue(source.contains("partnerVisible = true"))
        assertTrue(source.contains("rotationY = rotation.value"))
        assertTrue(source.contains("couple_replay_button"))
        assertTrue(source.contains("answerImageModel = partnerImageModel"))
        assertTrue(source.contains("coupleResultPresentation(pack, questionIndex"))
        assertTrue(source.contains("presentation.detailLines.forEach"))
        assertTrue(main.contains("onReplay = {"))
        assertTrue(main.contains("openPackForPlay(activeRun.pack.id, freshRun = true)"))
        assertTrue(packResults.contains("onReplay = onReplay"))
    }
}
