# Harmony Developer Review Phase 2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the existing Harmony Developer Review MVP into a complete in-app QA workflow with Inbox, private screenshots, voice notes + transcription, exact UI-element marking, contextual reopen, and GitHub fix metadata.

**Architecture:** Keep Supabase as the durable developer inbox and GitHub as the implementation layer. Extend the already-merged `developer_feedback` model and admin-only `developer-feedback` private Storage bucket; keep screenshot, audio, Inbox, and target-selection responsibilities in separate Android components instead of growing `LiveChangeOverlay.kt` or `DeveloperReviewQuickNote.kt` into monoliths.

**Tech Stack:** Kotlin, Jetpack Compose, Android `Bitmap`/`Canvas`, `MediaRecorder`, existing OkHttp/Supabase auth stack, Supabase Postgres/Storage/Edge Functions, JUnit/contract tests, GitHub PR workflow.

**Spec:** `docs/superpowers/specs/2026-09-06-harmony-developer-review-phase2-design.md`

## Global Constraints

- Developer Review remains admin-only via the existing `is_ai_admin()` mechanism.
- Normal users and demo sessions must never see review controls or Inbox.
- Reuse `public.developer_feedback`; do not create a second feedback table.
- Reuse private bucket `developer-feedback`; never create public screenshot/audio URLs.
- Never embed service-role, transcription-provider, or GitHub secrets in Android.
- Existing text-only `🛠 Notiz` submission must keep working after every slice.
- `AUTO_SAFE` authorizes the assistant workflow only; the Android app and Edge Functions never create/merge GitHub branches themselves.
- Element selection uses stable ids/test tags, never screen coordinates.
- Do not modify Baby/Kid Generator scope.
- Before every implementation PR: fetch latest `main`, check overlapping PRs, keep the diff narrow, run available tests/build, inspect the PR patch, and only then merge.

---

### Task 1: Developer Inbox foundation

**Files:**
- Create: `app/src/main/java/com/example/ui/developer/DeveloperReviewInboxPolicy.kt`
- Create: `app/src/main/java/com/example/ui/screens/DeveloperReviewInboxScreen.kt`
- Modify: `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/DevStudioScreen.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewInboxPolicyTest.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewInboxWiringContractTest.kt`

**Interfaces:**
- Consumes: existing `DeveloperFeedbackItem`, `DeveloperFeedbackStatus`, `DeveloperFeedbackPriority`, `DeveloperFeedbackType`, `DeveloperReviewViewModel.refreshInbox()`, `DeveloperReviewViewModel.updateStatus(id, status)`.
- Produces: `DeveloperReviewInboxFilter`, `DeveloperReviewInboxGroup`, `DeveloperReviewInboxPolicy.apply(items, filter)`, `DeveloperReviewInboxScreen()`.

- [ ] **Step 1: Write the failing pure policy test**

```kotlin
@Test
fun `blockers stay visible and items group by game or screen`() {
    val items = listOf(
        feedback(id = "1", gameId = "christmas", priority = BLOCKER, status = NEW),
        feedback(id = "2", screen = "GamesScreen", priority = MEDIUM, status = FIXED),
    )
    val groups = DeveloperReviewInboxPolicy.apply(
        items,
        DeveloperReviewInboxFilter(statuses = setOf(NEW), query = "")
    )
    assertEquals(listOf("christmas"), groups.map { it.key })
    assertEquals("1", groups.single().items.single().id)
}
```

- [ ] **Step 2: Run the focused test and verify RED**

Run: `./gradlew :app:testDebugUnitTest --tests com.example.developer.DeveloperReviewInboxPolicyTest`

Expected: FAIL because `DeveloperReviewInboxPolicy` and filter/group models do not exist yet.

- [ ] **Step 3: Implement the pure Inbox policy**

