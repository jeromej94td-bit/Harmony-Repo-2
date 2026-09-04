# Harmony Question Mechanics Audit Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make every shipped Harmony question resolve to a deliberate, semantically appropriate answer mechanic and remove the current app-wide meaningless custom-answer fallback.

**Architecture:** Extend the existing `QuestionInteractionPolicy` in `data/model/QuestionInteraction.kt` instead of creating a duplicate policy. Add an explicit response-mode layer with migration-safe defaults, route the generic quiz runner through that policy, then add a pure advisory audit that inventories all loaded `QuestionPack` content and produces curated work items. Existing fullscreen mechanics and the merged photo renderer stay intact and are treated as higher-priority specialized routes.

**Tech Stack:** Kotlin, Jetpack Compose, JUnit 4, existing `QuestionPack`/`Question` models, existing fullscreen mechanic policies, GitHub contents API for branch updates.

**Spec:** `docs/superpowers/specs/2026-08-29-harmony-question-mechanics-audit-design.md`

## Global Constraints

- No keyword-driven automatic mechanic switching at runtime.
- Existing saved `AnswerEntity.answerText` values remain readable; no destructive Room migration for this rework.
- Existing fullscreen mechanics and Photo Rework behavior must not regress.
- `Schreibe deine eigene Antwort` becomes opt-in instead of being injected globally.
- `Ich habe noch nie` keeps its established `Überspringen` behavior.
- Gallery files remain local and local file paths must not be written into Harmony Brain answer text.
- Heuristics may flag audit candidates, but may never silently change runtime mechanics.
- Do not claim a green Android build until an executable full build actually runs.

---

### Task 1: Extend the Existing Interaction Policy With Explicit Response Semantics

**Files:**
- Modify: `app/src/main/java/com/example/data/model/QuestionInteraction.kt`
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Modify/Test: `app/src/test/java/com/example/data/model/QuestionInteractionTest.kt`

**Interfaces:**
- Produces `QuestionResponseKind`, `QuestionInteractionSpec`, `Question.responseKind`, and `QuestionInteractionPolicy.resolveSpec(pack, questionIndex, question)`.
- Keeps the existing `QuestionInteractionPolicy.resolve(pack, questionIndex)` and `FullscreenGameMechanicPolicy.resolve(...)` behavior source-compatible for existing callers.

- [ ] **Step 1: Write the failing response-policy tests**

Add tests equivalent to:

```kotlin
@Test
fun `ordinary choice is fixed by default`() {
    val question = Question("Was magst du?", listOf("A", "B"))
    val pack = normalPack(question)
    assertEquals(
        QuestionResponseKind.FIXED_CHOICE,
        QuestionInteractionPolicy.resolveSpec(pack, 0, question).responseKind
    )
    assertEquals(false, QuestionInteractionPolicy.resolveSpec(pack, 0, question).allowCustomText)
}

@Test
fun `explicit optional text stays available`() {
    val question = Question(
        "Was fehlt dir?",
        listOf("Zeit", "Ruhe"),
        responseKind = QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT
    )
    val spec = QuestionInteractionPolicy.resolveSpec(normalPack(question), 0, question)
    assertEquals(QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT, spec.responseKind)
    assertEquals(true, spec.allowCustomText)
}

@Test
fun `question without options is open text by default`() {
    val question = Question("Was möchtest du mir sagen?")
    val spec = QuestionInteractionPolicy.resolveSpec(normalPack(question), 0, question)
    assertEquals(QuestionResponseKind.OPEN_TEXT, spec.responseKind)
}
```

Also assert that existing ranking/person-assignment/fullscreen routes still resolve exactly as before.

- [ ] **Step 2: Verify RED**

Run a focused Kotlin/JUnit test when a working Gradle environment is available. If repository Gradle remains unavailable, compile a minimal `kotlinc` harness containing the new tests and minimal `Question`/`QuestionPack` stubs. Expected failure before implementation: unresolved `QuestionResponseKind`, `responseKind`, `QuestionInteractionSpec`, or `resolveSpec`.

- [ ] **Step 3: Add migration-safe model fields and resolver**

Implement in `Models.kt`:

```kotlin
data class Question(
    val q: String,
    val options: List<String> = emptyList(),
    val defaultMine: String? = null,
    val responseKind: QuestionResponseKind? = null
)
```

Implement in `QuestionInteraction.kt`:

