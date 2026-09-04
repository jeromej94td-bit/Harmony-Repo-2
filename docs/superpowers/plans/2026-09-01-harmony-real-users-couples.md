# Harmony Real Users & Couples Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert Harmony’s production foundation from local/simulated identity into real Supabase users with optional solo use, secure two-person couple pairing, couple-aware account state, safe reset/delete semantics, and a future-safe hidden-answer reveal model while leaving the Panda game behavior unchanged.

**Architecture:** Supabase Auth remains the only identity source. PostgreSQL owns profiles, pairing, couple membership, invite codes, and future hidden-answer authorization; Android uses the existing Supabase client plus PostgREST RPCs to load one canonical `AppSession`. Existing Room content remains a device cache for this phase, but it is cleared whenever the authenticated owner changes so one account can never see another account’s local data.

**Tech Stack:** Android/Kotlin, Jetpack Compose, Room, supabase-kt 3.0.0 Auth + Functions + PostgREST, Supabase PostgreSQL/RLS/RPC, Supabase Edge Functions, JUnit/Robolectric.

**Spec:** `docs/superpowers/specs/2026-09-01-harmony-real-users-couples-design.md`

## Global Constraints

- Supabase project: `rspgnonlpkxdudbjxnrl`.
- Supabase Auth is the only production identity source.
- A signed-in user may explore Harmony without a partner.
- A user may belong to at most one active couple.
- A couple may contain exactly two authenticated users, never more.
- Pairing uses a generated shareable code; it does not request another password.
- Partner answer contents remain server-hidden until both couple members have answered the same round.
- Future result payloads include both members’ display names and profile/avatar URLs.
- `Harmony zurücksetzen` keeps the login, removes the couple connection, clears the current user’s Harmony progress and the shared relationship history covered by the reset flow.
- Account deletion permanently deletes the authenticated account and automatically disconnects the remaining partner.
- Harmony Brain must not run, generate, analyze, notify, or create anonymous auth users in production.
- Existing anonymous Supabase users are deleted only after the anonymous creation path is disabled and verified.
- `PandaEitherOrScreen.kt` and the Panda interaction flow must remain behaviorally unchanged in this phase.
- Do not migrate all chat/photos/moments to cloud in this phase unless required for account isolation or reset safety.

---

## File Structure

### New Android files

- `app/src/main/java/com/example/data/session/AppSession.kt` — immutable authenticated/couple session model.
- `app/src/main/java/com/example/data/session/AppSessionRepository.kt` — Supabase Auth + RPC access for bootstrap, pairing, reset, and unpair.
- `app/src/main/java/com/example/data/session/AccountCacheBoundary.kt` — clears Room/local media when authenticated owner changes.
- `app/src/main/java/com/example/ui/session/AppSessionViewModel.kt` — lifecycle-aware session state for `MainActivity`.
- `app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt` — Harmony UI for generating/sharing/entering a partner code.
- `app/src/main/java/com/example/ui/screens/AccountLifecycleScreen.kt` — Harmony-designed reset/delete confirmation screens.

### New Supabase files

- `supabase/migrations/20260901_000001_real_users_couples.sql` — profiles, couples, members, invites, hidden-answer foundation, RLS, and RPCs.
- `supabase/functions/delete-account/delete-account-core.mjs` — repository copy of the production delete lifecycle logic.
- `supabase/functions/delete-account/index.ts` — repository entrypoint matching the deployed function.

### Existing files to modify

- `gradle/libs.versions.toml` — add `supabase-postgrest` alias.
- `app/build.gradle.kts` — include PostgREST module.
- `app/src/main/java/com/example/data/SupabaseClient.kt` — install PostgREST on the canonical client.
- `app/src/main/java/com/example/MainActivity.kt` — real session gate, cache boundary, pairing/account overlays; remove Brain auto-generation startup.
- `app/src/main/java/com/example/ui/HarmonyViewModel.kt` — stop Brain runtime/analysis and accept cloud identity into local presentation state.
- `app/src/main/java/com/example/data/repository/HarmonyRepository.kt` — remove simulated seeded relationship data in production and expose safe cache/profile refresh helpers.
- `app/src/main/java/com/example/ui/screens/ProfileSheet.kt` — replace hard-coded invite code/simulator production UI with real couple state/actions.
- `app/src/main/java/com/example/ui/screens/AuthScreen.kt` — keep real Google/email login, hide demo bypass outside debug builds.

