---
name: repo-skills
description: Use when a Harmony task asks to move, sort, reclassify, assign, or reorganize games, packs, questions, or visible app categories.
---

# Repo Skills

## Core rule

Never invent a Harmony category, topic, pack ID, source section, or destination. Before suggesting or implementing any move, inspect the current repository and identify the real sorting path that currently controls that content.

## Required inspection

For every request containing ideas such as **verschieben**, **sortieren**, **umsortieren**, **einsortieren**, **Kategorie ändern** or **in einen anderen Bereich**, verify the current code before proposing a target.

Inspect, as applicable:

1. `GeneratedHarmonyAdrenaline360.kt` to understand the active curation pipeline.
2. The real `GeneratedHarmonyAdrenaline360SectionXX...kt` that owns the pack.
3. The relevant `Harmony360...Curation.kt` files for that section.
4. `Harmony360TopicNormalizationCuration.kt` and `Harmony360RelationshipTopicCuration.kt` for later overrides/filtering.
5. `GeneratedContentRegistry.kt` and `DeveloperDataManager.kt` when GENERATED/CUSTOM precedence can overwrite the result.
6. The actual visible top-level topics defined by the app. Only propose destinations that really exist in the current code.

## Before making a proposal

Confirm all of these from the repo:
- exact pack ID
- exact current title
- source section / owning curation
- current topic
- real available destination topics
- whether a later curation pass or CUSTOM copy can override the move

If any of those cannot be verified, do not guess. Continue inspecting or explicitly say that the destination is not yet verified.

## Implementation rule

A pure sorting request changes **only the placement/topic** unless the user explicitly asks for content changes.

Preserve:
- pack IDs
- titles
- questions
- answer options
- mechanics/category (`cat`/`type`)
- tags
- order

Do not archive, drop, deduplicate, rewrite, or replace packs as a side effect of sorting.

If stale CUSTOM/Dev-Studio copies have higher precedence, normalize their topic too so they cannot silently restore the old location.

## Regression guard

For a pure sorting pass, add or update a test that proves:
- input and output contain the same pack IDs in the same order
- no pack is added or removed by sorting
- questions/options/mechanics/tags stay unchanged
- only the intended topic values change

## Branch/version awareness

If Google AI Studio, another agent branch, or a screenshot describes a state that differs from current GitHub `main`, say so explicitly. Never treat an external log as proof that the same code exists in the repository.

## Failure pattern to avoid

Do not repeat the previous failure mode: applying a broad global topic policy based on assumed names while ignoring the real section-curation pipeline. The repository structure is the source of truth.
