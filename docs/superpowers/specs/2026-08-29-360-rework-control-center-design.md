# 360 Rework – Control Center Design

## Purpose

`360 Rework` is the permanent project-control system for the Harmony content and experience overhaul. Its job is to make the project resumable from any ChatGPT chat, Codex session, model, device, or later date without relying on conversation memory.

The repository, not a chat transcript, is the source of truth.

## Goals

1. A new agent can understand the current 360 Rework state in under two minutes.
2. Every major work package has a visible stage, substage count, percentage, status, definition of done, and exact next action.
3. Completed work is tied to concrete PRs, commits, verification results, and known limitations.
4. Interrupted work can be resumed without guessing what was already done.
5. Progress is visible both globally and inside each stage, e.g. `Stage 05/08` and `2/5 complete = 40%`.
6. No item may be marked done solely because code was written; it must satisfy its definition of done and record verification evidence.

## Naming

The permanent project name is exactly:

**360 Rework**

Recommended identifiers:

- Human-readable: `360 Rework`
- Markdown heading prefix: `360 REWORK`
- Branch prefix when useful: `360-rework/...`
- Status references: `360 Rework → Stage 05 → 2/5`

## Repository Structure

The live project-control files will be:

```text
START_HERE_360_REWORK.md
docs/360-rework/
├── 360_REWORK_MASTER_ROADMAP.md
├── 360_REWORK_CURRENT_STATE.md
└── 360_REWORK_WORKLOG.md
```

The design specification lives separately under `docs/superpowers/specs/` and is not part of the day-to-day handover surface.

## File Responsibilities

### 1. `START_HERE_360_REWORK.md`

The universal entry point for a new model or chat.

It must instruct the worker to read, in this order:

1. `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`
2. `docs/360-rework/360_REWORK_CURRENT_STATE.md`
3. the newest entries in `docs/360-rework/360_REWORK_WORKLOG.md`

It also establishes these operating rules:

- Never infer progress from chat memory alone.
- Verify repository state before claiming work is complete.
- Do not repeat already-completed work unless a regression is proven.
- Update the 360 Rework status files after every meaningful completed work package.
- Record PR/commit IDs, tests, build state, blockers, and the exact next action.
- Keep `CURRENT_STATE` short and current; move history into `WORKLOG`.
- Do not silently change the master plan. Any scope change must be recorded in the worklog.

### 2. `360_REWORK_MASTER_ROADMAP.md`

The durable visual stage board.

Each stage must contain:

- Stage number and title
- Status
- Stage position, e.g. `Stage 05/08`
- Substage progress, e.g. `2/5 complete`
- Percentage
- Checklist of substages
- Definition of Done
- Dependencies
- Notes / blockers when applicable

Allowed status values:

- `⬜ PLANNED`
- `🔵 NEXT`
- `🟡 IN PROGRESS`
- `🧪 VERIFY`
- `🟠 BLOCKED`
- `✅ DONE`

The master board must show both global position and local progress. Example:

```text
Stage 05/08 — Harmony-360 Questions Quality Rework
Status: 🟡 IN PROGRESS
Progress: 2/5 complete (40%)
Overall plan position: Stage 5 of 8
Completed core stages: 4/8
```

### 3. `360_REWORK_CURRENT_STATE.md`

The fast handover file. It should normally remain short enough to scan in 1–3 pages.

Required fields:

- Last updated date
- Current stage
- Current substage
- Current progress fraction and percentage
- Overall project stage position
- Completed core-stage count
- Last completed stage/substage
- Last relevant PR
- Last relevant commit
- Verification state
- Current blockers
- `NEXT EXACT ACTION`
- `DO NOT REPEAT`
- Files/areas currently being worked on

The wording must be operational, not narrative.

### 4. `360_REWORK_WORKLOG.md`

The append-only project black box.

Each meaningful work package gets a dated entry with:

- Stage/substage
- What was changed
- Why it was changed
- Files or areas touched
- Branch
- PR
- Commit / merge commit
- Verification performed
- Verification not performed
- Known issues/blockers
- Decision changes
- Next action

Old entries are not rewritten to make history look cleaner. Corrections are added as new entries.

