package com.example.data.model

/**
 * UI-independent mechanic kinds supported by the reusable Harmony experience core.
 *
 * This intentionally contains only mechanics already proven by the proposal experience.
 */
enum class ExperienceStepKind {
    EITHER_OR,
    IMAGE_DUEL,
    RANKING,
    PARTNER_PREDICTION,
    SCENARIO,
    OPEN_PROMPT,
    REVEAL
}

data class ExperienceStep(
    val id: String,
    val kind: ExperienceStepKind
) {
    init {
        require(id.isNotBlank()) { "Experience steps need a stable id." }
    }
}

class ExperienceDefinition(
    val id: String,
    val title: String,
    steps: List<ExperienceStep>
) {
    val steps: List<ExperienceStep> = steps.toList()

    init {
        require(id.isNotBlank()) { "Experiences need a stable id." }
        require(title.isNotBlank()) { "Experiences need a title." }
        require(this.steps.isNotEmpty()) { "Experiences need at least one step." }
        require(this.steps.map(ExperienceStep::id).distinct().size == this.steps.size) {
            "Experience step ids must be unique."
        }
        require(this.steps.count { it.kind == ExperienceStepKind.REVEAL } == 1) {
            "An experience needs exactly one reveal."
        }
        require(this.steps.last().kind == ExperienceStepKind.REVEAL) {
            "An experience must end with its reveal."
        }
    }

    fun nextStepAfter(stepId: String): ExperienceStep? {
        val index = steps.indexOfFirst { it.id == stepId }
        return if (index < 0) null else steps.getOrNull(index + 1)
    }
}

data class ExperiencePosition(
    val stepIndex: Int,
    val itemIndex: Int
)

/**
 * Deterministic navigation over an experience definition.
 *
 * Content remains feature-owned. The navigator asks the caller only how many subrounds a stable
 * step id contains and normalizes non-positive counts to one safe navigable item.
 */
class ExperienceNavigator(
    private val definition: ExperienceDefinition,
    private val itemCountResolver: (String) -> Int
) {
    private fun countFor(step: ExperienceStep): Int =
        itemCountResolver(step.id).coerceAtLeast(1)

    fun totalItemCount(): Int = definition.steps.sumOf(::countFor)

    fun currentStep(position: ExperiencePosition): ExperienceStep? {
        if (position.stepIndex !in definition.steps.indices) return null
        val step = definition.steps[position.stepIndex]
        if (position.itemIndex !in 0 until countFor(step)) return null
        return step
    }

    fun next(position: ExperiencePosition): ExperiencePosition? {
        val step = currentStep(position) ?: return null
        val count = countFor(step)

        if (position.itemIndex + 1 < count) {
            return position.copy(itemIndex = position.itemIndex + 1)
        }
        if (position.stepIndex >= definition.steps.lastIndex) return null

        return ExperiencePosition(
            stepIndex = position.stepIndex + 1,
            itemIndex = 0
        )
    }

    fun progress(position: ExperiencePosition): Float {
        if (currentStep(position) == null) return 0f

        val totalItems = totalItemCount()
        if (totalItems <= 1) return 1f

        val completedBefore = definition.steps
            .take(position.stepIndex)
            .sumOf(::countFor)

        return ((completedBefore + position.itemIndex).toFloat() / (totalItems - 1).toFloat())
            .coerceIn(0f, 1f)
    }
}
