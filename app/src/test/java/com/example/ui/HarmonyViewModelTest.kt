package com.example.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HarmonyViewModelTest {

    private fun testViewModelWithActiveRun(currentIndex: Int): HarmonyViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = HarmonyViewModel(app)
        
        val fakePack = QuestionPack(
            id = "test_pack",
            title = "Test Pack",
            tags = emptyList(),
            cat = "test_cat",
            topic = "test_topic",
            type = "quiz",
            questions = listOf(
                Question("Q1"),
                Question("Q2"),
                Question("Q3"),
                Question("Q4")
            )
        )
        
        viewModel.startPackForTest(fakePack, currentIndex)
        return viewModel
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `askExitRun opens exit confirmation instead of changing question index`() = runTest {
        val viewModel = testViewModelWithActiveRun(currentIndex = 2)
        
        // Extract private StateFlows via reflection to verify business logic directly
        val exitConfirmField = HarmonyViewModel::class.java.getDeclaredField("_isExitConfirmOpen").apply {
            isAccessible = true
        }
        val isExitConfirmOpenFlow = exitConfirmField.get(viewModel) as StateFlow<Boolean>

        val activeRunField = HarmonyViewModel::class.java.getDeclaredField("_activeRun").apply {
            isAccessible = true
        }
        val activeRunFlow = activeRunField.get(viewModel) as StateFlow<ActivePackRun?>
        
        viewModel.askExitRun()
        
        assertTrue(isExitConfirmOpenFlow.value)
        assertEquals(2, activeRunFlow.value?.currentIndex)
    }
}
