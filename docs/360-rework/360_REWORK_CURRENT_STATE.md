# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Operationally completed core stages:** 2/8  
**Current core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-02 delivery:** ✅ 12/12 packages landed (100%)  
**Stage-02 final package:** 24.17 / PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`  
**Parallel Stage-05 progress:** 1/5 = 20% after 25.7 replacement lands  
**Stage 05.1:** ✅ 7/7 = 100% after 25.7 replacement lands  
**NEXT CORE ACTION:** 03.1 — general mixed-step Experience definition/state model  
**NEXT STAGE-05 ACTION:** 05.2 — Food / travel / leisure / culture

## Source-of-truth rule

PR #24 is an **umbrella/reference PR only** and must never be merged wholesale.

`24.x` and `25.x` are logical 360-Rework work-package IDs. Real changes land through narrow PRs so they stay independently traceable and revertible.

## Stage 02 — 💍 Unser perfekter Antrag

**Operational status:** ✅ CLOSED / DELIVERED  
**Delivery coverage:** 12/12 = 100%

| Substage | State | Evidence |
|---|---|---|
| 02.1 Experience flow model | ✅ Landed | PR #27 |
| 02.2 Mood/details Either-Or | ✅ Landed | PR #31 |
| 02.3 Proposal-location image duels | ✅ Landed | PR #55 plus in-app routing |
| 02.4 Refreshed ring-image duels | ✅ Landed | PR #48, wired by PR #77 |
| 02.5 Proposal priority ranking | ✅ Landed | PR #49, wired by PR #77 |
| 02.6 Partner prediction A → B → Reveal | ✅ Landed | PR #50, wired by PR #77 |
| 02.7 Concrete proposal scenarios | ✅ Landed | PR #56, wired by PR #77 |
| 02.8 Personal open prompts | ✅ Landed | PR #58, wired by PR #77 |
| 02.9 Qualitative `Euer perfekter Antrag` reveal | ✅ Landed | PR #63, rendered by PR #77 |
| 02.10 Reuse strongest existing proposal/ring/wedding content | ✅ Landed | PR #68 |
| 02.11 Entry/navigation + complete playable runner | ✅ Landed | PR #77, merge `82c951c53e42f4d6a76a61ee3596d4e36ef4dd05` |
| 02.12 Verification contracts / final gate package | ✅ Landed | PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca` |

### Stage-02 verification caveat

PR #79 stores the final UI/asset/end-to-end verification contracts, including the 35-subround journey to the qualitative reveal. The repository Actions/Gradle environment could not execute a trustworthy full Android build because the available jobs fail before executable step 1 / credits are unavailable. The operator explicitly authorized integrating the completed work rather than leaving it stranded.

Therefore:

- Stage 02 is operationally closed and the work is in `main`.
- No green full Android/Gradle build is claimed.
- The committed verification contracts remain available to execute automatically once a functioning build environment is available.
- A future CI run is an infrastructure verification follow-up, not a reason to reopen or duplicate Stage-02 feature implementation.

## NEXT EXACT CORE ACTION — Stage 03

**03.1 — General mixed-step Experience definition/state model.**

Use the completed proposal experience as the reference implementation and extract only the reusable state/model contract first. Do not copy another one-off runner and do not bundle the later reusable UI step types into 03.1.

## Parallel Stage 05 — Harmony-360 Questions Quality Rework

### 05.1 — Relationship / communication / everyday-life quality pass

**Status after the clean 25.7 replacement lands:** ✅ DONE  
**Substage progress:** 7/7 = 100%  
**Stage-05 progress:** 1/5 = 20%

Target sections:

- Section 01 — Beziehung & Nähe
- Section 02 — Kommunikation
- Section 06 — Alltag & Zuhause
- Section 12 — Kommunikation & Konflikte

Final scoped shape:

- 72 raw packs across the four sections.
- 72/72 explicit deterministic curation decisions.
- Curated source counts 16 / 17 / 16 / 17 = 66.
- Six confirmed redundant/filler packs archived from the visible curated output.
- One additional Quick Game `h360_need_now_quick`, exactly 10 situations × 2 choices.
- Final scoped output: 67 unique packs.
- Dedicated ledger: `docs/360-rework/360_REWORK_STAGE05_WORKLOG.md`.

### NEXT EXACT STAGE-05 ACTION

**05.2 — Food / travel / leisure / culture.** Start as a new narrow 25.x package after 25.7 is merged. Do not reopen 05.1 unless a regression is demonstrated.

## Parallel Stage 06 cleanup

Stage 06 remains **0/5 complete**. Existing cleanup packages are targeted partial repairs only; they do not complete an entire defect class by themselves.

## Other parallel work

Question Rework, performance fixes, Live Change, Harmony Coach and other independent branches remain separate. Their existence must not silently rewrite the 360-Rework percentages or be merged merely because they are open.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not rebuild or remerge Stage-02 packages merely because an older tracker was stale.
- Do not replace the refreshed ring assets again unless a regression is proven.
- Do not force-merge stale/conflicting PR #76; its useful work is preserved in the clean current-main replacement.
- Do not claim a green full Android/Gradle suite until an executable build actually runs successfully.
- Do not block completed feature work indefinitely solely because GitHub Actions credits/runners are unavailable; record the caveat and preserve executable contracts.

## Handover

Read `docs/360-rework/360_REWORK_MASTER_ROADMAP.md` for the complete stage plan and Definitions of Done.

Read `docs/360-rework/360_REWORK_WORKLOG.md` for wider history and `docs/360-rework/360_REWORK_STAGE05_WORKLOG.md` for the dedicated 25.x ledger.