## Progress Rules

### Stage-level progress

The overall plan uses eight core stages. Two values are shown:

- `Current stage position`, e.g. `Stage 5/8`
- `Completed core stages`, e.g. `4/8`

The current position is descriptive. The completed-stage count tells how many entire core stages have actually met their Definition of Done. We do not pretend that every stage has identical effort.

### Substage progress

Each stage has explicit substages. The percentage is calculated from completed substages only:

`completed substages / total substages × 100`

Examples:

- `0/5 = 0%`
- `1/5 = 20%`
- `2/5 = 40%`
- `4/5 = 80%`
- `5/5 = 100%`

A partially coded substage remains incomplete until its Definition of Done is met.

### Status transitions

Normal flow:

`⬜ PLANNED → 🔵 NEXT → 🟡 IN PROGRESS → 🧪 VERIFY → ✅ DONE`

A stage or substage can become `🟠 BLOCKED` from any non-done state. The blocker must be recorded in `CURRENT_STATE` and `WORKLOG`.

## Initial 360 Rework Roadmap

### Stage 01/08 — Ring Image Quality Rework

Initial status: `✅ DONE`

Purpose: replace the ten prioritized engagement-ring WebP assets with the refreshed versions while preserving the existing image-precedence behavior.

Evidence already available:

- PR #22: `Refresh engagement ring image assets`
- 10 changed files, all ring WebPs
- Merge commit: `b696acb25f8f9b52235a6fba256ec9dc041edab9`
- Two GitHub Actions jobs failed before executing their first workflow step; therefore the history must not state that the full test suite was green for this change.

Definition of Done: the ten intended ring assets are present on `main` and no unrelated files are part of the merged diff.

### Stage 02/08 — Unser perfekter Antrag

Initial status: `🔵 NEXT`

Purpose: build the first flagship Harmony Experience as the reference implementation for mixed-mechanic experiences.

Substages:

1. Experience data model and proposal flow definition
2. Das-oder-Das rounds for proposal mood/details
3. Proposal-location image duels
4. Refreshed ring-image duels integrated into the experience
5. Drag-and-drop ranking
6. Partner prediction A → B → Reveal
7. Scenario rounds
8. Open personal prompts
9. Final `Euer perfekter Antrag` qualitative reveal
10. Reuse the necessary strong proposal/ring/wedding content inside the new experience
11. Integrate the experience into normal Harmony navigation without deleting legacy packs
12. Tests/build/UI verification

Progress at creation: `0/12 = 0%`

Definition of Done: the new proposal experience is reachable in normal navigation, its mixed mechanics work as designed, the refreshed ring assets are used, verification evidence is recorded, and legacy packs remain available for safe follow-up consolidation.

### Stage 03/08 — Reusable Harmony Experience Engine

Initial status: `⬜ PLANNED`

Purpose: generalize the proven proposal implementation into a reusable experience layer rather than creating future experiences as one-off special cases.

Target mechanics include:

- EitherOr
- ImageDuel
- Ranking
- PartnerPrediction
- Scenario
- OpenPrompt
- Reveal

The design should preserve compatibility with existing content during migration.

Definition of Done: the proposal uses the reusable abstractions and at least one additional test fixture or minimal second flow proves that the mechanics are not proposal-specific.

### Stage 04/08 — Legacy Proposal/Ring Content Consolidation

Initial status: `⬜ PLANNED`

Purpose: clean up duplication only after the new experience and reusable layer are stable.

Substages:

1. Inventory legacy proposal/ring/wedding packs against content already reused by the new experience
2. Mark redundant standalone packs hidden/archive-first in normal navigation
3. Preserve unique strong questions/assets not yet migrated
4. Verify no useful content was lost and rollback remains possible
5. Decide, with evidence, whether any obsolete legacy content can later be deleted

This stage does not rebuild the proposal experience. It only consolidates the old presentation safely.

### Stage 05/08 — Harmony-360 Questions Quality Rework

Initial status: `⬜ PLANNED`

Purpose: replace template-generated pseudo-variety with authored, relationship-relevant interactions. This stage is about rewriting/curating the substance of questions, not merely fixing malformed data.

