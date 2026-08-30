package com.example.data

import org.junit.Test
import org.junit.Assert.assertTrue

class CheckBeziehungPacksTest {
    @Test
    fun testBeziehungPacks() {
        val packs = GeneratedContentRegistry.PACKS.filter { it.topic == "beziehung" }
        println("BEZIEHUNG PACKS COUNT: ${packs.size}")
        packs.forEach { println("BEZIEHUNG PACK: ${it.id} - ${it.title}") }
    }
}