```kotlin
enum class QuestionResponseKind {
    FIXED_CHOICE,
    CHOICE_WITH_OPTIONAL_TEXT,
    OPEN_TEXT,
    PHOTO_ONLY,
    CHOICE_WITH_OPTIONAL_PHOTO
}

data class QuestionInteractionSpec(
    val responseKind: QuestionResponseKind,
    val allowCustomText: Boolean = false,
    val allowSkip: Boolean = false,
    val fullscreenMechanic: FullscreenGameMechanicKind? = null
)
```

Add `resolveSpec(pack, questionIndex, question)` with this order:

1. keep `FullscreenGameMechanicPolicy.resolve(pack, questionIndex)` as `fullscreenMechanic`;
2. explicit `question.responseKind` wins for standard response semantics;
3. `pack.cat == "nie"` resolves fixed choice with `allowSkip=true`;
4. empty `question.options` resolves `OPEN_TEXT`;
5. all other normal questions resolve `FIXED_CHOICE`;
6. `allowCustomText` is true only for `CHOICE_WITH_OPTIONAL_TEXT` and `OPEN_TEXT`.

Do not use prompt keywords in this method.

- [ ] **Step 4: Verify GREEN and compatibility**

Run the focused interaction tests. Expected: new response tests PASS and all pre-existing `QuestionInteractionTest` cases remain PASS.

- [ ] **Step 5: Commit**

Commit message: `feat: add explicit question response policy`

---

### Task 2: Remove the Global Custom-Answer Injection From the Quiz Runner

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt`
- Create/Test: `app/src/test/java/com/example/ui/screens/QuizAnswerOptionsPolicyTest.kt`
- Create: `app/src/main/java/com/example/ui/screens/QuizAnswerOptionsPolicy.kt`

**Interfaces:**
- Produces pure `QuizAnswerOptionsPolicy.build(processedOptions, spec, customTextLabel, skipLabel)` returning the visible answer-button list and whether the custom-text dialog entry is present.
- Consumes `QuestionInteractionSpec` from Task 1.

- [ ] **Step 1: Write failing pure option-list tests**

```kotlin
@Test
fun `fixed choices do not receive an artificial custom answer`() {
    val spec = QuestionInteractionSpec(QuestionResponseKind.FIXED_CHOICE)
    assertEquals(
        listOf("A", "B", "C"),
        QuizAnswerOptionsPolicy.build(listOf("A", "B", "C"), spec, "Eigene Antwort", "Überspringen")
    )
}

@Test
fun `optional text receives exactly one custom entry`() {
    val spec = QuestionInteractionSpec(
        QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT,
        allowCustomText = true
    )
    assertEquals(
        listOf("A", "B", "Eigene Antwort"),
        QuizAnswerOptionsPolicy.build(listOf("A", "B"), spec, "Eigene Antwort", "Überspringen")
    )
}

