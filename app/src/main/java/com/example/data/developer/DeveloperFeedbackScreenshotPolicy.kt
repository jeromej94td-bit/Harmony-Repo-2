package com.example.data.developer

import java.util.UUID

object DeveloperFeedbackScreenshotPolicy {
    const val MAX_BYTES: Int = 5 * 1024 * 1024

    fun objectPath(clientFeedbackId: UUID): String =
        "screenshots/$clientFeedbackId.jpg"

    fun isUploadable(bytes: ByteArray): Boolean =
        bytes.isNotEmpty() && bytes.size <= MAX_BYTES
}
