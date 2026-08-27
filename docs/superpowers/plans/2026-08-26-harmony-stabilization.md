# Harmony Stabilization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stabilize Harmony Brain, personalized games, Moments, completed-game behavior, Live Change, drawing gameplay and non-image content without damaging protected image/introspection games.

**Architecture:** Consolidate live AI through one Supabase Edge Function and one Android gateway, keep personalization data local in Room, and extend existing Compose/Room models minimally. Separate pure policy logic from Android UI so critical behavior can be tested without an Android SDK.

**Tech Stack:** Kotlin, Jetpack Compose, Room, OkHttp, kotlinx.serialization, Supabase Edge Functions (Deno/TypeScript), Gemini Interactions API.

**Spec:** `docs/superpowers/specs/2026-08-26-harmony-stabilization-design.md`

## Global Constraints
- Chat microphone behavior is unchanged.
- `Tauche ins Selbstbewusstsein` is not materially changed.
- Image-based `Das oder Das` packs are not materially changed.
- Foreground test generation: one attempt immediately then every 60 seconds, maximum 20 stored games per day.
- No fabricated local businesses in offline search fallbacks.
- Existing note preview behavior remains.

---

### Task 1: Pure policies and failing regression tests

**Files:**
- Create: `app/src/main/java/com/example/data/brain/HarmonyBrainIntentPolicy.kt`
- Create: `app/src/main/java/com/example/data/GameRunPolicy.kt`
- Create: `app/src/main/java/com/example/data/brain/AutoGenerationPolicy.kt`
- Create: `tools/harmony_policy_tests.kt`

**Interfaces:**
- Produces: `HarmonyBrainIntentPolicy.needsLiveSearch(query: String): Boolean`
- Produces: `GameRunPolicy.initialState(total: Int, answers: Map<Int,String>): GameRunInitialState`
- Produces: `AutoGenerationPolicy.DAILY_LIMIT`, `INTERVAL_MS`, `canGenerate(count: Int): Boolean`

- [ ] Write policy tests that require German activity/place queries to route live, complete packs to open results, partial packs to resume at the first missing question, and the daily limit to equal 20.
- [ ] Run with `kotlinc` before production helpers exist; confirm compilation/failure is caused by missing helpers.
- [ ] Implement the three small pure helpers.
- [ ] Compile and run the policy tests; require all assertions to pass.

### Task 2: Unified Supabase Harmony Brain backend

**Files:**
- Create: `supabase/functions/harmony-brain-generate/index.ts`
- Modify: `app/src/main/java/com/example/data/brain/gateway/SupabaseHarmonyBrainGateway.kt`
- Modify: `app/src/main/java/com/example/data/SupabaseClientProvider.kt`
- Modify: `app/src/main/java/com/example/ui/HarmonyViewModel.kt`

**Interfaces:**
- Edge request: `{mode, query, context, useCurrentInfo, language}`
- Edge response: `{ok, grounded, answer, questions, recommendations, sources, searchQueries, model, latencyMs, errorType?, errorMessage?}`

- [ ] Add a static contract test script that fails while the gateway still references the old Supabase project/auth flow and old local search helper.
- [ ] Implement the Deno function locally with chat/questions/recommendations and Maps→Search grounding fallback for search.
- [ ] Point Android Supabase configuration/gateway to `yepluyipizbbrgoffqdq`, use the project publishable key, send `BuildConfig.GEMINI_API_KEY` as `x-gemini-key`, and remove anonymous-auth dependence from this path.
- [ ] Replace `HarmonyViewModel.needsBrainWebSearch` with `HarmonyBrainIntentPolicy.needsLiveSearch` and remove fabricated venue names from offline fallback.
- [ ] Deploy `harmony-brain-generate` with `verify_jwt=false` because custom `x-gemini-key` authorization is required by the function.
- [ ] Call the deployed function without a Gemini key and verify an explicit 401 configuration response rather than a crash/HTML response.

### Task 3: Completed-game resume/results behavior

**Files:**
- Modify: `app/src/main/java/com/example/ui/HarmonyViewModel.kt`
- Create: `tools/game_run_source_assertions.py`

**Interfaces:**
- Consumes: `GameRunPolicy.initialState` from Task 1.

- [ ] Add source assertion that fails while `startPack()` hardcodes `currentIndex=0` and `isFinished=false`.
- [ ] Update `startPack()` to use total count + stored answers and open results immediately when complete.
- [ ] Run pure policy tests and source assertions.

### Task 4: Single automatic generation path + 20/day + unread state

**Files:**
- Modify: `app/src/main/java/com/example/data/brain/ForegroundGameGenerator.kt`
- Modify: `app/src/main/java/com/example/data/brain/AutoGenerationState.kt`
- Modify: `app/src/main/java/com/example/data/brain/repository/BrainRepository.kt`
- Modify: `app/src/main/java/com/example/ui/HarmonyViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/GamesScreen.kt`

**Interfaces:**
- Consumes: `AutoGenerationPolicy` from Task 1.
- New unread definition: generated `GAME` with `status=PUBLISHED` and `firstShownAt == null`.

- [ ] Add assertions for no 10-game limit, no two-game startup batch, and unread count using `firstShownAt` rather than total generated count.
- [ ] Make `ForegroundGameGenerator` attempt once on START, then once each 60 seconds, and count only successful inserts up to 20/day.
- [ ] Strengthen `storeGeneratedGame` to reject whole-game near-duplicates using existing normalized generated content.
- [ ] Change Games UI to a primary `Für dich` section with unread badge and generated-game cards; remove Daily Activity banner/card.
- [ ] Run policy/source assertions.

