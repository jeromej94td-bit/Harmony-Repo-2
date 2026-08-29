# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed core stages:** 1/8  
**Current stage:** Stage 02/08 — 💍 Unser perfekter Antrag  
**Current substage:** 02.1 — Experience data model and deterministic proposal flow definition  
**Current stage progress:** 0/12 complete (0%)  
**Current status:** 🧪 VERIFY<br>
**Latest fully verified work package:** ✅ 24.1 — Control Center Foundation<br>
**GitHub PR:** #25  
**Merge commit:** `c0479277ab8b53c888a4c5bf2fb35d827230c6cc`  
**Next work package:** 24.2 — Stage 02.1 Experience data model / proposal flow

## Merge strategy

PR #24 is an **umbrella/reference PR only** and must not be merged wholesale.

`24.1`, `24.2`, `24.3`, etc. are logical **360 Rework work-package IDs**. GitHub itself assigns a separate integer PR number to each package.

Every package must be narrow, independently verifiable, independently mergeable, and independently traceable/revertible. A finished package is merged into `main` before the next risky package is treated as complete.

## Last completed 360 Rework package

**Work package:** 24.1 — Control Center Foundation  
**Status:** ✅ MERGED  
**GitHub PR:** #25 — `[24.1] 360 Rework — Control Center Foundation`  
**Merge commit:** `c0479277ab8b53c888a4c5bf2fb35d827230c6cc`  
**Merged scope:** exactly 4 documentation files; no app code, questions, images, mechanics or navigation changes.

Verification facts:

- PR #25 was merged into `main`.
- The PR changed exactly 4 documentation files.
- No product-code file was part of the merge.
- The repo-wide GitHub Action failed before the first executable workflow step (`steps: null`).
- Therefore this remains an infrastructure verification caveat, not a recorded application-code failure and not a green-suite claim.

## Last completed product work

**Stage:** 01/08 — Ring Image Quality Rework  
**Status:** ✅ DONE  
**PR:** #22 — `Refresh engagement ring image assets`  
**Merge commit:** `b696acb25f8f9b52235a6fba256ec9dc041edab9`

Verified facts:

- PR #22 is merged into `main`.
- The merged change contains exactly 10 changed files.
- Those 10 files are the intended engagement-ring WebPs.
- Two GitHub Actions jobs failed before executing their first workflow step.
- Therefore: **do not claim the full test suite was green for Stage 01.**

## Current objective

Build `Unser perfekter Antrag` as the first flagship mixed-mechanic Harmony Experience and reference implementation for the later reusable Experience system.

The intended experience includes:

- proposal mood/details Das-oder-Das rounds
- proposal-location image duels
- refreshed engagement-ring image duels
- drag-and-drop ranking
- partner prediction A → B → Reveal
- concrete proposal scenarios
- open personal prompts
- qualitative `Euer perfekter Antrag` reveal

These are **not** to be delivered in one PR. They are split into small `24.x` packages aligned with substages or another similarly narrow coherent change.

## Active work package

**Work package:** 24.2 — Stage 02.1 Experience data model / proposal flow<br>
**Branch:** `360-rework/24-2-proposal-flow-model`<br>
**GitHub PR:** #27 — `[24.2] 360 Rework — Deterministic proposal flow model`<br>
**Commit:** `9984b449ae9c6325396d1f247e46564b26011c7f`<br>
**Implemented scope:** `ProposalExperienceDefinition.kt` defines the UI-independent proposal step contract, validates stable flow IDs and reveal placement, and records the deterministic Stage 02 sequence.<br>
**Deliberately excluded:** UI, navigation, content/mechanic implementation, legacy migration and deletion.<br>
**Verification status:** whitespace/diff validation passed. No tests were added or run by explicit instruction. A local Android build could not start because this repository checkout has no Gradle Wrapper and no system Gradle executable is installed.

## NEXT EXACT ACTION

**Work package 24.2 / Stage 02.1:** run the focused build in an environment that provides the project's Gradle Wrapper or a compatible Gradle installation. Only after that verification succeeds may 02.1 be marked complete and the next work package 24.3 started.

Do **not** include 02.2 Das-oder-Das UI, ring duels, ranking, partner prediction, legacy navigation cleanup, or other later substages in package 24.2.

Expected handover after 02.1 is completed, independently verified and merged:

> `360 Rework → Stage 02/08 → 1/12 complete (8.3%) → next package 24.3`

Then update this file, the master roadmap, and the worklog in the relevant small package.

## Current blockers / verification caveats

- No known product-code blocker prevents Stage 02.1 verification.
- The local checkout exposes no `gradlew` / `gradlew.bat`, and this environment has no `gradle` executable. This prevents local build verification without being evidence of an application-code failure.
- Existing GitHub Actions infrastructure has recently produced failures before workflow step 1. Treat this as a verification caveat; do not silently convert it into an application-code failure or a green-test claim.
- Before Stage 02 implementation, inspect the actual current runner/model files rather than assuming the planned mechanic interfaces already exist.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not combine several unrelated 360 Rework substages into one PR.
- Do not rebuild or re-merge 24.1; it is already in `main` via PR #25.
- Do not replace the ten refreshed ring assets again unless a regression is proven.
- Do not delete existing proposal/ring/wedding source content yet.
- Do not hide all legacy proposal packs during Stage 02.1; full consolidation belongs to Stage 04/08.
- Do not add Daily to this 360 Rework scope.
- Do not add unrelated new categories while the quality/experience rework is in progress.
- Do not mark Stage 02 progress based on design discussion alone; only verified implementation substages count.

## Handover rule

For historical detail, read `docs/360-rework/360_REWORK_WORKLOG.md`.

For the full stage plan and Definitions of Done, read `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`.
