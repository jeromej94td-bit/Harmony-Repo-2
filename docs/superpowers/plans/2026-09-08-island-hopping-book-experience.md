# Island Hopping Magical Book Experience Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn only `h500_089_inselhopping_prioritaet` into a magical Harmony book flow where opening the game plays the book intro and every answer touch immediately starts a page turn before the existing runner advances.

**Architecture:** Keep all six curated questions and the existing ViewModel/persistence callbacks. Add an exact pack-ID feature gate, a Compose book-question board with immediate page-turn animation, and a one-time intro in `QuizRunnerScreen`; reuse the existing SceneView/Filament intro with Compose fallback.

**Tech Stack:** Kotlin, Jetpack Compose, SceneView/Filament, Blender 4.2 LTS, JUnit/Robolectric source-contract tests, Gradle 9.3.1 wrapper.

**Spec:** `docs/superpowers/specs/2026-09-08-island-hopping-book-experience-design.md`

## Global Constraints
- Affect only `h500_089_inselhopping_prioritaet`.
- Preserve the six curated questions in `Harmony360FoodTravelLeisureCultureQualityRework.kt` verbatim.
- Answer touch starts the page turn immediately; no pre-turn confirmation pause.
- Existing `onPick`/ViewModel logic remains the persistence and progression authority.
- Other priority-poker games keep `DirectPriorityPokerBoard` unchanged.
- Do not touch Google/Supabase auth, package ID, signing/keystore, or locale catalogs.

---### Task 1: Exact pack routing contract

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/IslandHoppingBookPolicy.kt`
- Modify: `app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt`
- Test: `app/src/test/java/com/example/ui/screens/IslandHoppingBookContractTest.kt`

**Interfaces:**
- Produces: `IslandHoppingBookPolicy.PACK_ID` and `IslandHoppingBookPolicy.isEnabled(packId: String): Boolean`.
- Produces routing from `PRIORITY_POKER` + exact pack ID to `IslandHoppingBookBoard`.

- [ ] Write a failing contract test that requires the exact pack ID, special board routing, and the generic priority-poker fallback for every other pack.
- [ ] Run only `IslandHoppingBookContractTest` and confirm RED because the policy/board do not exist.
- [ ] Add the minimal policy and exact routing branch before the generic `when` block.
- [ ] Re-run the focused test and existing `PriorityPokerOneScreenContractTest`; both must pass.
- [ ] Commit only Task 1 files.

### Task 2: Immediate page-turn question board

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/IslandHoppingBookBoard.kt`
- Create: `app/src/main/java/com/example/ui/screens/IslandHoppingPageTurn.kt`
- Test: `app/src/test/java/com/example/ui/screens/IslandHoppingBookTransitionContractTest.kt`

**Interfaces:**
- `IslandHoppingBookBoard(question, options, selectedAnswer, profile, questionIndex, totalQuestions, onPick, modifier)`.
- `IslandHoppingPageTurn(progress: Float, modifier: Modifier)` renders the turning cream page, perspective, shadow and gold edge.- [ ] Write a failing transition contract requiring answer-touch to launch the turn immediately, `onPick` to occur only at the commit point, and only one callback per physical tap.
- [ ] Run the transition test and confirm RED because the board/page-turn files do not exist.
- [ ] Implement the minimal Compose book spread: warm paper, navy/violet surround, gold page edges, turquoise/pink atmospheric accents, the current question, four existing option labels, progress text, and test tags.
- [ ] On option touch, start the page-turn `Animatable` immediately. Use the animation itself as touch feedback; do not insert a separate selection-delay phase.
- [ ] Call `onPick(answer)` once when the turn reaches the commit point; finish the visual turn while the runner advances to the next question.
- [ ] Re-run both island-hopping tests and the generic priority-poker test.
- [ ] Commit only Task 2 files.

### Task 3: One-time magical book intro

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/IslandHoppingBookIntro.kt`
- Modify: `app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt`
- Extend test: `app/src/test/java/com/example/ui/screens/IslandHoppingBookContractTest.kt`

**Interfaces:**
- `IslandHoppingBookIntro(onFinished: () -> Unit, modifier: Modifier = Modifier)` wraps the reusable Harmony book cinematic/fallback.
- `QuizRunnerScreen` shows it once for the island-hopping pack before question 1 becomes interactive.

- [ ] Extend the failing contract test to require exact pack gating and one-time intro wiring in `QuizRunnerScreen`.
- [ ] Run the focused test and confirm RED for missing intro wiring.
- [ ] Implement the themed intro wrapper and a `remember(pack.id)` intro-complete state in `QuizRunnerScreen`.
- [ ] Keep Filament lifetime limited to the intro; remove it from composition when the intro completes.
- [ ] Re-run focused tests and generic quiz-runner source contracts.
- [ ] Commit Task 3 files.### Task 4: Blender hero asset completion

**Files:**
- Create: `tools/blender/create_harmony_magic_book.py`
- Create: `app/src/main/assets/models/harmony_magic_book.glb`
- Extend test: `app/src/test/java/com/example/FairyBookFilamentContractTest.kt`

**Interfaces:**
- Produces an animated GLB containing the Harmony book cover, spine, page block, gold trim and emissive heart/gem, with an opening animation usable by `FairyBookFilamentIntro`.

- [ ] Extend the Filament contract test to require the GLB file to exist and be non-empty.
- [ ] Run the test and confirm RED because the asset is currently missing.
- [ ] Write a Blender 4.2 Python script that creates mobile-safe beveled geometry, shared PBR materials and the book-opening keyframes.
- [ ] Run Blender headless with the script and export `harmony_magic_book.glb`.
- [ ] Re-open the GLB headless in Blender and print object/action counts to verify it loads.
- [ ] Re-run the Filament contract test.
- [ ] Commit the Blender script and generated GLB without changing signing/auth configuration.

### Task 5: Regression, build and signer verification

**Files:**
- Test existing island-hopping curation and priority-poker contracts.
- Build output only; no production source changes unless a verification exposes a real defect.

- [ ] Run focused tests for island-hopping routing/transition, priority poker, curated travel questions and FairyBook Filament.
- [ ] Run `gradlew.bat :app:assembleDebug --max-workers=1`.
- [ ] Verify the debug APK signer SHA-1 is still `63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F`.
- [ ] Run `git diff --check` and inspect the diff for auth, Supabase, package ID, locale or signing changes; there must be none.
- [ ] Run `git status --short` and ensure only intentional pre-existing Filament/audio work plus the committed feature work remain.
- [ ] Do not merge until the complete verification set is green.
