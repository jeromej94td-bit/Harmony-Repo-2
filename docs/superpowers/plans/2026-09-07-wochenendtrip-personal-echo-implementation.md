# Wochenendtrip Personal Echo Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the approved `Wochenendtrip - Unser Kompass` flow that selects two genuine prior multiple-choice answers, renders them as image-backed rotating seals, keeps both new choices private, and reveals only after both people answered.

**Architecture:** A pure Kotlin catalog and selector decode the existing `AnswerEntity` history and return zero or one stable two-motif duel per question. A dedicated Compose board owns the private two-person state machine and uses eight local scene assets; `QuizRunnerScreen` passes read-only history and routes only the fixed weekend pack ID to the new board. All other scenario games keep the existing `ScenarioBoard`.

**Tech Stack:** Kotlin, Jetpack Compose, Room `AnswerEntity`, existing `EitherOrAnswerCodec`, Android resources, JUnit 4, Compose UI tests, Gradle.

**Spec:** `docs/superpowers/specs/2026-09-07-wochenendtrip-personal-echo-design.md`

## Global Constraints

- Activate the new board only for pack ID `h500_076_wochenendtrip_szenario`; never route by question text.
- Read only historical multiple-choice answers; exclude free text, media, generated guesses, and the current weekend pack.
- The first release supports exactly eight motifs: Couch & Decke, Kino, Stadt, Land, Roadtrip, Zug, Camping, and 5-Sterne-Hotel.
- Show no historical or current answer until both people have chosen.
- Only the two seals are clickable during choice; only opened seals are clickable for continuation.
- Use local assets and deterministic local selection; no runtime image generation or network call.
- Preserve Google authentication, Android signing, Supabase, other packs, and global navigation.
- Keep `gradle.properties`, `.idea/`, and unrelated working-tree changes out of every commit.

---

## File Structure

- Create `app/src/main/java/com/example/data/model/WeekendEcho.kt`: motif catalog, history decoding, deterministic selection, and answer codec.
- Create `app/src/test/java/com/example/WeekendEchoSelectorTest.kt`: catalog, privacy source filtering, deterministic pair selection, fallback, and codec tests.
- Create `app/src/main/java/com/example/ui/screens/WeekendEchoBoard.kt`: private round state machine and full-screen scene orchestration.
- Create `app/src/main/java/com/example/ui/screens/WeekendEchoVisuals.kt`: split-scene layout, animated pandas, rotating seals, reveal motion, and reduced-motion behavior.
- Create `app/src/test/java/com/example/WeekendEchoRoutingContractTest.kt`: fixed-ID routing and history-plumbing source contract.
- Create `app/src/androidTest/java/com/example/ui/screens/WeekendEchoBoardTest.kt`: click targets and reveal-state behavior.
- Add eight files under `app/src/main/res/drawable-nodpi/`: `weekend_echo_couch.webp`, `weekend_echo_cinema.webp`, `weekend_echo_city.webp`, `weekend_echo_country.webp`, `weekend_echo_roadtrip.webp`, `weekend_echo_train.webp`, `weekend_echo_camping.webp`, and `weekend_echo_hotel.webp`.
- Modify `app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt`: route the fixed weekend pack to `WeekendEchoBoard`.
- Modify `app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt`: accept complete answer history and pass pack ID, question index, and history to the fullscreen board.
- Modify `app/src/main/java/com/example/MainActivity.kt`: pass `uiState.answers` into `QuizRunnerScreen`.

---

### Task 1: Historical Answer Catalog And Selector

**Files:**
- Create: `app/src/main/java/com/example/data/model/WeekendEcho.kt`
- Test: `app/src/test/java/com/example/WeekendEchoSelectorTest.kt`

**Interfaces:**
- Consumes: `AnswerEntity`, `EitherOrAnswerCodec.decode(String): CoupleChoice?`.
- Produces: `WeekendEchoMotif`, `WeekendEchoCandidate`, `WeekendEchoDuel`, `WeekendEchoSelector.select(List<AnswerEntity>, Int): WeekendEchoDuel?`, and `WeekendEchoAnswerCodec`.

- [ ] **Step 1: Write failing catalog and selector tests**

