package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HeartOrHeadTransitionPolicyTest {

    private val policyClass = Class.forName("com.example.ui.screens.HarmonyImageChoicePolicyKt")

    private fun invokeLong(name: String, vararg args: Any): Long {
        val method = policyClass.methods.firstOrNull { it.name == name && it.parameterCount == args.size }
            ?: error("Missing transition helper $name")
        return method.invoke(null, *args) as Long
    }

    @Test
    fun `four cards use strictly staggered delays`() {
        val delays = (0 until 4).map { invokeLong("heartOrHeadCardDelayMillis", it) }
        assertEquals(0L, delays.first())
        assertTrue(delays.zipWithNext().all { (a, b) -> b > a })
    }

    @Test
    fun `question waits for all four cards to finish`() {
        val total = invokeLong("heartOrHeadCardsTransitionDurationMillis", 4)
        val lastDelay = invokeLong("heartOrHeadCardDelayMillis", 3)
        val cardDuration = invokeLong("heartOrHeadCardFlipDurationMillis")
        assertEquals(lastDelay + cardDuration, total)
    }
}
