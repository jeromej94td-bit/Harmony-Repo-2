# Happy Couple Love Balance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the approved “Welches Paar ist GLÜCKLICH?” four-card rain/umbrella image question as the first playable question in `Liebe im Gleichgewicht`, with a slower sequential flip reveal and preserved existing answers.

**Architecture:** Reuse the existing `HarmonyImageChoiceQuestion` interception path instead of adding a second quiz runner. Add a dedicated `HAPPY_COUPLE` image-choice kind and renderer so the four approved cards stay image-dominant and do not inherit the egg card labels/hearts. Prepending a question changes persisted `questionIndex` semantics, so a Room 8→9 data migration shifts existing `liebegleichgewicht` answer indices before the new index 0 is used.

**Tech Stack:** Kotlin, Jetpack Compose, Room, Robolectric/Compose UI tests, Android drawable-nodpi assets.

**Spec:** User-approved Harmony mock from 2026-08-29 in this conversation; final card art is cropped from `/mnt/data/a_polished_mobile_app_quiz_ui_screenshot_with_a_da.png`.

## Global Constraints

- Repository: `jeromej94td-bit/Harmony-Repo-2`.
- Target branch for PR: `main`; implementation branch: `feature/happy-couple-love-balance`.
- Location: relationship topic → `Liebe im Gleichgewicht` (`packId = liebegleichgewicht`).
- The new question must be the first question.
- Copy: `Welches Paar ist GLÜCKLICH?` and `Wähle das Paar, das für dich am glücklichsten wirkt.`
- Answers are exactly `1`, `2`, `3`, `4`.
- Four cards reveal in order 1→2→3→4 at 700 ms intervals.
- Each flip lasts longer than the current egg card’s 430 ms; target duration is 620 ms.
- No “Frag deinen Partner” follow-up in this change.
- No extra bottom helper CTA/text on this image question.

---

### Task 1: Lock content, routing, timing, and answer preservation with tests

**Files:**
- Modify: `app/src/test/java/com/example/ui/screens/HarmonyImageChoiceQuestionTest.kt`
- Create: `app/src/test/java/com/example/data/db/LoveBalanceQuestionMigrationTest.kt`
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoicePolicy.kt`
- Modify: `app/src/main/java/com/example/data/db/AppDatabase.kt`

**Interfaces:**
- Produces: `HarmonyImageChoiceKind.HAPPY_COUPLE`.
- Produces: `happyCoupleRevealDelayMillis(index: Int): Long`.
- Produces: `HAPPY_COUPLE_REVEAL_DURATION_MILLIS = 620`.
- Produces: `HarmonyDatabase.MIGRATION_8_9`.

- [ ] **Step 1: Write failing content/routing/timing tests**

Add assertions that `HarmonyPacksData.PACKS.first { it.id == "liebegleichgewicht" }.questions.first()` is the new four-option question, that `harmonyImageChoiceKind("liebegleichgewicht", 0)` resolves to `HAPPY_COUPLE`, that index 1 is not intercepted, that reveal delays are `[0, 700, 1400, 2100]`, and that reveal duration is 620 ms and greater than 430 ms.

- [ ] **Step 2: Run the focused test and verify RED**

Run: `./gradlew :app:testDebugUnitTest --tests com.example.ui.screens.HarmonyImageChoiceQuestionTest`

Expected: FAIL because `HAPPY_COUPLE` and its timing/content do not exist yet.

- [ ] **Step 3: Write a failing migration test**

Create a test that seeds `answers` for `liebegleichgewicht` at indices 0 and 1, applies `MIGRATION_8_9`, and verifies they become indices 1 and 2 without loss. Also verify unrelated pack answers keep their indices.

- [ ] **Step 4: Run the migration test and verify RED**

Run: `./gradlew :app:testDebugUnitTest --tests com.example.data.db.LoveBalanceQuestionMigrationTest`

Expected: FAIL because migration 8→9 does not exist yet.

- [ ] **Step 5: Implement minimal production behavior**

Add the new question at the front of `liebegleichgewicht`, add `HAPPY_COUPLE` routing for index 0, add 700 ms timing and 620 ms duration constants/helpers, bump Room to version 9, register `MIGRATION_8_9`, and shift `liebegleichgewicht` answer indices using a collision-safe temporary offset. Shift matching `brain_answer_history.questionIndex` values by +1 as well.

- [ ] **Step 6: Run focused tests and verify GREEN**

Run both focused test classes above and confirm PASS.

### Task 2: Add the approved four-card renderer and assets

**Files:**
- Modify: `app/src/test/java/com/example/ui/screens/HarmonyImageChoiceUiTest.kt`
- Create: `app/src/main/java/com/example/ui/screens/HarmonyHappyCoupleQuestion.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoiceQuestion.kt`
- Create: `app/src/main/res/drawable-nodpi/happy_couple_01.png`
- Create: `app/src/main/res/drawable-nodpi/happy_couple_02.png`
- Create: `app/src/main/res/drawable-nodpi/happy_couple_03.png`
- Create: `app/src/main/res/drawable-nodpi/happy_couple_04.png`

**Interfaces:**
- Produces composable `HarmonyHappyCoupleQuestion(question, options, selectedAnswer, onPick, modifier)`.
- UI test tags: `harmony_happy_couple_question`, `happy_couple_option_0` … `happy_couple_option_3`, with `_selected` suffix for the selected card.

- [ ] **Step 1: Write failing Compose UI test**

Render `HarmonyImageChoiceQuestion(kind = HAPPY_COUPLE, ...)`, advance the manual clock beyond the four reveals, assert all four card tags exist, tap card 3, and assert answer `3` plus selected tag. Assert the approved subtitle exists and the generic bottom helper text does not exist.

- [ ] **Step 2: Run the UI test and verify RED**

Run: `./gradlew :app:testDebugUnitTest --tests com.example.ui.screens.HarmonyImageChoiceUiTest`

Expected: FAIL because the dedicated happy-couple renderer/assets are not wired yet.

- [ ] **Step 3: Add approved image assets**

Store the four cropped final cards as `happy_couple_01.png` through `happy_couple_04.png` in `drawable-nodpi`.

- [ ] **Step 4: Implement the dedicated renderer**

Render a 2×2 image grid in the existing Harmony glass/neon container. Use the approved subtitle and image-only cards; keep the numbers baked into the approved card art. Animate each card with the existing 3D rotation/alpha/scale language but use `happyCoupleRevealDelayMillis` and 620 ms duration. Enable tapping only after the card is mostly revealed and send the corresponding option string.

- [ ] **Step 5: Wire the renderer through `HarmonyImageChoiceQuestion`**

Special-case `HAPPY_COUPLE` before the legacy egg/steak/travel visual lookup and return after rendering it.

- [ ] **Step 6: Run the UI test and verify GREEN**

Run the focused UI test and confirm PASS.

### Task 3: Regression verification and PR readiness

**Files:**
- Verify all modified files and binary assets.

- [ ] **Step 1: Run focused unit/UI tests**

Run the two image-choice test classes plus the migration test.

- [ ] **Step 2: Run broader app unit tests**

Run: `./gradlew :app:testDebugUnitTest`

- [ ] **Step 3: Review PR diff**

Confirm the diff only changes the requested game, migration, tests, plan, and four assets; no partner-follow-up mechanic is included.

- [ ] **Step 4: Mark PR ready only when verification is green**

If repository-wide GitHub Actions fail before executing tests due an existing CI/infrastructure problem, report that limitation explicitly instead of claiming a green build.
