# Stage 03 — Reusable Harmony Experience Engine Design

**Date:** 2026-08-29
**Project:** 360 Rework
**Stage:** 03/08 — Reusable Harmony Experience Engine
**Design package:** 26.0
**First implementation slice:** 26.1 / 03.1 — General mixed-step experience definition/state model

## Goal

Turn the completed `Unser perfekter Antrag` experience into the reference architecture for reusable mixed-mechanic Harmony experiences without changing its visible behavior in 03.1.

The reusable engine must let future experiences such as `Unser Zuhause`, `Unsere Traumreise`, `Unsere Intimität` and other flagship flows define ordered steps and deterministic navigation without copying proposal-specific state/navigation classes.

## Current problem

Stage 02 already has the right concepts but they are proposal-specific:

- `ProposalFlowStepKind`
- `ProposalFlowStep`
- `ProposalExperienceDefinition`
- `ProposalRunnerPosition`
- `ProposalExperienceRunnerPolicy`

The existing proposal runner also owns content-specific item counts and rendering decisions. Reusing that structure directly would cause every future experience to create another parallel set of nearly identical `XxxFlowStep`, `XxxRunnerPosition` and `XxxRunnerPolicy` types.

## Chosen architecture

Create a small UI-independent generic engine in the data/model layer.

### Core types

`ExperienceStepKind`

- `EITHER_OR`
- `IMAGE_DUEL`
- `RANKING`
- `PARTNER_PREDICTION`
- `SCENARIO`
- `OPEN_PROMPT`
- `REVEAL`

The enum intentionally mirrors only the mechanics already proven by Stage 02. New kinds are added later only when an actual experience requires them.

`ExperienceStep`

- stable nonblank `id`
- `kind: ExperienceStepKind`

It contains no question text, images, answers or Compose code.

`ExperienceDefinition`

- stable nonblank `id`
- nonblank `title`
- ordered immutable `steps`
- at least one step
- unique step IDs
- exactly one `REVEAL`, and it must be the final step
- helper for finding the next step by ID

`ExperiencePosition`

- `stepIndex`
- `itemIndex`

It represents navigation position only. It does not own answers or UI state.

`ExperienceNavigator`

A deterministic navigator operating on an `ExperienceDefinition` plus an item-count resolver.

Public behavior:

- returns the current step when a position is valid
- advances inside a multi-item step before advancing to the next step
- returns `null` after the final item of the final step
- calculates normalized progress across all subrounds
- treats any resolved item count below 1 as one navigable item so reveal/single-screen steps remain safe
- never reads proposal content directly

The item-count dependency is supplied as a resolver such as `(stepId: String) -> Int`. This keeps content ownership outside the generic engine.

## Proposal compatibility

03.1 must not rewrite the visible proposal experience.

The existing proposal-specific API remains available as a compatibility layer:

- `ProposalFlowStepKind` / `ProposalFlowStep` / `ProposalExperienceDefinition` may either delegate to or be migrated onto the generic types without breaking current callers.
- `ProposalRunnerPosition` and `ProposalExperienceRunnerPolicy` keep their existing public behavior.
- The proposal item-count mapping remains proposal-owned and is passed into the generic navigation primitive.
- `ProposalExperienceScreen` does not receive a broad rendering refactor in 03.1.

The compatibility requirement is behavioral: all 35 Stage-02 subround positions, progress semantics and final `ProposalReveal` path must remain unchanged.

## Data flow

1. A feature defines an `ExperienceDefinition` with ordered generic steps.
2. The feature owns the content for each step and knows how many items that step contains.
3. The feature supplies an item-count resolver to `ExperienceNavigator`.
4. The UI stores an `ExperiencePosition` or a compatibility wrapper around it.
5. On answer completion, the UI asks the navigator for the next position.
6. Rendering remains feature-specific until later Stage-03 slices introduce reusable mechanic adapters.

## Scope of 03.1

Included:

- generic step-kind enum
- generic step model
- generic experience definition and validation
- generic position model
- deterministic generic navigation
- generic progress calculation
- proposal compatibility/adaptation needed to prove no Stage-02 behavior regression
- focused unit/contract tests

Explicitly excluded:

- generic Compose renderer
- moving proposal answer state into the engine
- persistence/database changes
- new navigation/catalog entries
- generic mechanic implementations for Either-Or, Image Duel, Ranking, Partner Prediction, Scenario, Open Prompt or Reveal
- converting unrelated legacy quiz packs into experiences
- changing proposal copy, images, animations or pacing

Those belong to 03.2–03.7 or later stages.

## Error handling and invariants

Construction should fail fast for malformed definitions:

- blank experience ID
- blank title
- empty steps
- blank step ID
- duplicate step IDs
- no reveal
- reveal before the final position
- multiple reveals

Navigation with an invalid out-of-range position returns no current step / no next position rather than indexing unsafely.

Item-count resolvers returning zero or negative values are normalized to one navigable item. This matches the safe behavior already used by the proposal policy and avoids division-by-zero progress calculations.

## Testing strategy

Implementation follows TDD.

First RED contract should describe the generic API before production code exists. The focused suite must cover:

- valid generic definition
- each malformed-definition invariant
- multi-item advancement within a step
- step-to-step advancement
- terminal navigation after reveal
- progress from first item to final reveal
- zero/negative item-count normalization
- invalid-position safety
- proposal parity: generic navigation produces the same position sequence and progress values as the existing proposal policy for the current proposal definition
- proposal still accounts for exactly 35 navigable subrounds and ends at `REVEAL`

If a Gradle-capable runner is unavailable, the PR must state that explicitly. Missing CI credits or runner startup is not treated as an application regression and is not a reason to leave the completed implementation unmerged after static/diff/contract review.

## File boundaries

Preferred new production file:

- `app/src/main/java/com/example/data/model/ExperienceDefinition.kt`

Preferred focused test file:

- `app/src/test/java/com/example/data/model/ExperienceDefinitionTest.kt`

Proposal compatibility may require narrow edits to:

- `ProposalExperienceDefinition.kt`
- `ProposalExperienceRunnerPolicy.kt`
- existing proposal contract tests if type aliases/adapters require compile updates

No UI files should need behavior changes for 03.1.

## Rollout

1. Land 26.0 design only.
2. Implement 26.1 / 03.1 with RED → GREEN focused contracts.
3. Verify proposal parity and diff scope.
4. Merge 26.1 when conflict-free and no concrete regression is found, even if repository Actions cannot start because of the known infrastructure/credit problem.
5. Advance to 03.2 — reusable `EitherOr` step.

## Definition of Done for 03.1

03.1 is complete when a UI-independent generic experience definition/state/navigation model exists, malformed flows are rejected deterministically, progress/navigation support variable subround counts, and the existing proposal experience can use or map to that generic foundation without changing its 35-subround behavior or visible Stage-02 flow.