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

- [x] **Step 1: Create the entry file**

The file must contain:
- Project identity: `360 Rework`.
- Mandatory reading order: master roadmap → current state → newest worklog entries.
- Rule that repository state outranks chat memory.
- Rule to verify before claiming completion.
- Rule to update all three live tracking files after meaningful completed work.
- Minimal handover prompt: `Continue 360 Rework.`

- [x] **Step 2: Verify the entry file on the branch**

Use GitHub `fetch_file` on `START_HERE_360_REWORK.md` and confirm all three live file paths and the `Continue 360 Rework.` shorthand are present.

- [x] **Step 3: Commit via GitHub contents API**

Implemented on the documentation integration branch.

### Task 2: Create the visual master stage board

**Files:**
- Create: `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`

**Interfaces:**
- Consumes: eight-stage structure and progress rules from the approved spec.
- Produces: durable global and local progress view.

- [x] **Step 1: Create the roadmap summary table**
- [x] **Step 2: Define all stage checklists and Definitions of Done**
- [x] **Step 3: Verify progress arithmetic and status consistency**
- [x] **Step 4: Commit**

Verified initial values include Stage 01 = `1/1 (100%)`, Stage 02 = `0/12 (0%)`, and the explicit `2/5 = 40%` examples for Stages 05 and 06.

### Task 3: Create the fast current-state handover

**Files:**
- Create: `docs/360-rework/360_REWORK_CURRENT_STATE.md`

- [x] **Step 1: Create required current-state fields**
- [x] **Step 2: Keep the file operational and short**
- [x] **Step 3: Verify against roadmap and known main history**
- [x] **Step 4: Commit**

The current handover points to `Stage 02/08`, substage `02.1`, with `0/12 = 0%` and Stage 01 tied to PR #22 / merge commit `b696acb25f8f9b52235a6fba256ec9dc041edab9`.

### Task 4: Create append-only worklog and seed verified history

**Files:**
- Create: `docs/360-rework/360_REWORK_WORKLOG.md`

- [x] **Step 1: Add worklog operating rule**
- [x] **Step 2: Add Stage 01 ring-image entry**
- [x] **Step 3: Add control-center creation entry**
- [x] **Step 4: Commit**

The worklog records the ring-image verification gap and the later rebase-style rebuild onto the newer mainline without force-moving the original branch.

### Task 5: Cross-file consistency verification and PR

- [x] **Step 1: Fetch all four live files from the branch**

Confirmed project name, Stage 02, `0/12`, `1/8` completed core stages, Stage 01 ring history, and `02.1` next action are consistent.

- [x] **Step 2: Compare branch with main**

The first documentation branch was found to be three commits behind current `main`; integration was therefore rebuilt onto the newer mainline rather than forcing or risking unrelated changes.

- [ ] **Step 3: Open PR**
- [ ] **Step 4: Verify PR metadata**
- [ ] **Step 5: Merge after fresh verification**