```kotlin
class WeekendEchoSelectorTest {
    @Test fun `catalog contains exactly the eight approved motifs`() {
        assertEquals(8, WeekendEchoMotif.entries.size)
        assertEquals(
            setOf("couch_blanket", "cinema", "city", "country", "roadtrip", "train", "camping", "hotel"),
            WeekendEchoMotif.entries.map { it.key }.toSet()
        )
    }

    @Test fun `selector decodes both people from prior panda choices`() {
        val history = listOf(
            AnswerEntity(
                packId = WeekendEchoSelector.SOURCE_PACK_ID,
                questionIndex = 8,
                answerText = EitherOrAnswerCodec.encode("Kino 🎬", "Couch & Decke 🛋️"),
                timestamp = 100L
            )
        )
        val candidates = WeekendEchoSelector.candidates(history)
        assertEquals(setOf(WeekendEchoMotif.CINEMA, WeekendEchoMotif.COUCH_BLANKET), candidates.map { it.motif }.toSet())
    }

    @Test fun `selector ignores plain text and current pack answers`() {
        val history = listOf(
            AnswerEntity("some_open_pack", 0, "Couch & Decke", 100L),
            AnswerEntity(WeekendEchoSelector.PACK_ID, 0, "Couch & Decke", 200L)
        )
        assertTrue(WeekendEchoSelector.candidates(history).isEmpty())
    }

    @Test fun `selector returns null with fewer than two approved motifs`() {
        val history = listOf(
            AnswerEntity(WeekendEchoSelector.SOURCE_PACK_ID, 8, EitherOrAnswerCodec.encode("Kino 🎬", "Kino 🎬"), 100L)
        )
        assertNull(WeekendEchoSelector.select(history, questionIndex = 3))
    }

    @Test fun `selection is stable for the same question`() {
        val history = approvedHistory()
        assertEquals(
            WeekendEchoSelector.select(history, 3),
            WeekendEchoSelector.select(history.reversed(), 3)
        )
    }

    @Test fun `round codec preserves both private selections`() {
        val encoded = WeekendEchoAnswerCodec.encode("cinema", "couch_blanket")
        assertEquals(WeekendEchoRoundAnswer("cinema", "couch_blanket"), WeekendEchoAnswerCodec.decode(encoded))
    }
}
```

- [ ] **Step 2: Run tests and confirm they fail because the model is absent**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoSelectorTest`

Expected: compilation fails with unresolved `WeekendEcho*` symbols.

- [ ] **Step 3: Implement the pure model and deterministic selector**

```kotlin
enum class WeekendEchoMotif(
    val key: String,
    val displayText: String,
    val acceptedValues: Set<String>,
    val chapterWeights: List<Int>
) {
    COUCH_BLANKET("couch_blanket", "Couch & Decke", setOf("couch & decke"), listOf(1, 2, 1, 10, 8, 4, 9, 6)),
    CINEMA("cinema", "Kino", setOf("kino"), listOf(4, 4, 3, 9, 4, 7, 5, 5)),
    CITY("city", "Stadt", setOf("stadt"), listOf(10, 7, 5, 6, 5, 7, 4, 6)),
    COUNTRY("country", "Land", setOf("land"), listOf(10, 6, 6, 7, 6, 5, 6, 7)),
    ROADTRIP("roadtrip", "Roadtrip", setOf("roadtrip"), listOf(8, 8, 5, 7, 7, 6, 8, 8)),
    TRAIN("train", "Zug", setOf("zug"), listOf(6, 5, 8, 6, 6, 5, 10, 5)),
    CAMPING("camping", "Camping", setOf("camping"), listOf(5, 8, 10, 6, 5, 4, 5, 9)),
    HOTEL("hotel", "5-Sterne-Hotel", setOf("5-sterne-hotel"), listOf(6, 8, 10, 9, 8, 5, 8, 7));

    companion object {
        fun fromStored(raw: String): WeekendEchoMotif? {
            val normalized = raw.lowercase()
                .replace(Regex("[^a-z0-9äöüß&-]+"), " ")
                .trim()
            return entries.firstOrNull { motif -> motif.acceptedValues.any { it == normalized } }
        }
    }
}

