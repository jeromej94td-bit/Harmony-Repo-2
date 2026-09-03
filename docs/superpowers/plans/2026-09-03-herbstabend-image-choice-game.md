# Unser Herbstabend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the complete six-round, photorealistic “Unser Herbstabend” image-choice game to Harmony without changing authentication, navigation, audio, video, or existing game behavior.

**Architecture:** The pack remains ordinary `QuestionPack` content and therefore uses the existing catalogue, quiz runner, persistence, partner, and result flows. A stable `pack.id` plus question-index policy selects one of six new image-choice kinds. A dedicated Compose renderer owns the 2×2 autumn card layout, per-round image mapping, and staggered motion; it does not inspect translated question text.

**Tech Stack:** Kotlin, Jetpack Compose, Android resources, JUnit 4, Robolectric/Compose UI tests, Roborazzi, Gradle.

**Spec:** `docs/superpowers/specs/2026-09-03-herbstabend-image-choice-game-design.md`

## Global Constraints

- Work only on `feature/herbstabend`, whose `origin` is `https://github.com/jeromej94td-bit/Harmony-Repo-2.git`.
- Keep GitHub as the delivery target. Do not use Supabase, Gemini, Firebase, or Google Drive for this feature.
- Route visuals only through `pack.id == "herbstabend"` and question index; never match localized prompt or answer text.
- Do not touch authentication, signing, account e-mail, audio, video, or unrelated screens.
- Use 24 separate, text-free, photorealistic images. Do not crop assets out of the approved full-screen mockups.
- Preserve the approved medium-dark autumn room exposure and make rain droplets and water trails visibly readable on the window.
- Use test-first steps and commit after each coherent task.

---

### Task 1: Lock the pack content contract

**Files:**
- Create: `app/src/test/java/com/example/data/model/AutumnEveningPackContractTest.kt`
- Modify: `app/src/main/java/com/example/data/model/Models.kt`

- [x] Write a failing test that selects `HarmonyPacksData.DEFAULT_PACKS.single { it.id == "herbstabend" }` and asserts title, category `lieber`, topic `hobbys`, type `quiz`, emoji, tags, six exact prompts, and four exact options per prompt from the approved spec.
- [x] Run `:app:testDebugUnitTest --tests com.example.data.model.AutumnEveningPackContractTest` and confirm failure because the pack is absent.
- [x] Add the `herbstabend` `QuestionPack` to `DEFAULT_PACKS` with the exact approved German copy.
- [x] Re-run the focused test and confirm it passes.
- [x] Commit with `feat: add autumn evening question pack`.

### Task 2: Add stable routing and animation policy

**Files:**
- Create: `app/src/test/java/com/example/ui/screens/AutumnEveningImageChoicePolicyTest.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoicePolicy.kt`

- [x] Write failing tests that assert question indices 0–5 route to `AUTUMN_STORY`, `AUTUMN_DRINK`, `AUTUMN_SNACK`, `AUTUMN_NOOK`, `AUTUMN_SOUND`, and `AUTUMN_SCENT`; out-of-range indices and another pack with identical text must return no autumn kind.
- [x] Add those six enum values and a private ordered list used only when `pack.id == "herbstabend"`.
- [x] Add `autumnEveningRevealDelayMillis(index)` with row-major delays for a 2×2 grid: 0, 110, 420, and 530 milliseconds.
- [x] Re-run the focused policy test and confirm it passes.
- [x] Commit with `feat: route autumn evening image rounds`.

### Task 3: Create and validate the 24 production image assets

**Files:**
- Create: `app/src/main/res/drawable-nodpi/autumn_story_01.png` through `autumn_story_04.png`
- Create: `app/src/main/res/drawable-nodpi/autumn_drink_01.png` through `autumn_drink_04.png`
- Create: `app/src/main/res/drawable-nodpi/autumn_snack_01.png` through `autumn_snack_04.png`
- Create: `app/src/main/res/drawable-nodpi/autumn_nook_01.png` through `autumn_nook_04.png`
- Create: `app/src/main/res/drawable-nodpi/autumn_sound_01.png` through `autumn_sound_04.png`
- Create: `app/src/main/res/drawable-nodpi/autumn_scent_01.png` through `autumn_scent_04.png`
- Create: `app/src/test/java/com/example/ui/screens/AutumnEveningAssetContractTest.kt`

