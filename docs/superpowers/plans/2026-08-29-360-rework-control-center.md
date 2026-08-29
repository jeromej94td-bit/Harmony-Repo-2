# 360 Rework Control Center Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create the permanent 360 Rework project-control surface in Harmony-Repo-2 so any future chat/model can resume from the exact current stage without relying on conversation memory.

**Architecture:** Keep one root entry file plus three focused Markdown files under `docs/360-rework/`: a durable roadmap, a short current-state handover, and an append-only worklog. The repository is the source of truth; progress uses both global stage position (`Stage X/8`) and explicit substage completion (`x/y = z%`).

**Tech Stack:** GitHub repository Markdown, Git history, pull requests, existing Harmony-Repo-2 workflow.

**Spec:** `docs/superpowers/specs/2026-08-29-360-rework-control-center-design.md`

## Global Constraints

- Permanent project name: `360 Rework`.
- Live files: `START_HERE_360_REWORK.md`, `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`, `docs/360-rework/360_REWORK_CURRENT_STATE.md`, `docs/360-rework/360_REWORK_WORKLOG.md`.
- Stage statuses are exactly: `⬜ PLANNED`, `🔵 NEXT`, `🟡 IN PROGRESS`, `🧪 VERIFY`, `🟠 BLOCKED`, `✅ DONE`.
- Progress must show both overall stage position and completed/total substage fraction with percentage.
- A substage is complete only after its Definition of Done and verification evidence are recorded.
- Current initial state: Stage 01/08 Ring Image Quality Rework is complete; Stage 02/08 `Unser perfekter Antrag` is next at `0/12 = 0%`.
- Stage 01 evidence: PR #22, merge commit `b696acb25f8f9b52235a6fba256ec9dc041edab9`, exactly 10 ring WebPs changed; two Actions failed before executing step 1, so tests must not be described as green.
- Daily remains outside the current 360 Rework scope.

---

### Task 1: Create universal 360 Rework entry point

**Files:**
- Create: `START_HERE_360_REWORK.md`

**Interfaces:**
- Consumes: paths and operating rules from the approved design spec.
- Produces: the single file every new chat/model should read first.

- [ ] **Step 1: Create the entry file**

The file must contain:
- Project identity: `360 Rework`.
- Mandatory reading order: master roadmap → current state → newest worklog entries.
- Rule that repository state outranks chat memory.
- Rule to verify before claiming completion.
- Rule to update all three live tracking files after meaningful completed work.
- Minimal handover prompt: `Continue 360 Rework.`

- [ ] **Step 2: Verify the entry file on the branch**

Use GitHub `fetch_file` on `START_HERE_360_REWORK.md` at branch `docs/360-rework-control-center` and confirm all three live file paths and the `Continue 360 Rework.` shorthand are present.

- [ ] **Step 3: Commit via GitHub contents API**

Commit message: `docs: add 360 Rework entry point`.

### Task 2: Create the visual master stage board

**Files:**
- Create: `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`

**Interfaces:**
- Consumes: eight-stage structure and progress rules from the approved spec.
- Produces: durable global and local progress view.

- [ ] **Step 1: Create the roadmap summary table**

The table must show all eight core stages with status, stage position, completed/total substages, and percentage.

Initial values:
- Stage 01/08: `✅ DONE`, `1/1`, `100%`.
- Stage 02/08: `🔵 NEXT`, `0/12`, `0%`.
- Stages 03–08: `⬜ PLANNED`, `0/y`, `0%`, with explicit substage counts defined below.

- [ ] **Step 2: Define all stage checklists and Definitions of Done**

Required stages:
1. Ring Image Quality Rework — 1/1.
2. Unser perfekter Antrag — 12 substages.
3. Reusable Harmony Experience Engine — 7 substages: core step model, EitherOr, ImageDuel, Ranking, PartnerPrediction, Scenario/OpenPrompt, Reveal/state compatibility.
4. Existing Proposal/Ring Content Consolidation — 5 substages: inventory, migrate strong content, remove duplicate navigation presentation, archive/hide legacy packs, regression verification.
5. Harmony-360 Questions Quality Rework — 5 section groups exactly as the spec defines.
6. Broken, Duplicate and Low-Quality Content Cleanup — 5 defect groups exactly as the spec defines.
7. Additional Flagship Harmony Experiences — initially 12 candidate experiences, tracked as `0/12` until activated/refined.
8. Automated Content Quality Gate — 6 checks exactly as the spec defines.

Every stage section must show `Stage X/8`, status, `x/y`, percentage, dependencies, checklist, and Definition of Done.

- [ ] **Step 3: Verify progress arithmetic and status consistency**

Check manually from fetched Markdown:
- Stage 01 = `1/1 (100%)`.
- Stage 02 = `0/12 (0%)`.
- Stage 05 example supports `2/5 = 40%`.
- Stage 06 example supports `2/5 = 40%`.
- No stage later than 01 is marked done initially.