enum class WeekendEchoPerson { PERSON_A, PERSON_B, BOTH }

data class WeekendEchoCandidate(
    val motif: WeekendEchoMotif,
    val people: Set<WeekendEchoPerson>,
    val sourceQuestionIndex: Int,
    val timestamp: Long
)

data class WeekendEchoDuel(val left: WeekendEchoCandidate, val right: WeekendEchoCandidate)

data class WeekendEchoRoundAnswer(val firstMotifKey: String, val secondMotifKey: String)

object WeekendEchoSelector {
    const val PACK_ID = "h500_076_wochenendtrip_szenario"
    const val SOURCE_PACK_ID = "entweder_oder_panda"

    fun candidates(history: List<AnswerEntity>): List<WeekendEchoCandidate> = history
        .asSequence()
        .filter { it.packId == SOURCE_PACK_ID }
        .mapNotNull { answer ->
            val pair = EitherOrAnswerCodec.decode(answer.answerText) ?: return@mapNotNull null
            val a = WeekendEchoMotif.fromStored(pair.userChoice)
            val b = WeekendEchoMotif.fromStored(pair.partnerChoice)
            listOfNotNull(
                a?.let { Triple(it, WeekendEchoPerson.PERSON_A, answer) },
                b?.let { Triple(it, WeekendEchoPerson.PERSON_B, answer) }
            )
        }
        .flatten()
        .groupBy { it.first }
        .map { (motif, matches) ->
            val newest = matches.maxBy { it.third.timestamp }
            WeekendEchoCandidate(motif, matches.map { it.second }.toSet(), newest.third.questionIndex, newest.third.timestamp)
        }
        .sortedBy { it.motif.key }

    fun select(history: List<AnswerEntity>, questionIndex: Int): WeekendEchoDuel? {
        val scored = candidates(history).sortedWith(
            compareByDescending<WeekendEchoCandidate> { it.motif.chapterWeights[questionIndex.coerceIn(0, 7)] }
                .thenBy { stableTieBreak(it.motif.key, questionIndex) }
                .thenBy { it.motif.key }
        )
        return if (scored.size >= 2) WeekendEchoDuel(scored[0], scored[1]) else null
    }
}
```

Implement `stableTieBreak` with a deterministic character fold rather than `String.hashCode()` assumptions, and implement `WeekendEchoAnswerCodec` with prefix `weekend-echo-v1:` plus an ASCII unit separator.

- [ ] **Step 4: Run selector tests**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoSelectorTest`

Expected: PASS.

- [ ] **Step 5: Commit selector and spec correction**

```powershell
git add -- app/src/main/java/com/example/data/model/WeekendEcho.kt app/src/test/java/com/example/WeekendEchoSelectorTest.kt docs/superpowers/specs/2026-09-07-wochenendtrip-personal-echo-design.md
git commit -m "feat: select personal weekend echo choices"
```

---

### Task 2: Eight Local Scene Assets

**Files:**
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_couch.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_cinema.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_city.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_country.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_roadtrip.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_train.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_camping.webp`
- Create: `app/src/main/res/drawable-nodpi/weekend_echo_hotel.webp`
- Test: `app/src/test/java/com/example/WeekendEchoAssetContractTest.kt`

**Interfaces:**
- Consumes: `WeekendEchoMotif.key`.
- Produces: one 768 x 1024 portrait WebP per key, with no text, people, pandas, icons, or logos.

- [ ] **Step 1: Write the failing resource contract**

```kotlin
class WeekendEchoAssetContractTest {
    @Test fun `every approved motif has a local drawable mapping`() {
        val source = File("src/main/java/com/example/ui/screens/WeekendEchoVisuals.kt").readText()
        WeekendEchoMotif.entries.forEach { motif ->
            assertTrue(source.contains("WeekendEchoMotif.${motif.name}"))
            assertTrue(source.contains("weekend_echo_${motif.key}"))
        }
    }
}
```

- [ ] **Step 2: Run the resource contract and confirm failure**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoAssetContractTest`

Expected: FAIL because `WeekendEchoVisuals.kt` and drawables do not exist.

- [ ] **Step 3: Generate all eight scene assets independently**

