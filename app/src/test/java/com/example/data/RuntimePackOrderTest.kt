package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.HarmonyPacksData
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RuntimePackOrderTest {

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @After
    fun resetState() {
        DeveloperDataManager.resetAll(context)
        HarmonyPacksData.setDynamicPacks(emptyList())
    }

    @Test
    fun `Dev Studio pack order survives the sync into runtime packs`() {
        DeveloperDataManager.resetAll(context)

        val restoredProject = """
            {
              "packs": [
                {"id":"runtime_order_first","title":"First","tags":["test"],"cat":"test","topic":"test","type":"quiz","questions":[],"pairs":[]},
                {"id":"runtime_order_second","title":"Second","tags":["test"],"cat":"test","topic":"test","type":"quiz","questions":[],"pairs":[]},
                {"id":"runtime_order_third","title":"Third","tags":["test"],"cat":"test","topic":"test","type":"quiz","questions":[],"pairs":[]}
              ],
              "packOrder": [
                "runtime_order_first",
                "runtime_order_second",
                "runtime_order_third"
              ]
            }
        """.trimIndent()

        DeveloperDataManager.importProjectJson(
            context = context,
            rawText = restoredProject,
            replace = true
        )

        val runtimeIds = HarmonyPacksData.PACKS
            .map { it.id }
            .filter { it.startsWith("runtime_order_") }

        assertEquals(
            listOf(
                "runtime_order_first",
                "runtime_order_second",
                "runtime_order_third"
            ),
            runtimeIds
        )
    }
}