```kotlin
data class DeveloperReviewInboxFilter(
    val statuses: Set<DeveloperFeedbackStatus> = setOf(DeveloperFeedbackStatus.NEW),
    val priorities: Set<DeveloperFeedbackPriority> = emptySet(),
    val types: Set<DeveloperFeedbackType> = emptySet(),
    val query: String = "",
)

data class DeveloperReviewInboxGroup(
    val key: String,
    val items: List<DeveloperFeedbackItem>,
)

object DeveloperReviewInboxPolicy {
    fun apply(
        items: List<DeveloperFeedbackItem>,
        filter: DeveloperReviewInboxFilter,
    ): List<DeveloperReviewInboxGroup> {
        val needle = filter.query.trim().lowercase()
        return items.asSequence()
            .filter { filter.statuses.isEmpty() || it.status in filter.statuses }
            .filter { filter.priorities.isEmpty() || it.priority in filter.priorities }
            .filter { filter.types.isEmpty() || it.type in filter.types }
            .filter {
                needle.isEmpty() || listOfNotNull(
                    it.note,
                    it.transcript,
                    it.context.gameId,
                    it.context.screen,
                    it.context.questionText,
                    it.context.elementId,
                ).any { value -> value.lowercase().contains(needle) }
            }
            .groupBy { it.context.gameId ?: it.context.screen ?: "Harmony" }
            .map { (key, values) -> DeveloperReviewInboxGroup(key, values) }
            .sortedByDescending { group -> group.items.maxOfOrNull { it.createdAt }.orEmpty() }
            .toList()
    }
}
```

- [ ] **Step 4: Extend ViewModel with refresh state without changing submit behavior**

Add `isRefreshingInbox: Boolean` instead of reusing global `isBusy`, so opening the Inbox does not disable unrelated Quick Note interactions.

```kotlin
fun refreshInbox() {
    if (_state.value.isRefreshingInbox || !_state.value.isAdmin) return
    viewModelScope.launch {
        _state.value = _state.value.copy(isRefreshingInbox = true, error = null)
        runCatching { repository.loadFeedback() }
            .onSuccess { items -> _state.value = _state.value.copy(isRefreshingInbox = false, feedbackItems = items) }
            .onFailure { error -> _state.value = _state.value.copy(isRefreshingInbox = false, error = error.message ?: "Inbox konnte nicht geladen werden") }
    }
}
```

- [ ] **Step 5: Add Developer Inbox screen and Dev Studio tab**

Add a new tab title `🛠 Review` to `DevStudioScreen.kt`. Render `DeveloperReviewInboxScreen()` in a new `when (selectedTab)` branch. The screen must call `checkAccess()` then `refreshInbox()`, show NEW count, filter chips, grouped cards, note/transcript preview, context, and status action buttons.

Status buttons in Slice 1 are limited to:

```kotlin
when (item.status) {
    DeveloperFeedbackStatus.NEW -> NEW to REVIEWED
    DeveloperFeedbackStatus.REVIEWED -> REVIEWED to IN_PROGRESS
    DeveloperFeedbackStatus.FIXED -> FIXED to VERIFIED
    else -> null
}
```

- [ ] **Step 6: Write wiring contract test**

Assert that `DevStudioScreen.kt` contains `🛠 Review` and `DeveloperReviewInboxScreen`, and that the Inbox source exposes NEW count + status transition wiring.

- [ ] **Step 7: Run focused tests + Android build**

Run:

```bash
./gradlew :app:testDebugUnitTest --tests 'com.example.developer.DeveloperReviewInbox*'
./gradlew :app:assembleDebug
```

Expected: PASS. If GitHub Actions is used, verify that runner steps actually executed before claiming success.

- [ ] **Step 8: Commit and PR**

```bash
git add app/src/main/java/com/example/ui/developer/DeveloperReviewInboxPolicy.kt \
        app/src/main/java/com/example/ui/screens/DeveloperReviewInboxScreen.kt \
        app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt \
        app/src/main/java/com/example/ui/screens/DevStudioScreen.kt \
        app/src/test/java/com/example/developer/DeveloperReviewInboxPolicyTest.kt \
        app/src/test/java/com/example/developer/DeveloperReviewInboxWiringContractTest.kt
git commit -m "feat: add Harmony Developer Review inbox"
```

---

### Task 2: Private screenshot attachments

