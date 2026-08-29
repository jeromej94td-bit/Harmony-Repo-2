# 360 Rework — Master Roadmap

## Project snapshot

**Project:** 360 Rework  
**Core stages:** 8  
**Operationally completed core stages:** 2/8  
**Current core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-02 delivery:** ✅ 12/12 = 100%  
**Stage-02 final package:** 24.17 / PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`  
**Parallel Stage-05 progress:** 1/5 = 20% after the clean 25.7 replacement lands  
**Current exact core substage:** 03.1 — general mixed-step Experience definition/state model

> Stage completion here means the approved feature packages are integrated into `main` with the available verification contracts and known infrastructure caveats recorded. A green full Android/Gradle build is only claimed when it actually executes successfully.

## Visual stage board

| Stage | Original plan reference | Area | Status | Progress |
|---|---|---|---|---:|
| 01/08 | prerequisite | Ring Image Quality Rework | ✅ DONE | 1/1 = 100% |
| 02/08 | Point 1 | 💍 Unser perfekter Antrag | ✅ DONE | 12/12 = 100% delivered |
| 03/08 | Point 2 | Reusable Harmony Experience Engine | 🔵 NEXT / ACTIVE | 0/7 |
| 04/08 | Point 3 | Existing Proposal/Ring Content Consolidation | ⬜ PLANNED | 0/5 |
| 05/08 | Point 4 | Harmony-360 Questions Quality Rework | 🟡 PARALLEL ACTIVE | 1/5 = 20% after 25.7 replacement |
| 06/08 | Point 5 | Broken, Duplicate & Low-Quality Content Cleanup | 🟡 PARTIAL PARALLEL REPAIRS | 0/5 |
| 07/08 | Point 6 | Additional Flagship Harmony Experiences | ⬜ PLANNED | 0/12 |
| 08/08 | Point 7 | Automated Content Quality Gate | ⬜ PLANNED | 0/6 |

---

## Stage 01/08 — Ring Image Quality Rework

**Status:** ✅ DONE  
**Progress:** 1/1 = 100%

- [x] 01.1 Replace the 10 prioritized engagement-ring WebPs with refreshed versions and verify the merged diff contains no unrelated files.

**Evidence:** PR #22, merge `b696acb25f8f9b52235a6fba256ec9dc041edab9`; exactly 10 ring WebPs changed. Repository-wide Actions did not execute their first workflow step, so no full green-suite claim is attached to this stage.

---

## Stage 02/08 — 💍 Unser perfekter Antrag

**Original plan reference:** Point 1  
**Status:** ✅ DONE / DELIVERED  
**Progress:** 12/12 = 100%  
**Dependency:** Stage 01 complete

### Substages

- [x] **02.1 Experience data model and deterministic proposal flow definition** — PR #27.
- [x] **02.2 Das-oder-Das proposal mood/details rounds** — PR #31.
- [x] **02.3 Proposal-location image duels** — PR #55 plus in-app routing into the proposal experience.
- [x] **02.4 Refreshed ring-image duels integrated into the experience** — PR #48, wired by the complete runner in PR #77.
- [x] **02.5 Drag-and-drop ranking for proposal priorities** — PR #49, wired by PR #77.
- [x] **02.6 Partner prediction A → B → Reveal** — PR #50, wired by PR #77.
- [x] **02.7 Concrete proposal scenario rounds** — PR #56, wired by PR #77.
- [x] **02.8 Open personal prompts** — PR #58, wired by PR #77.
- [x] **02.9 Final qualitative `Euer perfekter Antrag` reveal** — PR #63, rendered by PR #77.
- [x] **02.10 Reuse strongest existing proposal/ring/wedding content** — PR #68.
- [x] **02.11 Experience entry/navigation and end-to-end playable flow** — PR #77, merge `82c951c53e42f4d6a76a61ee3596d4e36ef4dd05`.
- [x] **02.12 Final verification-contract package** — PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`; UI-entry/ring-asset contracts plus deterministic 35-subround end-to-end journey to the qualitative reveal are committed.

### Verification caveat

The repository Actions/Gradle environment was not able to provide a trustworthy executable full Android run because affected jobs terminate before executable step 1 / available Actions credits are exhausted. PR #79 was nevertheless intentionally integrated so completed work and its verification contracts do not remain stranded.

No green full Android/Gradle build is claimed. The contracts remain committed for execution as soon as a functioning build environment exists. This is an infrastructure follow-up rather than an unfinished Stage-02 feature slice.

### Definition of Done

`Unser perfekter Antrag` is delivered as a complete mixed-mechanic Harmony Experience using refreshed ring assets, deterministic pacing, location and ring duels, ranking, partner prediction, scenarios, open prompts and a qualitative couple reveal, with an accessible app entry and committed integrated verification contracts.

---

## Stage 03/08 — Reusable Harmony Experience Engine

**Original plan reference:** Point 2  
**Status:** 🔵 NEXT / ACTIVE  
**Progress:** 0/7

- [ ] **03.1 General mixed-step Experience definition/state model**
- [ ] **03.2 Reusable `EitherOr` step**
- [ ] **03.3 Reusable `ImageDuel` step**
- [ ] **03.4 Reusable `Ranking` step**
- [ ] **03.5 Reusable `PartnerPrediction` step**
- [ ] **03.6 Reusable `Scenario` + `OpenPrompt` steps**
- [ ] **03.7 Reusable `Reveal`/result flow plus legacy compatibility during migration**

