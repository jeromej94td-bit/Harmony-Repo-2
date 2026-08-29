# 360 Rework — Master Roadmap

## Project snapshot

**Project:** 360 Rework  
**Core stages:** 8  
**Completed core stages:** 1/8  
**Current stage:** Stage 02/08 — Unser perfekter Antrag  
**Current stage progress:** 0/12 complete (0%)

> Global stage position tells us *where* we are in the plan. The x/y fraction and percentage tell us *how far* we are inside that stage.

## Visual stage board

| Stage | Original plan reference | Area | Status | Progress | Percent |
|---|---|---|---|---:|---:|
| 01/08 | prerequisite already completed | Ring Image Quality Rework | ✅ DONE | 1/1 | 100% |
| 02/08 | Point 1 | 💍 Unser perfekter Antrag | 🔵 NEXT | 0/12 | 0% |
| 03/08 | Point 2 | Reusable Harmony Experience Engine | ⬜ PLANNED | 0/7 | 0% |
| 04/08 | Point 3 | Existing Proposal/Ring Content Consolidation | ⬜ PLANNED | 0/5 | 0% |
| 05/08 | Point 4 | Harmony-360 Questions Quality Rework | ⬜ PLANNED | 0/5 | 0% |
| 06/08 | Point 5 | Broken, Duplicate & Low-Quality Content Cleanup | ⬜ PLANNED | 0/5 | 0% |
| 07/08 | Point 6 | Additional Flagship Harmony Experiences | ⬜ PLANNED | 0/12 | 0% |
| 08/08 | Point 7 | Automated Content Quality Gate | ⬜ PLANNED | 0/6 | 0% |

---

## Stage 01/08 — Ring Image Quality Rework

**Original plan reference:** prerequisite completed before the formal stage board  
**Status:** ✅ DONE  
**Progress:** 1/1 complete (100%)  
**Dependencies:** none

### Substages

- [x] 01.1 Replace the 10 prioritized engagement-ring WebPs with the refreshed versions and verify the merged diff contains no unrelated files.

### Evidence

- PR: `#22 — Refresh engagement ring image assets`
- Merge commit: `b696acb25f8f9b52235a6fba256ec9dc041edab9`
- Changed files: exactly 10 ring WebPs
- Verification limitation: two GitHub Actions jobs failed before executing their first workflow step, therefore this stage must **not** be described as having a fully green test suite.

### Definition of Done

The ten intended refreshed ring assets are present on `main`, the merged diff contains only those ten ring WebPs, and the verification limitation is documented.

---

## Stage 02/08 — 💍 Unser perfekter Antrag

**Original plan reference:** Point 1  
**Status:** 🧪 VERIFY<br>
**Progress:** 0/12 complete (0%)  
**Dependencies:** Stage 01 complete; uses refreshed ring assets

### Substages

- [ ] 02.1 Experience data model and deterministic proposal flow definition — merged implementation awaiting build verification
- [ ] 02.2 Das-oder-Das proposal mood/details rounds — merged implementation awaiting verification
- [ ] 02.3 Proposal-location image duels
- [ ] 02.4 Refreshed ring-image duels integrated into the experience — implementation prepared in PR #46
- [ ] 02.5 Drag-and-drop ranking for proposal priorities
- [ ] 02.6 Partner prediction A → B → Reveal
- [ ] 02.7 Concrete proposal scenario rounds
- [ ] 02.8 Open personal prompts
- [ ] 02.9 Final qualitative `Euer perfekter Antrag` reveal
- [ ] 02.10 Reuse the strongest existing proposal/ring/wedding content needed by this experience
- [ ] 02.11 Experience entry/navigation and end-to-end playable flow
- [ ] 02.12 Tests, build verification and UI verification

### Merged implementation awaiting verification

- **24.2 / 02.1:** PR #27, merge commit `919aee586977cebe9700360eaf3be29d4e4a697f`; defines the deterministic, UI-independent proposal flow contract.
- **24.3 / 02.2:** PR #31, merge commit `3012f1bbcb0c4406e4fb8b34258ddb2a4d88bd5f`; adds the mood and detail Das-oder-Das round content bound to that contract.

Both packages were explicitly merged with documented verification gaps. They remain unchecked until the focused Gradle build and later UI verification are recorded.

### Definition of Done

`Unser perfekter Antrag` is a complete, playable mixed-mechanic Harmony Experience using the refreshed ring assets, with deterministic pacing, partner-prediction reveal, ranking, scenarios, open prompts, and a qualitative couple result. It has an accessible navigation entry and its core flow is verified. Full legacy cleanup is **not** part of this stage.

---

## Stage 03/08 — Reusable Harmony Experience Engine

**Original plan reference:** Point 2  
**Status:** ⬜ PLANNED  
**Progress:** 0/7 complete (0%)  
**Dependencies:** Stage 02 reference implementation

### Substages

- [ ] 03.1 General mixed-step experience definition/state model
- [ ] 03.2 Reusable `EitherOr` step
- [ ] 03.3 Reusable `ImageDuel` step
- [ ] 03.4 Reusable `Ranking` step
- [ ] 03.5 Reusable `PartnerPrediction` step
- [ ] 03.6 Reusable `Scenario` + `OpenPrompt` steps
- [ ] 03.7 Reusable `Reveal`/result flow plus compatibility with legacy content during migration

