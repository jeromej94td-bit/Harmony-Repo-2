package com.example.data.model

/**
 * Product-level tombstones for game catalog entries that must never be surfaced again,
 * even when dynamic/generated content still contains legacy ids.
 */
object RemovedGameCatalogPolicy {
    private const val MISCHUNG_CATEGORY_ID = "mischung"

    fun allowsCategoryId(categoryId: String): Boolean =
        categoryId != MISCHUNG_CATEGORY_ID

    fun allowsPackCategoryId(categoryId: String): Boolean =
        categoryId != MISCHUNG_CATEGORY_ID
}