**Files:**
- Create: `app/src/main/java/com/example/ui/developer/DeveloperReviewCapture.kt`
- Create: `app/src/main/java/com/example/data/developer/DeveloperFeedbackAttachmentStore.kt`
- Modify: `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt`
- Modify: `app/src/main/java/com/example/ui/screens/LiveChangeOverlay.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewScreenshotContractTest.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperFeedbackAttachmentStoreTest.kt`

**Interfaces:**
- Produces: `DeveloperReviewCapture.captureActivityContent(activity): DeveloperAttachmentPayload`, `DeveloperFeedbackAttachmentStore.uploadScreenshot(clientFeedbackId, payload): String`, `DeveloperFeedbackAttachmentStore.createSignedUrl(path): String`.
- Preserves: `DeveloperFeedbackDraft.screenshotPath` and the existing `submitFeedback(...)` request shape.

- [ ] **Step 1: Write failing attachment path test**

```kotlin
@Test
fun `screenshot path is scoped to current user and feedback id`() {
    val path = DeveloperFeedbackAttachmentStore.objectPath(
        userId = "user-123",
        feedbackId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        fileName = "screen.webp",
    )
    assertEquals("user/user-123/00000000-0000-0000-0000-000000000001/screen.webp", path)
}
```

- [ ] **Step 2: Implement attachment store around existing authenticated OkHttp conventions**

Use the existing Supabase publishable key + current user access token. Upload to:

`POST ${SUPABASE_URL}/storage/v1/object/developer-feedback/{encodedPath}`

Headers: `Authorization: Bearer <session token>`, `apikey`, `Content-Type`, `x-upsert: true`.

Signed preview URL:

`POST ${SUPABASE_URL}/storage/v1/object/sign/developer-feedback/{encodedPath}` with `{"expiresIn":600}`.

Never persist signed URLs in `developer_feedback`; persist only the object path.

- [ ] **Step 3: Implement runtime capture abstraction**

`DeveloperReviewCapture.captureActivityContent(activity)` must capture `activity.findViewById<View>(android.R.id.content)` so the result is app content rather than the full system screenshot. Draw into a `Bitmap`, compress as WebP on supported API levels and JPEG fallback otherwise, and return bytes/content type/extension.

```kotlin
data class DeveloperAttachmentPayload(
    val bytes: ByteArray,
    val contentType: String,
    val extension: String,
)
```

- [ ] **Step 4: Hide developer chrome during capture**

Add a small shared `DeveloperReviewCaptureVisibility` state holder. `LiveChangeHud` reads `hideDeveloperChrome`; screenshot action sets it true, waits one Compose frame, captures, then restores it in `finally`.

- [ ] **Step 5: Keep one stable client feedback UUID across draft attachments and submit**

Add draft-session state in `DeveloperReviewViewModel`:

```kotlin
data class DeveloperReviewDraftSession(
    val clientFeedbackId: UUID = UUID.randomUUID(),
    val screenshotPath: String? = null,
    val audioPath: String? = null,
)
```

Pass the same `clientFeedbackId` to `repository.submitFeedback(...)`, so attachment retries and row upsert remain idempotent.

- [ ] **Step 6: Add Screenshot button + preview to Quick Note**

The button captures and uploads without closing the draft. On success set `screenshotPath`; on failure keep note/type/priority unchanged and show retry text.

- [ ] **Step 7: Add signed screenshot thumbnail to Inbox detail/card**

Generate signed URL only when a screenshot card/detail is displayed. Expired URL is regenerated on refresh.

- [ ] **Step 8: Run tests/build and commit**

Run focused screenshot/attachment tests and `:app:assembleDebug`, then commit with:

`feat: attach private screenshots to developer feedback`

---

### Task 3: Voice note recording + private upload

**Files:**
- Create: `app/src/main/java/com/example/ui/developer/DeveloperVoiceNoteRecorder.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt`
- Modify: `app/src/main/java/com/example/data/developer/DeveloperFeedbackAttachmentStore.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperVoiceNoteContractTest.kt`

