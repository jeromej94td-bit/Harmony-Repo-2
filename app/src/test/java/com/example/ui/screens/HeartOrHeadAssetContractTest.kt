package com.example.ui.screens

import com.example.R
import org.junit.Assert.assertTrue
import org.junit.Test

class HeartOrHeadAssetContractTest {

    @Test
    fun `Herz oder Kopf source art and finale symbols exist`() {
        val expected = listOf(
            "heart_head_panda_atlas_01",
            "heart_head_panda_atlas_02",
            "heart_head_final_heart",
            "heart_head_final_head",
            "heart_head_final_gut",
            "heart_head_final_balance"
        )
        val actual = R.drawable::class.java.fields.map { it.name }.toSet()
        expected.forEach { name -> assertTrue("missing drawable $name", name in actual) }
    }
}
