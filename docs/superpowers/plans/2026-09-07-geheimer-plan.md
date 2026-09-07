# Der geheime Plan Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the three-chapter private-choice game "Der geheime Plan" with local custom answers and a final shared plan reveal.

**Architecture:** A fixed `geheimer_plan` pack supplies the three prompts. `SecretPlanAnswerCodec` stores each private answer pair, and `SecretPlanProgress` reconstructs the final reveal from saved chapters and the current pair. `FullscreenQuestionMechanicBoard` routes only that pack to `SecretPlanBoard`; all other secret-choice packs remain unchanged.

**Tech Stack:** Kotlin, Jetpack Compose, Room `AnswerEntity` history, Compose UI tests, JUnit 4/Robolectric, Gradle Android debug build.

**Spec:** `docs/superpowers/specs/2026-09-07-geheimer-plan-design.md`

## Global Constraints

- Use fixed pack ID `geheimer_plan` and route it only when `FullscreenGameMechanicKind.SECRET_CHOICE` is active.
- Keep answers local; do not add network, AI, permissions, Supabase, auth, Google-login, e-mail-login, profile-sync, or signing changes.
- Offer four fixed options plus `Eigene Idee …` in every chapter; custom text is trimmed, non-empty, and capped at 80 characters.
- Persist private pairs using `secret-plan-v1:` and expose answer labels only on the third chapter's final plan card.
- Use Aurora-Glass colors and ensure only explicit answer, handoff, reveal, and save controls are clickable.
- Do not alter generic `SECRET_CHOICE` routing or the `h500_076_wochenendtrip_szenario` route.

---

### Task 1: Register the curated three-chapter pack

**Files:**
- Modify: `app/src/main/java/com/example/data/model/HarmonyExpansionPacks.kt`
- Test: `app/src/test/java/com/example/SecretPlanPackTest.kt`

**Interfaces:**
- Produces: `SecretPlanCatalog.PACK_ID`, `SecretPlanCatalog.CHAPTERS`, and one `QuestionPack` in `HarmonyExpansionPacks.PACKS`.
- Consumes: `QuestionPack` and `Question` from `Models.kt`.

- [ ] **Step 1: Write the failing pack-contract test**

