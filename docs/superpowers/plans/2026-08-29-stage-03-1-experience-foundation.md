# Stage 03.1 Reusable Experience Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a UI-independent reusable Harmony experience definition/state/navigation core while preserving the existing proposal experience’s 9-step / 35-subround order, progress and visible behavior.

**Architecture:** Introduce generic `ExperienceStepKind`, `ExperienceStep`, `ExperienceDefinition`, `ExperiencePosition`, and `ExperienceNavigator` in the model layer. Keep proposal content and rendering proposal-owned; adapt `ProposalExperienceRunnerPolicy` to delegate navigation/progress to the generic navigator while retaining its current public API.

**Tech Stack:** Kotlin/JVM model code, JUnit-style repository tests, existing Android/Kotlin project structure.

**Spec:** `docs/superpowers/specs/2026-08-29-stage-03-reusable-experience-engine-design.md`

## Global Constraints

- Stage 03.1 must not change proposal copy, images, animations, pacing, navigation entry, persistence, or Compose rendering behavior.
- The proposal experience remains 9 steps and exactly 35 navigable subrounds.
- Generic item counts below 1 normalize to one navigable item.
- Malformed generic definitions fail fast: blank id/title, empty steps, blank/duplicate step IDs, missing reveal, early reveal, or multiple reveals.
- Invalid out-of-range positions must not index unsafely.
- No new mechanic kinds beyond those already proven by Stage 02.
- No GitHub Actions workflow is added or manually triggered.

---

### Task 1: Generic experience definition and navigator contract

**Files:**
- Create: `app/src/test/java/com/example/data/model/ExperienceDefinitionTest.kt`
- Create: `app/src/main/java/com/example/data/model/ExperienceDefinition.kt`

**Interfaces:**
- Produces: `ExperienceStepKind`, `ExperienceStep`, `ExperienceDefinition`, `ExperiencePosition`, `ExperienceNavigator`.
- `ExperienceNavigator` consumes an `ExperienceDefinition` plus `(String) -> Int` item-count resolver.

- [ ] **Step 1: Write the failing generic contract test**

Test real behavior for: valid definition, all malformed definition invariants, current-step safety, within-step advancement, step-to-step advancement, terminal reveal, normalized zero/negative counts, and normalized progress from first position to final reveal.

- [ ] **Step 2: Run the focused Kotlin/JUnit-equivalent contract and verify RED**

Expected: compile/test failure because the generic experience classes do not exist yet.

- [ ] **Step 3: Implement the minimal generic model**

Create the seven proven step kinds only. `ExperienceDefinition` copies the input step list, validates invariants, and exposes `nextStepAfter(stepId)`. `ExperienceNavigator` normalizes `itemCountResolver(step.id).coerceAtLeast(1)`, exposes `currentStep(position)`, `next(position)`, `progress(position)`, and `totalItemCount()`.

- [ ] **Step 4: Re-run the focused contract and verify GREEN**

Expected: all generic definition/navigation cases pass.

- [ ] **Step 5: Commit**

Commit message: `feat: add reusable experience navigation core`

---

### Task 2: Proposal compatibility and ratio/parity contract

**Files:**
- Modify: `app/src/main/java/com/example/data/model/ProposalExperienceRunnerPolicy.kt`
- Create: `app/src/main/java/com/example/data/model/ProposalExperienceAdapter.kt`
- Create: `app/src/test/java/com/example/data/model/ProposalExperienceGenericParityTest.kt`

**Interfaces:**
- Consumes: generic types from Task 1 and existing `ProposalExperienceDefinitions`, `ProposalExperienceRunnerPolicy.itemCount` content ownership.
- Produces: `ProposalExperienceAdapter.definition` plus mapping helpers between `ProposalRunnerPosition` and `ExperiencePosition`.
- Existing callers keep using `ProposalExperienceRunnerPolicy.steps`, `itemCount`, `next`, and `progress` unchanged.

- [ ] **Step 1: Write the failing proposal parity test**

Assert:
- generic adapter has the same experience id/title, 9 ordered step IDs, and equivalent kinds;
- proposal item counts are exactly `5, 6, 3, 5, 1, 3, 6, 5, 1` = 35 total;
- for every valid proposal position, generic and proposal `next` positions match;
- progress values match within float tolerance for every valid position;
- final position is reveal and terminates cleanly.

- [ ] **Step 2: Run parity contract and verify RED**

Expected: failure because adapter/delegation does not yet exist.

- [ ] **Step 3: Implement the minimal proposal adapter and delegation**

Map each `ProposalFlowStepKind` explicitly to the same generic kind. Build the generic definition from the existing proposal definition. Keep proposal item-count logic where it is. Make proposal `next` and `progress` convert to `ExperiencePosition`, delegate to one `ExperienceNavigator`, then convert back.

- [ ] **Step 4: Re-run generic + proposal parity contracts and verify GREEN**

Expected: generic tests pass; proposal parity shows 9 steps, 35 items, identical next/progress semantics.

- [ ] **Step 5: Commit**

Commit message: `refactor: delegate proposal flow to experience core`

---

### Task 3: Final regression and merge-scope verification

**Files:**
- No production scope expansion.
- Update 360 Rework tracker only after code verification, if no parallel tracker branch has changed those files in the meantime.

**Interfaces:**
- Consumes: Task 1 and Task 2 output.
- Produces: merge-ready 26.1 branch with no UI behavior changes.

- [ ] **Step 1: Re-run focused model contracts from a clean local temp compile/harness**

Expected: all generic and proposal-parity checks pass with no warnings/errors attributable to the new model code.

- [ ] **Step 2: Inspect `main...branch`**

Expected code scope: generic model/test, narrow proposal adapter/policy/test, plus this approved plan/spec tracking only. No Compose/UI/image/content/database files.

- [ ] **Step 3: Confirm branch is not behind current `main`**

If `main` advanced, reconcile before opening/merging the PR; never overwrite newer tracker or Stage-05 work.

- [ ] **Step 4: Open a narrow 26.1 PR and verify mergeability/file list**

PR must explicitly state no full Android/Gradle build is claimed if the known runner infrastructure still prevents execution.

- [ ] **Step 5: Merge once conflict-free and verified**

Do not manually trigger GitHub Actions.

## Self-review

- Spec coverage: generic definition, position, navigation, progress, validation, item-count normalization and proposal parity are each covered by a task.
- Scope: no generic renderer or mechanic migration is pulled forward from 03.2–03.7.
- Type consistency: proposal public API remains unchanged; only internal navigation/progress delegation changes.
- Ratio protection: the parity contract explicitly fixes the current proposal distribution at 9 steps / 35 subrounds and compares every valid position and progress value.
