# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed core stages:** 1/8  
**Current core stage:** Stage 02/08 — 💍 Unser perfekter Antrag  
**Current substage:** 02.10 — stärkste bestehende Antrag-/Ring-/Wedding-Inhalte wiederverwenden  
**Verified Stage-02 progress:** 0/12 complete (0%)  
**Implementation coverage:** 9/12 substages have dedicated implementation merged  
**Current status:** 🧪 IMPLEMENTATION THROUGH 02.9 MERGED; END-TO-END VERIFICATION OPEN  
**Next Stage-02 work package:** 24.15 — Stage 02.10 content reuse  
**Latest merged Stage-02 package:** 24.14 — Stage 02.9 qualitative reveal (PR #63, merge `c7873ad413129f9fd271a0320faa238b7d8a9091`)

## Source-of-truth rule

PR #24 is an **umbrella/reference PR only** and must never be merged wholesale.

`24.x` IDs are logical 360 Rework work-package IDs. Every real change lands through its own small GitHub PR so it remains independently traceable and revertible.

Implementation coverage and verified completion are deliberately separate. A substage can have its content/model/UI implementation on `main` while its checkbox remains open until Stage 02.12 records the required build/UI verification.

## Stage 02 — authoritative implementation state

| Substage | Implementation state | Evidence / remaining gap |
|---|---|---|
| 02.1 Experience flow model | 🧪 Merged | PR #27; final Gradle/UI verification still open |
| 02.2 Mood/details Either-Or | 🧪 Merged | PR #31; final runner/build/UI verification open |
| 02.3 Proposal-location image duels | 🧪 Merged + routed into `antrag` | PR #55 plus in-app integration commit `229dfa6ded47e59627aab1bf213fe8d7e775c375`; final UI/build verification open |
| 02.4 Refreshed ring-image duels | 🧪 Model/content merged | PR #48; end-to-end runner wiring remains part of 02.11 |
| 02.5 Proposal priority ranking | 🧪 Model/content merged | PR #49; end-to-end runner wiring remains part of 02.11 |
| 02.6 Partner prediction A → B → Reveal | 🧪 Model/content merged | PR #50; end-to-end runner wiring remains part of 02.11 |
| 02.7 Concrete proposal scenarios | 🧪 Model/content merged | PR #56, merge `123059397aa9dddd42ef39303d60414fa023c25b`; focused Kotlin contract 3/3 PASS |
| 02.8 Open personal prompts | 🧪 Model/content merged | PR #58, merge `69bd969bacccd414f4ea8bd06ae33aa0f33f3679`; focused Kotlin contract 3/3 PASS |
| 02.9 Qualitative `Euer perfekter Antrag` reveal | 🧪 Result builder merged | PR #63, merge `c7873ad413129f9fd271a0320faa238b7d8a9091`; focused Kotlin contract 3/3 PASS; result-screen/runner wiring remains in 02.11 |
| 02.10 Reuse strongest existing content | 🔵 NEXT | Not started in the authoritative Stage-02 line yet |
| 02.11 Entry/navigation + complete playable runner | ⬜ PLANNED | Must wire all Stage-02 mechanics and reveal together |
| 02.12 Tests/build/UI verification | ⬜ PLANNED | Required before Stage 02 can be marked complete |

## NEXT EXACT ACTION — Stage 02

**24.15 / Stage 02.10:** inventory the existing proposal, engagement-ring and relevant wedding content already present in the app, select only the strongest material that adds something not already covered by 02.2–02.9, and expose that selected content for the perfect-proposal experience.

Constraints for 02.10:

- reuse rather than duplicate;
- do not delete, hide or archive legacy packs yet — that belongs to Stage 04;
- do not broaden into navigation/end-to-end runner work — that belongs to 02.11;
- do not touch unrelated Stage-05 or Photo-Rework work currently progressing in parallel.

## Parallel work observed

Parallel agents have already begun Stage 05 work (`25.x`) and Photo Rework PR #61. Those tracks are independent and do **not** make Stage 02 complete. The current core-stage handover remains Stage 02 until its Definition of Done is met.

## Verification caveat

Repository-wide GitHub Actions have repeatedly failed before executable workflow step 1. This remains an infrastructure verification caveat:

- it is not a green full-suite result;
- it is not evidence of an application regression by itself;
- focused local Kotlin checks are recorded per narrow work package;
- Stage 02 stays unverified until 02.12 records executable build/UI evidence.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not rebuild or remerge 02.1–02.9 merely because this tracker was stale.
- Do not replace the refreshed ring assets again unless a regression is proven.
- Do not delete/hide proposal, ring or wedding legacy content during 02.10; consolidation belongs to Stage 04.
- Do not jump to Stage 03/04/05 as the Stage-02 successor until 02.10–02.12 are resolved.
- Do not manually trigger GitHub Actions for these small packages.

## Handover

Read `docs/360-rework/360_REWORK_MASTER_ROADMAP.md` for the complete stage plan and Definitions of Done.

Read `docs/360-rework/360_REWORK_WORKLOG.md` for historical decisions and package evidence.
