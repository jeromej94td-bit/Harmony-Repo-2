package com.example.developer

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeveloperReviewQuickNoteContextContractTest {
    @Test
    fun `quick note records active pack round and question context`() {
        val source = File("src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt").readText()
        assertTrue(source.contains("gameId = activeRun.pack.id"))
        assertTrue(source.contains("round = activeRun.currentIndex + 1"))
        assertTrue(source.contains("questionId = \"${'$'}{activeRun.pack.id}:${'$'}{activeRun.currentIndex}\""))
        assertTrue(source.contains("questionText = currentQuestion?.q"))
    }
}
