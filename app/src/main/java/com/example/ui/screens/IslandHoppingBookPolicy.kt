package com.example.ui.screens

internal object IslandHoppingBookPolicy {
    const val PACK_ID = "h500_089_inselhopping_prioritaet"

    fun isEnabled(packId: String): Boolean = packId == PACK_ID
}