- [ ] Write a failing resource contract test listing all 24 exact filenames and asserting each file exists, is a decodable PNG, has both dimensions at least 768 px, and has a portrait/card-compatible aspect ratio between 0.60 and 1.05.
- [ ] Generate each option as a standalone text-free photorealistic editorial image with coherent warm autumn color grading, no people, no logos, no watermarks, and no recognizable copyrighted characters.
- [ ] For `autumn_nook_01.png`, show a medium-dark warm window nook with unmistakable rain droplets and water trails on the glass; keep all four room images atmospheric but readable.
- [ ] Inspect every generated image before copying it into `drawable-nodpi`; regenerate mismatches instead of accepting a merely plausible image.
- [ ] Run the focused asset contract test and confirm it passes.
- [ ] Commit with `feat: add photorealistic autumn evening artwork`.

### Task 4: Build the dedicated autumn card renderer

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/AutumnEveningQuestion.kt`
- Create: `app/src/test/java/com/example/ui/screens/AutumnEveningRendererContractTest.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoiceQuestion.kt`

- [ ] Write a failing contract test for a pure `autumnEveningVisuals(kind)` mapping that verifies each kind has exactly four drawable IDs in answer order and a distinct short subtitle.
- [ ] Implement the mapping with immutable data and `@DrawableRes` IDs.
- [ ] Implement `AutumnEveningQuestion` as a 2×2 card grid: aubergine glass surface, copper border for selection, ivory labels, `ContentScale.Crop`, rounded 22 dp cards, check indicator, and accessibility content descriptions combining prompt and option.
- [ ] Animate cards in row-major order using `autumnEveningRevealDelayMillis`: horizontal entrance, slight Y rotation, fade, and scale settling; honor Compose motion timing without blocking input after reveal.
- [ ] In `HarmonyImageChoiceQuestion`, delegate all six autumn kinds to the new renderer before profile/database collection and return immediately.
- [ ] Run the focused renderer and policy tests and confirm they pass.
- [ ] Commit with `feat: render autumn evening image choices`.

### Task 5: Add English copy without weakening offline fallback

**Files:**
- Modify: `app/src/main/java/com/example/ui/EnglishContent.kt`
- Create: `app/src/test/java/com/example/ui/AutumnEveningTranslationTest.kt`

- [ ] Write a failing test asserting English translations for the pack title, six prompts, all 24 answer labels, and renderer subtitles.
- [ ] Add exact English entries to `EXACT_ENGLISH_CONTENT`; German remains the source and fallback for every other locale.
- [ ] Run the focused translation test and confirm it passes.
- [ ] Commit with `feat: localize autumn evening game in English`.

### Task 6: Verify the approved visual direction on representative rounds

**Files:**
- Create: `app/src/test/java/com/example/ui/screens/AutumnEveningVisualContractTest.kt`
- Create on test run: `app/build/autumn-evening-preview/drink.png`
- Create on test run: `app/build/autumn-evening-preview/nook.png`

- [ ] Add Compose/Robolectric tests at `de-rDE-w411dp-h1100dp-xxhdpi` rendering the drink and room rounds inside `HarmonyTheme(darkTheme = true)` and `AmbientBackground`.
- [ ] Advance the controlled clock past the final stagger, assert the prompt and all four option labels exist, select one card, and assert the selected semantics/test tag.
- [ ] Capture Roborazzi previews for the drink and room rounds.
- [ ] Inspect both previews: no clipping, readable ivory copy, balanced 2×2 grid, copper selection state, medium-dark room exposure, and visible rain on the Fensternest card.
- [ ] If visual inspection fails, adjust only the autumn renderer/assets and repeat until it matches the approved direction.
- [ ] Commit with `test: cover autumn evening visual flow`.

### Task 7: Integration verification and GitHub delivery

**Files:**
- Verify only; modify files only to fix issues caused by this feature.

- [ ] Run all new focused tests together.
- [ ] Run `:app:compileDebugKotlin` and `:app:assembleDebug` with the known JDK 21 and Android SDK environment.
- [ ] Run relevant existing image-choice regression tests, including `GoldenMasterImageChoiceWiringContractTest`, `GoldenMasterImageChoiceLayoutPolicyTest`, and `HappyCoupleVisualContractTest`.
- [ ] Inspect `git status --short` and `git diff --check`; exclude the four generated introspection MP3 baseline artifacts from every commit.
- [ ] Review the complete diff against the design spec and confirm no auth, signing, audio, video, or unrelated navigation file changed.
- [ ] Push `feature/herbstabend` to `origin` and create a pull request into `main` in `jeromej94td-bit/Harmony-Repo-2` with test evidence and the known unrelated baseline-test caveat.
- [ ] Report the exact branch, latest commit, pull-request URL, and any verification limitation without claiming a merge unless GitHub confirms it.
