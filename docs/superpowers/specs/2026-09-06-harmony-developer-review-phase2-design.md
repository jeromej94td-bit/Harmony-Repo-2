# Harmony Developer Review Phase 2 – Design

**Status:** proposed design for review

## Goal

Harmony should become the primary place where the app owner tests, records, locates, and verifies development feedback. The developer should be able to use Harmony normally, open the existing developer review control at any point, attach precise context, and submit feedback without leaving the app or manually explaining where the issue occurred.

The durable workflow is:

`Harmony app -> Supabase developer_feedback -> assistant review -> GitHub branch/PR -> status back to Supabase -> in-app verification`

GitHub remains the implementation layer. Supabase remains the review inbox. The app must not create arbitrary GitHub branches or merge code by itself.

## Existing foundation

Phase 1 already provides:

- admin-gated Developer Review access
- `developer_feedback` table with status/type/priority/execution mode/context fields
- authenticated `harmony-developer-feedback` Edge Function
- quick text note from the Live Change HUD
- current quiz pack/round/question context
- repository methods to submit, load, and update feedback
- workflow states `NEW -> REVIEWED -> IN_PROGRESS -> FIXED -> VERIFIED`

Phase 2 extends this foundation rather than replacing it.

## Product behavior

### Developer Review entry point

The current `🛠 Notiz` control remains the entry point in developer mode. It opens a compact Review sheet with four primary actions:

1. **Textnotiz** – existing behavior, extended with attachments and element context.
2. **Screenshot** – capture the current app content and attach it to the feedback draft.
3. **Sprachnotiz** – record a short voice note, upload it, transcribe it, and attach transcript + audio.
4. **Element markieren** – temporarily enter a selection mode and tap a registered UI element to attach its stable `element_id`.

The sheet also exposes type, priority, execution mode, current context, and attachment previews.

### Screenshot flow

- Screenshot capture is explicit; no background screenshots.
- The app captures the current Harmony content without system bars where practical.
- Developer-only overlays should be hidden during capture so the screenshot shows the app state being reviewed rather than the review UI itself.
- The image is compressed to a practical JPEG/WebP size before upload.
- The object is stored in the existing private `developer-feedback` Storage bucket under a path tied to the feedback id, for example `user/<uid>/<feedback-id>/screen.webp`.
- `developer_feedback.screenshot_path` stores only the private object path, not a public URL.
- The Inbox requests a short-lived signed URL only when the developer opens the item.
- If upload fails, the text note remains intact and the user can retry the attachment without losing the draft.

### Voice note + transcription flow

- Recording starts only after the developer explicitly taps/holds the microphone control and grants microphone permission.
- Use a short bounded recording intended for QA notes, not continuous recording.
- Audio is stored privately under `user/<uid>/<feedback-id>/note.m4a`.
- The feedback row can exist before transcription completes.
- A dedicated authenticated Edge Function, `harmony-developer-feedback-transcribe`, accepts the feedback id, verifies the caller is an admin and owns/has access to the row, reads the private audio object, runs transcription through the configured server-side transcription provider, and writes `transcript` back to the row.
- The Android client polls or refreshes that single feedback item after transcription request; it does not hold provider secrets.
- If transcription fails, the audio remains available and the note can still be saved/edited manually.
- The transcript is advisory developer input and is never automatically treated as executable code.

### Exact element marking

Arbitrary Compose hit-testing is intentionally avoided because it is fragile and difficult to map reliably back to source code.

Instead, Harmony gets an explicit review-target mechanism:

- reusable `Modifier.developerReviewTarget(id, label, metadata)`
- backed by stable ids that match or extend existing `testTag` values
- target registry active only in developer mark mode
- eligible controls visually receive a subtle outline on mark mode entry
- the next target tap selects the element without executing its normal action
- after selection, normal interaction resumes and `element_id` is attached to the draft

Examples:

- `christmas_option_2`
- `quiz_answer_3`
- `runner_back_button`
- `proposal_ring_card_1`

Target metadata may include screen/component name and a short developer label. Element ids must be stable across app launches and must never depend on screen coordinates.

### Developer Inbox

Add a proper `Developer Review` screen inside the developer-only area instead of relying only on the quick-note dialog.

The Inbox provides:

- total NEW count
- filters: status, priority, type, game/screen
- grouping by game/screen
- compact card with note/transcript preview, game, round, question, element id, app version and created time
- screenshot thumbnail when present
- audio/transcript indicator when present
- status transitions
- links/metadata for GitHub PR, branch and fixed commit once available

Primary transitions available in-app:

- `NEW -> REVIEWED`
- `REVIEWED -> IN_PROGRESS`
- `FIXED -> VERIFIED`
- reopening a verified item may move it back to `NEW` with a new developer comment only if later implemented; Phase 2 does not need threaded comments.

### Context and reopen behavior

Every feedback item should capture as much stable context as is currently available:

- screen
- route
- game id
- part
- round
- question id
- question text snapshot
- element id
- app version
- build number
- git commit/build source identifier when available
- device metadata

The Inbox may expose `Zur Stelle springen` only for flows where a deterministic reopen contract exists. Normal quiz packs should be the first supported deep-reopen target. Unsupported special flows show context but do not fake navigation.

## Architecture

### Android components

Keep responsibilities split:

