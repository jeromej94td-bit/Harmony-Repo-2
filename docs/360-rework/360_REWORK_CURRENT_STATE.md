# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed green core stages:** 1/8  
**Core Stage 02 status:** 🧪 IMPLEMENTATION + CONTRACT COVERAGE COMPLETE; GREEN ANDROID BUILD PENDING INFRASTRUCTURE  
**Stage-02 package coverage:** 12/12 = 100%  
**Latest Stage-02 package:** 24.17 / PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`  
**Current functional core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-03 progress after PR #99:** 4/7  
**Current Stage-03 substage after PR #99:** 03.5 — Reusable `PartnerPrediction` step  
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

PR #81 introduced:

- generic `ExperienceStepKind`, `ExperienceStep` and `ExperienceDefinition`;
- generic `ExperiencePosition` and deterministic `ExperienceNavigator`;
- fail-fast flow validation and safe invalid-position handling;
- feature-owned item-count resolution with non-positive counts normalized to one;
- `ProposalExperienceAdapter` compatibility layer;
- proposal navigation/progress delegation without moving proposal content or UI state;
- contract/parity coverage locking the existing nine proposal steps, 35 navigable positions and shipped progress ratios.

### 03.2 Reusable Either-Or step

**Status:** ✅ DONE  
**Package:** 26.2 / PR #85  
**Merge:** `4ef6d583144df4f59c59daef301ffe2596b89b7b`  

PR #85 introduced:

- reusable `ExperienceEitherOrRound` with fail-fast validation;
- a narrow proposal-to-generic adapter so proposal content stays proposal-owned;
- stateless `ExperienceEitherOrBoard` with caller-owned selection state;
- explicit first/second choice tags and selection semantics;
- proposal integration by replacing the old private Either-Or pane with the generic board;
- model and Compose contracts while preserving the existing 11 proposal Either-Or rounds, text, order and direct advance behavior.

### 03.3 Reusable ImageDuel step

**Status:** ✅ DONE  
**Package:** 26.3 / PR #87  
**Merge:** `704fa2a14732ed8141abb7d98da2018d27853aeb`  

PR #87 introduced:

- reusable `ExperienceImageDuelOption` and `ExperienceImageDuelRound` with fail-fast validation;
- narrow proposal adapters for both location duels and ring duels;
- stateless `ExperienceImageDuelBoard` plus `AnimatedExperienceImageDuelBoard`;
- the shipped proposal-location choreography moved into the generic board: cards first, question after 620 ms, selection lock, then 420 ms hold + 760 ms 3D transition;
- `ProposalLocationDuelBoard` retained as a compatibility wrapper with the existing `proposal_location_*` tags and callbacks;
- proposal ring rounds now use the same generic board while preserving existing drawable keys and saved asset-key answers;
- the old private proposal ring-card renderer was removed.

TDD/verification evidence for 26.3: generic model contracts landed before production code; focused Kotlin RED compilation failed on the absent generic image-duel types, followed by a passing isolated Kotlin model/adapter harness. Existing proposal-location tests continue to specify legacy tags, timing, selection-lock and the three-round sequence. A new Robolectric Compose contract covers generic rendering, stable tags and the pick callback. GitHub reported the final PR conflict-free and 0 commits behind `main`; no green full Android/Gradle/Robolectric run is claimed because repository CI has no runnable status checks/credits.

### 03.4 Reusable Ranking step

**Status:** ✅ DONE  
**Package:** 26.4 / PR #99  
**Merge:** `958ba77670958d34ff2eecb581ab159114e310bd`  
**Stage-03 progress:** **4/7**

PR #99 introduced:

- reusable `ExperienceRankingItem` and validated `ExperienceRankingRound` with stable item IDs;
- `ExperienceRankingSelectionCodec` as the compatibility bridge to the existing label-based `RankingAnswerCodec`;
- the existing persisted ranking payload format remains unchanged and decodable;
- stateless `ExperienceRankingBoard` wrapping the shipped drag/drop `RankingSlotBoard` instead of copying its gestures or layout;
- proposal priorities now use the generic ranking board while preserving the same five priority IDs, labels, order semantics and reveal input;
- a dedicated wrapper test tag plus existing `ranking_slot_*` tags remain available for UI contracts.

TDD/verification evidence for 26.4: RED isolated Kotlin compilation confirmed the generic ranking API was absent; GREEN isolated Kotlin model/adapter verification produced `EXPERIENCE_RANKING_MODEL_PASS`. A Robolectric/Compose contract for the wrapper is present but was not executed here because repository GitHub Actions/credits are unavailable. No green full Android/Gradle build is claimed.

### NEXT EXACT ACTION — core

Begin **03.5 — Reusable `PartnerPrediction` step** as its own narrow 26.x package. Preserve the existing A predicts B → B answers → reveal semantics and existing encoded answers while moving only the mechanic into the reusable experience layer. Do not pull Scenario, OpenPrompt or Reveal into 03.5.

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

Repository-wide GitHub Actions continue to terminate before executable workflow step 1 or expose no runnable status checks in affected runs, so no green full Android/Gradle result is claimed from those failures. Focused local/static/contract evidence is recorded per narrow package and completed implementation is not left unmerged solely because CI credits/runners are unavailable.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not combine unrelated substages in one PR.
- Do not overwrite newer main changes while reconciling tracker work.
- Do not count unrelated content reworks toward Stage-03 engine progress.
- Do not reopen Stage 02 feature implementation unless a regression is demonstrated.
- Do not claim a green Android build until one actually executes.
