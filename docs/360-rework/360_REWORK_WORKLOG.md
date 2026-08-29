# 360 Rework — Worklog

This file is the append-only project black box for **360 Rework**.

## Worklog rules

- Add a new dated entry for every meaningful completed work package or material project-control decision.
- Do not rewrite older entries to make history look cleaner.
- If an older entry is wrong or incomplete, append a correction entry.
- Record work-package ID, real GitHub PR number, branch, commit/merge commit, verification performed, verification not performed, blockers, decision changes, and next action.
- A code/content change is not considered complete merely because a commit exists.
- Umbrella PR #24 is never merged wholesale; actual work lands via small `24.x` packages.

---

## 2026-08-29 — 24.6.4 parallel numbering correction and Stage 06 continuation record

**Work package:** 24.6.4 — Project-control correction<br>
**Status:** 🟡 PREPARED FOR MERGE<br>
**Branch:** `360-rework/24-6-4-numbering-status-sync`

### Why this correction exists

Stage-02 work progressed concurrently while parallel Stage-06 cleanup was being prepared. PR #37 had already established logical **24.7** as the proposal merge-status sync and reserved **24.8** for Stage 02.3 proposal-location image duels. Two parallel cleanup PRs were created before that concurrent numbering change was noticed.

No application code is being rewritten for this correction. The authoritative logical IDs are corrected in PR metadata and in the 360 Rework tracker while historical commit subjects remain unchanged.

### Authoritative package mapping

- **24.6.2 — Stage 06.4 repetition audit helper**
  - GitHub PR: #39
  - Merge commit: `d4dc969bf5a47d4be5b5470157fe3750cace21d1`
  - Historical merge subject contains `[24.7]`; this is a superseded label only.
  - Added `scripts/audit_harmony360_repetition.py` plus focused unit tests.
  - Fresh focused local Python verification: 2/2 tests passed.
- **24.6.3 — Stage 06.4 targeted scenario de-template cleanup**
  - GitHub PR: #40
  - Merge commit: `ee2ca1b8315be2a8a9db45e78328d49ba3706e39`
  - Historical merge subject contains `[24.8]`; this is a superseded label only.
  - Replaced only the repeated generic opener in the Morgenroutine, Sportliche Ziele and Bücher scenario packs with concrete topic-specific prompts/options.
  - Fresh focused local Kotlin verification passed.

### Number ownership after correction

- **24.7 remains Stage-02 proposal merge-status sync** via PR #37.
- **24.8 remains the active NEXT Stage-02 package** for proposal-location image duels.
- Future parallel Stage-06 slices should use a non-conflicting `24.6.x` ID unless the tracker explicitly assigns another range.

### Stage accounting

Stage 06 remains `0/5 complete (0%)`. Packages 24.4, 24.5, 24.6, 24.6.2 and 24.6.3 repair confirmed defects and start the repeated-content audit, but none of 06.1–06.4 is marked complete until the wider defect class is audited and remaining exceptions are resolved or documented.

### Verification caveat

The repo-wide GitHub Actions for the affected code PRs again failed before executable workflow step 1 (`steps: null`). Those failures are not treated as green results or as proven application regressions. The focused local Python/Kotlin results above are separate evidence.

### Next action

Preserve the Stage-02 handover exactly as already established: **24.8 / Stage 02.3 proposal-location image duels**. Parallel cleanup may continue only under a non-conflicting package ID.

---

## 2026-08-29 — 24.7 proposal merge-status sync

**Work package:** 24.7 — Proposal merge-status sync<br>
**Status:** ✅ MERGED<br>
**Branch:** `360-rework/24-7-control-state-sync`<br>
**GitHub PR:** #37 — `[24.7] 360 Rework — Sync merged proposal status`<br>

### Correction to the earlier handover

The earlier 24.3 entry still described PR #31 as a draft. PR #31 was subsequently marked ready and merged into `main` with merge commit `3012f1bbcb0c4406e4fb8b34258ddb2a4d88bd5f`.

### Recorded state

- 24.2 / Stage 02.1 and 24.3 / Stage 02.2 are both merged but remain `🧪 VERIFY` because no Gradle-capable environment has recorded the focused build result.
- The active functional scope advances to 24.8 / Stage 02.3 proposal-location image duels.
- Parallel Stage 06 updates from 24.4–24.6 remain documented separately and do not alter the active Stage 02 scope.

### Verification

- Documentation-only diff checked with `git diff --check`.
- No Android build or tests were run; this package changes no application code.

---