**Interfaces:**
- Produces: `DeveloperVoiceNoteRecorder.start()`, `DeveloperVoiceNoteRecorder.stop(): File`, `DeveloperVoiceNoteRecorder.cancel()`, `uploadVoiceNote(clientFeedbackId, file): String`.
- Uses the same draft-session `clientFeedbackId` from Task 2.

- [ ] **Step 1: Write failing recording contract test**

Verify manifest contains `android.permission.RECORD_AUDIO`; Quick Note uses `ActivityResultContracts.RequestPermission`; recorder finalizes to `.m4a`; draft stores `audioPath` without requiring transcript.

- [ ] **Step 2: Add microphone permission only**

Add:

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

Do not add broad storage permissions; recordings stay in app cache until private upload completes.

- [ ] **Step 3: Implement `DeveloperVoiceNoteRecorder` with `MediaRecorder`**

Configure MPEG-4 container + AAC audio, one cache file per recording, and explicit states `Idle`, `Recording`, `Finalizing`. `stop()` must release recorder in `finally`; `cancel()` deletes partial file.

- [ ] **Step 4: Add bounded Quick Note recording UI**

Mic button behavior:
- first tap: request permission if needed; otherwise start
- active state: visible `● Aufnahme läuft`
- second tap: stop + upload
- cap recording at 60 seconds and auto-stop
- cancel action deletes local partial audio

- [ ] **Step 5: Upload audio privately**

Use object name `note.m4a` under the same `user/<uid>/<feedback-id>/` path. Set `DeveloperReviewDraftSession.audioPath` after successful upload.

- [ ] **Step 6: Keep text note save independent**

`DeveloperFeedbackDraft(note=..., audioPath=session.audioPath)` must be valid whether transcription exists or not.

- [ ] **Step 7: Run focused tests/build and commit**

Commit: `feat: add private developer voice notes`

---

### Task 4: Server-side voice transcription

**Files:**
- Add Supabase Edge Function: `harmony-developer-feedback-transcribe/index.ts`
- Modify: `app/src/main/java/com/example/data/developer/DeveloperFeedbackRepository.kt`
- Modify: `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt`
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewInboxScreen.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewTranscriptionContractTest.kt`

**Interfaces:**
- Produces Android method `requestTranscription(feedbackId: String): Unit`.
- Edge Function accepts `{ "feedback_id": "<uuid>" }` and returns `{ "ok": true, "transcript": "..." }`.

- [ ] **Step 1: Write failing endpoint/auth contract test**

Assert Android calls `/functions/v1/harmony-developer-feedback-transcribe`, sends only feedback id, and never contains provider API keys.

- [ ] **Step 2: Deploy authenticated Edge Function skeleton with `verify_jwt=true`**

Function algorithm:
1. require POST
2. build caller-scoped Supabase client from request Authorization header
3. call `is_ai_admin()` and reject non-admin with 403
4. load feedback row by id and require `audio_path`
5. download object from private `developer-feedback` bucket
6. transcribe with server-side configured provider
7. update `developer_feedback.transcript`
8. return normalized transcript

- [ ] **Step 3: Use server-side Groq Whisper when `GROQ_API_KEY` is configured**

Call `https://api.groq.com/openai/v1/audio/transcriptions` with multipart form fields:
- file = downloaded M4A
- model = `whisper-large-v3`
- language = `de`
- temperature = `0`

If `GROQ_API_KEY` is missing, return `503 {"error":"transcription_unconfigured"}` rather than exposing any secret or failing silently. Secret configuration is a deployment prerequisite and stays outside Android/GitHub source.

- [ ] **Step 4: Add repository/ViewModel request and refresh**

After voice upload + feedback row save, call transcription; on success update the matching `feedbackItems` item transcript. On failure preserve audio and show `Transkription erneut versuchen`.

- [ ] **Step 5: Show transcript in Quick Note/Inbox**

Audio remains useful if transcription fails. Inbox shows separate audio indicator and transcript preview.

- [ ] **Step 6: Verify Supabase security and function behavior**

Run read-only checks for function `verify_jwt=true`, bucket public=false, and current storage RLS. Test authenticated admin request when an admin JWT is available; verify non-admin rejection separately.

- [ ] **Step 7: Run Android tests/build and commit**

