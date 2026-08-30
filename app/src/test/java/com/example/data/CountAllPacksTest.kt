package com.example.data

import org.junit.Test
import org.junit.Assert.assertTrue

class CountAllPacksTest {
    @Test
    fun countPacks() {
        val total = GeneratedContentRegistry.PACKS.size
        val beziehung = GeneratedContentRegistry.PACKS.count { it.topic == "beziehung" }
        val moral = GeneratedContentRegistry.PACKS.count { it.topic == "moral" }
        println("TOTAL PACKS: \$total")
        println("BEZIEHUNG: \$beziehung")
        println("MORAL: \$moral")
    }
}