Use the approved split-scene reference and the same art direction for each image: medium-bright violet twilight, realistic cinematic depth, Harmony magenta/cyan/gold accents, space for center crop, no embedded controls or copy. Generate exactly one independent image for each of the eight motifs. Keep characters out of the assets because pandas are rendered as a separate Compose layer.

- [ ] **Step 4: Convert and place assets non-destructively**

Convert each selected PNG to lossless WebP with its exact filename above. Verify every image opens, has portrait dimensions, and stays below 1.5 MB.

- [ ] **Step 5: Add drawable mapping in `WeekendEchoVisuals.kt`**

```kotlin
@DrawableRes
internal fun WeekendEchoMotif.drawableRes(): Int = when (this) {
    WeekendEchoMotif.COUCH_BLANKET -> R.drawable.weekend_echo_couch
    WeekendEchoMotif.CINEMA -> R.drawable.weekend_echo_cinema
    WeekendEchoMotif.CITY -> R.drawable.weekend_echo_city
    WeekendEchoMotif.COUNTRY -> R.drawable.weekend_echo_country
    WeekendEchoMotif.ROADTRIP -> R.drawable.weekend_echo_roadtrip
    WeekendEchoMotif.TRAIN -> R.drawable.weekend_echo_train
    WeekendEchoMotif.CAMPING -> R.drawable.weekend_echo_camping
    WeekendEchoMotif.HOTEL -> R.drawable.weekend_echo_hotel
}
```

- [ ] **Step 6: Run resource test and Android resource processing**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoAssetContractTest processDebugResources`

Expected: PASS.

- [ ] **Step 7: Commit the asset set**

```powershell
git add -- app/src/main/res/drawable-nodpi/weekend_echo_*.webp app/src/main/java/com/example/ui/screens/WeekendEchoVisuals.kt app/src/test/java/com/example/WeekendEchoAssetContractTest.kt
git commit -m "feat: add weekend echo scene artwork"
```

---

### Task 3: Private Round State Machine

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/WeekendEchoRoundPolicy.kt`
- Test: `app/src/test/java/com/example/WeekendEchoRoundPolicyTest.kt`

**Interfaces:**
- Consumes: `WeekendEchoDuel` and seal selections by motif key.
- Produces: `WeekendEchoPhase`, `WeekendEchoRoundState`, and pure transitions `pick(String)`, `handoffReady()`, `reveal()`, and `canContinue`.

- [ ] **Step 1: Write failing privacy transition tests**

```kotlin
class WeekendEchoRoundPolicyTest {
    @Test fun `first choice is hidden before handoff`() {
        val picked = WeekendEchoRoundState.initial().pick("cinema")
        assertEquals(WeekendEchoPhase.HANDOFF, picked.phase)
        assertFalse(picked.exposesAnswers)
    }

    @Test fun `reveal is impossible until both people choose`() {
        val state = WeekendEchoRoundState.initial().pick("cinema")
        assertEquals(state, state.reveal())
    }

    @Test fun `both selections become visible only after explicit reveal`() {
        val ready = WeekendEchoRoundState.initial()
            .pick("cinema")
            .handoffReady()
            .pick("couch_blanket")
        assertFalse(ready.exposesAnswers)
        val revealed = ready.reveal()
        assertTrue(revealed.exposesAnswers)
        assertTrue(revealed.canContinue)
    }
}
```

- [ ] **Step 2: Run the policy test and confirm unresolved symbols**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoRoundPolicyTest`

Expected: compilation failure.

- [ ] **Step 3: Implement immutable phase transitions**

```kotlin
internal enum class WeekendEchoPhase { PERSON_A, HANDOFF, PERSON_B, READY_TO_REVEAL, REVEALED }

