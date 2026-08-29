package com.example.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.brain.db.BrainInteractionEntity
import com.example.data.db.HarmonyDatabase
import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import com.example.ui.screens.buildPackResultRows
import com.example.ui.screens.hasCompletePackResults
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PackResultsAndSkipFlowTest {

    private val pack = QuestionPack(
        id = "results_test_pack",
        title = "Results Test",
        tags = listOf("test"),
        cat = "test",
        topic = "beziehung",
        type = "quiz",
        questions = listOf(
            Question("Q1", listOf("A", "B")),
            Question("Q2", listOf("C", "D"))
        )
    )

    @Test
    fun `all answered legacy pack can still route directly to results`() {
        val complete = listOf(
            AnswerEntity(pack.id, 0, "A"),
            AnswerEntity(pack.id, 1, "D")
        )
        val incomplete = complete.take(1)

        assertTrue(hasCompletePackResults(pack, complete))
        assertFalse(hasCompletePackResults(pack, incomplete))
    }

    @Test
    fun `results rows contain saved answers and decode couple choices`() {
        val answers = listOf(
            AnswerEntity(pack.id, 0, "A"),
            AnswerEntity(pack.id, 1, EitherOrAnswerCodec.encode("C", "D"))
        )

        val rows = buildPackResultRows(pack, answers)

        assertEquals(2, rows.size)
        assertEquals("Q1", rows[0].prompt)
        assertEquals("A", rows[0].answerText)
        assertNull(rows[0].coupleChoice)
        assertEquals("C", rows[1].coupleChoice?.userChoice)
        assertEquals("D", rows[1].coupleChoice?.partnerChoice)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `skip advances without creating a fake answer`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HarmonyViewModel(app)
        viewModel.startPackForTest(pack, currentIndex = 0)
        viewModel.uiState.first { it.activeRun != null }

        val activeRunField = HarmonyViewModel::class.java.getDeclaredField("_activeRun").apply {
            isAccessible = true
        }
        val activeRunFlow = activeRunField.get(viewModel) as StateFlow<ActivePackRun?>

        viewModel.skipCurrentQuestion()

        assertEquals(1, activeRunFlow.value?.currentIndex)
        assertTrue(activeRunFlow.value?.currentAnswers?.isEmpty() == true)
    }

    @Test
    fun `finished marker survives skipped questions and replay reset clears current result state`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = HarmonyDatabase.getInstance(app)
        val packId = "results_replay_reset_test"

        db.answerDao().deleteAnswersForPack(packId)
        db.brainRoomDao().clearFinishedPack(packId)
        db.answerDao().insertAnswer(AnswerEntity(packId, 0, "A"))
        db.brainRoomDao().insertInteraction(
            BrainInteractionEntity(
                contentId = packId,
                contentType = "PACK",
                action = "FINISHED_PACK"
            )
        )

        assertTrue(db.brainRoomDao().hasFinishedPack(packId))
        assertTrue(db.answerDao().getAllAnswersDirect().any { it.packId == packId })

        db.answerDao().deleteAnswersForPack(packId)
        db.brainRoomDao().clearFinishedPack(packId)

        assertFalse(db.brainRoomDao().hasFinishedPack(packId))
        assertFalse(db.answerDao().getAllAnswersDirect().any { it.packId == packId })
    }
}
