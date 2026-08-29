# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed green core stages:** 1/8  
**Core Stage 02 status:** 🧪 IMPLEMENTATION + CONTRACT COVERAGE COMPLETE; GREEN ANDROID BUILD PENDING INFRASTRUCTURE  
**Stage-02 package coverage:** 12/12 = 100%  
**Latest Stage-02 package:** 24.17 / PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`  
**Current functional core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-03 progress after PR #84:** 2/7  
**Current Stage-03 substage after PR #84:** 03.3 — Reusable `ImageDuel` step  
**Stage-05 progress:** 1/5 = 20%  
**Stage 05.1:** ✅ 7/7 = 100%  
**Next Stage-05 substage:** 05.2 — Food / travel / leisure / culture

## Source-of-truth rule

PR #24 remains an **umbrella/reference PR only** and must never be merged wholesale. `24.x`, `25.x` and `26.x` are logical 360 Rework work-package IDs; real GitHub PR numbers remain integers.

Parallel branches count only after they reach `main`. Narrow packages must preserve all newer main changes and may not bundle unrelated runtime work.

## Stage 02 — latest authoritative state

All twelve Stage-02 implementation/contract slices are on `main`:

- 02.1 experience model / deterministic flow — PR #27.
- 02.2 mood/details — PR #31.
- 02.3 proposal-location image duels — PR #55 plus app routing.
- 02.4 ring-image duels — PR #48.
- 02.5 priority ranking — PR #49.
- 02.6 partner prediction — PR #50.
- 02.7 proposal scenarios — PR #56.
- 02.8 personal open prompts — PR #58.
- 02.9 qualitative reveal — PR #63.
- 02.10 legacy proposal/ring/wedding reuse audit — PR #68.
- 02.11 complete fullscreen runner + `antrag` entry routing — PR #77, merge `82c951c53e42f4d6a76a61ee3596d4e36ef4dd05`.
- 02.12 final verification contracts — PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`.

PR #79 adds UI/Robolectric, ring-asset and 35-subround end-to-end contracts and records the deterministic path to `ProposalReveal`. It explicitly does **not** provide a green Android/Gradle build because repository Actions still fail before executable step 1. A later real Android build remains an infrastructure verification check, not another feature slice.

## Stage 03 — Reusable Harmony Experience Engine

### 03.1 Generic mixed-step experience foundation

**Status:** ✅ DONE  
**Package:** 26.1 / PR #81  

PR #81 introduced generic `ExperienceStepKind`, `ExperienceStep`, `ExperienceDefinition`, `ExperiencePosition` and `ExperienceNavigator`, plus the Proposal compatibility bridge. The shipped proposal journey remains nine steps / 35 positions with locked progress-ratio parity.

### 03.2 Reusable EitherOr step

**Status after PR #84 merges:** ✅ DONE  
**Progress:** **2/7**

PR #84 / work package 26.2 introduces:

- generic `EitherOrRound` with stable IDs, prompt and exactly two distinct nonblank choices;
- immutable ordered `EitherOrStepContent` with safe indexed lookup and item count;
- `ProposalEitherOrAdapter` mapping the shipped Proposal Either-Or content into the generic mechanic model;
- proposal item-count resolution for Mood/Details through the reusable adapter;
- parity coverage locking both Proposal Either-Or steps at 5 + 6 rounds and preserving every ID, prompt and answer pair;
- focused Kotlin verification confirming the complete Proposal journey stays at 35 positions with the existing progress ratios.

The Proposal Compose screen, copy, images, catalog routing, persistence and all non-EitherOr mechanics remain unchanged by 26.2.

### NEXT EXACT ACTION — core

Begin **03.3 — Reusable `ImageDuel` step** as its own narrow 26.x package. Reuse the generic Stage-03 core and follow the same compatibility pattern for Proposal location/ring image duels without broadening into Ranking, Prediction or a generic full renderer.

## Parallel Stage 05 — Questions Quality Rework

### 05.1 Relationship / communication / everyday-life

**Status:** ✅ DONE  
**Progress:** **7/7 = 100%**  
**Stage-05 overall:** **1/5 = 20%**

Scope: Sections 01, 02, 06 and 12.

- 72 raw packs.
- 72/72 explicit Keep/Rewrite/Archive decisions.
- Curated section counts: 16 / 17 / 16 / 17 = 66.
- Six redundant/filler packs archived from the curated target output.
- One additional Quick Game `h360_need_now_quick`, exactly 10 situations × 2 choices.
- Final scoped target: 67 unique packs with canonical packs retained.
- `NormensLoeschungen` currently targets only `h500_430_team_zukunft_offene_runde`, outside 05.1.

Package trail: 25.0 PR #59, 25.1 PR #60, 25.2 PR #62, 25.3 PR #64, 25.4 PR #69, 25.5 PR #70, 25.6 PR #71, 25.7 PR #76.

The detailed 25.x ledger is `docs/360-rework/360_REWORK_STAGE05_WORKLOG.md`.

### NEXT EXACT ACTION — Stage 05

Start **05.2 — Food / travel / leisure / culture** under a new narrow 25.x range. Do not reopen 05.1 unless a regression is proven.

## Newer parallel main changes preserved

The separately merged **Sex & Intimität** rework at `c15a18488f2eb269aa03a3620c1c569f316d6be2` remains preserved and is not retroactively counted as completion of 05.4.

Open parallel Question Rework work remains outside 360-Rework progress until merged and explicitly reconciled.

## Stage 06

Stage 06 remains 0/5 complete. Existing targeted cleanup packages are partial repairs, not completed defect classes.

## Verification caveat

Repository-wide GitHub Actions continue to terminate before executable workflow step 1 in affected runs, so no green full Android/Gradle result is claimed from those failures. Focused local/static/contract evidence is recorded per narrow package and completed implementation is not left unmerged solely because CI credits/runners are unavailable.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not combine unrelated substages in one PR.
- Do not overwrite newer main changes while reconciling tracker work.
- Do not count unrelated content reworks toward Stage-03 engine progress.
- Do not reopen Stage 02 feature implementation unless a regression is demonstrated.
- Do not claim a green Android build until one actually executes.
