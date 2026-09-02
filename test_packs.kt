package com.example.data

fun main() {
    val packs = GeneratedContentRegistry.PACKS.filter { it.topic == "beziehung" }
    println("Packs in beziehung: ${packs.size}")
    packs.forEach { println("- ${it.id}: ${it.title}") }
}