### Task 5: Home cleanup and Live Change dismiss

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- Modify: `app/src/main/java/com/example/ui/screens/LiveChangeOverlay.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`

**Interfaces:**
- `LiveChangeLauncher(onStart: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier)`

- [ ] Add source assertions that fail while Home contains the large `HARMONY BRAIN COACH` block and launcher has no dismiss callback.
- [ ] Remove the Home coach block while retaining the normal Chat-tab Harmony Brain.
- [ ] Add an X dismiss button to Live Change launcher and keep dismissed state in `HarmonyApp` until the next activity recreation or developer action.
- [ ] Run source assertions.

### Task 6: Moments with durable multi-photo storage + Brain memory

**Files:**
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Modify: `app/src/main/java/com/example/data/db/AppDatabase.kt`
- Modify: `app/src/main/java/com/example/data/repository/HarmonyRepository.kt`
- Modify: `app/src/main/java/com/example/data/brain/repository/BrainRepository.kt`
- Modify: `app/src/main/java/com/example/ui/HarmonyViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/MomentsScreen.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Create: `tools/moments_source_assertions.py`

**Interfaces:**
- `MomentEntity.imagePathsJson: String = "[]"`
- `HarmonyRepository.addMoment(title, content, imageUris, emoji)`
- `BrainRepository.recordMoment(title, content)`

- [ ] Add assertions for schema version 8, `MIGRATION_7_8`, image JSON field, multi-image picker and brain recording.
- [ ] Add Room column + migration.
- [ ] Copy picked images into app-owned `filesDir/moments` and persist their paths as JSON.
- [ ] Record a moment memory fact plus extracted interest signals.
- [ ] Replace basic moment card rendering with image carousel/animated card transition and quieter milestone rendering.
- [ ] Run source assertions.

### Task 7: Drawing game

**Files:**
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Modify: `app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt`
- Create: `app/src/main/java/com/example/ui/screens/DrawingPromptCanvas.kt`
- Create: `tools/drawing_source_assertions.py`

**Interfaces:**
- New pack type: `draw`.
- `DrawingPromptCanvas(prompt, onDone)` returns completion through `onDone("Gezeichnet 🎨")`.

- [ ] Add assertions requiring `zeichnen` type `draw`, exactly five fun prompts, and a canvas component with palette/clear/undo.
- [ ] Convert the pack to five no-option drawing prompts.
- [ ] Add Compose canvas with palette, brush sizes, undo and clear.
- [ ] Route draw pack questions through the canvas in QuizRunner while preserving existing quiz/tot behavior.
- [ ] Run source assertions.

### Task 8: Content expansion and organization

**Files:**
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Create: `tools/content_quality_assertions.py`

**Interfaces:**
- Existing pack IDs remain stable except newly added packs use unique IDs.

- [ ] Add quality assertions requiring `Unbeliebte Meinungen >= 16`, `Zustimmen oder Ablehnen >= 15`, no `tot` type for `liebegleichgewicht`, and protected image pack pair counts unchanged from baseline snapshot.
- [ ] Convert `Liebe im Gleichgewicht` to normal quiz questions with four nuanced response options.
- [ ] Add at least ten new questions each to `Unbeliebte Meinungen` and `Zustimmen oder Ablehnen`.
- [ ] Expand thin non-image packs and add natural new packs across relationship, future, travel, food, values and fun/creative topics.
- [ ] Reassign obviously mismatched topics/categories without changing protected content.
- [ ] Run content assertions and protected-pair snapshot comparison.

### Task 9: Notes list behavior

**Files:**
- Inspect/modify only if required: `app/src/main/java/com/example/ui/memory/MemoryViewModel.kt`
- Inspect/modify only if required: `app/src/main/java/com/example/ui/screens/MemoryEditorSheet.kt`
- Inspect/modify only if required: `app/src/main/java/com/example/data/repository/MemoryRepository.kt`
- Create: `tools/memory_list_assertions.py`

**Interfaces:**
- One save action for a multiline list must produce one `MemoryEntryEntity`, not one per line.

- [ ] Trace list save data flow and identify every `insertEntries` call reachable from the editor.
- [ ] Encode a source regression assertion for the confirmed faulty path, or record `no source-level split found` if current code already saves a single entry.
- [ ] If a source split is found, change only that path to persist one entry with newline-separated list body.
- [ ] Re-run memory assertions and ensure link/note preview code remains untouched.

### Task 10: Verification and handoff ZIP

**Files:**
- Modify: `BUILD_REPORT.md`
- Create: `HARMONY_REWORK_REPORT.md`
- Create: `/mnt/data/Harmony-2.1-stabilized.zip`

**Interfaces:**
- Final ZIP contains source, Supabase function source, spec/plan, and test scripts; it excludes `.git` and transient caches.

- [ ] Run all policy tests and source assertion scripts.
- [ ] Run existing compatible Python verifiers (`verify_localization_repair.py`, `verify_language_inventory_guard.py`, `verify_merlin_theme_assets.py`) and record results.
- [ ] Audit protected game content and microphone-related source diff.
- [ ] Confirm Supabase Edge Function is ACTIVE and retrieve its deployed source/version.
- [ ] Write the rework report with changed files, tests run, known blocker that Android APK compilation is unavailable without wrapper/SDK, and exact next build command for Android Studio.
- [ ] Package the clean working tree into `/mnt/data/Harmony-2.1-stabilized.zip` and verify the archive can be listed.