```kotlin
@Test
fun `secret plan pack has the three approved chapters and custom card`() {
    val pack = HarmonyExpansionPacks.PACKS.single { it.id == SecretPlanCatalog.PACK_ID }
    assertEquals(3, pack.questions.size)
    assertEquals("Wofür nehmt ihr euch spontan einen freien Tag?", pack.questions[0].q)
    assertEquals("Eigene Idee …", pack.questions[0].options.last())
    assertEquals("Welche gemeinsame Idee sollte endlich passieren?", pack.questions[2].q)
    assertTrue(pack.tags.contains("mechanik_geheime_wahl"))
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `& "C:\\Users\\Ralfg\\.stromruf-build-tools\\gradle\\gradle-9.3.1\\bin\\gradle.bat" testDebugUnitTest --tests com.example.SecretPlanPackTest`

Expected: FAIL because `SecretPlanCatalog` and `geheimer_plan` do not exist.

- [ ] **Step 3: Add the catalog and pack**

Add this catalog to `HarmonyExpansionPacks.kt`:

```kotlin
object SecretPlanCatalog {
    const val PACK_ID = "geheimer_plan"
    const val CUSTOM_OPTION = "Eigene Idee …"
    val CHAPTERS = listOf(
        Question("Wofür nehmt ihr euch spontan einen freien Tag?", listOf("Kleiner Roadtrip", "Zeit nur für uns", "Etwas Neues erleben", "Ein Herzensprojekt starten", CUSTOM_OPTION)),
        Question("Welche Überraschung würde euch wirklich freuen?", listOf("Ein geplanter Abend", "Ein spontaner Ausflug", "Eine persönliche Geste", "Ein gemeinsames Upgrade", CUSTOM_OPTION)),
        Question("Welche gemeinsame Idee sollte endlich passieren?", listOf("Unser nächster Kurztrip", "Ein neues Ritual", "Ein Projekt zu zweit", "Ein mutiger erster Schritt", CUSTOM_OPTION))
    )
}
```

Append a `QuestionPack` with ID `SecretPlanCatalog.PACK_ID`, title `Der geheime Plan`, `cat = "reden"`, `topic = "beziehung"`, `type = "quiz"`, emoji `🗺️`, and tags `listOf("zukunft", "planung", "geheime-wahl", "mechanik_geheime_wahl")`.

- [ ] **Step 4: Run the pack-contract test to verify it passes**

Run the command from Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

Run: `git add app/src/main/java/com/example/data/model/HarmonyExpansionPacks.kt app/src/test/java/com/example/SecretPlanPackTest.kt; git commit -m "feat: add secret plan pack"`

### Task 2: Define private answer storage and final-plan reconstruction

**Files:**
- Create: `app/src/main/java/com/example/data/model/SecretPlan.kt`
- Test: `app/src/test/java/com/example/SecretPlanProgressTest.kt`

**Interfaces:**
- Produces: `SecretPlanChoice`, `SecretPlanPair`, `SecretPlanPlanLine`, `SecretPlanAnswerCodec`, and `SecretPlanProgress`.
- Consumes: `AnswerEntity` and `SecretPlanCatalog.PACK_ID`.
- Used by: `SecretPlanBoard` in Task 3.

- [ ] **Step 1: Write failing codec and progress tests**

```kotlin
@Test
fun `codec preserves preset and trimmed custom choices`() {
    val encoded = SecretPlanAnswerCodec.encode(
        SecretPlanChoice.Preset("Kleiner Roadtrip"),
        SecretPlanChoice.Custom("  Lissabon   im Frühling  ")
    )
    assertEquals(
        SecretPlanPair(SecretPlanChoice.Preset("Kleiner Roadtrip"), SecretPlanChoice.Custom("Lissabon im Frühling")),
        SecretPlanAnswerCodec.decode(encoded)
    )
}
@Test
fun `progress groups equal choices as now plan and different choices as also nice`() {
    val history = listOf(
        AnswerEntity(SecretPlanCatalog.PACK_ID, 0, SecretPlanAnswerCodec.encode("Kleiner Roadtrip", "Kleiner Roadtrip"), 1L),
        AnswerEntity(SecretPlanCatalog.PACK_ID, 1, SecretPlanAnswerCodec.encode("Ein geplanter Abend", "Eine persönliche Geste"), 2L)
    )
    val lines = SecretPlanProgress.lines(history, 2, SecretPlanPair("Unser nächster Kurztrip", "Unser nächster Kurztrip"))
    assertEquals(listOf("Kleiner Roadtrip", "Unser nächster Kurztrip"), lines.filter { it.isShared }.map { it.first.text })
    assertEquals("Ein geplanter Abend", lines.single { !it.isShared }.first.text)
}
```

- [ ] **Step 2: Run the tests to verify they fail**

Run: `& "C:\\Users\\Ralfg\\.stromruf-build-tools\\gradle\\gradle-9.3.1\\bin\\gradle.bat" testDebugUnitTest --tests com.example.SecretPlanProgressTest`

Expected: FAIL because the secret-plan state types do not exist.

- [ ] **Step 3: Implement strict codecs and deterministic reconstruction**

Implement this public surface in `SecretPlan.kt`:

```kotlin
sealed interface SecretPlanChoice {
    val text: String
    data class Preset(override val text: String) : SecretPlanChoice
    data class Custom(override val text: String) : SecretPlanChoice
}
data class SecretPlanPair(val first: SecretPlanChoice, val second: SecretPlanChoice)
data class SecretPlanPlanLine(val chapterIndex: Int, val first: SecretPlanChoice, val second: SecretPlanChoice) {
    val isShared: Boolean get() = SecretPlanProgress.normalized(first.text) == SecretPlanProgress.normalized(second.text)
}
```

Add `String` overloads to `SecretPlanAnswerCodec.encode` that make preset choices. Use a unit separator between typed fields. Reject wrong prefixes, missing fields, blank custom text, custom text above 80 characters, control separators, and unknown choice kinds. `SecretPlanProgress.lines(history, currentIndex, current)` filters exactly `SecretPlanCatalog.PACK_ID`, keeps one valid pair per index 0 through 2, overlays the transient current pair, returns `null` until all three exist, then returns lines sorted 0, 1, 2.

- [ ] **Step 4: Run the tests to verify they pass**

Run the command from Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

Run: `git add app/src/main/java/com/example/data/model/SecretPlan.kt app/src/test/java/com/example/SecretPlanProgressTest.kt; git commit -m "feat: add secret plan answer state"`

### Task 3: Build the private-choice and final reveal board

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/SecretPlanBoard.kt`
- Test: `app/src/test/java/com/example/SecretPlanBoardTest.kt`