### Definition of Done

The proposal implementation no longer depends on one-off mechanics. The listed step types can be composed into another Harmony Experience without copying the proposal runner, while existing legacy content remains usable during migration.

---

## Stage 04/08 — Existing Proposal/Ring Content Consolidation

**Original plan reference:** Point 3  
**Status:** ⬜ PLANNED  
**Progress:** 0/5 complete (0%)  
**Dependencies:** Stages 02 and 03

### Substages

- [ ] 04.1 Inventory all standalone proposal, engagement-ring and relevant wedding content
- [ ] 04.2 Migrate/reuse strong questions and assets in the new Experience system
- [ ] 04.3 Remove duplicate presentation from normal navigation/catalog surfaces
- [ ] 04.4 Hide/archive legacy standalone packs without deleting source content prematurely
- [ ] 04.5 Regression verification for migrated content, images and navigation

### Definition of Done

Users no longer see redundant proposal/ring experiences as separate competing entries, strong content is preserved, legacy packs are safely hidden/archived rather than destructively deleted, and navigation is verified.

---

## Stage 05/08 — Harmony-360 Questions Quality Rework

**Original plan reference:** Point 4  
**Status:** ⬜ PLANNED  
**Progress:** 0/5 complete (0%)  
**Dependencies:** content architecture can remain legacy-compatible; no requirement to finish Stage 07 first

### Substages

- [ ] 05.1 Relationship / communication / everyday-life sections
- [ ] 05.2 Food / travel / leisure / culture sections
- [ ] 05.3 Future / money / work / family sections
- [ ] 05.4 Psychology / feelings / health / intimacy sections
- [ ] 05.5 Values / belief / society / humor / fantasy / teamwork sections

### Progress example

If 05.1 and 05.2 are complete:

> `360 Rework → Stage 05/08 → 2/5 complete (40%)`

### Definition of Done

The audited Harmony-360 areas are no longer dominated by noun-substitution templates or repeated generic answer quartets. Retained questions are relationship-relevant, concrete, appropriately toned, and worth keeping.

---

## Stage 06/08 — Broken, Duplicate & Low-Quality Content Cleanup

**Original plan reference:** **Point 5 — schlechte Inhalte gezielt entfernen und reparieren**  
**Status:** ⬜ PLANNED  
**Progress:** 0/5 complete (0%)  
**Dependencies:** can run alongside Stage 05 where defects are unambiguous

### Substages

- [ ] 06.1 Missing answer options and malformed questions
- [ ] 06.2 English leftovers and language mismatches
- [ ] 06.3 Typos, awkward translations and broken wording
- [ ] 06.4 Semantic duplicates, repeated stems and repeated generic option quartets
- [ ] 06.5 Brand/franchise cleanup where IP-neutral replacements are preferred

### Progress example requested for handover

If two of these five groups are complete:

> `360 Rework → Stage 06/08 (Original Point 5) → 2/5 complete (40%)`

### Definition of Done

Known malformed, duplicated, linguistically broken and clearly low-quality content from the audit has either been corrected, intentionally removed/archived, or documented as a deliberate exception. No completion claim is made without verification of the affected source/runtime path.

---

## Stage 07/08 — Additional Flagship Harmony Experiences

**Original plan reference:** Point 6  
**Status:** ⬜ PLANNED  
**Progress:** 0/12 complete (0%)  
**Dependencies:** reusable Experience engine from Stage 03

### Candidate experiences / initial substages

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

When an experience becomes active, it may receive its own deeper sub-plan rather than being treated as one undifferentiated bulk task.

### Definition of Done

Each listed flagship experience that remains in scope has a polished mixed-mechanic flow or is explicitly removed from scope with the decision recorded in the worklog.

---

## Stage 08/08 — Automated Content Quality Gate

**Original plan reference:** Point 7  
**Status:** ⬜ PLANNED  
**Progress:** 0/6 complete (0%)  
**Dependencies:** quality rules established through Stages 05 and 06

### Substages

- [ ] 08.1 Detect repeated stems/templates
- [ ] 08.2 Detect repeated generic option quartets
- [ ] 08.3 Detect English leftovers in German content
- [ ] 08.4 Detect missing options / malformed items
- [ ] 08.5 Detect tiny or suspiciously thin packs
- [ ] 08.6 Produce a report usable in CI or local verification

### Definition of Done

The repository has repeatable automated checks that detect the main quality failures identified during 360 Rework and produce an actionable result for local or CI verification.

---

## Update protocol

After every meaningful completed substage/work package, the worker must:

1. Verify the repository state.
2. Update the checkbox, x/y fraction, percentage and status here.
3. Update `360_REWORK_CURRENT_STATE.md` with the exact current substage and `NEXT EXACT ACTION`.
4. Append a dated entry to `360_REWORK_WORKLOG.md`.
5. Record branch, PR, commit/merge commit, tests/build state, verification gaps and blockers.
6. Never silently rewrite the master plan; record scope/ordering changes in the worklog.
