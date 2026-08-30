package com.example.data.model

/**
 * Feature-neutral reveal/result content for reusable Harmony experiences.
 *
 * The experience engine owns only the renderable result shape. Feature-specific synthesis stays
 * with the feature that collected the answers and can be adapted into this model at the boundary.
 */
data class ExperienceRevealSection(
    val id: String,
    val title: String,
    val values: List<String>
) {
    init {
        require(id.isNotBlank()) { "Reveal sections need a stable id." }
        require(title.isNotBlank()) { "Reveal sections need a title." }
        require(values.isNotEmpty()) { "Reveal sections need at least one value." }
        require(values.all(String::isNotBlank)) { "Reveal section values cannot be blank." }
    }
}

data class ExperienceRevealResult(
    val title: String,
    val subtitle: String,
    val sections: List<ExperienceRevealSection>,
    val closing: String
) {
    init {
        require(title.isNotBlank()) { "Experience reveal needs a title." }
        require(subtitle.isNotBlank()) { "Experience reveal needs a subtitle." }
        require(closing.isNotBlank()) { "Experience reveal needs a closing line." }
    }
}
