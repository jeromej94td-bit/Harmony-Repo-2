package com.example

import com.example.ui.screens.LargeOptionCardLayoutPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class LargeOptionCardLayoutPolicyTest {

    @Test
    fun `very short cards release vertical padding for readable labels`() {
        val metrics = LargeOptionCardLayoutPolicy.metrics(cardHeightDp = 40)
        assertEquals(8, metrics.horizontalPaddingDp)
        assertEquals(4, metrics.verticalPaddingDp)
    }

    @Test
    fun `short cards progressively restore padding`() {
        val short = LargeOptionCardLayoutPolicy.metrics(cardHeightDp = 80)
        assertEquals(10, short.horizontalPaddingDp)
        assertEquals(6, short.verticalPaddingDp)

        val compact = LargeOptionCardLayoutPolicy.metrics(cardHeightDp = 120)
        assertEquals(10, compact.horizontalPaddingDp)
        assertEquals(8, compact.verticalPaddingDp)
    }

    @Test
    fun `normal cards keep the existing twelve dp inset`() {
        val metrics = LargeOptionCardLayoutPolicy.metrics(cardHeightDp = 180)
        assertEquals(12, metrics.horizontalPaddingDp)
        assertEquals(12, metrics.verticalPaddingDp)
    }
}
