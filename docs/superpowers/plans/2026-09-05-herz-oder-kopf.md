# Herz oder Kopf Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the six-round `Herz oder Kopf` Harmony Panda image-choice game with 24 production cards, staggered flip transitions, input lock, and stable routing.

**Architecture:** Add a dedicated question renderer and transition policy for this pack rather than changing the legacy egg/steak renderer. Route the six questions by pack id + question index, keep the pack data in `Models.kt`, and store 24 optimized drawable assets in `drawable-nodpi`. The renderer delays `onPick` until old cards have flipped out, hides the old question only after the exit, then lets the newly composed question flip cards in before revealing its text.

**Tech Stack:** Kotlin, Jetpack Compose, Android resources, JUnit/Robolectric where appropriate.

**Spec:** `skills/harmony-panda/SKILL.md` and `skills/harmony-panda/STYLE_GUIDE.md` once PR #271 lands; until then use the approved Harmony Panda references from the design conversation.

## Global Constraints
- Do not modify unrelated games or their button behavior.
- Recurring couple: round cuddly male panda + female panda with pink bow where a pair scene is appropriate.
- Exactly 6 rounds with exactly 4 options per round.
- Input is locked for the complete transition.
- Old cards flip out staggered; only then may the old question disappear.
- New cards flip in staggered; only after all are visible may the new question appear.
- Panda imagery is not forced into abstract final symbolism.

---

### Task 1: Contracts first
**Files:**
- Create: `app/src/test/java/com/example/data/model/HeartOrHeadPackContractTest.kt`
- Create: `app/src/test/java/com/example/ui/screens/HeartOrHeadImageChoicePolicyTest.kt`
- Create: `app/src/test/java/com/example/ui/screens/HeartOrHeadTransitionPolicyTest.kt`
- Create: `app/src/test/java/com/example/ui/screens/HeartOrHeadAssetContractTest.kt`

- [ ] Write pack, routing, timing, and 24-resource contracts.
- [ ] Run focused tests and verify they fail because the feature/resources do not exist yet.

### Task 2: Pack and stable routing
**Files:**
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoicePolicy.kt`

- [ ] Add pack `herz_oder_kopf` with the six approved rounds.
- [ ] Add six dedicated `HarmonyImageChoiceKind` values and index-based mapping.
- [ ] Add pure stagger/timing helpers used by the renderer.
- [ ] Run focused pack/policy/timing tests.

### Task 3: 24 production cards
**Files:**
- Create: `app/src/main/res/drawable-nodpi/heart_head_date_01.webp` … `heart_head_final_04.webp`

- [ ] Generate/optimize all 24 approved card artworks.
- [ ] Add all resources using stable names.
- [ ] Run the asset contract.

### Task 4: Dedicated animated renderer
**Files:**
- Create: `app/src/main/java/com/example/ui/screens/HeartOrHeadQuestion.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoiceQuestion.kt`

- [ ] Map the six kinds to four resources and a short subtitle.
- [ ] Implement 2x2 Harmony Panda cards with rounded magenta/violet styling.
- [ ] Implement staggered entrance flip.
- [ ] On tap: show selection feedback, lock input, stagger-flip old cards out, then fade question, then call `onPick`.
- [ ] On the newly composed question: keep question hidden until all four cards have finished stagger-flipping in.
- [ ] Re-enable input only after question reveal completes.
- [ ] Preserve accessibility semantics and selected state.

### Task 5: Verification and PR
**Files:**
- Update tests only if a real implementation defect is uncovered; do not weaken contracts.

- [ ] Run all Herz-oder-Kopf focused tests.
- [ ] Run `:app:compileDebugKotlin` / normal Android PR build.
- [ ] Confirm only intended files changed.
- [ ] Open a PR against current `main`; do not merge automatically.
