package com.example

import com.example.data.model.RemovedGameCatalogPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemovedGameCatalogPolicyTest {

    @Test
    fun `mischung is blocked for categories and packs`() {
        assertFalse(RemovedGameCatalogPolicy.allowsCategoryId("mischung"))
        assertFalse(RemovedGameCatalogPolicy.allowsPackCategoryId("mischung"))
    }

    @Test
    fun `unrelated catalog ids remain allowed`() {
        assertTrue(RemovedGameCatalogPolicy.allowsCategoryId("tot"))
        assertTrue(RemovedGameCatalogPolicy.allowsPackCategoryId("wer"))
    }
}