- [ ] **Step 4: Commit**

Commit message: `docs: add 360 Rework master roadmap`.

### Task 3: Create the fast current-state handover

**Files:**
- Create: `docs/360-rework/360_REWORK_CURRENT_STATE.md`

**Interfaces:**
- Consumes: current repo state, roadmap, merged ring-image work.
- Produces: one short operational handover for the next worker.

- [ ] **Step 1: Create required current-state fields**

Set:
- Last updated: `2026-08-29`.
- Current stage: `Stage 02/08 — Unser perfekter Antrag`.
- Current substage: `02.1 — Experience data model and proposal flow definition`.
- Stage progress: `0/12 = 0%`.
- Completed core stages: `1/8`.
- Last completed stage: `Stage 01/08 — Ring Image Quality Rework`.
- Last relevant PR: `#22 — Refresh engagement ring image assets`.
- Last merge commit: `b696acb25f8f9b52235a6fba256ec9dc041edab9`.
- Verification: 10 intended ring WebPs merged; Actions failed before step 1; do not state full test suite green.
- Current blockers: none for starting Stage 02; existing Actions infrastructure remains a verification caveat.
- `NEXT EXACT ACTION`: design/implement the reusable mixed-step data model and deterministic first flow for `Unser perfekter Antrag`, without modifying legacy navigation yet.
- `DO NOT REPEAT`: ring asset replacement, deleting legacy proposal packs, adding Daily, adding unrelated new categories.

- [ ] **Step 2: Keep the file operational and short**

Ensure history is not duplicated here; link to the worklog for historical detail.

- [ ] **Step 3: Verify against `main` and roadmap**

Fetch current state and confirm PR #22 merge SHA matches `main` history and the roadmap says Stage 02 is next.

- [ ] **Step 4: Commit**

Commit message: `docs: add 360 Rework current state`.

### Task 4: Create append-only worklog and seed verified history

**Files:**
- Create: `docs/360-rework/360_REWORK_WORKLOG.md`

**Interfaces:**
- Consumes: known verified Stage 01 history plus creation of the control center.
- Produces: append-only chronological project black box.

- [ ] **Step 1: Add worklog operating rule**

State that existing entries are never rewritten to make history cleaner; corrections are appended as new entries.

- [ ] **Step 2: Add Stage 01 ring-image entry**

Record:
- Date: `2026-08-29`.
- Stage: `01/08`.
- Change: 10 refreshed ring WebPs.
- PR: `#22`.
- Source branch: `fix/ring-image-quality`.
- Merge commit: `b696acb25f8f9b52235a6fba256ec9dc041edab9`.
- Verification performed: PR merged, changed_files = 10, ring WebPs only.
- Verification gap: two Actions failed before workflow step 1; no green-suite claim.
- Next action: establish 360 Rework control center, then Stage 02.1.

- [ ] **Step 3: Add control-center creation entry**

Record:
- Date: `2026-08-29`.
- Stage: Project Control / 360 Rework.
- Approved design spec path.
- Implementation plan path.
- Branch: `docs/360-rework-control-center`.
- Files introduced by this work package.
- Next action: merge tracking PR, then start Stage 02.1.

- [ ] **Step 4: Commit**

Commit message: `docs: add 360 Rework worklog`.

### Task 5: Cross-file consistency verification and PR

**Files:**
- Verify all four live files plus spec and implementation plan.

**Interfaces:**
- Consumes: Tasks 1–4 outputs.
- Produces: mergeable documentation change with one consistent source of truth.

- [ ] **Step 1: Fetch all four live files from the branch**

Confirm:
- all paths exist;
- project name is exactly `360 Rework`;
- Stage 02 is current everywhere;
- Stage 02 progress is `0/12 = 0%` everywhere;
- Stage 01 is done and tied to PR #22 / merge SHA;
- `NEXT EXACT ACTION` points to Stage 02.1;
- status vocabulary matches the spec.

- [ ] **Step 2: Compare branch with main**

Use GitHub compare and confirm only documentation files from this control-center work are changed.

- [ ] **Step 3: Open PR**

Title: `Add 360 Rework project control center`.

Body must summarize:
- permanent cross-chat/model handover system;
- eight-stage roadmap;
- x/y and percentage tracking;
- current state Stage 02/08 at 0/12;
- seeded verified ring-image history;
- rule that completion requires verification evidence.

- [ ] **Step 4: Verify PR metadata**

Confirm base = `main`, head = `docs/360-rework-control-center`, PR is mergeable or report the exact blocker.

- [ ] **Step 5: Merge only after user-approved execution and fresh verification**

Use the repository’s permitted merge method. After merge, fetch PR info and `main` ref and confirm the merge landed before claiming the 360 Rework control center is live on `main`.
