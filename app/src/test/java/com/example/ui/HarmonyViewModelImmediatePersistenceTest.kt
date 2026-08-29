package com.example.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.HarmonyDatabase
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HarmonyViewModelImmediatePersistenceTest {

    @Test
    fun `non final answer is persisted before the pack is finished`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = HarmonyDatabase.getInstance(app)
        val viewModel = HarmonyViewModel(app)
        val packId = "immediate_persistence_${UUID.randomUUID()}"
        val pack = QuestionPack(
            id = packId,
            title = "Immediate persistence",
            tags = listOf("test"),
            cat = "test",
            topic = "test",
            type = "quiz",
            questions = listOf(
                Question("Q1", listOf("A", "B")),
                Question("Q2", listOf("C", "D"))
            )
        )

        db.answerDao().deleteAnswersForPack(packId)
        viewModel.startPackForTest(pack)
        viewModel.pickAnswer("A")

        val persisted = withTimeout(2_000) {
            db.answerDao().getAnswersForPack(packId).first { answers ->
                answers.any { it.questionIndex == 0 && it.answerText == "A" }
            }
        }

        assertEquals("A", persisted.single { it.questionIndex == 0 }.answerText)
    }
}
