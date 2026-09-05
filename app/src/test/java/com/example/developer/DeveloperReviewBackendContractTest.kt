package com.example.developer

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeveloperReviewBackendContractTest {

    @Test
    fun `developer feedback repository uses protected Supabase inbox endpoints`() {
        val source = File("src/main/java/com/example/data/developer/DeveloperFeedbackRepository.kt").readText()

        assertTrue(source.contains("harmony-developer-feedback"))
        assertTrue(source.contains("is_ai_admin"))
        assertTrue(source.contains("developer_feedback"))
        assertTrue(source.contains("currentSessionOrNull"))
    }
}
