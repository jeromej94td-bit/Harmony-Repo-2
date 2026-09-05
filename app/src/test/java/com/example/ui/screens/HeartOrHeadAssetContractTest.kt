package com.example.ui.screens

import com.example.R
import org.junit.Assert.assertTrue
import org.junit.Test

class HeartOrHeadAssetContractTest {

    @Test
    fun `all 24 Herz oder Kopf drawable resources exist`() {
        val expected = listOf(
            "heart_head_date_01", "heart_head_date_02", "heart_head_date_03", "heart_head_date_04",
            "heart_head_gift_01", "heart_head_gift_02", "heart_head_gift_03", "heart_head_gift_04",
            "heart_head_conflict_01", "heart_head_conflict_02", "heart_head_conflict_03", "heart_head_conflict_04",
            "heart_head_future_01", "heart_head_future_02", "heart_head_future_03", "heart_head_future_04",
            "heart_head_love_01", "heart_head_love_02", "heart_head_love_03", "heart_head_love_04",
            "heart_head_final_01", "heart_head_final_02", "heart_head_final_03", "heart_head_final_04"
        )
        val actual = R.drawable::class.java.fields.map { it.name }.toSet()
        expected.forEach { name -> assertTrue("missing drawable $name", name in actual) }
    }
}