## 2026-08-29 — 24.3 started before 24.2 build verification

**Work package:** 24.3 — Stage 02.2 Proposal mood/details Das-oder-Das rounds<br>
**Status:** 🟡 IN PROGRESS<br>
**Branch:** `360-rework/24-3-proposal-either-or`<br>
**GitHub PR:** #31 — `[24.3] 360 Rework — Proposal mood and detail rounds` (draft)<br>
**Implementation commit:** `487bb671d89c2efd203601b31eb62f0ed2d1f48d`

### Ordering decision

The operator explicitly authorized 24.3 to begin while 24.2 remains `🧪 VERIFY` because a Gradle-capable build environment is unavailable. This does not mark 24.2 or 24.3 complete and does not authorize the later Stage 02 mechanics.

### Scope

- Define only the proposal mood and proposal detail Das-oder-Das rounds for the existing `proposal_mood` and `proposal_details` flow IDs.
- Keep the content UI-independent and out of standalone navigation until the planned experience entry exists.

### Next action

Review the draft PR and record the requested merge decision. The Gradle verification gap remains open.

---

## 2026-08-29 — 24.2 Stage 02.1 implementation prepared

**Work package:** 24.2 — Experience data model / deterministic proposal flow<br>
**Status:** 🧪 VERIFY<br>
**Branch:** `360-rework/24-2-proposal-flow-model`<br>
**GitHub PR:** #27 — `[24.2] 360 Rework — Deterministic proposal flow model`<br>
**Implementation commit:** `9984b449ae9c6325396d1f247e46564b26011c7f`

### What changed

- Added the UI-independent `ProposalExperienceDefinition` contract.
- Defined the fixed Stage 02 proposal sequence with stable step ids from mood/details through the final reveal.
- Added model-level guards for empty or duplicate step ids and for a reveal that is absent, early, or repeated.
- Kept rendering, navigation, mechanic implementations, content migration and legacy deletion out of this package.

### Verification performed

- `git diff --check` completed without whitespace errors.
- The change is limited to the proposal-flow model and the 360 Rework handover files.

### Verification gap

- No tests were added or run by explicit operator instruction.
- A local Android build could not start: the checkout has no Gradle Wrapper and this environment has no system `gradle` executable.
- This is an environment/tooling limitation, not a recorded application-code build result.

### Merge decision

The operator explicitly authorized the small package to merge despite the unavailable local build. Stage 02.1 remains `🧪 VERIFY` after merge until a Gradle-capable environment records the focused build result.

### Next action

Run the focused build in a Gradle-capable environment. If it succeeds, record the result, mark 02.1 complete, and advance to 24.3.

---

## 2026-08-29 — Stage 01/08 — Ring Image Quality Rework

**Status:** ✅ DONE  
**Progress after this entry:** 1/1 complete (100%)  
**Source branch:** `fix/ring-image-quality`  
**PR:** #22 — `Refresh engagement ring image assets`  
**Merge commit:** `b696acb25f8f9b52235a6fba256ec9dc041edab9`

### What changed

- Replaced the 10 prioritized engagement-ring WebP assets with refreshed image versions.
- Preserved the existing image-precedence behavior so the compiled refreshed resources can win over legacy bundled/Drive sources.
- The merged PR changed exactly 10 files, all ring WebPs.

### Verification performed

- PR #22 confirmed merged.
- `changed_files = 10` confirmed on the PR.
- The final diff was limited to the intended ring WebPs.

### Verification gap

- Two GitHub Actions jobs failed before executing their first workflow step.
- No full green test-suite claim is valid for this work package.

### Next action at that time

Create a persistent cross-chat/model tracking system, then begin Stage 02 — `Unser perfekter Antrag`.

---

## 2026-08-29 — 360 Rework Control Center designed

### Decision

The permanent project name is **360 Rework**. The repository becomes the source of truth for project progress so work can continue across chats, Codex sessions, models, devices, interruptions, or exhausted credits without relying on conversation history.

Every active work package is represented with:

- overall stage position such as `Stage 06/08`;
- local stage progress such as `2/5 complete = 40%`;
- explicit status;
- Definition of Done;
- verification evidence;
- exact next action.

The roadmap keeps an **Original plan reference** so user-facing references remain recognizable. Example: earlier **Point 5 — schlechte Inhalte gezielt entfernen und reparieren** maps to technical `Stage 06/08` because Stage 01 records the already-completed ring prerequisite.

### Initial encoded state

