package com.example.ui.screens

import com.example.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HappyCoupleResultImageMappingTest {
    @Test
    fun `result answer uses the same four happy couple images as gameplay`() {
        assertEquals(R.drawable.love_balance_happy_green_20260912, happyCoupleImageResForAnswer("1"))
        assertEquals(R.drawable.love_balance_happy_yellow_20260912, happyCoupleImageResForAnswer("2"))
        assertEquals(R.drawable.love_balance_happy_red_20260912, happyCoupleImageResForAnswer("3"))
        assertEquals(R.drawable.love_balance_happy_blue_20260912, happyCoupleImageResForAnswer("4"))
        assertNull(happyCoupleImageResForAnswer(""))
        assertNull(happyCoupleImageResForAnswer("5"))
    }
}