Substage groups:

1. Relationship / communication / everyday-life sections
2. Food / travel / leisure / culture sections
3. Future / money / work / family sections
4. Psychology / feelings / health / intimacy sections
5. Values / belief / society / humor / fantasy / teamwork sections

Example during execution: `Stage 05/08 — 2/5 complete (40%)`.

### Stage 06/08 — Broken, Duplicate and Low-Quality Content Cleanup

Initial status: `⬜ PLANNED`

Purpose: perform the mechanical and defect-focused cleanup that is distinct from Stage 05's authored rewrites.

Substages:

1. Missing answer options and malformed questions
2. English leftovers and language mismatches
3. Typos and awkward translations
4. Semantic duplicates / repeated question stems / repeated answer quartets
5. Brand/franchise cleanup where IP-neutral replacements are preferred

Example status: `Stage 06/08 — 2/5 complete (40%)`.

### Stage 07/08 — Additional Flagship Harmony Experiences

Initial status: `⬜ PLANNED`

Candidate experiences:

- Unser Zuhause
- Unsere Traumreise
- So lieben wir
- Wie wir miteinander reden
- Wenn wir uns streiten
- Unsere Familie
- Unsere Zukunft
- Unser Umgang mit Geld
- Unsere Intimität
- Was wäre wenn?
- Wir als Team
- Unser Humor

Each experience must receive its own substage count when activated, rather than being marked done as one bulk item.

### Stage 08/08 — Automated Content Quality Gate

Initial status: `⬜ PLANNED`

Purpose: add automated checks so the same quality problems do not return.

Initial checks:

1. Repeated stems/templates
2. Repeated generic option quartets
3. English leftovers in German content
4. Missing options / malformed items
5. Tiny or suspiciously thin packs
6. Report usable in CI or local verification

## Current State at System Creation

At the time this control system is introduced:

- Stage 01 is complete and merged.
- Completed core stages: `1/8`.
- Stage 02 is the next active implementation target.
- Stage 02 progress is `0/12 = 0%` because its architecture/content was discussed but not yet implemented.
- The exact next planned substage is `02.1 — Experience data model and proposal flow definition`.
- No later stage should be treated as completed merely because some isolated code or content exists.
- The ring-quality PR is the most recent completed 360 Rework work package.

## Update Protocol After Every Work Package

The worker finishing a meaningful substage must perform all of the following before handing off:

1. Verify the actual repository state.
2. Update the relevant substage checkbox and fraction in `MASTER_ROADMAP`.
3. Update `CURRENT_STATE` with the exact active stage/substage and next action.
4. Append a dated `WORKLOG` entry.
5. Record branch, PR, commit/merge commit, tests/builds, and any verification gaps.
6. Record blockers rather than hiding them.
7. Keep the next action concrete enough that a new model can execute it without reconstructing the prior chat.

## Handover Prompt

The preferred minimal prompt for a new chat/model is:

> Read `START_HERE_360_REWORK.md` in Harmony-Repo-2, then follow the current stage and `NEXT EXACT ACTION`. Verify the repo before changing status.

An even shorter user shorthand can be:

> Continue 360 Rework.

A model using the shorthand should locate `START_HERE_360_REWORK.md` and follow the same protocol.

## Non-Goals

- This system does not replace GitHub history, PRs, or tests.
- It does not auto-mark work complete based on commits.
- It does not require every tiny edit to become a new stage.
- It does not make chat memory authoritative.
- It does not reintroduce Daily into the current 360 Rework scope.

## Success Criteria

The control center is successful when:

1. The four live files exist on `main`.
2. A new agent can identify the current stage, fraction, percentage, completed-stage count, last completed work, blockers, and exact next action without prior conversation context.
3. Stage 01 correctly records the ring-image merge and its verification limitation.
4. Stage 02 is visibly the next target at `0/12` with `02.1` as the next exact substage until implementation begins.
5. Every later work package updates roadmap, current state, and worklog before handoff.
6. The user can ask "Wo stehen wir beim 360 Rework?" and receive a repo-grounded answer such as `Stage 05/08 — 2/5 complete (40%)`.
