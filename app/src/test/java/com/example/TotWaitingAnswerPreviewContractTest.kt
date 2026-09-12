package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TotWaitingAnswerPreviewContractTest {

    @Test
    fun `tot waiting state shows own selection with the gameplay image while partner stays hidden`() {
        val source = File("src/main/java/com/example/ui/screens/CouplePackRevealScreen.kt").readText()
        val revealCard = source
            .substringAfter("private fun CoupleQuestionRevealCard(")
            .substringBefore("private fun CoupleAnswerRow(")

        assertTrue(source.contains("private fun TotOwnAnswerPreview("))
        assertTrue(source.contains("text = tr(\"Deine Auswahl\", \"Your choice\")"))
        assertTrue(source.contains("TravelDestinationCatalog.assetKeyFor(pack.id, myAnswer) ?: myAnswer"))
        assertTrue(source.contains("TotImageProvider.getImageUrl("))
        assertTrue(revealCard.contains("showOwnTotPreview"))
        assertTrue(revealCard.contains("state != CoupleRevealState.NEEDS_OWN_ANSWER"))
        assertTrue(revealCard.contains("state != CoupleRevealState.READY"))

        val revealedBranch = revealCard
            .substringAfter("state == CoupleRevealState.READY ->")
            .substringBefore("state == CoupleRevealState.NEEDS_OWN_ANSWER")
        assertTrue(revealedBranch.contains("visible = ownVisible"))
        assertTrue(revealedBranch.contains("visible = partnerVisible"))
        assertTrue(revealCard.contains("val resolvedPartnerAnswer = result?.partnerAnswerText.orEmpty()"))
    }
}