Commit: `feat: transcribe developer voice notes`

---

### Task 5: Exact element marking framework

**Files:**
- Create: `app/src/main/java/com/example/ui/developer/DeveloperReviewTargetRegistry.kt`
- Create: `app/src/main/java/com/example/ui/developer/DeveloperReviewTargetModifier.kt`
- Modify: `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewTargetRegistryTest.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewTargetContractTest.kt`

**Interfaces:**
- Produces:

```kotlin
data class DeveloperReviewTarget(
    val id: String,
    val label: String,
    val component: String? = null,
    val metadata: Map<String, String> = emptyMap(),
)

class DeveloperReviewTargetRegistry {
    val markMode: StateFlow<Boolean>
    val selected: StateFlow<DeveloperReviewTarget?>
    fun beginMarking()
    fun select(target: DeveloperReviewTarget): Boolean
    fun cancelMarking()
}

fun Modifier.developerReviewTarget(
    target: DeveloperReviewTarget,
    registry: DeveloperReviewTargetRegistry,
): Modifier
```

- [ ] **Step 1: Write RED registry behavior test**

```kotlin
@Test
fun `first marked target wins and mark mode ends`() {
    val registry = DeveloperReviewTargetRegistry()
    registry.beginMarking()
    assertTrue(registry.select(DeveloperReviewTarget("quiz_answer_3", "Antwort 4")))
    assertFalse(registry.markMode.value)
    assertEquals("quiz_answer_3", registry.selected.value?.id)
}
```

- [ ] **Step 2: Implement pure registry**

Selection is accepted only while mark mode is active. `select()` returns true only when it consumed the mark action.

- [ ] **Step 3: Implement modifier without coordinate hit-testing**

In mark mode the modifier draws a subtle developer outline and installs a developer-only click interceptor. When tapped, it calls `registry.select(target)` and consumes that mark tap; normal game click is not invoked for that one selection. Outside mark mode the modifier becomes behaviorally inert.

- [ ] **Step 4: Add `Element markieren` flow to Quick Note**

The review sheet closes temporarily, mark mode starts, user taps one registered target, then the sheet reopens showing `Element: <label> (<id>)`. `DeveloperReviewContext.elementId` is populated on save.

- [ ] **Step 5: Register a minimal first target set**

Do not sweep the whole app in this PR. Register only shared/current runner anchors with stable ids such as:
- `runner_back_button`
- `quiz_answer_0..N`
- `quiz_question_text`
- `christmas_option_0..N`

- [ ] **Step 6: Run tests/build and commit**

Commit: `feat: add exact developer element marking`

---

### Task 6: Context coverage across common Harmony flows

