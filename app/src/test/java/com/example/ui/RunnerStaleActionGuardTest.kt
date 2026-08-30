package com.example.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RunnerStaleActionGuardTest {

    @Suppress("UNCHECKED_CAST")
    private fun activeRunFlow(viewModel: HarmonyViewModel): StateFlow<ActivePackRun?> {
        val field = HarmonyViewModel::class.java.getDeclaredField("_activeRun").apply {
            isAccessible = true
        }
        return field.get(viewModel) as StateFlow<ActivePackRun?>
    }

    private fun TestScope.observeUiState(viewModel: HarmonyViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        runCurrent()
    }

    private fun viewModel(packType: String = "quiz"): HarmonyViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        return HarmonyViewModel(app).apply {
            startPackForTest(
                QuestionPack(
                    id = "stale_action_guard_$packType",
                    title = "Stale action guard",
                    tags = emptyList(),
                    cat = "test",
                    topic = "test",
                    type = packType,
                    questions = listOf(
                        Question("Q1", listOf("A", "B")),
                        Question("Q2", listOf("A", "B")),
                        Question("Q3", listOf("A", "B"))
                    )
                )
            )
        }
    }

    @Test
    fun `stale double tap answer cannot answer the next question`() = runTest {
        val viewModel = viewModel()
        observeUiState(viewModel)
        val activeRun = activeRunFlow(viewModel)

        viewModel.pickAnswer("A", expectedIndex = 0)
        viewModel.pickAnswer("A", expectedIndex = 0)

        assertEquals(1, activeRun.value?.currentIndex)
        assertEquals("A", activeRun.value?.currentAnswers?.get(0))
        assertFalse(activeRun.value?.currentAnswers?.containsKey(1) == true)
    }

    @Test
    fun `stale double tap skip cannot skip the next question`() = runTest {
        val viewModel = viewModel()
        observeUiState(viewModel)
        val activeRun = activeRunFlow(viewModel)

        viewModel.skipCurrentQuestion(expectedIndex = 0)
        viewModel.skipCurrentQuestion(expectedIndex = 0)

        assertEquals(1, activeRun.value?.currentIndex)
    }

    @Test
    fun `stale draw done callback cannot advance twice`() = runTest {
        val viewModel = viewModel(packType = "draw")
        observeUiState(viewModel)
        val activeRun = activeRunFlow(viewModel)

        viewModel.pickAnswer("DRAWING_COMPLETED", expectedIndex = 0)
        viewModel.nextStep(expectedIndex = 0)
        viewModel.nextStep(expectedIndex = 0)

        assertEquals(1, activeRun.value?.currentIndex)
        assertEquals("DRAWING_COMPLETED", activeRun.value?.currentAnswers?.get(0))
    }
}
