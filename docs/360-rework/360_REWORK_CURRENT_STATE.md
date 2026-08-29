# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed core stages:** 1/8  
**Current stage:** Stage 02/08 — 💍 Unser perfekter Antrag  
**Current substage:** 02.1 — Experience data model and deterministic proposal flow definition  
**Current stage progress:** 0/12 complete (0%)  
**Current status:** 🔵 NEXT

## Last completed work

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

## NEXT EXACT ACTION

**Start Stage 02.1:** inspect the current Harmony runner/content models and define the reusable mixed-step data model plus a deterministic first proposal flow. Do not modify legacy proposal navigation or delete old packs in this substage.

Expected handover after 02.1 is completed and verified:

> `360 Rework → Stage 02/08 → 1/12 complete (8.3%)`

Then update this file, the master roadmap, and the worklog.

## Current blockers / verification caveats

- No known blocker prevents starting Stage 02.1.
- Existing GitHub Actions infrastructure has recently produced failures before workflow step 1. Treat this as a verification caveat; do not silently convert it into a code failure or a green-test claim.
- Before Stage 02 implementation, inspect the actual current runner/model files rather than assuming the planned mechanic interfaces already exist.

## DO NOT REPEAT

- Do not replace the ten refreshed ring assets again unless a regression is proven.
- Do not delete existing proposal/ring/wedding source content yet.
- Do not hide all legacy proposal packs during Stage 02.1; full consolidation belongs to Stage 04/08.
- Do not add Daily to this 360 Rework scope.
- Do not add unrelated new categories while the quality/experience rework is in progress.
- Do not mark Stage 02 progress based on design discussion alone; only verified implementation substages count.

## Files / areas likely relevant next

The next worker must verify exact current paths in the repo before editing, but the known Harmony content/runtime areas include:

- content/question models and pack definitions
- generated Harmony content registries
- game runner / question rendering flow
- image duel / Das-oder-Das rendering
- ranking mechanics
- partner-prediction mechanics, if any exist
- result/reveal state

## Handover rule

For historical detail, read `docs/360-rework/360_REWORK_WORKLOG.md`.

For the full plan and Definitions of Done, read `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`.
