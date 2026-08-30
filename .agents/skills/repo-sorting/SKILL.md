---
name: repo-sorting
description: Use when a Harmony task asks to move, sort, reclassify, assign, or reorganize games, packs, questions, or visible app categories.
---

# Repo Sorting

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

## Re-home before archive

When a cleanup/rework task includes sorting as well as explicit content changes, useful content must be **re-homed before it is archived or dropped**.

- If a pack belongs cleanly to another existing topic, move the pack there rather than deleting it.
- If a source pack mixes several real topics and Harmony only routes at pack level, do **not** mislabel the whole pack as one destination.
- Instead, merge the useful questions or ideas into suitable existing destination packs while preserving their real topic and mechanics as far as practical.
- Record the source-to-destination mapping in code/tests so the move is reviewable.
- When the mixed source disappears only because its useful content was redistributed, classify that source as `MERGE`, not `ARCHIVE`.
- Use `ARCHIVE` only for content that is genuinely redundant, low-value, or intentionally removed after the redistribution check.
- Prefer existing stable destination pack IDs. Do not invent split-pack IDs merely to make sorting easier unless the user explicitly approves a new pack.

This rule does not weaken the pure-sorting rule above: a request that is only about placement must still preserve content unchanged.

## Regression guard

For a pure sorting pass, add or update a test that proves:
- input and output contain the same pack IDs in the same order
- no pack is added or removed by sorting
- questions/options/mechanics/tags stay unchanged
- only the intended topic values change

For an approved mixed-content redistribution, additionally test:
- the source is classified as `MERGE` rather than silently archived
- every approved source idea is present in its intended destination pack
- destination pack IDs and final topics remain valid
- the merged source is not still exposed as a duplicate runtime pack

## Branch/version awareness

If Google AI Studio, another agent branch, or a screenshot describes a state that differs from current GitHub `main`, say so explicitly. Never treat an external log as proof that the same code exists in the repository.

## Failure pattern to avoid

Do not repeat the previous failure mode: applying a broad global topic policy based on assumed names while ignoring the real section-curation pipeline. The repository structure is the source of truth.

Do not repeat the cleanup failure mode of archiving a mixed pack before checking whether useful questions belong in existing destinations. **Move/merge useful content first; archive only the true remainder.**
