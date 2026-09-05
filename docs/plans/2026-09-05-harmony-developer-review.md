# Harmony Developer Review – Implementation Plan

**Goal:** Harmony becomes its own contextual QA/development inbox: while an authorized developer uses the app normally, a floating review control can attach a correction to the exact current screen/game/question. Supabase is the durable inbox; GitHub stays the implementation layer.

## Architecture

- Reuse the existing `ai_admin_users` allowlist / `is_ai_admin()` authorization. Do not introduce a second developer identity system.
- Persist feedback in `public.developer_feedback` with RLS restricted to admins.
- Submit through authenticated Edge Function `harmony-developer-feedback`; the function revalidates the admin row and forces server-owned fields (`created_by`, `repository`, initial status).
- Android keeps a small `DeveloperReviewContext` describing the current app location. Root Harmony state supplies route/game/question context; individual screens can later refine `elementId` through stable test tags.
- Global `DeveloperReviewOverlay` is only shown for an authenticated admin and never in demo mode.
- Developer Studio gains an Inbox tab backed by the same table.
- Feedback workflow: `NEW -> REVIEWED -> IN_PROGRESS -> FIXED -> VERIFIED`.
- Phase 1 ships text notes, type, priority, execution mode, automatic context and inbox.
- Phase 2 adds private screenshot/audio attachments, transcription and tap-to-mark element context on top of the same row model.

## Task 1 – Supabase feedback inbox

1. Add migration creating `developer_feedback`, indexes, `updated_at` trigger, RLS policies and private `developer-feedback` storage bucket.
2. Admins can select/update/delete rows. Direct authenticated inserts are allowed only for admins; the Edge Function remains the app write path.
3. Seed feature flag `developer_review` enabled so the client can later gain remote kill-switch behavior without schema changes.
4. Verify table, RLS, indexes and bucket with read-only SQL.

## Task 2 – Authenticated Edge Function

1. Deploy `harmony-developer-feedback` with `verify_jwt=true`.
2. Resolve the caller from the bearer token.
3. Verify caller has an `ai_admin_users` row using caller-scoped Supabase client.
4. Validate enum-like fields and non-empty note.
5. Upsert by `client_feedback_id` to make retries idempotent.
6. Return normalized inserted row id/status.

## Task 3 – Android data layer (test first)

Files:
- `app/src/main/java/com/example/data/developer/DeveloperFeedbackModels.kt`
- `app/src/main/java/com/example/data/developer/DeveloperFeedbackRepository.kt`
- tests under `app/src/test/java/com/example/developer/`

1. Add enums/type-safe models for status/type/priority/execution mode.
2. Add `DeveloperReviewContext` and payload serialization.
3. Add repository methods: `isCurrentUserAdmin()`, `submitFeedback(...)`, `loadFeedback()`.
4. Use current authenticated access token and existing Supabase URL/publishable key conventions.
5. Unit-test payload/context mapping and retry idempotency contract.

## Task 4 – Review ViewModel + global overlay (test first)

Files:
- `app/src/main/java/com/example/ui/developer/DeveloperReviewViewModel.kt`
- `app/src/main/java/com/example/ui/developer/DeveloperReviewOverlay.kt`
- `app/src/main/java/com/example/MainActivity.kt`

1. ViewModel loads admin eligibility once per signed-in user and exposes feedback submission state.
2. Add floating `🛠` review button in a safe side position, above system/navigation overlays.
3. Tap opens a compact feedback sheet/dialog with note, type, priority and execution mode.
4. Build automatic context from root state: selected tab, results/special flow, active pack id/title/current index/question text.
5. On success close dialog and show confirmation; on failure keep draft visible and show retryable error.
6. Overlay is absent in demo/non-admin sessions.

## Task 5 – Developer Studio Inbox (test first)

File:
- `app/src/main/java/com/example/ui/screens/DevStudioScreen.kt`

1. Add `🛠 Review` tab.
2. Show NEW count, feedback cards grouped by screen/game, priority/type/status and contextual identifiers.
3. Provide status transitions at minimum NEW -> REVIEWED and FIXED -> VERIFIED.
4. No automatic GitHub branch per note; the assistant clusters notes before implementation.

## Task 6 – Context coverage

1. Normal quiz: pack id, title, current index, question text.
2. Results: pack id.
3. Main tabs: stable route names.
4. Special flows: root screen identifier now; deeper part/round registration is incremental.
5. Add stable `elementId` support so screen components can opt into element-level notes later.

## Task 7 – Phase 2 follow-up

1. Private screenshot capture/upload tied to feedback id.
2. Press-and-hold voice note recording, private upload and transcription into `transcript`.
3. Tap-to-mark mode using stable review/test tags; do not attempt fragile arbitrary Compose hit-testing.
4. Deep-link/reopen exact game/question from Developer Inbox where runner semantics allow it.

## Verification

- Source/contract tests are committed before production changes where practical.
- GitHub Actions hosted runners are currently quota-blocked; do not claim Gradle/JUnit ran until a runner actually executes steps.
- Before PR: fetch latest `main`, compare changed files, rebase/squash onto latest `main`, inspect full PR patch and ensure no Baby/Kid Generator changes.
- Verify Supabase by querying table/policies/indexes and calling the Edge Function with an authenticated admin session when an app session is available.