- Stage 01/08 — Ring Image Quality Rework: `✅ DONE`, `1/1 = 100%`.
- Stage 02/08 — `Unser perfekter Antrag`: `🔵 NEXT`, `0/12 = 0%`.
- Current exact substage: `02.1 — Experience data model and deterministic proposal flow definition`.
- Completed core stages: `1/8`.

---

## 2026-08-29 — Merge strategy changed to small 24.x slices

**Parent / umbrella:** GitHub PR #24  
**Umbrella policy:** **DO NOT MERGE WHOLE**

### Why

The 360 Rework contains many independent changes. Merging a large collection at once would make regressions harder to isolate, verify and revert.

### New rule

GitHub does not support decimal PR numbers. Therefore `24.1`, `24.2`, `24.3`, etc. are permanent **360 Rework work-package IDs**, while each package also gets its own real integer GitHub PR number.

Each `24.x` package must have:

- one dedicated branch;
- one narrow coherent change/substage;
- one independent diff and verification cycle;
- one separate GitHub PR;
- one separate merge to `main`;
- one worklog entry with the real PR and merge commit.

PR #24 remains an unmerged umbrella/reference surface and can later be closed without merging when its slices have landed.

### Package 24.1

**Name:** Control Center Foundation  
**Branch:** `360-rework/24-1-control-center-foundation`  
**GitHub PR:** #25 — `[24.1] 360 Rework — Control Center Foundation`

Scope is intentionally documentation-only:

- `START_HERE_360_REWORK.md`
- `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`
- `docs/360-rework/360_REWORK_CURRENT_STATE.md`
- `docs/360-rework/360_REWORK_WORKLOG.md`

No app code, questions, images, mechanics or navigation changes belong in 24.1.

### Verification before merge

- Branch is 0 commits behind `main`.
- Diff contains exactly 4 documentation files.
- GitHub marks PR #25 mergeable.
- Repo-wide Actions again fail before the first executable workflow step (`steps: null`), so this is recorded as an infrastructure verification caveat rather than an application-code test failure.

### Next package after 24.1 reaches main

**24.2 — Stage 02.1:** inspect current Harmony runner/content models and implement only the mixed-step data model plus deterministic proposal-flow definition. Later mechanics remain separate packages.

---

## 2026-08-29 — 24.1 final merge confirmed

**Work package:** 24.1 — Control Center Foundation  
**Status:** ✅ MERGED  
**GitHub PR:** #25 — `[24.1] 360 Rework — Control Center Foundation`  
**Source branch:** `360-rework/24-1-control-center-foundation`  
**Merge commit:** `c0479277ab8b53c888a4c5bf2fb35d827230c6cc`

### Final merged scope

Exactly 4 documentation files were merged:

- `START_HERE_360_REWORK.md`
- `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`
- `docs/360-rework/360_REWORK_CURRENT_STATE.md`
- `docs/360-rework/360_REWORK_WORKLOG.md`

No app code, questions, images, mechanics, navigation or legacy content were changed by 24.1.

### Verification performed

- GitHub reports PR #25 as `merged: true`.
- Final merge commit is `c0479277ab8b53c888a4c5bf2fb35d827230c6cc`.
- The PR changed exactly 4 files.
- The files were documentation-only.

### Verification caveat

The repo-wide GitHub Action again failed before the first executable workflow step (`steps: null`). This is recorded as an infrastructure verification caveat. It is not evidence of an application-code regression and is not treated as a green test result either.

### Project state after merge

- Umbrella PR #24 remains open and must not be merged wholesale.
- Stage 01/08 remains ✅ DONE.
- Stage 02/08 remains 🔵 NEXT at `0/12 = 0%` because 24.1 changed only project-control documentation.
- The next functional work package is **24.2 — Stage 02.1 Experience data model / deterministic proposal flow**.

---

## 2026-08-29 — Parallel Stage 06 cleanup packages 24.4–24.6 merged separately

These packages were intentionally developed independently of the active Stage 02 proposal work. Each authoritative mainline package was rebuilt on the then-current `main` before merge so no stacked cleanup was accidentally bundled into another work package.

### 24.4 — Stage 06.1 partial cleanup

**Status:** ✅ MERGED TO MAIN  
**Authoritative GitHub PR:** #35 — `[24.4] Repair malformed Never Have I Ever answer options`  
**Merge commit:** `817d0a0c002aeac7a560f31513092957d242880a`

What changed:

