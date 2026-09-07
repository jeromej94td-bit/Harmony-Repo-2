package com.example.developer

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeveloperReviewInboxWiringContractTest {

    @Test
    fun `quick note can open full developer inbox`() {
        val quickNote = File("src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt").readText()
        val inbox = File("src/main/java/com/example/ui/screens/DeveloperReviewInboxScreen.kt").readText()

        assertTrue(quickNote.contains("DeveloperReviewInboxScreen"))
        assertTrue(quickNote.contains("Inbox"))
        assertTrue(inbox.contains("Offene Hinweise"))
        assertTrue(inbox.contains("DeveloperReviewInboxPolicy.apply"))
    }

    @Test
    fun `inbox exposes review status transitions`() {
        val inbox = File("src/main/java/com/example/ui/screens/DeveloperReviewInboxScreen.kt").readText()

        assertTrue(inbox.contains("DeveloperFeedbackStatus.NEW -> DeveloperFeedbackStatus.REVIEWED"))
        assertTrue(inbox.contains("DeveloperFeedbackStatus.REVIEWED -> DeveloperFeedbackStatus.IN_PROGRESS"))
        assertTrue(inbox.contains("DeveloperFeedbackStatus.FIXED -> DeveloperFeedbackStatus.VERIFIED"))
        assertTrue(inbox.contains("reviewViewModel.updateStatus"))
    }
}