internal data class WeekendEchoRoundState(
    val phase: WeekendEchoPhase,
    val firstMotifKey: String? = null,
    val secondMotifKey: String? = null
) {
    val exposesAnswers: Boolean get() = phase == WeekendEchoPhase.REVEALED
    val canContinue: Boolean get() = phase == WeekendEchoPhase.REVEALED

    fun pick(key: String): WeekendEchoRoundState = when (phase) {
        WeekendEchoPhase.PERSON_A -> copy(phase = WeekendEchoPhase.HANDOFF, firstMotifKey = key)
        WeekendEchoPhase.PERSON_B -> copy(phase = WeekendEchoPhase.READY_TO_REVEAL, secondMotifKey = key)
        else -> this
    }

    fun handoffReady() = if (phase == WeekendEchoPhase.HANDOFF) copy(phase = WeekendEchoPhase.PERSON_B) else this
    fun reveal() = if (phase == WeekendEchoPhase.READY_TO_REVEAL) copy(phase = WeekendEchoPhase.REVEALED) else this

    companion object { fun initial() = WeekendEchoRoundState(WeekendEchoPhase.PERSON_A) }
}
```

- [ ] **Step 4: Run policy tests**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoRoundPolicyTest`

Expected: PASS.

- [ ] **Step 5: Commit the state machine**

```powershell
git add -- app/src/main/java/com/example/ui/screens/WeekendEchoRoundPolicy.kt app/src/test/java/com/example/WeekendEchoRoundPolicyTest.kt
git commit -m "feat: add private weekend echo round state"
```

---

### Task 4: Split Scene, Pandas, And Rotating Seal Controls

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/WeekendEchoVisuals.kt`
- Test: `app/src/androidTest/java/com/example/ui/screens/WeekendEchoBoardTest.kt`

**Interfaces:**
- Consumes: two `WeekendEchoCandidate` values, `WeekendEchoRoundState`, `ProfileEntity`, and two callbacks.
- Produces: `WeekendSplitScene`, `WalkingPandaPair`, and `RotatingEchoSeal` composables with stable test tags.

- [ ] **Step 1: Write failing Compose interaction tests**

```kotlin
@Test fun backgroundAndPandasAreNotClickable() {
    setWeekendBoard()
    composeRule.onNodeWithTag("weekend_echo_background").assertHasNoClickAction()
    composeRule.onNodeWithTag("weekend_echo_pandas").assertHasNoClickAction()
}

@Test fun onlyTwoSealsAreChoices() {
    setWeekendBoard()
    composeRule.onNodeWithTag("weekend_echo_left_seal").assertHasClickAction()
    composeRule.onNodeWithTag("weekend_echo_right_seal").assertHasClickAction()
    composeRule.onAllNodes(hasClickAction()).assertCountEquals(2)
}
```

- [ ] **Step 2: Run the instrumentation test and confirm missing board symbols**

Run: `./gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.ui.screens.WeekendEchoBoardTest`

Expected: compile failure before the composables exist.

- [ ] **Step 3: Implement the split image surface and non-clickable panda layer**

Use two clipped `Image` layers with `ContentScale.Crop`, each occupying half the width, and a Canvas Aurora seam at the center. Render two existing Harmony-style vector pandas from behind on the left only; the panda pair receives no `clickable`, `combinedClickable`, or semantics action modifier.

- [ ] **Step 4: Implement the seal as the only choice control**

```kotlin
@Composable
internal fun RotatingEchoSeal(
    side: WeekendEchoSide,
    motif: WeekendEchoMotif,
    selected: Boolean,
    revealed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "weekend_echo_seal")
    val angle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6_000, easing = LinearEasing)),
        label = "weekend_echo_seal_rotation"
    )
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .testTag("weekend_echo_${side.tag}_seal"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize().graphicsLayer { rotationZ = angle }) { drawSealRing(...) }
        Icon(motif.icon, contentDescription = motif.displayText)
    }
}
```

Define `WeekendEchoSide.LEFT("left")` and `WeekendEchoSide.RIGHT("right")` and pass it explicitly to each seal. Read `Settings.Global.ANIMATOR_DURATION_SCALE` through a small injectable policy; when animation is disabled, use a static ring and color transition.

- [ ] **Step 5: Run instrumentation tests on the connected target when available**

Run: `./gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.ui.screens.WeekendEchoBoardTest`

Expected: PASS when a device/emulator is present. If none is connected, run `assembleDebugAndroidTest` and record the device test as pending rather than claiming it passed.

- [ ] **Step 6: Commit visual primitives**

```powershell
git add -- app/src/main/java/com/example/ui/screens/WeekendEchoVisuals.kt app/src/androidTest/java/com/example/ui/screens/WeekendEchoBoardTest.kt
git commit -m "feat: add interactive weekend echo scene"
```

---

### Task 5: Dedicated Board And Private Reveal

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/WeekendEchoBoard.kt`
- Modify: `app/src/androidTest/java/com/example/ui/screens/WeekendEchoBoardTest.kt`
- Test: `app/src/test/java/com/example/WeekendEchoSourceContractTest.kt`

