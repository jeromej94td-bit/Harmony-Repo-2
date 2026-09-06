package com.example.developer

import com.example.data.developer.DeveloperFeedbackScreenshotPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class DeveloperFeedbackScreenshotPolicyTest {

    @Test
    fun `screenshot path stays private and scoped to feedback id`() {
        val id = UUID.fromString("12345678-1234-1234-1234-123456789abc")

        assertEquals(
            "screenshots/12345678-1234-1234-1234-123456789abc.jpg",
            DeveloperFeedbackScreenshotPolicy.objectPath(id),
        )
    }

    @Test
    fun `screenshot policy rejects empty and oversized payloads`() {
        assertFalse(DeveloperFeedbackScreenshotPolicy.isUploadable(ByteArray(0)))
        assertTrue(DeveloperFeedbackScreenshotPolicy.isUploadable(ByteArray(1024)))
        assertFalse(
            DeveloperFeedbackScreenshotPolicy.isUploadable(
                ByteArray(DeveloperFeedbackScreenshotPolicy.MAX_BYTES + 1),
            ),
        )
    }
}
