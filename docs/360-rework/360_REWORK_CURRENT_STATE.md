# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed core stages:** 2/8  
**Current core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Current substage:** 03.1 — general mixed-step experience definition/state model  
**Stage-02 progress:** 12/12 complete (100%)  
**Current status:** ✅ STAGE 02 COMPLETE; STAGE 03 NEXT  
**Next work package:** 24.19 — Stage 03.1 reusable mixed-step experience definition/state model  
**Latest completed Stage-02 package:** 24.17 — Stage 02 final proposal verification contracts (PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`)

## Source-of-truth rule

PR #24 is an **umbrella/reference PR only** and must never be merged wholesale.

`24.x` IDs are logical 360 Rework work-package IDs. Every real change lands through its own small GitHub PR so it remains independently traceable and revertible.

## Stage 02 — authoritative completion state

`Unser perfekter Antrag` is now implemented as one deterministic mixed-mechanic experience and has the available repository-level verification contracts on `main`.

| Substage | Final state | Evidence |
|---|---|---|
| 02.1 Experience flow model | ✅ Complete | PR #27; stable deterministic proposal flow contract |
| 02.2 Mood/details Either-Or | ✅ Complete | PR #31; 5 mood + 6 detail rounds |
| 02.3 Proposal-location image duels | ✅ Complete | PR #55 + in-app routing commit `229dfa6ded47e59627aab1bf213fe8d7e775c375` |
| 02.4 Refreshed ring-image duels | ✅ Complete | PR #48; wired into final runner by 24.16 |
| 02.5 Proposal priority ranking | ✅ Complete | PR #49; wired into final runner by 24.16 |
| 02.6 Partner prediction A → B → Reveal | ✅ Complete | PR #50; wired into final runner by 24.16 |
| 02.7 Concrete proposal scenarios | ✅ Complete | PR #56, merge `123059397aa9dddd42ef39303d60414fa023c25b` |
| 02.8 Open personal prompts | ✅ Complete | PR #58, merge `69bd969bacccd414f4ea8bd06ae33aa0f33f3679` |
| 02.9 Qualitative `Euer perfekter Antrag` reveal | ✅ Complete | PR #63, merge `c7873ad413129f9fd271a0320faa238b7d8a9091` |
| 02.10 Reuse strongest existing content | ✅ Complete | PR #68, merge `84d4ea5e9dea63ffbbb538bd2dce2a9a4b0db013`; legacy audit reuses strong ring concepts and deliberately avoids duplicate proposal/wedding content |
| 02.11 Entry/navigation + complete playable runner | ✅ Complete | PR #77, merge `82c951c53e42f4d6a76a61ee3596d4e36ef4dd05`; existing `antrag` entry now launches the complete 9-step fullscreen experience |
| 02.12 Tests/build/UI verification | ✅ Complete with infrastructure caveat | PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`; UI/entry, asset and end-to-end contracts cover all 35 configured subrounds and the final reveal |

### Stage-02 verification caveat

The repository Actions/runner infrastructure still does not provide a trustworthy executable Android/Gradle run: affected jobs have repeatedly stopped before executable step 1. Therefore **no green Android build is claimed**.

PR #79 closes the Stage-02 feature slice with the verification that is currently possible and commits the executable contracts so they can run automatically once CI/build infrastructure is available. A later successful Android build is an infrastructure confirmation, not another Stage-02 feature package.

## NEXT EXACT ACTION — Stage 03

**24.19 / Stage 03.1:** extract the general mixed-step experience definition/state model from the completed proposal reference implementation without changing the already-working proposal UX.

Constraints:

- preserve the completed Stage-02 proposal behavior;
- do not copy another one-off runner;
- introduce reusable primitives incrementally and keep legacy content compatible;
- keep Stage-05/06 parallel content work separate unless a shared file is genuinely required;
- check active PRs/branches before every Stage-03 slice.

## Parallel work observed

Stage 05 question-quality work and other app reworks are progressing in parallel. They do not block Stage 03 as long as the file scopes remain separate.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not rebuild or remerge completed Stage-02 slices merely because older tracker entries are stale.
- Do not replace the refreshed ring assets again unless a regression is proven.
- Do not delete/hide proposal, ring or wedding legacy content outside the planned Stage-04 consolidation.
- Do not manually trigger GitHub Actions merely to work around the known pre-step infrastructure failure.

## Handover

Read `docs/360-rework/360_REWORK_MASTER_ROADMAP.md` for the complete stage plan and Definitions of Done.

Read `docs/360-rework/360_REWORK_WORKLOG.md` for historical decisions and package evidence.