**Interfaces:**
- Consumes: `question`, `questionIndex`, `historicalAnswers`, `selectedAnswer`, `profile`, and `onPick`.
- Produces: `WeekendEchoBoard(...)` that falls back to `ScenarioBoard` when no duel exists.

- [ ] **Step 1: Add failing privacy and copy contracts**

```kotlin
@Test fun `board source omits removed explanatory copy`() {
    val source = File("src/main/java/com/example/ui/screens/WeekendEchoBoard.kt").readText()
    assertFalse(source.contains("Damals gewählt"))
    assertFalse(source.contains("Darum führt eure Reise"))
    assertFalse(source.contains("Weiter zum nächsten Kapitel"))
}
```

Add Compose tests that pick Person A, assert no answer text, complete handoff, pick Person B, assert no answer text, explicitly reveal, then assert the chosen motif label appears and the opened seals remain the only continuation actions.

- [ ] **Step 2: Run tests and confirm failure**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoSourceContractTest assembleDebugAndroidTest`

Expected: failure because the board is absent.

- [ ] **Step 3: Implement the board with saveable encoded state**

```kotlin
@Composable
internal fun WeekendEchoBoard(
    question: String,
    options: List<String>,
    questionIndex: Int,
    historicalAnswers: List<AnswerEntity>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val duel = remember(historicalAnswers, questionIndex) {
        WeekendEchoSelector.select(historicalAnswers, questionIndex)
    }
    if (duel == null) {
        ScenarioBoard(question, options, selectedAnswer, profile, onPick, modifier)
        return
    }
    // rememberSaveable fields: phase name, first key, second key.
    // Person A and Person B see the same two seals; selection moves only the internal phase.
    // READY_TO_REVEAL shows no answer and an explicit seal-opening action.
    // REVEALED shows only the concise selected motif label; tapping either opened seal calls onPick once.
}
```

Pass the original question options into the board and use them in the fallback.

- [ ] **Step 4: Guard continuation against duplicate taps**

Use a saveable `submitted` Boolean. On the first opened-seal tap, encode both selections with `WeekendEchoAnswerCodec` and call `onPick`; ignore later taps while composition is leaving the screen.

- [ ] **Step 5: Run unit and instrumentation compile tests**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoSourceContractTest --tests com.example.WeekendEchoRoundPolicyTest assembleDebugAndroidTest`

Expected: PASS.

- [ ] **Step 6: Commit the complete board**

```powershell
git add -- app/src/main/java/com/example/ui/screens/WeekendEchoBoard.kt app/src/androidTest/java/com/example/ui/screens/WeekendEchoBoardTest.kt app/src/test/java/com/example/WeekendEchoSourceContractTest.kt
git commit -m "feat: add private personal echo reveal"
```

---

### Task 6: Fixed-ID Routing And History Plumbing

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt`
- Modify: `app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Create: `app/src/test/java/com/example/WeekendEchoRoutingContractTest.kt`

**Interfaces:**
- Consumes: `uiState.answers` from `MainActivity`.
- Produces: runner parameters `answerHistory: List<AnswerEntity>`, `packId: String`, and `questionIndex: Int` at the fullscreen mechanic boundary.

- [ ] **Step 1: Write the failing routing contract**

```kotlin
class WeekendEchoRoutingContractTest {
    @Test fun `routing uses fixed pack id and passes history`() {
        val boardSource = File("src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt").readText()
        val runnerSource = File("src/main/java/com/example/ui/screens/QuizRunnerScreen.kt").readText()
        val activitySource = File("src/main/java/com/example/MainActivity.kt").readText()
        assertTrue(boardSource.contains("packId == WeekendEchoSelector.PACK_ID"))
        assertTrue(boardSource.contains("WeekendEchoBoard("))
        assertTrue(runnerSource.contains("answerHistory: List<AnswerEntity>"))
        assertTrue(activitySource.contains("answerHistory = uiState.answers"))
        assertFalse(boardSource.contains("question.contains("))
    }
}
```

