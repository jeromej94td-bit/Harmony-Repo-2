package com.example.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SkipExistingAnswerRegressionTest {

    private val pack = QuestionPack(
        id = "skip_existing_answer_test",
        title = "Skip Existing Answer Test",
        tags = listOf("test"),
        cat = "test",
        topic = "beziehung",
        type = "quiz",
        questions = listOf(
            Question("Q1", listOf("A", "B")),
            Question("Q2", listOf("C", "D"))
        )
    )

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `skip after returning to answered question removes stale run answer`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HarmonyViewModel(app)
        viewModel.startPackForTest(pack, currentIndex = 0)

        val activeRunField = HarmonyViewModel::class.java.getDeclaredField("_activeRun").apply {
            isAccessible = true
        }
        val activeRunFlow = activeRunField.get(viewModel) as MutableStateFlow<ActivePackRun?>
        activeRunFlow.value = ActivePackRun(
            pack = pack,
            currentIndex = 0,
            currentAnswers = mutableMapOf(0 to "A"),
            isFinished = false
        )

        viewModel.skipCurrentQuestion()

        assertEquals(1, activeRunFlow.value?.currentIndex)
        assertFalse(activeRunFlow.value?.currentAnswers?.containsKey(0) == true)
    }

    @Test
    fun `skip path removes only current durable answer before recording skip`() {
        val persistenceSource = source("app/src/main/java/com/example/data/repository/AnswerSkipPersistence.kt")
        val skipSource = source("app/src/main/java/com/example/ui/HarmonyViewModelSkip.kt")

        assertTrue(persistenceSource.contains("suspend fun HarmonyDatabase.deleteAnswerForSkip"))
        assertTrue(persistenceSource.contains("DELETE FROM answers WHERE packId = ? AND questionIndex = ?"))
        assertTrue(persistenceSource.contains("runInTransaction"))
        assertTrue(skipSource.contains("run.currentAnswers as? MutableMap<Int, String>"))
        assertTrue(skipSource.contains("remove(questionIndex)"))
        assertTrue(skipSource.contains("deleteAnswerForSkip(packId, questionIndex)"))
        assertTrue(skipSource.contains("recordBrainSkip(packId, questionIndex)"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
