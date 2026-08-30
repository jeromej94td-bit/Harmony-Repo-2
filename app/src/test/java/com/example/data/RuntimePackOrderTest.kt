package com.example.data

import com.example.data.model.HarmonyPacksData
import com.example.data.model.QuestionPack
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test

class RuntimePackOrderTest {

    @After
    fun resetDynamicPacks() {
        HarmonyPacksData.setDynamicPacks(emptyList())
    }

    @Test
    fun `dynamic packs keep the order supplied by Dev Studio`() {
        val first = pack("runtime_order_first")
        val second = pack("runtime_order_second")
        val third = pack("runtime_order_third")

        HarmonyPacksData.setDynamicPacks(listOf(first, second, third))

        val ids = HarmonyPacksData.PACKS.map { it.id }
        assertTrue(ids.indexOf(first.id) < ids.indexOf(second.id))
        assertTrue(ids.indexOf(second.id) < ids.indexOf(third.id))
    }

    private fun pack(id: String) = QuestionPack(
        id = id,
        title = id,
        tags = listOf("test"),
        cat = "test",
        topic = "test",
        type = "quiz"
    )
}