**Files:**
- Modify only the concrete shared runner/special-flow files identified from latest `main` during this task.
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewQuickNote.kt` only if context builder extraction is needed.
- Create if needed: `app/src/main/java/com/example/ui/developer/DeveloperReviewContextResolver.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewContextCoverageContractTest.kt`

**Interfaces:**
- Produces deterministic context for normal quiz, results, main game list, Christmas, Proposal, Introspection, Panda and other currently active special flows that expose stable round/question state.

- [ ] **Step 1: Inventory current active flows from latest `main`**

Search for runner dispatch and special flow state. Explicitly skip Baby/Kid Generator.

- [ ] **Step 2: Write a failing context coverage contract**

Require normal quiz to include pack/round/question, results to include pack id, main games to include route, and each selected special flow to expose a stable screen/game/round identifier where its state supports it.

- [ ] **Step 3: Extract a context resolver if Quick Note would otherwise gain flow-specific branches**

Preferred API:

```kotlin
object DeveloperReviewContextResolver {
    fun forQuiz(packId: String, index: Int, questionText: String?): DeveloperReviewContext
    fun forResults(packId: String): DeveloperReviewContext
    fun forRoute(screen: String, route: String): DeveloperReviewContext
}
```

Special screens may pass explicit context rather than teaching the resolver every game type.

- [ ] **Step 4: Add stable element targets to common shared components**

Use existing test tags where stable; add new deterministic ids only when necessary. Never derive ids from screen coordinates or translated visible strings.

- [ ] **Step 5: Run targeted contracts/build and commit**

Commit: `feat: expand developer review context coverage`

---

### Task 7: Deep reopen + GitHub fix metadata polish

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/DeveloperReviewInboxScreen.kt`
- Modify: `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- Modify: relevant root navigation/runner entry file from latest `main`
- Modify: `app/src/main/java/com/example/data/developer/DeveloperFeedbackRepository.kt` only if a dedicated metadata update method is needed
- Test: `app/src/test/java/com/example/developer/DeveloperReviewReopenPolicyTest.kt`
- Test: `app/src/test/java/com/example/developer/DeveloperReviewGithubMetadataContractTest.kt`

**Interfaces:**
- Produces `DeveloperReviewReopenTarget` and `DeveloperReviewReopenPolicy.resolve(item)`.
- Displays existing `githubPr`, `githubBranch`, `fixedCommit` fields and status `FIXED/VERIFIED` in Inbox.

- [ ] **Step 1: Write pure reopen policy test**

```kotlin
@Test
fun `normal quiz feedback can reopen exact question but unsupported flow cannot`() {
    assertEquals(
        DeveloperReviewReopenTarget.Quiz("christmas", 3),
        DeveloperReviewReopenPolicy.resolve(feedback(gameId = "christmas", round = 4, route = "games/runner"))
    )
    assertNull(DeveloperReviewReopenPolicy.resolve(feedback(route = "unsupported/special")))
}
```

- [ ] **Step 2: Implement reopen policy only for deterministic supported flows**

First supported contract: normal quiz pack + 1-based stored round. Convert to zero-based runner index safely. Do not fake navigation for unsupported special flows.

- [ ] **Step 3: Add `Zur Stelle springen` to Inbox when target resolves**

Click starts the pack at the stored index using existing runner semantics. If pack/question no longer exists, show `Diese Stelle existiert im aktuellen Build nicht mehr` and retain the feedback item.

- [ ] **Step 4: Render GitHub result metadata**

If present, show:
- `PR #<githubPr>`
- branch
- shortened fixed commit
- FIXED/VERIFIED state

Do not put GitHub credentials or write actions in the Android app.

- [ ] **Step 5: Verify assistant write-back contract directly against Supabase**

The assistant workflow updates matching rows after a merged fix:

```sql
update public.developer_feedback
set status = 'FIXED',
    github_pr = :pr,
    github_branch = :branch,
    fixed_commit = :commit
where id = :feedback_id;
```

The in-app developer may then transition FIXED -> VERIFIED after retest.

- [ ] **Step 6: Full regression/build verification**

Run all developer review tests and `:app:assembleDebug`. Verify text-only note, screenshot note, audio note, element-marked note, Inbox filters/status, and normal quiz reopen manually on a developer-enabled build.

- [ ] **Step 7: Commit**

Commit: `feat: complete Harmony Developer Review workflow`

---

## Delivery / PR order

1. `feat/developer-review-inbox`
2. `feat/developer-review-screenshots`
3. `feat/developer-review-voice-note`
4. `feat/developer-review-transcription`
5. `feat/developer-review-element-marking`
6. `feat/developer-review-context-coverage`
7. `feat/developer-review-reopen-metadata`

Each PR must start from the latest `main` after the previous slice is merged. Do not stack all seven branches from one old base.

## Plan self-review

- Spec coverage: Inbox, screenshot, audio, transcription, exact target ids, context, private Storage, status flow, assistant/GitHub metadata, deterministic reopen, admin-only security and failure behavior are all mapped to Tasks 1–7.
- Placeholder scan: no TBD/TODO or unspecified implementation steps remain. Provider setup has explicit `transcription_unconfigured` behavior if server secret is absent.
- Type consistency: Tasks 2–4 share `DeveloperReviewDraftSession.clientFeedbackId`; screenshot/audio paths continue to use the existing `DeveloperFeedbackDraft` fields; Tasks 5–6 write the existing `DeveloperReviewContext.elementId`; Task 7 reads the existing GitHub metadata fields.
