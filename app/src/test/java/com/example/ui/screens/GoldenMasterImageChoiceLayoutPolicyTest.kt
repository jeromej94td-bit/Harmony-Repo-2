package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class GoldenMasterImageChoiceLayoutPolicyTest {
    @Test
    fun `egg steak and travel keep the old three-column card choreography`() {
        listOf("EGG", "STEAK", "TRAVEL").forEach { kind ->
            val layout = GoldenMasterImageChoiceLayoutPolicy.forKindName(kind)
            assertNotNull(layout)
            layout!!
            assertEquals(3, layout.columns)
            assertEquals(28, layout.containerRadiusDp)
            assertEquals(11, layout.horizontalPaddingDp)
            assertEquals(15, layout.verticalPaddingDp)
            assertEquals(42, layout.headerIconSizeDp)
            assertEquals(21f, layout.questionFontSp)
            assertEquals(12f, layout.subtitleFontSp)
            assertEquals(7, layout.columnSpacingDp)
            assertEquals(8, layout.rowSpacingDp)
            assertEquals(0.63f, layout.cardAspectRatio)
            assertEquals(17, layout.cardRadiusDp)
            assertEquals(-82f, layout.startRotationY)
            assertEquals(-18f, layout.startTranslationXDp)
            assertEquals(10.5f, layout.titleFontSp)
            assertEquals(8f, layout.detailFontSp)
            assertEquals(17, layout.selectionIconSizeDp)
        }
    }

    @Test
    fun `other image mechanics keep their current Repo 2 layout`() {
        assertNull(GoldenMasterImageChoiceLayoutPolicy.forKindName("TRAUMHAUS"))
        assertNull(GoldenMasterImageChoiceLayoutPolicy.forKindName("HAPPY_COUPLE"))
    }
}