**Interfaces:**
- Consumes: `SecretPlanCatalog`, `SecretPlanAnswerCodec`, `SecretPlanProgress`, `AnswerEntity`, `ProfileEntity`, `FullscreenMechanicShell`, and existing private handoff panes.
- Produces: `SecretPlanBoard(question, options, questionIndex, historicalAnswers, selectedAnswer, profile, onPick, modifier)`.
- Used by: `FullscreenQuestionMechanicBoard` in Task 4.

- [ ] **Step 1: Write failing Compose tests**

```kotlin
@Test
fun `custom entry is private and cannot be empty`() {
    setBoard(questionIndex = 0)
    composeRule.onNodeWithTag("secret_plan_custom_option").performClick()
    composeRule.onNodeWithTag("secret_plan_custom_save").assertIsNotEnabled()
    composeRule.onNodeWithTag("secret_plan_custom_input").performTextInput("Lissabon")
    composeRule.onNodeWithTag("secret_plan_custom_save").performClick()
    composeRule.onAllNodesWithText("Lissabon", substring = true).assertCountEquals(0)
}
@Test
fun `final chapter reveals plan only after both private choices`() {
    setBoard(questionIndex = 2, historicalAnswers = savedFirstTwoChapters())
    completeTwoPrivateChoices()
    composeRule.onNodeWithTag("secret_plan_final_card").assertExists()
    composeRule.onNodeWithText("Jetzt planen").assertExists()
    composeRule.onNodeWithText("Auch schön").assertExists()
}
```

- [ ] **Step 2: Run the board tests to verify they fail**

Run: `& "C:\\Users\\Ralfg\\.stromruf-build-tools\\gradle\\gradle-9.3.1\\bin\\gradle.bat" testDebugUnitTest --tests com.example.SecretPlanBoardTest`

Expected: FAIL because `SecretPlanBoard` and its test tags do not exist.

- [ ] **Step 3: Implement the Compose state machine**

Use `rememberSaveable(questionIndex, selectedAnswer)` phases `PERSON_A`, `HANDOFF`, `PERSON_B`, `SEALED`, and `FINAL_PLAN`. Fixed options use `LargeOptionGrid`; append a dedicated `Eigene Idee …` action tagged `secret_plan_custom_option`. The custom action renders an `OutlinedTextField` tagged `secret_plan_custom_input` and a disabled-until-valid `PrimaryMechanicButton` tagged `secret_plan_custom_save`.

In private phases, selection never displays the label. `HANDOFF` uses `PairPrivateHandoffPane`. `SEALED` shows `secret_plan_chapter_continue` for indices 0 and 1, and calls `onPick(SecretPlanAnswerCodec.encode(first, second))` exactly once. For index 2, calculate `SecretPlanProgress.lines`; only three valid lines open `FINAL_PLAN`. Render `secret_plan_final_card`, divide lines by `isShared`, and use headings `Jetzt planen` and `Auch schön` without scores or winner copy. `secret_plan_save` calls `onPick` once. Make `secret_plan_background` and `secret_plan_pandas` non-clickable Aurora-Glass decoration; use local Compose animation only.

- [ ] **Step 4: Run the board tests to verify they pass**

Run the command from Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

