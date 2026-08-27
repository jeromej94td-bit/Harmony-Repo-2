package com.example.data

data class GameRunInitialState(
    val currentIndex: Int,
    val isFinished: Boolean
)

/** Pure resume/results policy shared by the ViewModel and regression tests. */
object GameRunPolicy {
    fun initialState(total: Int, answers: Map<Int, String>): GameRunInitialState {
        if (total <= 0) return GameRunInitialState(currentIndex = 0, isFinished = false)
        val answeredIndexes = answers
            .filterValues { it.isNotBlank() }
            .keys
            .filter { it in 0 until total }
            .toSet()
        val finished = answeredIndexes.size == total
        val index = if (finished) {
            (total - 1).coerceAtLeast(0)
        } else {
            (0 until total).firstOrNull { it !in answeredIndexes } ?: 0
        }
        return GameRunInitialState(currentIndex = index, isFinished = finished)
    }
}
