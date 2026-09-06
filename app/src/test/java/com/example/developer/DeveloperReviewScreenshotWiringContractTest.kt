package com.example.developer

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeveloperReviewScreenshotWiringContractTest {

    @Test
    fun `quick note can capture and attach current app screen`() {
        val quickNote = File("src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt").readText()
        val capture = File("src/main/java/com/example/ui/developer/DeveloperReviewScreenshotCapture.kt").readText()

        assertTrue(quickNote.contains("📷 Screenshot anhängen"))
        assertTrue(quickNote.contains("captureDeveloperReviewScreenshot"))
        assertTrue(quickNote.contains("screenshotBytes = pendingScreenshot"))
        assertTrue(capture.contains("android.R.id.content"))
        assertTrue(capture.contains("Bitmap.CompressFormat.JPEG"))
    }

    @Test
    fun `repository uploads screenshot only to private developer feedback bucket`() {
        val repository = File("src/main/java/com/example/data/developer/DeveloperFeedbackRepository.kt").readText()

        assertTrue(repository.contains("DeveloperFeedbackScreenshotPolicy.objectPath"))
        assertTrue(repository.contains("/storage/v1/object/developer-feedback/"))
        assertTrue(repository.contains("image/jpeg"))
        assertTrue(repository.contains("screenshotPath"))
    }
}