@Test
fun `never have I ever keeps skip instead of custom text`() {
    val spec = QuestionInteractionSpec(
        QuestionResponseKind.FIXED_CHOICE,
        allowSkip = true
    )
    assertEquals(
        listOf("Habe ich", "Habe ich noch nie", "Überspringen"),
        QuizAnswerOptionsPolicy.build(listOf("Habe ich", "Habe ich noch nie"), spec, "Eigene Antwort", "Überspringen")
    )
}
```

- [ ] **Step 2: Verify RED**

Expected failure: `QuizAnswerOptionsPolicy` does not exist.

- [ ] **Step 3: Implement the pure policy**

`build(...)` rules:

```kotlin
return when {
    spec.allowSkip -> options + skipLabel
    spec.allowCustomText && spec.responseKind == QuestionResponseKind.CHOICE_WITH_OPTIONAL_TEXT -> options + customTextLabel
    spec.responseKind == QuestionResponseKind.OPEN_TEXT -> listOf(customTextLabel)
    else -> options
}
```

Avoid duplicate fallback entries if a source option already equals the localized label.

- [ ] **Step 4: Route `QuizRunnerScreen` through the policy**

Replace the current unconditional:

```kotlin
val fallbackText = if (isNie) ... else ...
val options = processedOptions + fallbackText
```

with:

```kotlin
val interactionSpec = QuestionInteractionPolicy.resolveSpec(pack, activeRun.currentIndex, q)
val options = QuizAnswerOptionsPolicy.build(
    processedOptions,
    interactionSpec,
    customTextLabel = tr("Schreibe deine eigene Antwort", "Write your own answer"),
    skipLabel = tr("Überspringen", "Skip")
)
```

Keep the existing pizza/burger two-option special case and specialized `imageChoiceKind != null` path unchanged. Only open the custom-text dialog when the clicked entry is the custom-text label and `interactionSpec.allowCustomText` is true.

- [ ] **Step 5: Verify**

Run the pure option-policy tests and static-check that special image/fullscreen branches still bypass the normal option renderer.

- [ ] **Step 6: Commit**

Commit message: `fix: make custom quiz answers opt in`

---

### Task 3: Move the Existing Photo Rework Onto Explicit Response Semantics

**Files:**
- Modify: `app/src/main/java/com/example/data/model/Models.kt`
- Modify: `app/src/main/java/com/example/ui/screens/PhotoQuestionPolicy.kt`
- Modify: `app/src/main/java/com/example/ui/screens/HarmonyImageChoicePolicy.kt`
- Modify/Test: `app/src/test/java/com/example/ui/screens/PhotoQuestionPolicyTest.kt`
- Modify/Test: `app/src/test/java/com/example/ui/screens/PhotoQuestionRoutingTest.kt`

**Interfaces:**
- Existing gallery UI remains `PhotoQuestionBoard`.
- The two approved questions get explicit `QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO` and `PHOTO_ONLY` metadata in their source `Question` definitions.
- `PhotoQuestionPolicy` remains responsible for copy, legacy answer normalization, storage-file naming, and backward-compatible raw-question fallback.

- [ ] **Step 1: Add failing metadata-routing tests**

Assert that the shipped `gespraechsanreger` and `schnapp` questions resolve through `QuestionInteractionPolicy.resolveSpec(...)` to the correct photo response kinds, while an ordinary prompt containing `Foto` remains `FIXED_CHOICE`.

- [ ] **Step 2: Verify RED**

Expected: current questions do not carry explicit photo response metadata.

- [ ] **Step 3: Annotate only the two approved questions**

Use:

```kotlin
responseKind = QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO
```

for `Was ist dein Lieblingsfoto von uns? 📸`, and:

```kotlin
responseKind = QuestionResponseKind.PHOTO_ONLY
```

for `Welches gemeinsame Foto ist dein Lieblingsfoto?`.

Keep exact-text fallback in `PhotoQuestionPolicy.modeForQuestion(...)` for old/dynamic copies already shipped; do not add keyword matching.

- [ ] **Step 4: Keep renderer routing backward compatible**

`HarmonyImageChoicePolicy` continues returning `MEMORY_MATCH` for those photo interactions until a dedicated enum case is worthwhile. Add no new gallery storage behavior.

- [ ] **Step 5: Verify and commit**

Expected: both photo tests PASS; unrelated `Foto` wording stays normal. Commit message: `refactor: bind photo questions to explicit response modes`

---

### Task 4: Add the App-Wide Advisory Question Audit

**Files:**
- Create: `app/src/main/java/com/example/data/QuestionMechanicsAudit.kt`
- Create/Test: `app/src/test/java/com/example/data/QuestionMechanicsAuditTest.kt`

**Interfaces:**
- Produces `QuestionAuditFinding`, `QuestionAuditKind`, and `QuestionMechanicsAudit.scan(packs)`.
- Audit findings are read-only data and do not modify `QuestionPack` or policy results.

- [ ] **Step 1: Write failing audit tests**

Use fixtures that must flag:

```kotlin
QuestionPack(
    id = "photo_candidate",
    ...,
    questions = listOf(Question("Welches Foto würdest du auswählen?", listOf("A", "B")))
)
```

as `PHOTO_SEMANTICS_CANDIDATE`, and:

```kotlin
Question("Ordne diese vier Dinge nach Wichtigkeit", listOf("A", "B", "C", "D"))
```

as `ORDERING_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC` when no ranking mechanic is resolved.

Also test duplicate options, empty-option quiz questions, and source options literally containing generic fallback text.

- [ ] **Step 2: Verify RED**

Expected: audit types do not exist.

- [ ] **Step 3: Implement advisory scan**

`QuestionAuditKind` includes at least:

```kotlin
PHOTO_SEMANTICS_CANDIDATE,
ORDERING_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC,
PREDICTION_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC,
EMPTY_OPTIONS_WITHOUT_OPEN_TEXT,
DUPLICATE_OPTIONS,
GENERIC_FALLBACK_IN_SOURCE_OPTIONS,
UNSTABLE_INDEX_SPECIAL_CASE
```

Each `QuestionAuditFinding` stores `packId`, `questionIndex`, raw question, current resolved response/fullscreen mechanic, kind, and a short reason.

Keyword/regex checks are allowed only here as candidate detectors. `scan(...)` must never mutate packs or call any write path.

- [ ] **Step 4: Verify audit purity**

Test that `scan` leaves input packs structurally equal before/after and that policy output for the same question is unchanged.

- [ ] **Step 5: Commit**

Commit message: `feat: add advisory question mechanics audit`

---

### Task 5: Curate High-Confidence Mismatches and Stabilize Touched Routes

**Files:**
- Create: `app/src/main/java/com/example/data/model/QuestionMechanicsCuration.kt`
- Create/Test: `app/src/test/java/com/example/data/model/QuestionMechanicsCurationTest.kt`
- Modify: `app/src/main/java/com/example/data/model/QuestionInteraction.kt`
- Modify only the specific generated/default source files whose candidates are accepted after review.

**Interfaces:**
- Produces a stable key `QuestionMechanicsCuration.key(packId, rawQuestion)` and explicit curated response/fullscreen overrides keyed by stable pack+normalized-prompt identity.
- `QuestionInteractionPolicy.resolveSpec(...)` consults explicit question metadata first, then curated stable-key overrides, then existing legacy/fullscreen routing, then safe defaults.

- [ ] **Step 1: Write stable-key and override precedence tests**

```kotlin
@Test
fun `stable key ignores insignificant whitespace`() {
    assertEquals(
        QuestionMechanicsCuration.key("p", "  Eine   Frage? "),
        QuestionMechanicsCuration.key("p", "Eine Frage?")
    )
}

