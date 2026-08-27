package com.example.ui.screens

import com.example.data.model.PersonSide
import kotlin.math.max

/** Lightweight geometry so drag/drop hit testing stays deterministic and unit-testable. */
internal data class DropRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    fun contains(x: Float, y: Float, hitSlop: Float = 0f): Boolean =
        x >= left - hitSlop &&
            x <= right + hitSlop &&
            y >= top - hitSlop &&
            y <= bottom + hitSlop

    fun distanceSquaredTo(x: Float, y: Float): Float {
        val dx = max(max(left - x, 0f), x - right)
        val dy = max(max(top - y, 0f), y - bottom)
        return dx * dx + dy * dy
    }
}

internal fun resolvePersonDrop(
    pointerX: Float,
    pointerY: Float,
    userBounds: DropRect?,
    partnerBounds: DropRect?,
    hitSlop: Float
): PersonSide? {
    val insideUser = userBounds?.contains(pointerX, pointerY) == true
    val insidePartner = partnerBounds?.contains(pointerX, pointerY) == true
    when {
        insideUser && !insidePartner -> return PersonSide.USER
        insidePartner && !insideUser -> return PersonSide.PARTNER
    }

    val nearUser = userBounds?.contains(pointerX, pointerY, hitSlop) == true
    val nearPartner = partnerBounds?.contains(pointerX, pointerY, hitSlop) == true
    return when {
        nearUser && !nearPartner -> PersonSide.USER
        nearPartner && !nearUser -> PersonSide.PARTNER
        nearUser && nearPartner -> {
            val userDistance = userBounds?.distanceSquaredTo(pointerX, pointerY) ?: Float.POSITIVE_INFINITY
            val partnerDistance = partnerBounds?.distanceSquaredTo(pointerX, pointerY) ?: Float.POSITIVE_INFINITY
            if (partnerDistance < userDistance) PersonSide.PARTNER else PersonSide.USER
        }
        else -> null
    }
}

/**
 * Ranking/assignment options are rendered directly underneath the prompt. Generated Harmony 360
 * questions used to repeat those same options in the headline, wasting most of the visible space.
 */
internal fun compactInteractionQuestion(question: String, options: List<String>): String {
    if (options.size < 2) return question.trim()

    val splitAt = question.lastIndexOf(':')
    if (splitAt <= 0 || splitAt >= question.lastIndex) return question.trim()

    val suffix = question.substring(splitAt + 1)
    val repeatsVisibleOptions = options.all { option ->
        suffix.contains(option.trim(), ignoreCase = true)
    }
    if (!repeatsVisibleOptions) return question.trim()

    return question
        .substring(0, splitAt)
        .replace(Regex("\\s+(?:rank|rankt|ordne|sortiere|ranking)\\s*$", RegexOption.IGNORE_CASE), "")
        .trim()
}
