# Tot Selected Answer Preview Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Show the player's own selected answer, including the same option image used during gameplay, while a paired partner answer is still hidden in `tot` games.

**Architecture:** Extend the existing `CouplePackRevealScreen` waiting/reveal card. Pass the current `QuestionPack` into the card, render a compact `TotOwnAnswerPreview` for `tot` packs before reveal, and resolve imagery through the existing `TravelDestinationCatalog` plus `TotImageProvider` path. Partner answer rendering remains only in the explicit revealed branch.

**Tech Stack:** Kotlin, Jetpack Compose, Coil, JUnit 4, Gradle.

**Spec:** Approved in chat: show “Deine Auswahl” with a small image while waiting for partner reveal; never expose the partner answer early.

## Global Constraints

- Apply centrally to `pack.type == "tot"` games.
- Reuse `TotImageProvider`; do not add a second image mapping.
- Partner answers remain hidden until the existing reveal action.
- Do not stage generated `introspection_*_golden.mp3` files.
- Preserve existing selection/gameplay animation and pair flow.

---

### Task 1: Waiting answer preview

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/CouplePackRevealScreen.kt`
- Test: `app/src/test/java/com/example/TotWaitingAnswerPreviewContractTest.kt`

**Interfaces:**
- Consumes: `QuestionPack`, `TravelDestinationCatalog.assetKeyFor`, `TotImageProvider.getImageUrl`, `CoupleRevealState`.
- Produces: private `TotOwnAnswerPreview(pack: QuestionPack, myAnswer: String)` composable and `showOwnTotPreview` waiting-state guard.

- [ ] **Step 1: Write the failing regression test**

Assert that the reveal screen contains the `TotOwnAnswerPreview`, uses `TotImageProvider`, and keeps partner text inside the revealed branch.

- [ ] **Step 2: Run the targeted test and verify RED**

Run: `gradlew.bat :app:testDebugUnitTest --tests com.example.TotWaitingAnswerPreviewContractTest --console=plain`
Expected: FAIL because the preview is not present yet.

- [ ] **Step 3: Implement the minimal preview**

Pass `pack` to `CoupleQuestionRevealCard`, render the player's answer for non-revealed `tot` states, use the shared image resolver, and leave partner answer rendering untouched in the revealed state.

- [ ] **Step 4: Verify GREEN and build**

Run the targeted test, `git diff --check`, then `gradlew.bat :app:assembleDebug --console=plain`. Expected: all exit 0.

- [ ] **Step 5: Commit and publish**

Stage only the source, test, and plan; commit, push, merge to main, rebuild merged main, verify APK identity, and install with ADB replace mode without uninstalling.
