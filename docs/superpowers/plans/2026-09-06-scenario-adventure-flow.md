# Scenario Adventure Flow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make Harmony 360 scenario packs play as a continuous, resumable chapter adventure with an intro, persisted route reconstruction, dedicated finale, and no global skip button.

**Architecture:** Keep the existing runner/ViewModel as the source of truth for question persistence and automatic chapter advancement. Add a pure scenario-adventure policy that resolves the current embedded scenario chapter and reconstructs choice styles; a dedicated Compose board reads already-persisted answers for that resolved pack, while the final chapter delays `onPick` until the finale is explicitly closed. A small shared Compose presence flag suppresses the global skip button only while this board is mounted.

**Tech Stack:** Kotlin, Jetpack Compose, Room, JUnit4

**Spec:** `docs/superpowers/specs/2026-09-06-scenario-adventure-flow.md`

## Global Constraints

- Preserve existing per-question answer persistence and `HarmonyViewModel.pickAnswer` automatic advancement.
- Do not alter Google authentication, Android signing identity, or unrelated mechanics.
- Scenario pack matching must be unambiguous; unknown/remote content falls back safely to a one-chapter scenario.
- The finale must include the pending final choice before that answer is committed.

---

### Task 1: Pure scenario adventure policy

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/ScenarioAdventurePolicy.kt`
- Create: `app/src/test/java/com/example/ui/screens/ScenarioAdventurePolicyTest.kt`

**Interfaces:**
- Produces: `ScenarioAdventurePackRef`, `ScenarioAdventureLocation`, `resolveScenarioAdventureLocation(...)`, `scenarioChoiceIndex(...)`, `scenarioDominantStyle(...)`, and `scenarioRouteIndexes(...)`.

- [ ] **Step 1: Write failing tests** covering unique pack resolution, ambiguous-match rejection, resume-route reconstruction, unknown answer rejection, and dominant-style calculation including a pending final answer.
- [ ] **Step 2: Run the focused policy test/probe and verify RED** because the production policy does not exist yet.
- [ ] **Step 3: Implement the minimal pure Kotlin policy** with exact normalized prompt/options matching and no Android dependencies.
- [ ] **Step 4: Run the focused policy test/probe and verify GREEN.**
- [ ] **Step 5: Commit the policy and tests.**

### Task 2: Dedicated resumable Adventure board

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/ScenarioAdventureBoard.kt`
- Modify: `app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt`

**Interfaces:**
- Consumes: existing `question`, `options`, `selectedAnswer`, `profile`, and `onPick` callback plus the Task 1 policy.
- Produces: `ScenarioAdventureBoard(...)` and `ScenarioAdventurePresence.isActive`.

- [ ] **Step 1: Add a contract test** that requires the SCENARIO renderer to route to `ScenarioAdventureBoard` and prevents the legacy `ScenarioBoard` call from remaining in the SCENARIO branch.
- [ ] **Step 2: Verify RED** against the current renderer.
- [ ] **Step 3: Implement `ScenarioAdventureBoard`.** Resolve the current chapter against embedded scenario packs; observe persisted answers from `AnswerDao.getAnswersForPack`; show a fresh-run intro; show `Kapitel N/M`; call `onPick` immediately for non-final chapters; on the final chapter show a route/result finale before calling `onPick`.
- [ ] **Step 4: Rewire only `FullscreenGameMechanicKind.SCENARIO` in `FullscreenQuestionMechanicBoard` to the new board.** Leave every other mechanic untouched.
- [ ] **Step 5: Verify the contract test/probe and policy checks remain GREEN.**

### Task 3: Suppress generic Skip during adventure

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/RunnerSkipButton.kt`
- Test: `app/src/test/java/com/example/ui/screens/ScenarioAdventureRoutingContractTest.kt`

**Interfaces:**
- Consumes: `ScenarioAdventurePresence.isActive` from Task 2.
- Produces: no global skip UI while the adventure board is mounted; unchanged skip UI everywhere else.

- [ ] **Step 1: Extend the contract test** so the skip composable checks `ScenarioAdventurePresence.isActive` and returns before rendering when true.
- [ ] **Step 2: Verify RED** before changing the skip button.
- [ ] **Step 3: Add the minimal early return in `RunnerSkipButton`.**
- [ ] **Step 4: Verify the contract test/probe is GREEN.**

### Task 4: Verification and integration review

**Files:**
- Review all files changed on `feat/scenario-adventure-flow`.

**Interfaces:**
- Consumes: all prior tasks.
- Produces: reviewable pull request targeting `main`.

- [ ] **Step 1: Re-run focused pure Kotlin checks from a clean temporary compilation.** Expected: all scenario policy assertions pass.
- [ ] **Step 2: Inspect the branch diff against `main`** and confirm no auth/signing/unrelated files changed.
- [ ] **Step 3: Open a pull request and inspect GitHub checks.** If Actions fail before checkout/test steps, report that as infrastructure rather than claiming a tested Android build.
- [ ] **Step 4: Perform a final requirement checklist against the spec:** intro, distinct real chapters, persisted resume route, no skip, pending-final finale, safe unknown-content fallback.