- `DeveloperFeedbackModels.kt` – durable data contracts only.
- `DeveloperFeedbackRepository.kt` – network/storage metadata operations; no Compose state.
- `DeveloperReviewViewModel.kt` – draft/submission/inbox state, refresh, status transitions, attachment workflow state.
- `DeveloperReviewQuickNote.kt` – compact entry and draft editor.
- `DeveloperReviewInboxScreen.kt` – developer-only list/detail UI.
- `DeveloperReviewCapture.kt` – screenshot capture/compression abstraction.
- `DeveloperVoiceNoteRecorder.kt` – recording lifecycle abstraction.
- `DeveloperReviewTargetRegistry.kt` – element registration/selection state independent of game logic.
- small `developerReviewTarget(...)` modifier helper for screens/components.

Do not fold screenshot, recording, inbox, and target-selection logic into `LiveChangeOverlay.kt`.

### Supabase

Reuse the existing `developer_feedback` row and private `developer-feedback` bucket.

If required, add only attachment metadata that cannot be represented by the existing `screenshot_path`, `audio_path`, and `transcript` fields. Avoid a second feedback table in Phase 2.

Edge Functions:

- existing `harmony-developer-feedback` remains the trusted create/upsert path.
- new `harmony-developer-feedback-transcribe` handles private audio transcription.
- signed download URLs should be generated through authenticated server/client Storage APIs under existing RLS/storage policies; objects remain private.

### Assistant workflow

The assistant reads `developer_feedback` directly from the connected Harmony Supabase project. The scheduled task checks for newly created `NEW` items and notifies only when new items exist.

When implementation work starts:

1. group related feedback by game/screen/component
2. inspect latest `main` and overlapping PRs
3. mark items `REVIEWED` or `IN_PROGRESS`
4. implement on a focused GitHub branch using test-first changes where practical
5. create/review/merge the PR under the normal repository workflow
6. write `github_pr`, `github_branch`, `fixed_commit`, and status `FIXED` back to the matching feedback rows
7. developer verifies the behavior in-app and marks `VERIFIED`

`AUTO_SAFE` means the assistant may proceed with small isolated fixes after normal repository verification. It does not allow the mobile app or Edge Function to merge code autonomously.

## Security and privacy

- Developer Review remains admin-only via the existing `is_ai_admin()` mechanism.
- Never expose screenshots/audio through public buckets.
- Never embed service-role, transcription-provider, or GitHub secrets in Android.
- Edge Functions must use JWT verification and re-check admin authorization server-side.
- Attachment paths must be scoped to authenticated user/feedback id and validated before access.
- Normal users and demo sessions must not see the review controls or Inbox.
- Screenshot/audio capture is always explicit and visibly active.

## Failure handling

- Text feedback can save even if optional attachments fail.
- Attachment upload failures are retryable without creating duplicate rows; retain `client_feedback_id` idempotency.
- Recording interruption produces either a valid finalized audio file or no attachment; no half-written path is submitted.
- Transcription failure keeps audio and exposes retry/manual-text options.
- Missing/deleted attachment renders a clear placeholder in Inbox and does not break the feedback item.
- Offline draft persistence is optional follow-up, not required for initial Phase 2.

## Testing strategy

### Unit/contract tests

- screenshot path is attached to the same feedback id
- audio path/transcript payload mapping
- transcription endpoint/auth contract
- status transitions and filtering
- element target registry selects one stable id and suppresses the marked element's normal click for that selection
- Quick Note preserves existing text-only submission
- developer controls remain absent for non-admin/demo mode

### Compose/UI tests where available

- mark mode highlights/selects registered targets
- attachment previews appear in draft/inbox
- Inbox filters/grouping do not hide blocker items incorrectly
- screenshot action closes/hides review overlay during capture and restores it afterward

### Build verification

Before each implementation PR:

- fetch latest `main`
- check overlapping open PRs
- inspect exact changed-file diff
- run available Android unit/assemble checks when runners are available
- never claim CI passed when no steps executed
- do not touch Baby/Kid Generator scope

## Delivery slices

Implementation should be split into focused PRs so each can be verified and rolled back independently:

1. **Inbox foundation** – list/detail/filter/status UI on top of existing rows.
2. **Screenshot attachments** – capture, private upload, preview, signed retrieval.
3. **Voice note recording** – Android recorder + private upload, no transcription dependency yet.
4. **Voice transcription** – authenticated Edge Function + transcript refresh/error handling.
5. **Element marking framework** – registry/modifier/selection mode + first core targets.
6. **Context coverage pass** – register important targets across common runners and selected special flows.
7. **Deep reopen + GitHub result metadata polish** – only where deterministic and safe.

Each slice must preserve the working Phase 1 text-note path.

## Out of scope for Phase 2

- automatic arbitrary code generation from every note without repository review
- direct GitHub write credentials inside the Android app
- public screenshot/audio URLs
- arbitrary coordinate-based Compose element mapping
- full offline issue tracker
- threaded team collaboration/comments
- Baby/Kid Generator changes

## Acceptance criteria

Phase 2 is complete when an authorized developer can, from a running Harmony screen:

1. create a normal text feedback item with current context
2. explicitly attach a screenshot and later view it in the private Inbox
3. record a voice note, see its transcript, and retain the original audio privately
4. enter mark mode, tap a registered UI element, and see the stable element id on the feedback item
5. browse/filter the Developer Inbox and change review status
6. see GitHub fix metadata after the assistant resolves a note
7. mark the fix VERIFIED after retesting

All of this must work without exposing Developer Review to ordinary users and without requiring the developer to manually send screenshots through chat.