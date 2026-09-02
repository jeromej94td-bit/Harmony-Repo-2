# Real User Couples Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Finish Harmony's real-user session, partner connection, solo/couple lifecycle, profile identity, and couple-aware navigation without using GitHub Actions.

**Architecture:** Supabase Auth is the only production identity source. `AppSessionViewModel` owns signed-in/solo/paired state, while server-side RPCs atomically create/redeem invite codes, unpair, reset, and return the current partner. Android remains usable in solo mode; pair-only surfaces show a Partner-verbinden CTA instead of simulated partner data.

**Tech Stack:** Android Kotlin, Jetpack Compose, StateFlow/MVVM, Room, Supabase Auth/Postgres RPC/Edge Functions.

**Spec:** `docs/superpowers/specs/2026-09-01-harmony-real-users-couples-design.md`

## Global Constraints

- Production Supabase project is exactly `rspgnonlpkxdudbjxnrl`.
- No Harmony Brain runtime, anonymous-auth generation, Brain UI, Brain-generated games, or Brain backend calls.
- Panda either/or game remains unchanged in this phase.
- A couple has exactly two real authenticated users.
- No second password is used for pairing; the invite code is the only pairing credential after login.
- Solo users may browse the app before pairing.
- `Harmony zurücksetzen` keeps login but clears the relationship connection and Harmony progress/history covered by reset.
- `Konto löschen` is irreversible and must leave the former partner unpaired.
- Do not run or retry GitHub Actions for this work.

---

### Task 1: Harden session and pairing backend

**Files:**
- Modify: Supabase RPCs `get_app_session`, `create_partner_invite`, `join_partner_invite`, `leave_current_couple`, `reset_harmony`
- Modify: `supabase/functions/delete-account/index.ts` if repository copy exists

**Interfaces:**
- Consumes: `auth.uid()` and the `harmony_profiles`, `harmony_couples`, `harmony_couple_members`, `harmony_couple_invites`, `harmony_question_rounds`, `harmony_question_answers` tables.
- Produces: atomic two-person pairing and lifecycle behavior used by `AppSessionRepository`.

- [ ] **Step 1: Verify current schema and RPC definitions**

Run read-only SQL against `rspgnonlpkxdudbjxnrl` for the six tables and five RPCs. Expected: all objects exist, membership is unique per user, invite codes are hashed and one-time.

- [ ] **Step 2: Add or correct server constraints with a migration**

Use `apply_migration` only for DDL. Enforce one active couple membership per user and maximum two members per couple through database constraints/RPC locking.

- [ ] **Step 3: Make reset and leave invalidate outstanding invites**

Update RPCs so both former members become unpaired and stale invite codes cannot be redeemed after lifecycle changes.

- [ ] **Step 4: Verify lifecycle with SQL-level invariants**

Read back function definitions and constraints. Expected: no RPC can create a third member, self-pair, reuse an invite, or keep a deleted member paired.

### Task 2: Complete Android AppSession and error mapping

**Files:**
- Modify: `app/src/main/java/com/example/data/session/AppSessionRepository.kt`
- Modify: `app/src/main/java/com/example/ui/session/AppSessionViewModel.kt`
- Test: `app/src/test/java/com/example/data/session/AppSessionRepositoryTest.kt`

**Interfaces:**
- Consumes: Supabase Auth access token and pairing RPCs.
- Produces: `AppSessionUiState` with `SIGNED_OUT`, `READY`, `DEMO`, `ERROR`, active invite, current partner, and user-facing action errors.

- [ ] **Step 1: Add deterministic invite/error mapping tests**

Test `invalid_invite_code`, `invite_not_available`, `already_paired`, `cannot_pair_with_self`, and `inviter_already_paired` to stable German UI messages.

- [ ] **Step 2: Implement stable error mapping**

Keep raw server reason codes inside `HarmonySessionException`; expose friendly copy from `AppSessionViewModel` without leaking PostgREST error text.

- [ ] **Step 3: Refresh session after lifecycle actions**