- Added a narrow generated-content repair policy for `ichhabenochnie` packs.
- Questions with an empty options list receive `Habe ich` / `Habe ich noch nie`.
- Existing non-empty option lists remain unchanged.
- `GeneratedContentRegistry` applies the repair before its existing runtime transformations.
- Regression coverage checks the known malformed Hogwarts question and all registered Never Have I Ever questions.

Historical cleanup:

- Old draft PR #28 was closed as superseded after #35 reached `main`.

### 24.5 — Stage 06.2 partial cleanup

**Status:** ✅ MERGED TO MAIN  
**Authoritative GitHub PR:** #33 — `[24.5] Remove accidental English Harmony 360 prompts`  
**Merge commit:** `4e49989504824987933ebcf4587b58a7a8f8d0c1`

What changed:

- Added a narrowly scoped cleanup for the confirmed generated `What decides whether … is special for you? Rank:` template.
- Translates that template to a German ranking prompt before the existing Harmony 360 runtime rework.
- Includes regression coverage for the raw Museen ranking pack plus an unchanged-German control case.

Historical cleanup:

- Old draft PR #29 was closed as superseded after #33 reached `main`.

### 24.6 — Stage 06.3 partial cleanup

**Status:** ✅ MERGED TO MAIN  
**Authoritative GitHub PR:** #34 — `[24.6] Repair known Harmony 360 wording typos`  
**Merge commit:** `cf8b18a319613919d2f50d5853ac25a4ce065016`

What changed:

- Repairs `Schlagwewohnheiten` → `Schlafgewohnheiten`.
- Repairs `is deinem Partner` → `ist deinem Partner`.
- Adds regression coverage for both confirmed defects.

Historical note:

- Draft PR #30 could not be switched to ready-for-review because of a GitHub connector GraphQL schema error and was closed without discarding code.
- A stacked replacement was used only to preserve work while 24.5 was still isolated; the authoritative direct-to-main merge is PR #34.

### Verification caveat for 24.4–24.6

For the authoritative direct-to-main PRs, the repository-wide GitHub Actions jobs again failed before executable workflow step 1 (`steps: null`, no job logs). The operator explicitly authorized merging work that GitHub marked mergeable despite that infrastructure limitation. Therefore:

- do not claim a green full test suite for 24.4–24.6;
- do not record those pre-step Action failures as application regressions either.

### Stage accounting after these merges

Stage 06 remains `0/5 complete (0%)`. Packages 24.4, 24.5 and 24.6 fix confirmed defects inside 06.1, 06.2 and 06.3, but those substages remain open until a broader audit confirms the relevant defect class is exhausted or remaining exceptions are explicitly documented.

### Next action

Continue the active Stage 02 work package 24.3 independently. Parallel Stage 06 work may continue with a new small package only when its scope does not collide with Stage 02 files.


## 2026-08-29 — 24.9 Stage 02.4 prepared

**Work package:** 24.9 — Refreshed ring-image duels  
**Status:** 🧪 VERIFY / READY FOR REVIEW  
**Branch:** `360-rework/24-9-ring-image-duels-current`  
**GitHub PR:** #46 — `[24.9] 360 Rework — Refreshed ring image duels`  
**Implementation commit:** pending final PR commit

### Scope

- Added `ProposalRingImageDuels.kt` with five deterministic image duels for the existing `ring_style` step.
- Bound all ten refreshed Stage-01 drawable keys exactly once.
- Kept the package independent from the concurrent 24.8 proposal-location work.

### Verification

- Static source review and asset-key coverage completed.
- No Gradle build or long-running GitHub test was started; the repository has no Gradle wrapper/system Gradle.
- The previous PR #45 was closed because its documentation diff conflicted with the newer mainline control-state sync; no application code was lost.

### Next action

Merge this narrow PR, then continue with 24.10 / Stage 02.5 ranking after 24.8 coordination.


## 2026-08-29 — 24.10 Stage 02.5 prepared

**Work package:** 24.10 — Proposal priority ranking content  
**Status:** 🧪 VERIFY / READY FOR REVIEW  
**Branch:** `360-rework/24-10-proposal-priorities`  
**GitHub PR:** #49 — `[24.10] 360 Rework — Proposal priority ranking content`  
**Implementation commit:** `4e7f8d1d89f162fbc4e657de9409546650cbf90b`

### Scope

- Added five deterministic proposal-priority items for the existing `proposal_priorities` ranking step.
- Reuses the existing `RankingSlotBoard`, which already supports empty slots, drag-and-drop, restoring answers and a clear save/continue action.
- No new UI design or image generation is needed for this narrow package.

### Verification

