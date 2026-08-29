# 360 Rework — Stage 05 Worklog

**Purpose:** append-only package ledger for the parallel Stage-05 content-quality track.  
**Stage:** 05 — Harmony-360 Questions Quality Rework  
**Completed substage:** 05.1 — 7/7 = 100%  
**Stage-05 progress:** 1/5 = 20%  
**Next:** 05.2 — Food / travel / leisure / culture

This file supplements `360_REWORK_WORKLOG.md` with the dedicated 25.x package history. The general worklog remains the historical ledger for the wider 360 Rework project.

## 25.0 — Stage 05.1 approved design and plan

- GitHub PR: #59
- Branch: `360-rework/25-0-stage-05-1-spec-plan`
- Merge commit: `648a1898bb1bbb602d8013cb7d9f287509846318`
- Scope: approved Stage-05.1 design plus seven-slice implementation plan only; no runtime code.
- Progress after merge: 05.1 remained 0/7 = 0%.

## 25.1 / 05.1a — Curation infrastructure

- GitHub PR: #60
- Branch: `360-rework/25-1-stage-05-1a-curation-infra`
- Merge commit: `f1d38f7e0a6bba369006254409bfb31315a58d49`
- Added the explicit list-level curation layer for Sections 01/02/06/12 and wired it after the existing Harmony-360 cleanup pipeline.
- Added explicit archive/override primitives and focused tests.
- Focused Kotlin contract harness: RED before implementation, PASS after implementation.
- Progress after merge: 1/7 = 14.3%.

## 25.2 / 05.1b — Nähe & Zuneigung

- GitHub PR: #62
- Branch: `360-rework/25-2-stage-05-1b-nearness`
- Merge commit: `c84d19f61a32a227c9c0cf7e2fdea11b31d8fa87`
- Section 01: 18/18 packs received an explicit Keep/Rewrite/Archive decision.
- 14 packs rewritten, 2 strong packs kept, 2 overlapping filler packs archived.
- Progress after merge: 2/7 = 28.6%.

## 25.3 / 05.1c — Kommunikation

- GitHub PR: #64
- Branch: `360-rework/25-3-stage-05-1c-communication-mainline`
- Merge commit: `40b619562751b358e16ae37cce237323c2aba554`
- Section 02: 18/18 packs explicitly decided.
- 15 packs rewritten, `Direkte Worte` and `Schweigen` kept, redundant `Missverständnisse – Geheime Wahl` archived.
- Focused communication contract: RED before implementation, PASS after implementation.
- Progress after merge: 3/7 = 42.9%.

## 25.4 / 05.1d — Alltag & Zuhause

- GitHub PR: #69
- Branch: `360-rework/25-4-stage-05-1d-everyday`
- Merge commit: `e5426ed88d83c3cf916a622bb9940234939e6903`
- Section 06: 18/18 packs explicitly decided; 16 rewritten and 2 filler packs archived.
- Covers routines, mental load, household standards, shopping/budget, cooking, sleep, home office, visitors, Sundays, Feierabend, shared to-dos and screen/technology rules.
- Preserved the independently merged `NormensLoeschungen.apply(...)` layer while rebasing.
- Focused Section-06 contract: RED before implementation, PASS after implementation.
- Progress after merge: 4/7 = 57.1%.

## 25.5 / 05.1e — Streit & Wiederannäherung

- GitHub PR: #70
- Branch: `360-rework/25-5-stage-05-1e-conflict-repair`
- Merge commit: `02a4bd67343cb3d1ad87cd4aacdd517e34e675de`
- Section 12: 18/18 packs explicitly decided; 17 rewritten, generic `Gesprächsthemen` overlap archived.
- Covers conflict style, apology, silence/pause, compromise, feedback, misunderstanding, privacy/secrets, old conflicts, honesty, listening, being right, tone, timing, feelings, giving in, humor and reconciliation.
- Explicit regression contract added; the repository Actions runner still failed before executing step 1.
- A later focused Kotlin verification during 25.7 confirmed the Section-12 curation contract: 18 decisions, 17 visible packs, archive behavior, concrete repair output and absence of the known English ranking-template defect.
- Progress after merge: 5/7 = 71.4%.

## 25.6 / 05.1f — Quick Game `Was brauchst du gerade?`

- GitHub PR: #71
- Branch: `360-rework/25-6-stage-05-1f-need-now`
- Merge commit: `4f1dfe3e6f91c01202a92f4719bdb562b4cfe8e2`
- Added one deterministic pack `h360_need_now_quick` using the existing choice runner.
- Exactly 10 concrete situations with exactly 2 nonblank options each.
- Same-ID input is replaced instead of duplicated.
- A later focused Kotlin verification during 25.7 confirmed 10×2 validity, deduplication and pipeline registration.
- Progress after merge: 6/7 = 85.7%.

## 25.7 / 05.1g — Final cross-section audit + tracker

- Original PR #76 became stale/conflicting after Stage 02 advanced through 24.17 and was therefore not force-merged.
- Clean replacement branch: `360-rework/25-7-stage-05-1g-final-audit-current`, based on the then-current `main` after PR #79.
- Scope: preserve the completed final target-section audit, add the dedicated Stage-05 ledger and synchronize the trackers without reverting newer Stage-02 state.
- Final audit contract checks:
  - all four target sections contain 18 raw packs and 18 explicit curation decisions;
  - curated sizes are 16 / 17 / 16 / 17;
  - exactly six Stage-05.1 source packs are archived;
  - target output is 67 packs after curation plus the Quick Game;
  - no duplicate target IDs;
  - canonical relationship/communication/everyday/repair packs remain;
  - Quick Game stays exactly 10×2;
  - known English conflict-template defect stays absent;
  - unrelated packs remain value-equivalent through the Stage-05.1 pipeline, apart from the intentional Quick Game append.
- `NormensLoeschungen` currently targets only `h500_430_team_zukunft_offene_runde`, outside Stage 05.1.
- Existing focused verification evidence covers Section-12 curation, Quick Game 10×2, same-ID deduplication and Stage-05.1 pipeline registration.
- The final JUnit audit contract is committed for execution whenever Gradle/CI is available.
- Repository-wide GitHub Actions remain affected by the known pre-step/credit infrastructure issue; no green full Android/Gradle suite is claimed.
- Stage 02 was already advanced independently through 24.17 / PR #79 and must not be rolled back by this Stage-05 tracker update.
- Completion state after this replacement lands: Stage 05.1 = 7/7 = 100%; Stage 05 = 1/5 = 20%.

## Stage 05.1 completion summary

The four approved target areas are fully represented by explicit deterministic curation decisions. The runtime curation preserves the large generated source files for traceability, removes six confirmed redundant/filler packs from the visible target output, keeps canonical packs, replaces generic noun-substitution templates with relationship-specific questions, and adds the short two-choice Quick Game.

The next Stage-05 work is **05.2 — Food / travel / leisure / culture**. Do not reopen 05.1 unless a regression is demonstrated.