- [ ] **Step 2: Run the routing test and confirm failure**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoRoutingContractTest`

Expected: FAIL until signatures and routing are updated.

- [ ] **Step 3: Pass answer history from activity to runner**

Add `answerHistory: List<AnswerEntity>` to `QuizRunnerScreen`, then pass `answerHistory = uiState.answers` from the only production call in `MainActivity` and appropriate empty/sample lists from tests.

- [ ] **Step 4: Extend the fullscreen mechanic boundary**

Add `packId`, `questionIndex`, and `historicalAnswers` to `FullscreenQuestionMechanicBoard`. For `FullscreenGameMechanicKind.SCENARIO`, use:

```kotlin
if (packId == WeekendEchoSelector.PACK_ID) {
    WeekendEchoBoard(
        question = question,
        options = options,
        questionIndex = questionIndex,
        historicalAnswers = historicalAnswers,
        selectedAnswer = selectedAnswer,
        profile = profile,
        onPick = onPick,
        modifier = modifier
    )
} else {
    ScenarioBoard(question, options, selectedAnswer, profile, onPick, modifier)
}
```

- [ ] **Step 5: Run routing and existing scenario regression tests**

Run: `./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoRoutingContractTest --tests com.example.ScenarioPlayLayoutPolicyTest --tests com.example.ScenarioShortLandscapeSourceContractTest`

Expected: PASS.

- [ ] **Step 6: Commit routing separately**

```powershell
git add -- app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt app/src/main/java/com/example/MainActivity.kt app/src/test/java/com/example/WeekendEchoRoutingContractTest.kt
git commit -m "feat: route weekend pack to personal echo board"
```

---

### Task 7: Full Verification And Visual Check

**Files:**
- Modify only files required by failures caused by this feature.

**Interfaces:**
- Consumes: all previous tasks.
- Produces: verified debug APK and evidence that protected flows were not changed.

- [ ] **Step 1: Run focused unit tests**

Run:

```powershell
./gradlew.bat testDebugUnitTest --tests com.example.WeekendEchoSelectorTest --tests com.example.WeekendEchoAssetContractTest --tests com.example.WeekendEchoRoundPolicyTest --tests com.example.WeekendEchoSourceContractTest --tests com.example.WeekendEchoRoutingContractTest
```

Expected: PASS.

- [ ] **Step 2: Run protected regression contracts**

Run:

```powershell
./gradlew.bat testDebugUnitTest --tests com.example.GoogleAuthRegressionContractTest --tests com.example.HappyCoupleVisualContractTest --tests com.example.MemoryMatchFlowContractTest
```

Expected: PASS.

- [ ] **Step 3: Build app and Android tests**

Run: `./gradlew.bat assembleDebug assembleDebugAndroidTest`

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Run device UI tests if a target is connected**

Run: `./gradlew.bat connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.ui.screens.WeekendEchoBoardTest`

Expected: PASS on a connected device or emulator. If no device exists, report this exact limitation.

- [ ] **Step 5: Visually inspect the implemented states**

Capture Person A choice, handoff, Person B choice, ready-to-reveal, and revealed states at a 390 x 844-equivalent viewport. Verify the left-only rear-facing pandas, empty right scene, medium brightness, two rotating seals, absence of removed copy, and no click affordance outside seals.

- [ ] **Step 6: Check repository hygiene**

Run:

```powershell
git diff --check
git status --short
git log --oneline -8
```

Expected: no whitespace errors; `gradle.properties` and `.idea/` remain uncommitted and unchanged by this implementation.

- [ ] **Step 7: Commit only any verified final corrections**

Stage every correction by its exact feature-file path as identified in Step 5, never with `git add .` or another broad add, then commit with `git commit -m "fix: polish weekend echo experience"`. Skip this commit when verification required no corrections.
