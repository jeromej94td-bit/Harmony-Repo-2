package com.example.data.model

/** Keeps the shipped proposal reveal output intact while exposing it to the reusable experience UI. */
fun ProposalRevealSection.toExperienceRevealSection(): ExperienceRevealSection =
    ExperienceRevealSection(
        id = id,
        title = title,
        values = values
    )

fun ProposalRevealResult.toExperienceRevealResult(): ExperienceRevealResult =
    ExperienceRevealResult(
        title = title,
        subtitle = subtitle,
        sections = sections.map(ProposalRevealSection::toExperienceRevealSection),
        closing = closing
    )