Run: `git add app/src/main/java/com/example/ui/screens/SecretPlanBoard.kt app/src/test/java/com/example/SecretPlanBoardTest.kt; git commit -m "feat: add secret plan reveal board"`

### Task 4: Route only the fixed game to the new board

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt`
- Test: `app/src/test/java/com/example/SecretPlanRoutingTest.kt`

**Interfaces:**
- Consumes: `FullscreenGameMechanicKind.SECRET_CHOICE` and `SecretPlanCatalog.PACK_ID`.
- Produces: exact-pack routing to `SecretPlanBoard`; generic secret-choice routing remains `SecretChoiceRevealBoard`.

- [ ] **Step 1: Write the failing routing tests**

```kotlin
@Test
fun `secret plan id renders its dedicated board`() {
    setContent(packId = SecretPlanCatalog.PACK_ID)
    composeRule.onNodeWithTag("secret_plan_board").assertExists()
}
@Test
fun `another secret choice pack keeps the generic board`() {
    setContent(packId = "h500_057_karriereplaene_geheime_wahl")
    composeRule.onNodeWithTag("secret_choice_board").assertExists()
}
```

- [ ] **Step 2: Run the routing tests to verify they fail**

Run: `& "C:\\Users\\Ralfg\\.stromruf-build-tools\\gradle\\gradle-9.3.1\\bin\\gradle.bat" testDebugUnitTest --tests com.example.SecretPlanRoutingTest`

Expected: FAIL because the exact pack currently follows `SecretChoiceRevealBoard`.

- [ ] **Step 3: Add an exact routing guard before the `when` expression**

```kotlin
if (kind == FullscreenGameMechanicKind.SECRET_CHOICE && packId == SecretPlanCatalog.PACK_ID) {
    SecretPlanBoard(
        question = question, options = options, questionIndex = questionIndex,
        historicalAnswers = historicalAnswers, selectedAnswer = selectedAnswer,
        profile = profile, onPick = onPick, modifier = modifier
    )
    return
}
```

Keep the existing `SECRET_CHOICE -> SecretChoiceRevealBoard(...)` arm unchanged.

- [ ] **Step 4: Run the routing tests to verify they pass**

Run the command from Step 2. Expected: PASS.

- [ ] **Step 5: Commit**

Run: `git add app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt app/src/test/java/com/example/SecretPlanRoutingTest.kt; git commit -m "feat: route secret plan game"`

### Task 5: Verify the integrated game and record its final state

**Files:**
- Modify: `app/src/test/java/com/example/SecretPlanBoardTest.kt`
- Verification output: `app/build/secret-plan-preview/final-plan.png` (generated, never committed)

**Interfaces:**
- Consumes: complete Tasks 1–4.
- Produces: visual regression evidence for the final reveal.

- [ ] **Step 1: Add a final-plan render assertion**

Append this after final-card content assertions:

```kotlin
composeRule.onRoot().captureRoboImage(
    filePath = "build/secret-plan-preview/final-plan.png"
)
```

- [ ] **Step 2: Run the focused integrated suite**

Run: `& "C:\\Users\\Ralfg\\.stromruf-build-tools\\gradle\\gradle-9.3.1\\bin\\gradle.bat" testDebugUnitTest --tests com.example.SecretPlanPackTest --tests com.example.SecretPlanProgressTest --tests com.example.SecretPlanBoardTest --tests com.example.SecretPlanRoutingTest assembleDebug`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Record and inspect the final-plan state**

Run: `& "C:\\Users\\Ralfg\\.stromruf-build-tools\\gradle\\gradle-9.3.1\\bin\\gradle.bat" recordRoborazziDebug --tests com.example.SecretPlanBoardTest`

Expected: `app/build/secret-plan-preview/final-plan.png` exists and has readable Aurora-Glass sections, no private labels before reveal, and no winner language.

- [ ] **Step 4: Verify final diff and commit**

Run: `git diff --check; git status --short; git add app/src/test/java/com/example/SecretPlanBoardTest.kt; git commit -m "test: verify secret plan reveal"`

Expected: only the intended test change is staged; the generated preview output is not staged.

