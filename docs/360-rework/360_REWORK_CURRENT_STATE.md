# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed green core stages:** 1/8  
**Core Stage 02 status:** 🧪 IMPLEMENTATION + CONTRACT COVERAGE COMPLETE; GREEN ANDROID BUILD PENDING INFRASTRUCTURE  
**Stage-02 package coverage:** 12/12 = 100%  
**Latest Stage-02 package:** 24.17 / PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`  
**Next functional core development:** Stage 03/08 — Reusable Harmony Experience Engine  
**Parallel Stage-05 progress when PR #76 lands:** 1/5 = 20%  
**Stage 05.1 when PR #76 lands:** ✅ 7/7 = 100%  
**Next Stage-05 substage:** 05.2 — Food / travel / leisure / culture

## Source-of-truth rule

PR #24 remains an **umbrella/reference PR only** and must never be merged wholesale. `24.x` and `25.x` are logical 360 Rework work-package IDs; real GitHub PR numbers remain integers.

Parallel branches count only after they reach `main`. PR #76 must preserve all newer main changes and may not bundle unrelated runtime work.

## Stage 02 — latest authoritative state

All twelve Stage-02 implementation/contract slices are now on `main`:

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

PR #79 adds UI/Robolectric, ring-asset and 35-subround end-to-end contracts and records the deterministic path to `ProposalReveal`. It explicitly does **not** provide a green Android/Gradle build because repository Actions still fail before executable step 1. The later real Android build is therefore tracked as an infrastructure verification check, not another feature slice.

### NEXT EXACT ACTION — core

Begin **Stage 03 — Reusable Harmony Experience Engine** only as its own small package sequence, while retaining a separate infrastructure note to execute the Android/Gradle suite when runners work again. Do not reopen Stage 02 feature implementation unless a regression is demonstrated.

## Parallel Stage 05 — Questions Quality Rework

### 05.1 Relationship / communication / everyday-life

**Status when PR #76 lands:** ✅ DONE  
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

After PR #76 reaches `main`, start **05.2 — Food / travel / leisure / culture** under a new narrow 25.x range. Do not reopen 05.1 unless a regression is proven.

## Newer parallel main changes preserved

The separately merged **Sex & Intimität** rework at `c15a18488f2eb269aa03a3620c1c569f316d6be2` is preserved by the final 25.7 rebase. It is not part of the 05.1 accounting and therefore does not increase Stage-05 percentages here.

Open parallel PRs such as the app-wide Question Rework remain outside 360-Rework progress until merged and explicitly reconciled.

## Stage 06

Stage 06 remains 0/5 complete. Existing targeted cleanup packages are partial repairs, not completed defect classes.

## Verification caveat

The Stage-05.1 package history contains focused Kotlin verification for its individual slices plus the final cross-section regression contract. Repository-wide GitHub Actions still terminate before executable workflow step 1 in affected runs (`steps: null` / no usable job log), so no green full Android/Gradle result is claimed.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not combine unrelated substages in one PR.
- Do not overwrite newer main changes while rebasing tracker work.
- Do not count the separate Sex & Intimität rework as 05.1.
- Do not restart 05.1 after PR #76 unless a regression is shown.
- Do not claim a green Android build for Stage 02 until one actually executes.
