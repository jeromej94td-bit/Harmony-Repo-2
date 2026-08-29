# 360 Rework — Master Roadmap

## Project snapshot

**Project:** 360 Rework  
**Core stages:** 8  
**Completed core stages:** 2/8  
**Current core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-02 progress:** 12/12 complete (100%)  
**Current exact substage:** 03.1 — general mixed-step experience definition/state model

> `complete` means the substage has its intended implementation and the available verification contract on `main`. Repository Actions currently fail before executable step 1, so a green Android/Gradle build is not claimed where it could not actually run.

## Visual stage board

| Stage | Original plan reference | Area | Status | Progress |
|---|---|---|---|---:|
| 01/08 | prerequisite already completed | Ring Image Quality Rework | ✅ DONE | 1/1 |
| 02/08 | Point 1 | 💍 Unser perfekter Antrag | ✅ DONE | 12/12 |
| 03/08 | Point 2 | Reusable Harmony Experience Engine | 🔵 NEXT | 0/7 |
| 04/08 | Point 3 | Existing Proposal/Ring Content Consolidation | ⬜ PLANNED | 0/5 |
| 05/08 | Point 4 | Harmony-360 Questions Quality Rework | 🟡 PARALLEL WORK ACTIVE | 0/5 |
| 06/08 | Point 5 | Broken, Duplicate & Low-Quality Content Cleanup | 🟡 PARTIAL PARALLEL REPAIRS | 0/5 |
| 07/08 | Point 6 | Additional Flagship Harmony Experiences | ⬜ PLANNED | 0/12 |
| 08/08 | Point 7 | Automated Content Quality Gate | ⬜ PLANNED | 0/6 |

---

## Stage 01/08 — Ring Image Quality Rework

**Status:** ✅ DONE  
**Progress:** 1/1 complete (100%)

- [x] 01.1 Replace the 10 prioritized engagement-ring WebPs with refreshed versions and verify the merged diff contains no unrelated files.

**Evidence:** PR #22, merge `b696acb25f8f9b52235a6fba256ec9dc041edab9`; exactly 10 ring WebPs changed. Repository-wide Actions did not execute their first workflow step, so no full green-suite claim is attached to this stage.

---

## Stage 02/08 — 💍 Unser perfekter Antrag

**Original plan reference:** Point 1  
**Status:** ✅ DONE  
**Progress:** 12/12 complete (100%)  
**Dependencies:** Stage 01 complete

### Substages