### New/updated tests

- `app/src/test/java/com/example/BrainFreezeContractTest.kt`
- `app/src/test/java/com/example/AppSessionModelTest.kt`
- `app/src/test/java/com/example/AccountCacheBoundaryTest.kt`
- `app/src/test/java/com/example/PartnerConnectionContractTest.kt`
- `app/src/test/java/com/example/AccountLifecycleContractTest.kt`
- `app/src/test/java/com/example/PandaCoupleMigrationScopeContractTest.kt`
- update `AuthSessionRestorationContractTest.kt` to assert canonical session ownership rather than a local auth boolean.

---

### Task 1: Freeze Harmony Brain and stop anonymous-user creation

**Files:**
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Modify: `app/src/main/java/com/example/ui/HarmonyViewModel.kt`
- Modify: `app/src/main/java/com/example/data/brain/gateway/SupabaseBrainAuthSession.kt`
- Create: `app/src/test/java/com/example/BrainFreezeContractTest.kt`

**Interfaces:**
- Consumes: existing `HarmonyViewModel`, `SupabaseConfig.client.auth`.
- Produces: no production startup path that calls Brain generation, Brain analysis, or anonymous `/auth/v1/signup`.

- [ ] **Step 1: Write the failing Brain freeze contract test**

```kotlin
package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class BrainFreezeContractTest {
    @Test
    fun `production app does not start brain or create anonymous auth users`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val vm = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")
        val brainAuth = source("app/src/main/java/com/example/data/brain/gateway/SupabaseBrainAuthSession.kt")

        assertFalse(main.contains("attachAutoGeneration(this)"))
        assertFalse(vm.contains("HarmonyBrainEngine.analyzeAnswers"))
        assertFalse(vm.contains("ForegroundGameGenerator("))
        assertFalse(brainAuth.contains("/auth/v1/signup"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
```

- [ ] **Step 2: Run the test and verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests com.example.BrainFreezeContractTest`
Expected: FAIL because current production source still starts Brain and contains `/auth/v1/signup`.

- [ ] **Step 3: Remove active Brain runtime hooks**

In `MainActivity.onCreate`, remove:

```kotlin
viewModel.attachAutoGeneration(this)
```

In `HarmonyViewModel`, remove production initialization/collectors that call:

```kotlin
HarmonyBrainEngine.analyzeAnswers(...)
HarmonyBrainEngine.generateSuggestions(...)
ForegroundGameGenerator(...)
brainGateway.generateQuestions(...)
```

Keep dormant Brain database/entity source files if they are needed for a future reactivation, but do not expose or invoke them from production UI/state.

Replace `SupabaseBrainAuthSession.getOrFetchToken()` with a fail-closed implementation so no accidental anonymous signup remains:

```kotlin
suspend fun getOrFetchToken(forceRefresh: Boolean = false): String {
    error("Harmony Brain is disabled. Use the authenticated Harmony session when Brain is re-enabled.")
}
```

- [ ] **Step 4: Run focused and full unit tests**

Run:

```bash
./gradlew :app:testDebugUnitTest --tests com.example.BrainFreezeContractTest
./gradlew :app:testDebugUnitTest
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/example/MainActivity.kt app/src/main/java/com/example/ui/HarmonyViewModel.kt app/src/main/java/com/example/data/brain/gateway/SupabaseBrainAuthSession.kt app/src/test/java/com/example/BrainFreezeContractTest.kt
git commit -m "refactor: freeze harmony brain runtime"
```

---

### Task 2: Add secure Supabase user/couple schema and hidden-answer foundation

**Files:**
- Create: `supabase/migrations/20260901_000001_real_users_couples.sql`

**Interfaces:**
- Consumes: `auth.users`, `auth.uid()`.
- Produces RPCs: `get_app_session()`, `create_partner_invite()`, `join_partner_invite(text)`, `leave_current_couple()`, `reset_harmony()`, `get_question_round_status(uuid)`, `reveal_question_result(uuid)`.

- [ ] **Step 1: Create migration with canonical tables**

Use these table shapes:

```sql
create extension if not exists pgcrypto;