@Test
fun `explicit metadata beats curated fallback`() {
    // explicit responseKind on Question must win over a curation entry for the same stable key
}
```

- [ ] **Step 2: Verify RED**

Expected: curation layer does not exist.

- [ ] **Step 3: Implement deterministic normalization and precedence**

Normalize trim + repeated whitespace only. Do not lowercase or strip punctuation because semantically distinct prompts must not collapse accidentally.

- [ ] **Step 4: Inventory shipped packs and accept only high-confidence findings**

Run the audit against `HarmonyPacksData.PACKS` plus generated packs loaded into the runtime registry. For each accepted mismatch, add an explicit stable-key curation entry or source metadata. Do not convert ordinary mentions such as `Achterbahn-Foto` or `peinlichstes Foto des Abends` into gallery input.

- [ ] **Step 5: Add regression tests for every accepted curation group**

At minimum cover photo/media, ordering/ranking, partner prediction/secret choice, and open-text-vs-fixed-choice corrections that are actually accepted from the inventory.

- [ ] **Step 6: Commit**

Commit message: `fix: curate question mechanics mismatches`

---

### Task 6: Release Verification and PR Readiness

**Files:**
- Update: `docs/superpowers/specs/2026-08-29-harmony-question-mechanics-audit-design.md` only if implementation discoveries materially changed the documented architecture.
- Update PR #78 body with exact verification and remaining caveats.

**Interfaces:**
- No new runtime API.

- [ ] **Step 1: Run focused pure verification**

Run all new/modified pure policy tests. If full Gradle execution is unavailable, run `kotlinc` smoke harnesses for the pure policy and audit code and record that limitation explicitly.

- [ ] **Step 2: Verify current branch diff**

Confirm only intended question-mechanics, tests, and design/plan files changed. Confirm the branch is not accidentally carrying unrelated 360/Proposal/Live-Change work.

- [ ] **Step 3: Check full Android build availability**

If executable GitHub Actions/Gradle is available, run unit tests and `assembleDebug`. If jobs terminate before executable steps, report that as an infrastructure limitation and do not claim a green build.

- [ ] **Step 4: Review runtime-critical regressions**

Confirm by code/tests:

- fixed choices show only their real options;
- optional-text questions still expose custom text;
- `nie` retains Skip;
- photo-only and optional-photo still route to `PhotoQuestionBoard`;
- current ranking, assignment, prediction, secret, scale, scenario, priority, tournament, memory, and deep-talk policies still route to their existing boards;
- the audit is advisory-only.

- [ ] **Step 5: Update PR #78 from Draft to review-ready only when all executable verification available in this environment has passed**

Do not merge automatically unless explicitly authorized.