- [x] **02.1 Experience data model and deterministic proposal flow definition** — PR #27.
- [x] **02.2 Das-oder-Das proposal mood/details rounds** — PR #31.
- [x] **02.3 Proposal-location image duels** — PR #55 plus in-app integration commit `229dfa6ded47e59627aab1bf213fe8d7e775c375`.
- [x] **02.4 Refreshed ring-image duels integrated into the experience** — PR #48; final runner wiring completed in 24.16 / PR #77.
- [x] **02.5 Drag-and-drop ranking for proposal priorities** — PR #49; final runner wiring completed in 24.16 / PR #77.
- [x] **02.6 Partner prediction A → B → Reveal** — PR #50; final runner wiring completed in 24.16 / PR #77.
- [x] **02.7 Concrete proposal scenario rounds** — PR #56, merge `123059397aa9dddd42ef39303d60414fa023c25b`.
- [x] **02.8 Open personal prompts** — PR #58, merge `69bd969bacccd414f4ea8bd06ae33aa0f33f3679`.
- [x] **02.9 Final qualitative `Euer perfekter Antrag` reveal** — PR #63, merge `c7873ad413129f9fd271a0320faa238b7d8a9091`.
- [x] **02.10 Reuse the strongest existing proposal/ring/wedding content needed by this experience** — PR #68, merge `84d4ea5e9dea63ffbbb538bd2dce2a9a4b0db013`; audit deliberately avoids duplicate proposal/wedding rounds and records the strongest ring reuse.
- [x] **02.11 Experience entry/navigation and end-to-end playable flow** — PR #77, merge `82c951c53e42f4d6a76a61ee3596d4e36ef4dd05`; the existing `antrag` entry launches the complete 9-step fullscreen experience.
- [x] **02.12 Tests, build verification and UI verification** — PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`; committed UI/entry, ring-asset and end-to-end contracts cover all 35 configured subrounds and the qualitative final reveal.

### Verification caveat

The repository Actions/runner infrastructure has repeatedly terminated before executable workflow step 1. Stage 02 therefore does **not** claim a green Android/Gradle build that never ran. PR #79 closes the feature stage with the strongest currently available repository-level contracts; a later successful Android build is an infrastructure confirmation rather than another Stage-02 feature slice.

### Definition of Done

`Unser perfekter Antrag` is a complete, playable mixed-mechanic Harmony Experience using the refreshed ring assets, deterministic pacing, partner-prediction reveal, ranking, scenarios, open prompts and a qualitative couple result. It has an accessible navigation entry and committed end-to-end verification contracts. Full legacy cleanup remains outside this stage.

---

## Stage 03/08 — Reusable Harmony Experience Engine

**Original plan reference:** Point 2  
**Status:** 🔵 NEXT  
**Progress:** 0/7 complete

- [ ] 03.1 General mixed-step experience definition/state model
- [ ] 03.2 Reusable `EitherOr` step
- [ ] 03.3 Reusable `ImageDuel` step
- [ ] 03.4 Reusable `Ranking` step
- [ ] 03.5 Reusable `PartnerPrediction` step
- [ ] 03.6 Reusable `Scenario` + `OpenPrompt` steps
- [ ] 03.7 Reusable `Reveal`/result flow plus compatibility with legacy content during migration

### NEXT EXACT ACTION

**24.19 / 03.1:** extract a general mixed-step experience definition/state model from the completed proposal reference implementation. Preserve the current proposal UX and behavior while introducing reusable primitives incrementally.

**Definition of Done:** the proposal implementation becomes the reference for reusable mixed-step experiences without copying one-off runners, while legacy content remains usable during migration.

---

## Stage 04/08 — Existing Proposal/Ring Content Consolidation

**Original plan reference:** Point 3  
**Status:** ⬜ PLANNED  
**Progress:** 0/5 complete  
**Dependencies:** Stages 02 and 03

- [ ] 04.1 Inventory all standalone proposal, engagement-ring and relevant wedding content
- [ ] 04.2 Migrate/reuse strong questions and assets in the new Experience system
- [ ] 04.3 Remove duplicate presentation from normal navigation/catalog surfaces
- [ ] 04.4 Hide/archive legacy standalone packs without deleting source content prematurely
- [ ] 04.5 Regression verification for migrated content, images and navigation

**Definition of Done:** users no longer see redundant proposal/ring experiences as competing entries; strong content is preserved and navigation is verified.

---

## Stage 05/08 — Harmony-360 Questions Quality Rework

**Original plan reference:** Point 4  
**Status:** 🟡 PARALLEL WORK ACTIVE  
**Verified stage progress:** 0/5 complete  
**Note:** separate `25.x` slices are currently working inside 05.1. This parallel activity is independent from the Stage-03 core handover.

- [ ] 05.1 Relationship / communication / everyday-life sections
- [ ] 05.2 Food / travel / leisure / culture sections
- [ ] 05.3 Future / money / work / family sections
- [ ] 05.4 Psychology / feelings / health / intimacy sections
- [ ] 05.5 Values / belief / society / humor / fantasy / teamwork sections

**Definition of Done:** audited Harmony-360 areas are no longer dominated by noun-substitution templates or repeated generic answer quartets; retained questions are concrete, relationship-relevant and worth keeping.

---

## Stage 06/08 — Broken, Duplicate & Low-Quality Content Cleanup

**Original plan reference:** Point 5  
**Status:** 🟡 PARTIAL PARALLEL REPAIRS  
**Verified progress:** 0/5 complete

- [ ] 06.1 Missing answer options and malformed questions
- [ ] 06.2 English leftovers and language mismatches
- [ ] 06.3 Typos, awkward translations and broken wording
- [ ] 06.4 Semantic duplicates, repeated stems and repeated generic option quartets
- [ ] 06.5 Brand/franchise cleanup where IP-neutral replacements are preferred

Small `24.4–24.6.x` packages repaired confirmed defects but do not mark a defect class complete until the wider audit is exhausted or remaining exceptions are documented.

---

## Stage 07/08 — Additional Flagship Harmony Experiences

**Original plan reference:** Point 6  
**Status:** ⬜ PLANNED  
**Progress:** 0/12 complete  
**Dependency:** reusable Experience engine from Stage 03

- [ ] 07.1 Unser Zuhause
- [ ] 07.2 Unsere Traumreise
- [ ] 07.3 So lieben wir
- [ ] 07.4 Wie wir miteinander reden
- [ ] 07.5 Wenn wir uns streiten
- [ ] 07.6 Unsere Familie
- [ ] 07.7 Unsere Zukunft
- [ ] 07.8 Unser Umgang mit Geld
- [ ] 07.9 Unsere Intimität
- [ ] 07.10 Was wäre wenn?
- [ ] 07.11 Wir als Team
- [ ] 07.12 Unser Humor

---

## Stage 08/08 — Automated Content Quality Gate

**Original plan reference:** Point 7  
**Status:** ⬜ PLANNED  
**Progress:** 0/6 complete

- [ ] 08.1 Detect repeated stems/templates
- [ ] 08.2 Detect repeated generic option quartets
- [ ] 08.3 Detect English leftovers in German content
- [ ] 08.4 Detect missing options / malformed items
- [ ] 08.5 Detect tiny or suspiciously thin packs
- [ ] 08.6 Produce a report usable in CI or local verification

---

## Update protocol

After every meaningful completed substage/work package, the worker must:

1. Verify repository state.
2. Update checkbox/fraction/status without claiming verification that did not actually run.
3. Update `360_REWORK_CURRENT_STATE.md` with the exact active substage and `NEXT EXACT ACTION`.
4. Append a dated entry to `360_REWORK_WORKLOG.md`.
5. Record branch, real PR, commit/merge commit, verification performed, verification gaps and blockers.
6. Never silently rewrite scope or ordering; material decisions belong in the worklog.
7. Check active PRs/branches before starting the next slice so parallel agents do not collide.
