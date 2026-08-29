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
