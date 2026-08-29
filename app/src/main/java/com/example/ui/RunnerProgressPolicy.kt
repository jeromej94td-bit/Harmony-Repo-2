package com.example.ui

internal object RunnerProgressPolicy {
    fun firstUnanswered(total: Int, answeredIndexes: Set<Int>): Int? {
        if (total <= 0) return null
        return (0 until total).firstOrNull { it !in answeredIndexes }
    }

    fun nextUnanswered(total: Int, answeredIndexes: Set<Int>, afterIndex: Int): Int? {
        if (total <= 0) return null
        val boundedAfter = afterIndex.coerceIn(-1, total - 1)
        val afterRange = (boundedAfter + 1 until total)
        afterRange.firstOrNull { it !in answeredIndexes }?.let { return it }
        return (0..boundedAfter).firstOrNull { it in 0 until total && it !in answeredIndexes }
    }

    fun deferAutomaticAdvance(packType: String, answerText: String): Boolean =
        packType == "draw" && answerText == "DRAWING_COMPLETED"
}