- Static source review completed.
- No Gradle build or long-running GitHub test was started; the repository has no Gradle wrapper/system Gradle.

### Next action

Merge after the PR is confirmed mergeable, then continue with 24.11 / Stage 02.6 partner prediction.


## 2026-08-29 — 24.11 Stage 02.6 prepared

**Work package:** 24.11 — Partner prediction rounds  
**Status:** 🧪 VERIFY / READY FOR REVIEW  
**Branch:** `360-rework/24-11-partner-prediction`  
**GitHub PR:** #50 — `[24.11] 360 Rework — Partner prediction rounds`  
**Implementation commit:** `0db95cd2cf602bbcd8094588d610c3657eeb44a9`

### Scope

- Added three deterministic A → B → Reveal prediction rounds.
- Bound them to the existing `partner_prediction` flow step.
- Reused the existing prediction UI; no new design or generated image is needed.

### Verification

- Static source review completed.
- No Gradle build or long-running GitHub test was started.

### Next action

Merge after GitHub confirms the PR is mergeable, then continue with 24.12 / Stage 02.7 proposal scenarios.

---

## 2026-08-29 — 24.14.1 Stage 02 implementation-status correction

**Work package:** 24.14.1 — Stage 02 control-state sync  
**Status:** 🟡 PREPARED FOR MERGE  
**Branch:** `360-rework/24-14-1-stage-02-status-sync`

### Why this correction exists

The central Stage-02 tracker had stopped at 02.3 even though the repository continued to receive and merge the dedicated proposal packages. Parallel work then started in Stage 05, making the stale handover especially risky for subsequent agents.

This entry does not rewrite the historical entries above. It records the authoritative correction after checking current `main`, recent commits, open PRs and active branches.

### Authoritative Stage-02 implementation state

- **02.1 / 24.2** — flow model merged via PR #27; executable verification open.
- **02.2 / 24.3** — mood/details content merged via PR #31; final runner/build/UI verification open.
- **02.3 / 24.8** — proposal-location duels merged via replacement PR #55 and routed into the `antrag` game by merge/integration commit `229dfa6ded47e59627aab1bf213fe8d7e775c375`.
- **02.4 / 24.9** — refreshed ring-duel content merged via PR #48; runner wiring remains for 02.11.
- **02.5 / 24.10** — priority-ranking content merged via PR #49; runner wiring remains for 02.11.
- **02.6 / 24.11** — partner-prediction content merged via PR #50; runner wiring remains for 02.11.
- **02.7 / 24.12** — six concrete proposal scenarios merged via PR #56, merge `123059397aa9dddd42ef39303d60414fa023c25b`; focused Kotlin contract 3/3 PASS.
- **02.8 / 24.13** — five open personal prompts merged via PR #58, merge `69bd969bacccd414f4ea8bd06ae33aa0f33f3679`; focused Kotlin contract 3/3 PASS.
- **02.9 / 24.14** — qualitative perfect-proposal reveal builder merged via PR #63, merge `c7873ad413129f9fd271a0320faa238b7d8a9091`; focused Kotlin contract 3/3 PASS.

### Accounting decision

Stage 02 remains **0/12 verified complete** because the roadmap defines completion through executable verification, and several 02.4–02.9 components still await their actual end-to-end runner wiring in 02.11. At the same time, the tracker now separately records **9/12 implementation coverage** so agents do not rebuild already-landed Stage-02 packages.

### Parallel-work observation

A separate agent has already begun Stage 05 `25.x` work, and Photo Rework PR #61 is also active. Neither track changes the Stage-02 Definition of Done. Before starting each remaining Stage-02 slice, active branches/PRs must be checked for overlap.

### Verification performed

- Fresh `main` check after PR #63: `c7873ad413129f9fd271a0320faa238b7d8a9091`.
- Open PRs and active branches inspected for Stage-02 overlap.
- No `24.14`/reveal competitor existed before PR #63; PR #63 was verified mergeable, exactly two files and 0 commits behind `main` before merge.
- Documentation sync changes project-control files only; no Android code is changed by this package.
- No GitHub Actions workflow was manually triggered.

### Next exact action

**24.15 / Stage 02.10:** inventory the existing proposal, engagement-ring and relevant wedding content, select only the strongest material that adds value beyond 02.2–02.9, and expose it for reuse by the perfect-proposal experience. Do not delete or hide legacy content yet; that belongs to Stage 04. Then proceed to 02.11 end-to-end wiring and 02.12 executable verification before moving the core-stage handover onward.
