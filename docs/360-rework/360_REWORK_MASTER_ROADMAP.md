# 360 Rework — Master Roadmap

## Project snapshot

**Project:** 360 Rework  
**Core stages:** 8  
**Completed core stages:** 1/8  
**Current core stage:** Stage 02/08 — 💍 Unser perfekter Antrag  
**Verified Stage-02 progress:** 0/12 complete (0%)  
**Stage-02 implementation coverage:** 9/12 substages have dedicated implementation merged  
**Current exact substage:** 02.10 — reuse strongest existing proposal/ring/wedding content

> `complete` means verified against the substage Definition of Done. Merged implementation alone does not tick a checkbox. Stage 02.12 owns the final executable build/UI verification for the mixed experience.

## Visual stage board

| Stage | Original plan reference | Area | Status | Verified progress |
|---|---|---|---|---:|
| 01/08 | prerequisite already completed | Ring Image Quality Rework | ✅ DONE | 1/1 |
| 02/08 | Point 1 | 💍 Unser perfekter Antrag | 🧪 ACTIVE / VERIFY | 0/12; implementation through 02.9 |
| 03/08 | Point 2 | Reusable Harmony Experience Engine | ⬜ PLANNED | 0/7 |
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
**Status:** 🧪 ACTIVE / FINAL VERIFICATION OPEN  
**Verified progress:** 0/12 complete (0%)  
**Implementation coverage:** 9/12 substages have a dedicated implementation package on `main`  
**Dependencies:** Stage 01 complete

### Substages

- [ ] **02.1 Experience data model and deterministic proposal flow definition** — implementation merged in PR #27; Gradle/UI verification open.
- [ ] **02.2 Das-oder-Das proposal mood/details rounds** — implementation merged in PR #31; final runner/build/UI verification open.
- [ ] **02.3 Proposal-location image duels** — image-duel package merged via PR #55 and routed into the `antrag` game in commit `229dfa6ded47e59627aab1bf213fe8d7e775c375`; final UI/build verification open.
- [ ] **02.4 Refreshed ring-image duels integrated into the experience** — deterministic ring-duel content merged in PR #48; actual end-to-end runner wiring remains for 02.11.
- [ ] **02.5 Drag-and-drop ranking for proposal priorities** — deterministic priority content merged in PR #49 and reuses existing ranking mechanics; end-to-end runner wiring remains for 02.11.
- [ ] **02.6 Partner prediction A → B → Reveal** — deterministic prediction rounds merged in PR #50 and reuse the existing prediction board; end-to-end runner wiring remains for 02.11.
- [ ] **02.7 Concrete proposal scenario rounds** — six proposal scenarios merged in PR #56, merge `123059397aa9dddd42ef39303d60414fa023c25b`; focused Kotlin contract 3/3 PASS; runner wiring remains for 02.11.
- [ ] **02.8 Open personal prompts** — five personal free-text prompts merged in PR #58, merge `69bd969bacccd414f4ea8bd06ae33aa0f33f3679`; focused Kotlin contract 3/3 PASS; runner wiring remains for 02.11.
- [ ] **02.9 Final qualitative `Euer perfekter Antrag` reveal** — qualitative result builder merged in PR #63, merge `c7873ad413129f9fd271a0320faa238b7d8a9091`; focused Kotlin contract 3/3 PASS; visual result/runner wiring remains for 02.11.
- [ ] **02.10 Reuse the strongest existing proposal/ring/wedding content needed by this experience** — 🔵 NEXT.
- [ ] **02.11 Experience entry/navigation and end-to-end playable flow** — wire the Stage-02 components into one deterministic playable experience and render the final reveal.
- [ ] **02.12 Tests, build verification and UI verification** — executable verification gate before Stage 02 can be marked complete.

### NEXT EXACT ACTION

**24.15 / 02.10:** inventory existing proposal, engagement-ring and relevant wedding content already shipped in Harmony; select only strong material that adds value beyond 02.2–02.9; expose that selected material for reuse by the perfect-proposal experience.

02.10 must **not** delete, hide or archive legacy packs. That consolidation belongs to Stage 04. It also must not absorb entry/navigation or the end-to-end runner from 02.11.

### Definition of Done

`Unser perfekter Antrag` is a complete, playable mixed-mechanic Harmony Experience using the refreshed ring assets, deterministic pacing, partner-prediction reveal, ranking, scenarios, open prompts and a qualitative couple result. It has an accessible navigation entry and its core flow is verified. Full legacy cleanup is not part of this stage.

---

## Stage 03/08 — Reusable Harmony Experience Engine

**Original plan reference:** Point 2  
**Status:** ⬜ PLANNED  
**Progress:** 0/7 complete

- [ ] 03.1 General mixed-step experience definition/state model
- [ ] 03.2 Reusable `EitherOr` step
- [ ] 03.3 Reusable `ImageDuel` step
- [ ] 03.4 Reusable `Ranking` step
- [ ] 03.5 Reusable `PartnerPrediction` step
- [ ] 03.6 Reusable `Scenario` + `OpenPrompt` steps
- [ ] 03.7 Reusable `Reveal`/result flow plus compatibility with legacy content during migration

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
**Note:** separate `25.x` slices are currently working inside 05.1. This parallel activity does not replace the Stage-02 handover.

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
2. Update checkbox/fraction/status without confusing merged implementation with verified completion.
3. Update `360_REWORK_CURRENT_STATE.md` with the exact active substage and `NEXT EXACT ACTION`.
4. Append a dated entry to `360_REWORK_WORKLOG.md`.
5. Record branch, real PR, commit/merge commit, verification performed, verification gaps and blockers.
6. Never silently rewrite scope or ordering; material decisions belong in the worklog.
7. Check active PRs/branches before starting the next slice so parallel agents do not collide.
