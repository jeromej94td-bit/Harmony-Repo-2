package com.example

import com.example.data.model.AnswerEntity
import com.example.data.model.SecretPlanAnswerCodec
import com.example.data.model.SecretPlanChoice
import com.example.data.model.SecretPlanPair
import com.example.data.model.SecretPlanProgress
import com.example.data.model.SecretPlanCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SecretPlanProgressTest {
    @Test
    fun `codec preserves preset and trimmed custom choices`() {
        val encoded = SecretPlanAnswerCodec.encode(
            SecretPlanChoice.Preset("Kleiner Roadtrip"),
            SecretPlanChoice.Custom("  Lissabon   im Frühling  ")
        )

        assertEquals(
            SecretPlanPair(
                SecretPlanChoice.Preset("Kleiner Roadtrip"),
                SecretPlanChoice.Custom("Lissabon im Frühling")
            ),
            SecretPlanAnswerCodec.decode(encoded)
        )
        assertNull(SecretPlanAnswerCodec.decode("secret-plan-v1:custom:"))
    }

    @Test
    fun `progress groups equal choices as now plan and different choices as also nice`() {
        val history = listOf(
            AnswerEntity(SecretPlanCatalog.PACK_ID, 0, SecretPlanAnswerCodec.encode("Kleiner Roadtrip", "Kleiner Roadtrip"), 1L),
            AnswerEntity(SecretPlanCatalog.PACK_ID, 1, SecretPlanAnswerCodec.encode("Ein geplanter Abend", "Eine persönliche Geste"), 2L)
        )

        val lines = requireNotNull(
            SecretPlanProgress.lines(
                history = history,
                currentIndex = 2,
                current = SecretPlanPair("Unser nächster Kurztrip", "Unser nächster Kurztrip")
            )
        )

        assertEquals(listOf("Kleiner Roadtrip", "Unser nächster Kurztrip"), lines.filter { it.isShared }.map { it.first.text })
        assertEquals("Ein geplanter Abend", lines.single { !it.isShared }.first.text)
    }
}
