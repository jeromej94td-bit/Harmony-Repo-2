package com.example.data.repository

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.HarmonyDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HarmonyRepositoryAnswerPersistenceTest {

    @Test
    fun `saving the same answer twice records one brain history entry but a changed answer records another`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = HarmonyDatabase.getInstance(app)
        val repository = HarmonyRepository(db, app)
        val packId = "answer_idempotency_${UUID.randomUUID()}"
        val questionIndex = 0
        val questionId = "$packId-$questionIndex"

        val beforeCount = db.brainRoomDao().getAllAnswerHistory()
            .count { it.questionId == questionId }

        repository.saveAnswer(packId, questionIndex, "Antwort A")
        repository.saveAnswer(packId, questionIndex, "Antwort A")

        val afterDuplicateSave = db.brainRoomDao().getAllAnswerHistory()
            .filter { it.questionId == questionId }
        assertEquals(beforeCount + 1, afterDuplicateSave.size)

        repository.saveAnswer(packId, questionIndex, "Antwort B")
        repository.saveAnswer(packId, questionIndex, "Antwort B")

        val afterChangedAnswer = db.brainRoomDao().getAllAnswerHistory()
            .filter { it.questionId == questionId }
        assertEquals(beforeCount + 2, afterChangedAnswer.size)
        assertEquals("Antwort B", afterChangedAnswer.first().answerPersonA)
    }
}
