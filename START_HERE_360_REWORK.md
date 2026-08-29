# 360 Rework — START HERE

This file is the permanent entry point for every new ChatGPT chat, Codex session, model, device, or later continuation of the Harmony **360 Rework**.

## Source of truth

The repository is authoritative. Chat memory is not.

Before changing code, content, assets, roadmap status, or claiming something is finished, read these files in this exact order:

1. `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`
2. `docs/360-rework/360_REWORK_CURRENT_STATE.md`
3. the newest entries in `docs/360-rework/360_REWORK_WORKLOG.md`

Then verify the relevant repository state before starting work.

## How progress is expressed

Every active work package must be identifiable in three dimensions:

- **Overall stage position:** e.g. `Stage 06/08`
- **Progress inside that stage:** e.g. `2/5 complete = 40%`
- **Small merge package ID:** e.g. `24.7`

Example handover:

> `360 Rework → Stage 06/08 → 2/5 complete (40%) → next package 24.7`

A stage or substage is not complete just because code was written. Its Definition of Done and verification requirements in the master roadmap must be satisfied.

## Small-PR / work-package policy

**Never merge the whole 360 Rework as one large PR.**

PR #24 is the umbrella/reference PR and must not be merged wholesale. GitHub PR numbers are integers, so identifiers such as `24.1`, `24.2`, `24.3` are **360 Rework work-package IDs**, not literal GitHub PR numbers.

For every meaningful isolated change:

1. Assign the next `24.x` work-package ID.
2. Create a dedicated branch, preferably `360-rework/24-x-short-name`.
3. Keep the diff limited to one coherent substage or small change.
4. Verify that package independently.
5. Open its own GitHub PR with the `24.x` ID in the title.
6. Merge only that small PR into `main` when ready.
7. Record the real GitHub PR number, work-package ID, commit, verification and next package in the worklog.
8. Update roadmap/current state in the same package when practical so `main` remains an accurate handover point.

Do not bundle several unrelated substages just because they belong to the same stage. The goal is that a regression can be traced and reverted to one small merge.

## Allowed status values

- `⬜ PLANNED`
- `🔵 NEXT`
- `🟡 IN PROGRESS`
- `🧪 VERIFY`
- `🟠 BLOCKED`
- `✅ DONE`

## Operating rules for every worker

1. Never infer project progress from conversation memory alone.
2. Never redo completed work unless repository evidence shows a regression or the roadmap explicitly reopens it.
3. Verify repository state before saying a task, stage, build, test, PR, or merge is complete.
4. After every meaningful completed work package, update:
   - `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`
   - `docs/360-rework/360_REWORK_CURRENT_STATE.md`
   - `docs/360-rework/360_REWORK_WORKLOG.md`
5. Record work-package ID, real GitHub PR number, branch, commit/merge commit, tests/build verification, known gaps, blockers, and exact next action.
6. Keep `CURRENT_STATE` short and operational. Put history in `WORKLOG`.
7. Do not silently change the master plan. Record scope changes and decisions in the worklog.
8. Do not mark a substage complete while verification is still missing; use `🧪 VERIFY` or `🟠 BLOCKED` instead.
9. Preserve strong existing Harmony content where possible; do not delete legacy packs merely because a replacement is planned.
10. Daily is outside the current 360 Rework scope unless the roadmap is explicitly amended.

## Minimal continuation prompt

A user can start a fresh chat/model with only:

> **Continue 360 Rework.**

The worker should then find this file, read the three live control files, verify the repo, and follow `NEXT EXACT ACTION` plus the next `24.x` work-package ID.