After join/leave/reset, always use server-returned/refresh state and clear any active invite.

- [ ] **Step 4: Inspect source directly**

No Actions run. Verify changed files through GitHub contents API and ensure no anonymous sign-up path is referenced.

### Task 3: Finish partner connection and account UI

**Files:**
- Modify: `app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt`
- Modify: `app/src/main/java/com/example/ui/screens/AccountLifecycleScreen.kt`
- Modify: `app/src/main/java/com/example/ui/screens/ProfileSheet.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`

**Interfaces:**
- Consumes: `AppSessionUiState` and `AppSessionViewModel` actions.
- Produces: code creation, code entry, success state, unpair/reset/delete entry points, and solo profile state.

- [ ] **Step 1: Ensure solo profile shows `Noch nicht verbunden`**

Use `appSession.partner == null`; never display seeded `Alex` in production.

- [ ] **Step 2: Ensure both pairing paths are available**

Expose `Code erstellen` and `Code eingeben`; generated code is selectable/shareable and shows expiry.

- [ ] **Step 3: Show successful pairing identity**

After redeem, refresh and show both real profile names/avatar URLs. Do not request a password.

- [ ] **Step 4: Wire account lifecycle actions**

`Partner trennen`, `Harmony zurücksetzen`, `Konto löschen`, and logout call only the `AppSessionViewModel` lifecycle APIs.

### Task 4: Make app navigation couple-aware

**Files:**
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Create: `app/src/main/java/com/example/ui/screens/PartnerRequiredScreen.kt`

**Interfaces:**
- Consumes: `AppSession.partner` / `coupleId`.
- Produces: a reusable Harmony-styled gate for pair-only surfaces.

- [ ] **Step 1: Add `PartnerRequiredScreen`**

Provide a concise Harmony/Aurora CTA with `Partner verbinden` callback and no simulated content.

- [ ] **Step 2: Gate pair-only chat/shared surfaces**

If no partner is linked, show the CTA instead of partner chat/shared-couple content. Browsing games/categories remains available in solo mode.

- [ ] **Step 3: Preserve Panda behavior**

Do not change `PandaEitherOrScreen.kt` or its local handover flow in this phase.

### Task 5: Carry Brain removal into the user-feature branch

**Files:**
- Replace from cleanup branch: `app/src/main/java/com/example/ui/screens/ChatScreen.kt`
- Replace/disable: `app/src/main/java/com/example/data/brain/gateway/SupabaseHarmonyBrainGateway.kt`
- Remove production references from: `app/src/main/java/com/example/MainActivity.kt`, `app/src/main/java/com/example/ui/HarmonyViewModel.kt`

**Interfaces:**
- Consumes: archived Brain state from `archive/harmony-brain-2026-09-02` only for historical recovery.
- Produces: zero executable Harmony Brain UI/network path in the real-user branch.

- [ ] **Step 1: Bring the Brain-free partner chat into this branch**

Chat keeps text/image/voice/report/fullscreen-image functions only.

- [ ] **Step 2: Disable Brain gateway/network calls**

Any compatibility class must return a disabled result locally and never create/authenticate a user or call an Edge Function.

- [ ] **Step 3: Remove Brain UI entry points from production navigation**

No Brain toggle, coach, generated-game surface, or Dev Brain tab should be reachable from normal app flow.

### Task 6: Final direct verification and PR

**Files:**
- Review all files changed on `feat/real-users-couples-foundation-direct`.

**Interfaces:**
- Consumes: completed Tasks 1-5.
- Produces: merge-ready PR without GitHub Actions execution.

- [ ] **Step 1: Compare branch with its base**

Use GitHub compare API and inspect all changed filenames/diffs.

- [ ] **Step 2: Verify Supabase state**

Expected: zero anonymous users, pairing RPCs present, Brain Edge Functions disabled, regular accounts untouched.

- [ ] **Step 3: Open PR to `main`**

Describe direct verification explicitly and state that Actions were intentionally not retried because the account has exhausted included Actions minutes.