### NEXT EXACT ACTION

**03.1:** extract a general mixed-step Experience definition/state contract from the completed proposal reference implementation. Keep this first package model/state-only: stable IDs, ordered heterogeneous steps, current position/progress, deterministic advancement and completion semantics. Do not duplicate the proposal runner and do not pull the reusable Compose step renderers from 03.2–03.7 into this slice.

**Definition of Done:** the proposal implementation becomes the reference for reusable mixed-step experiences without copying one-off runners, while legacy content remains usable during migration.

---

## Stage 04/08 — Existing Proposal/Ring Content Consolidation

**Original plan reference:** Point 3  
**Status:** ⬜ PLANNED  
**Progress:** 0/5  
**Dependencies:** Stages 02 and 03

- [ ] 04.1 Inventory all standalone proposal, engagement-ring and relevant wedding content
- [ ] 04.2 Migrate/reuse strong questions and assets in the Experience system
- [ ] 04.3 Remove duplicate presentation from normal navigation/catalog surfaces
- [ ] 04.4 Hide/archive legacy standalone packs without deleting source content prematurely
- [ ] 04.5 Regression verification for migrated content, images and navigation

**Definition of Done:** users no longer see redundant proposal/ring experiences as competing entries; strong content is preserved and navigation is verified.

---

## Stage 05/08 — Harmony-360 Questions Quality Rework

**Original plan reference:** Point 4  
**Status:** 🟡 PARALLEL ACTIVE  
**Stage progress after the clean 25.7 replacement lands:** 1/5 = 20%  
**Completed substage:** 05.1 = 7/7 = 100%  
**Next:** 05.2 — Food / travel / leisure / culture

- [x] **05.1 Relationship / communication / everyday-life sections** — Sections 01/02/06/12 audited pack-by-pack; 72/72 explicit decisions, six redundant/filler packs archived from the curated target output, concrete relationship-specific rewrites, plus the 10×2 Quick Game `Was brauchst du gerade?`. Dedicated ledger: `360_REWORK_STAGE05_WORKLOG.md`.
- [ ] **05.2 Food / travel / leisure / culture sections**
- [ ] **05.3 Future / money / work / family sections**
- [ ] **05.4 Psychology / feelings / health / intimacy sections**
- [ ] **05.5 Values / belief / society / humor / fantasy / teamwork sections**

### 05.1 final shape

- Raw target packs: 18 + 18 + 18 + 18 = 72.
- Explicit decisions: 72/72.
- Curated source packs: 16 + 17 + 16 + 17 = 66.
- Six confirmed redundant/filler packs archived from the visible curated output.
- Added Quick Game: 1 pack, 10 questions, exactly 2 options each.
- Scoped final target: 67 packs with unique IDs.
- The final cross-section JUnit audit is preserved in the clean 25.7 replacement branch.
- Repository Actions remain affected by the known pre-step/credit issue, so no full green Android/Gradle result is claimed from CI.

**Definition of Done:** audited Harmony-360 areas are no longer dominated by noun-substitution templates or repeated generic answer quartets; retained questions are concrete, relationship-relevant and worth keeping.

---

## Stage 06/08 — Broken, Duplicate & Low-Quality Content Cleanup

**Original plan reference:** Point 5  
**Status:** 🟡 PARTIAL PARALLEL REPAIRS  
**Progress:** 0/5

- [ ] 06.1 Missing answer options and malformed questions
- [ ] 06.2 English leftovers and language mismatches
- [ ] 06.3 Typos, awkward translations and broken wording
- [ ] 06.4 Semantic duplicates, repeated stems and repeated generic option quartets
- [ ] 06.5 Brand/franchise cleanup where IP-neutral replacements are preferred

Targeted cleanup packages do not mark a defect class complete until its wider audit is exhausted or remaining exceptions are documented.

---

## Stage 07/08 — Additional Flagship Harmony Experiences

**Original plan reference:** Point 6  
**Status:** ⬜ PLANNED  
**Progress:** 0/12  
**Dependency:** Stage 03

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
**Progress:** 0/6

- [ ] 08.1 Detect repeated stems/templates
- [ ] 08.2 Detect repeated generic option quartets
- [ ] 08.3 Detect English leftovers in German content
- [ ] 08.4 Detect missing options / malformed items
- [ ] 08.5 Detect tiny or suspiciously thin packs
- [ ] 08.6 Produce a report usable in CI or local verification

---

## Update protocol

After every meaningful completed substage/work package:

1. Verify repository state first.
2. Update checkbox/fraction/status without confusing a green executable build with a merely committed verification contract.
3. Update `360_REWORK_CURRENT_STATE.md` with the exact active substage and `NEXT EXACT ACTION`.
4. Append the package to the relevant ledger (`360_REWORK_WORKLOG.md`, plus `360_REWORK_STAGE05_WORKLOG.md` for 25.x work).
5. Record branch, real PR, merge commit where available, verification performed, verification gaps and blockers.
6. Never silently rewrite scope or ordering.
7. Check active PRs/branches before starting the next slice so parallel agents do not collide.
