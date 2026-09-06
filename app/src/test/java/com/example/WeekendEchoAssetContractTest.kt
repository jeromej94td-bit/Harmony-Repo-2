package com.example

import com.example.data.model.WeekendEchoMotif
import com.example.ui.screens.drawableRes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeekendEchoAssetContractTest {
    @Test
    fun `every approved motif resolves to its own packaged drawable`() {
        val drawableIds = WeekendEchoMotif.entries.map { it.drawableRes() }

        assertEquals(8, drawableIds.size)
        assertEquals(8, drawableIds.distinct().size)
        assertTrue(drawableIds.all { it != 0 })
    }
}
