package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class HeartOrHeadImageChoicePolicyTest {

    @Test
    fun `herz oder kopf routes each round by stable pack index`() {
        val expected = listOf(
            "HEART_HEAD_DATE",
            "HEART_HEAD_GIFT",
            "HEART_HEAD_CONFLICT",
            "HEART_HEAD_FUTURE",
            "HEART_HEAD_LOVE",
            "HEART_HEAD_FINAL"
        )

        val actual = (0 until 6).map { index ->
            harmonyImageChoiceKind("herz_oder_kopf", index)?.name
        }

        assertEquals(expected, actual)
    }
}