create table if not exists public.harmony_profiles (
  user_id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null default '',
  avatar_url text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.harmony_couples (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  ended_at timestamptz
);

create table if not exists public.harmony_couple_members (
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  joined_at timestamptz not null default now(),
  primary key (couple_id, user_id)
);

create unique index if not exists harmony_one_active_couple_per_user
on public.harmony_couple_members(user_id);

create table if not exists public.harmony_couple_invites (
  id uuid primary key default gen_random_uuid(),
  created_by uuid not null references auth.users(id) on delete cascade,
  code_hash text not null unique,
  expires_at timestamptz not null default (now() + interval '24 hours'),
  used_at timestamptz,
  used_by uuid references auth.users(id) on delete set null,
  created_at timestamptz not null default now()
);

create table if not exists public.harmony_question_rounds (
  id uuid primary key default gen_random_uuid(),
  couple_id uuid not null references public.harmony_couples(id) on delete cascade,
  pack_id text not null,
  question_index integer not null,
  created_at timestamptz not null default now(),
  unique (couple_id, pack_id, question_index)
);

create table if not exists public.harmony_question_answers (
  round_id uuid not null references public.harmony_question_rounds(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  answer_text text not null,
  answered_at timestamptz not null default now(),
  primary key (round_id, user_id)
);
```

- [ ] **Step 2: Add auth-profile creation trigger**

```sql
create or replace function public.handle_harmony_auth_user_created()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.harmony_profiles(user_id, display_name, avatar_url)
  values (
    new.id,
    coalesce(new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'name', split_part(coalesce(new.email, ''), '@', 1), ''),
    coalesce(new.raw_user_meta_data->>'avatar_url', new.raw_user_meta_data->>'picture')
  )
  on conflict (user_id) do nothing;
  return new;
end;
$$;

drop trigger if exists on_harmony_auth_user_created on auth.users;
create trigger on_harmony_auth_user_created
after insert on auth.users
for each row execute function public.handle_harmony_auth_user_created();
```

Also backfill only current non-anonymous users:

```sql
insert into public.harmony_profiles(user_id, display_name, avatar_url)
select id,
       coalesce(raw_user_meta_data->>'full_name', raw_user_meta_data->>'name', split_part(coalesce(email, ''), '@', 1), ''),
       coalesce(raw_user_meta_data->>'avatar_url', raw_user_meta_data->>'picture')
from auth.users
where coalesce(is_anonymous, false) = false
on conflict (user_id) do nothing;
```

- [ ] **Step 3: Add two-member enforcement and secure pairing RPCs**

Implement `create_partner_invite()` so it:

1. requires `auth.uid()`;
2. rejects callers already present in `harmony_couple_members`;
3. invalidates caller’s previous unused invites;
4. generates a six-character uppercase code from `ABCDEFGHJKLMNPQRSTUVWXYZ23456789`;
5. stores only `encode(digest(code, 'sha256'), 'hex')`;
6. returns the plaintext code once plus `expires_at`.

Implement `join_partner_invite(p_code text)` in one transaction so it:

1. locks the invite row;
2. checks unexpired/unused;
3. rejects self-pairing;
4. rejects either user if already paired;
5. creates one `harmony_couples` row;
6. inserts exactly the inviter and joiner into `harmony_couple_members`;
7. marks the invite used;
8. returns the new `couple_id`.

Add a trigger on `harmony_couple_members` that raises when a couple already has two rows.

- [ ] **Step 4: Add RLS and answer secrecy**

Enable RLS on all public Harmony tables. Direct `SELECT` on `harmony_question_answers` must expose only the caller’s own row:

```sql
create policy harmony_answers_select_own
on public.harmony_question_answers
for select
to authenticated
using (user_id = auth.uid());

create policy harmony_answers_insert_own
on public.harmony_question_answers
for insert
to authenticated
with check (
  user_id = auth.uid()
  and exists (
    select 1
    from public.harmony_question_rounds r
    join public.harmony_couple_members m on m.couple_id = r.couple_id
    where r.id = round_id and m.user_id = auth.uid()
  )
);
```

Implement `get_question_round_status(round_id)` to return only `my_answered`, `partner_answered`, and `ready_to_reveal`, never `answer_text`.

Implement `reveal_question_result(round_id)` as `SECURITY DEFINER` and return zero rows until:

```sql
select count(distinct a.user_id) = 2
from public.harmony_question_answers a
where a.round_id = p_round_id;
```

After both answers exist, return both rows joined with `harmony_profiles(display_name, avatar_url)`.

- [ ] **Step 5: Apply migration on project and verify invariants**

Apply the migration through the Supabase migration tool to project `rspgnonlpkxdudbjxnrl`.

Verify with SQL:

```sql
select tablename, rowsecurity
from pg_tables
where schemaname = 'public'
  and tablename like 'harmony_%'
order by tablename;

select proname
from pg_proc
where proname in (
  'get_app_session', 'create_partner_invite', 'join_partner_invite',
  'leave_current_couple', 'reset_harmony',
  'get_question_round_status', 'reveal_question_result'
)
order by proname;
```

Expected: all new tables have RLS enabled and all seven RPCs exist.

- [ ] **Step 6: Commit**

```bash
git add supabase/migrations/20260901_000001_real_users_couples.sql
git commit -m "feat: add secure couple data model"
```

---

### Task 3: Build one canonical Android AppSession and account cache boundary

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/com/example/data/SupabaseClient.kt`
- Create: `app/src/main/java/com/example/data/session/AppSession.kt`
- Create: `app/src/main/java/com/example/data/session/AppSessionRepository.kt`
- Create: `app/src/main/java/com/example/data/session/AccountCacheBoundary.kt`
- Create: `app/src/main/java/com/example/ui/session/AppSessionViewModel.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Update: `app/src/test/java/com/example/AuthSessionRestorationContractTest.kt`
- Create: `app/src/test/java/com/example/AppSessionModelTest.kt`
- Create: `app/src/test/java/com/example/AccountCacheBoundaryTest.kt`

**Interfaces:**
- Produces: `AppSession`, `PairingState`, `AppSessionRepository.refresh()`, `AppSessionRepository.createInvite()`, `AppSessionRepository.joinInvite(code)`, `AppSessionRepository.unpair()`, `AppSessionRepository.resetHarmony()`.

- [ ] **Step 1: Add PostgREST dependency**

In `gradle/libs.versions.toml`:

```toml
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt", version.ref = "supabase" }
```

In `app/build.gradle.kts`:

```kotlin
implementation(libs.supabase.postgrest)
```

In `SupabaseConfig.client`:

```kotlin
install(io.github.jan.supabase.postgrest.Postgrest)
```

- [ ] **Step 2: Write failing model/cache tests**

`AppSessionModelTest.kt`:

```kotlin
@Test
fun `solo user is authenticated without a couple`() {
    val session = AppSession(userId = "u1", email = "a@example.com", me = HarmonyUserProfile("u1", "A", null))
    assertTrue(session.isAuthenticated)
    assertNull(session.activeCoupleId)
    assertEquals(PairingState.SOLO, session.pairingState)
}
```

`AccountCacheBoundaryTest.kt` must cover:

- same user ID => no clear;
- first owner registration => no clear;
- owner changes from `u1` to `u2` => clear exactly once and persist `u2`.

- [ ] **Step 3: Implement session models**

```kotlin
enum class PairingState { SOLO, PAIRED }

data class HarmonyUserProfile(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?
)

data class AppSession(
    val userId: String,
    val email: String?,
    val me: HarmonyUserProfile,
    val activeCoupleId: String? = null,
    val partner: HarmonyUserProfile? = null
) {
    val isAuthenticated: Boolean get() = userId.isNotBlank()
    val pairingState: PairingState get() = if (activeCoupleId == null) PairingState.SOLO else PairingState.PAIRED
}
```

- [ ] **Step 4: Implement `AppSessionRepository` using authenticated RPCs**

`refresh()` calls `get_app_session` and maps server output to `AppSession`.

`createInvite()` calls `create_partner_invite` and returns:

```kotlin
data class PartnerInvite(val code: String, val expiresAt: String)
```

`joinInvite(code)` trims/uppercases input before RPC.

No RPC accepts a caller-supplied `user_id`; server identity always comes from `auth.uid()`.

- [ ] **Step 5: Implement cache owner boundary**

Use `SharedPreferences("harmony_authenticated_owner")`. On owner change, run on `Dispatchers.IO`:

```kotlin
database.clearAllTables()
File(context.filesDir, "chat").deleteRecursively()
File(context.filesDir, "avatars").deleteRecursively()
File(context.filesDir, "picshare").deleteRecursively()
File(context.filesDir, "moments").deleteRecursively()
```

Then save the new owner ID. Do not clear on ordinary app restart for the same account.

- [ ] **Step 6: Replace local auth boolean as authority**

`AppSessionViewModel` states:

```kotlin
sealed interface SessionUiState {
    data object Loading : SessionUiState
    data object SignedOut : SessionUiState
    data class Ready(val session: AppSession) : SessionUiState
    data class Failed(val message: String) : SessionUiState
}
```

`MainActivity/HarmonyApp` renders `AuthScreen` only for `SignedOut`. After `onAuthSuccess`, call `sessionViewModel.refresh()`; do not set a free-standing `isAuthenticated = true` as the identity source.

- [ ] **Step 7: Run tests**

```bash
./gradlew :app:testDebugUnitTest --tests com.example.AppSessionModelTest
./gradlew :app:testDebugUnitTest --tests com.example.AccountCacheBoundaryTest
./gradlew :app:testDebugUnitTest --tests com.example.AuthSessionRestorationContractTest
```

Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add gradle/libs.versions.toml app/build.gradle.kts app/src/main/java/com/example/data/SupabaseClient.kt app/src/main/java/com/example/data/session app/src/main/java/com/example/ui/session app/src/main/java/com/example/MainActivity.kt app/src/test/java/com/example/AppSessionModelTest.kt app/src/test/java/com/example/AccountCacheBoundaryTest.kt app/src/test/java/com/example/AuthSessionRestorationContractTest.kt
git commit -m "feat: add canonical authenticated app session"
```

---

### Task 4: Replace simulated profile/partner state with real couple state and pairing UI

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt`
- Modify: `app/src/main/java/com/example/ui/screens/ProfileSheet.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Modify: `app/src/main/java/com/example/ui/HarmonyViewModel.kt`
- Modify: `app/src/main/java/com/example/data/repository/HarmonyRepository.kt`
- Modify: `app/src/main/java/com/example/ui/screens/AuthScreen.kt`
- Create: `app/src/test/java/com/example/PartnerConnectionContractTest.kt`

**Interfaces:**
- Consumes: `SessionUiState.Ready(AppSession)`, `AppSessionRepository` actions.
- Produces: Harmony UI for `Code erstellen`, `Code eingeben`, share action, paired state with both profile images/names.

- [ ] **Step 1: Write failing pairing UI contract test**

Assert production source contains:

```text
Partner verbinden
Code erstellen
Code eingeben
Code teilen
```

and does not contain hard-coded production text:

```text
Invite-Code: HRM-8731
```

Also assert `AuthScreen.kt` wraps the demo bypass in `BuildConfig.DEBUG`.

- [ ] **Step 2: Implement solo/paired profile header**

For `PairingState.SOLO`, show current profile image/name and:

```text
Noch nicht verbunden
Verbinde Harmony mit deinem Partner
```

with one primary action `Partner verbinden`.

For `PairingState.PAIRED`, show both remote profile images, both display names, and the relationship status. Do not read `ProfileEntity.partnerName` as the source of truth for this header.

- [ ] **Step 3: Implement partner connection sheet**

State machine:

```kotlin
private enum class PartnerConnectMode { HOME, CREATE, ENTER }
```

HOME shows `Code erstellen` and `Code eingeben`.

CREATE calls `createInvite()` and shows the returned code in large grouped typography. `Code teilen` launches:

```kotlin
Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "Verbinde dich mit mir in Harmony. Mein Code: $code")
}
```

ENTER accepts six characters, normalizes uppercase, and calls `joinInvite(code)`. On success, close the sheet and refresh `AppSession` immediately.

- [ ] **Step 4: Remove simulated relationship seeding from production startup**

`HarmonyRepository.ensureInitialData()` must not create a production relationship with names `Jerome` and `Alex`, simulated partner chat, or a fake invite relationship. Seed data may remain only behind an explicit debug/demo path.

`HarmonyViewModel` receives cloud identity via a method such as:

```kotlin
fun applySessionIdentity(session: AppSession)
```

and uses it only to bridge legacy screens that still require `ProfileEntity` during this phase. The cloud `AppSession` remains authoritative.

- [ ] **Step 5: Hide simulator/demo controls in release**

`ProfileSheet`: show `Partner-Simulator` only when `BuildConfig.DEBUG`.

`AuthScreen`: show `App im Demo-Modus testen` only when `BuildConfig.DEBUG`.

- [ ] **Step 6: Run tests**

```bash
./gradlew :app:testDebugUnitTest --tests com.example.PartnerConnectionContractTest
./gradlew :app:testDebugUnitTest
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/example/ui/screens/PartnerConnectionSheet.kt app/src/main/java/com/example/ui/screens/ProfileSheet.kt app/src/main/java/com/example/MainActivity.kt app/src/main/java/com/example/ui/HarmonyViewModel.kt app/src/main/java/com/example/data/repository/HarmonyRepository.kt app/src/main/java/com/example/ui/screens/AuthScreen.kt app/src/test/java/com/example/PartnerConnectionContractTest.kt
git commit -m "feat: add real partner pairing experience"
```

---

### Task 5: Add unpair, Harmony reset, and account deletion lifecycle

**Files:**
- Create: `app/src/main/java/com/example/ui/screens/AccountLifecycleScreen.kt`
- Modify: `app/src/main/java/com/example/ui/screens/ProfileSheet.kt`
- Modify: `app/src/main/java/com/example/MainActivity.kt`
- Create: `supabase/functions/delete-account/index.ts`
- Create: `supabase/functions/delete-account/delete-account-core.mjs`
- Create: `app/src/test/java/com/example/AccountLifecycleContractTest.kt`

**Interfaces:**
- Consumes RPCs: `leave_current_couple()`, `reset_harmony()`.
- Produces: `Partner trennen`, `Harmony zurücksetzen`, `Konto löschen` with distinct semantics.

- [ ] **Step 1: Write failing lifecycle contract test**

Assert the account UI exposes all three actions and reset/delete screens require explicit acknowledgement before the destructive button is enabled.

- [ ] **Step 2: Implement `leave_current_couple()` server behavior**

Server function must:

- identify caller only with `auth.uid()`;
- find active couple;
- delete membership rows for that couple;
- mark `harmony_couples.ended_at = now()`;
- keep both auth accounts intact;
- keep shared history archived for the later retention project.

After success, refresh `AppSession`; caller becomes `SOLO`.

- [ ] **Step 3: Implement `reset_harmony()` server behavior**

In one transaction:

```sql
delete from public.harmony_question_answers
where round_id in (select id from public.harmony_question_rounds where couple_id = v_couple_id);

delete from public.harmony_question_rounds where couple_id = v_couple_id;
delete from public.harmony_couple_members where couple_id = v_couple_id;
update public.harmony_couples set ended_at = now() where id = v_couple_id;
```

Invalidate unused invites owned by the caller. Keep `auth.users` and `harmony_profiles` intact.

Android then clears the current local Harmony cache and reloads a fresh solo session.

- [ ] **Step 4: Implement Harmony-designed reset screen**

Full-screen dark Harmony layout with:

```text
Dein Harmony-Konto zurücksetzen
Dein Login bleibt bestehen.
Eure Verbindung wird getrennt.
Dein Harmony-Fortschritt und der gemeinsame Beziehungsverlauf werden gelöscht.
Dieser Vorgang ist nicht umkehrbar.
```

Use a confirmation switch labeled:

```text
Ich stimme zu, Harmony zurückzusetzen
```

and disabled `Harmony zurücksetzen` button until checked.

- [ ] **Step 5: Update account deletion Edge Function**

Before admin deletion, resolve the authenticated user from the JWT exactly as the existing production function does. Then disconnect the current couple before deleting the auth user.

Pseudo-order in `delete-account-core.mjs`:

```js
const user = await resolveUserFromJwt(...)
await rpcAsServiceRole('disconnect_user_for_account_deletion', { p_user_id: user.id })
await deleteAuthUser(user.id)
return json({ ok: true })
```

`disconnect_user_for_account_deletion(uuid)` must delete the couple memberships and mark the couple ended; the remaining partner’s `auth.users` and profile row must remain.

- [ ] **Step 6: Implement Harmony-designed delete screen**

Copy:

```text
Wir sind traurig, dass du gehst
Dein Konto und deine persönlichen Daten werden dauerhaft gelöscht.
Wenn du mit einem Partner verbunden bist, werdet ihr automatisch entkoppelt.
Dein Konto kann danach nicht wiederhergestellt werden.
```

Require switch:

```text
Ich stimme zu, mein Konto zu löschen
```

Only then enable `Mein Konto löschen`.

- [ ] **Step 7: Deploy/verify delete function**

Deploy repository source to Supabase project `rspgnonlpkxdudbjxnrl` with JWT verification enabled. Verify function status is `ACTIVE` and `verify_jwt=true`.

- [ ] **Step 8: Run tests**

```bash
./gradlew :app:testDebugUnitTest --tests com.example.AccountLifecycleContractTest
./gradlew :app:testDebugUnitTest
```

Expected: PASS.

- [ ] **Step 9: Commit**

```bash
git add app/src/main/java/com/example/ui/screens/AccountLifecycleScreen.kt app/src/main/java/com/example/ui/screens/ProfileSheet.kt app/src/main/java/com/example/MainActivity.kt supabase/functions/delete-account app/src/test/java/com/example/AccountLifecycleContractTest.kt
git commit -m "feat: add harmony account lifecycle controls"
```

---

### Task 6: Preserve Panda behavior and verify future answer-reveal security

**Files:**
- Create: `app/src/test/java/com/example/PandaCoupleMigrationScopeContractTest.kt`
- Do not modify: `app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt`

**Interfaces:**
- Consumes: existing Panda flow.
- Produces: regression proof that this migration does not move Panda to remote two-user behavior.

- [ ] **Step 1: Capture Panda file SHA before implementation**

Run:

```bash
git hash-object app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt
```

Record the SHA in the implementation work log.

- [ ] **Step 2: Add scope contract**

```kotlin
@Test
fun `panda remains local handover flow during couple foundation migration`() {
    val panda = source("app/src/main/java/com/example/ui/screens/PandaEitherOrScreen.kt")
    assertTrue(panda.contains("USER_CHOICE"))
    assertTrue(panda.contains("HANDOVER"))
    assertTrue(panda.contains("PARTNER_CHOICE"))
    assertTrue(panda.contains("REVEAL"))
    assertFalse(panda.contains("AppSessionRepository"))
    assertFalse(panda.contains("reveal_question_result"))
}
```

- [ ] **Step 3: Verify live database answer secrecy with two test accounts**

Using two authenticated test JWTs belonging to the same test couple:

1. User A inserts an answer for a test round.
2. User B calls `get_question_round_status`: expected `partner_answered=true`, `ready_to_reveal=false`.
3. User B attempts direct select of A’s `harmony_question_answers`: expected zero rows.
4. User B calls `reveal_question_result`: expected zero rows.
5. User B inserts own answer.
6. Both call `reveal_question_result`: expected exactly two rows, each containing `display_name`, `avatar_url`, `answer_text`.

Delete the test couple/round after verification.

- [ ] **Step 4: Re-check Panda SHA**

Run the same `git hash-object` command. Expected: identical SHA.

- [ ] **Step 5: Run regression tests**

```bash
./gradlew :app:testDebugUnitTest --tests com.example.PandaCoupleMigrationScopeContractTest
./gradlew :app:testDebugUnitTest
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/test/java/com/example/PandaCoupleMigrationScopeContractTest.kt
git commit -m "test: protect panda flow during couple migration"
```

---

### Task 7: Remove existing anonymous Supabase users safely

**Files:**
- No replayable migration file. This is a one-time production cleanup after Task 1 is deployed and verified.

**Interfaces:**
- Consumes: `auth.users.is_anonymous`.
- Produces: zero anonymous Auth users without deleting any real Google/email account.

- [ ] **Step 1: Verify anonymous creation has stopped**

Before deletion, launch the new app build and exercise login, solo browsing, profile, and pairing screen. Then query twice several minutes apart:

```sql
select
  count(*) as total_users,
  count(*) filter (where coalesce(is_anonymous, false)) as anonymous_users,
  count(*) filter (where not coalesce(is_anonymous, false)) as real_users
from auth.users;
```

Expected: anonymous count does not increase.

- [ ] **Step 2: Verify anonymous users own no retained Harmony rows**

Run counts across all private/public user-linked Harmony tables and require zero rows where the referenced auth user is anonymous. If any count is non-zero, stop cleanup and inspect those rows rather than deleting users.

- [ ] **Step 3: Delete anonymous Auth rows only**

Execute exactly:

```sql
delete from auth.users
where coalesce(is_anonymous, false) = true;
```

Do not filter by email, creation date, or guessed IDs.

- [ ] **Step 4: Verify cleanup**

```sql
select
  count(*) filter (where coalesce(is_anonymous, false)) as anonymous_users,
  count(*) filter (where not coalesce(is_anonymous, false)) as real_users
from auth.users;
```

Expected: `anonymous_users = 0`; real-user count equals the pre-cleanup real-user count.

- [ ] **Step 5: Re-test Brain pollution path**

Open the app and navigate through normal features. Query the count again. Expected: still zero anonymous users.

---

### Task 8: Final integration QA and PR

**Files:**
- All files changed above.

**Interfaces:**
- Produces: merge-ready branch with verified Supabase schema and Android behavior.

- [ ] **Step 1: Run full unit suite**

```bash
./gradlew :app:testDebugUnitTest
```

Expected: PASS.

- [ ] **Step 2: Build debug APK**

```bash
./gradlew :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Two-account manual test matrix**

Verify:

1. User A signs in and can browse Harmony with no partner.
2. User A generates a code.
3. User B signs in separately and can browse Harmony with no partner.
4. User B enters A’s code.
5. Both profiles refresh to paired state and show both names/profile images.
6. Third-user join using consumed code fails.
7. Either paired user attempting to create a second active couple fails.
8. Logout A -> login B on same device never shows A’s local Room content.
9. `Partner trennen` leaves both accounts alive and solo.
10. Re-pair two accounts, then `Harmony zurücksetzen`: login remains, connection/progress/shared reset scope is removed.
11. Re-pair two accounts, delete A: A can no longer sign in; B remains able to sign in and is solo.
12. Panda flow is visually/functionally unchanged.
13. Supabase anonymous-user count remains zero.

- [ ] **Step 4: Review Supabase RLS/security**

Run Supabase security/advisor checks. Resolve any new RLS warnings affecting the tables created in Task 2 before PR.

- [ ] **Step 5: Compare branch against main and ensure scope**

Expected changed areas: session/couple/account UI, Brain runtime shutdown, Supabase schema/function, tests/docs. `PandaEitherOrScreen.kt` must not be changed.

- [ ] **Step 6: Open PR**

PR title:

```text
feat: real users and two-person couple foundation
```

PR body must include:

- Supabase migration applied status;
- anonymous users before/after counts;
- two-account pairing QA results;
- reset/delete QA results;
- statement that Panda behavior/file remained unchanged;
- test/build commands and outcomes.

---

## Self-Review

- Spec coverage: auth/session, solo mode, exactly-two pairing, invite generation/entry/share, profile/avatar foundation, hidden answer secrecy, reset, unpair, delete/unpair, Brain freeze, anonymous cleanup, local account isolation, and Panda non-change are each mapped to explicit tasks.
- Scope control: no full chat/photo/moment cloud migration and no Panda remote-answer migration are included.
- No unresolved implementation placeholders are required to execute the plan; function/table names and Android interfaces are fixed above.
- Security boundary: the Android app never chooses a `user_id` for privileged actions; `auth.uid()`/JWT is authoritative.
- Destructive cleanup is ordered after anonymous-user creation is disabled and verified.
