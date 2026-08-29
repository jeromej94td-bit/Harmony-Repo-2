# 360 Rework — Master Roadmap

## Project snapshot

**Project:** 360 Rework  
**Core stages:** 8  
**Completed green core stages:** 1/8  
**Stage 02 package coverage:** 12/12 = 100%  
**Stage 02 status:** 🧪 IMPLEMENTATION/CONTRACT COMPLETE; GREEN ANDROID BUILD PENDING INFRASTRUCTURE  
**Current functional core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-03 progress after PR #87:** 3/7  
**Next core substage after PR #87:** 03.4 — Reusable `Ranking` step  
**Stage-05 progress:** 1/5 = 20%

> Stage 02 has all twelve feature/contract slices on `main`. PR #79 closes 02.12 with available static/UI/end-to-end contracts but explicitly does not claim a green Android/Gradle build because Actions still fail before step 1. A later successful Android build is an infrastructure verification check, not a new feature slice.

## Visual stage board

| Stage | Original plan reference | Area | Status | Progress |
|---|---|---|---|---:|
| 01/08 | prerequisite | Ring Image Quality Rework | ✅ DONE | 1/1 = 100% |
| 02/08 | Point 1 | 💍 Unser perfekter Antrag | 🧪 CONTRACT-COMPLETE / BUILD CAVEAT | 12/12 slices = 100% |
| 03/08 | Point 2 | Reusable Harmony Experience Engine | 🟣 ACTIVE | 3/7 after PR #87 |
| 04/08 | Point 3 | Existing Proposal/Ring Content Consolidation | ⬜ PLANNED | 0/5 |
| 05/08 | Point 4 | Harmony-360 Questions Quality Rework | 🟡 PARALLEL ACTIVE | 1/5 = 20% |
| 06/08 | Point 5 | Broken, Duplicate & Low-Quality Content Cleanup | 🟡 PARTIAL PARALLEL REPAIRS | 0/5 |
| 07/08 | Point 6 | Additional Flagship Harmony Experiences | ⬜ PLANNED | 0/12 |
| 08/08 | Point 7 | Automated Content Quality Gate | ⬜ PLANNED | 0/6 |

---

## Stage 01/08 — Ring Image Quality Rework

**Status:** ✅ DONE  
**Progress:** 1/1 = 100%

- [x] 01.1 Replace the 10 prioritized engagement-ring WebPs with refreshed versions and keep the merged diff asset-only.

Evidence: PR #22, merge `b696acb25f8f9b52235a6fba256ec9dc041edab9`.

---

## Stage 02/08 — 💍 Unser perfekter Antrag

**Original plan reference:** Point 1  
**Status:** 🧪 CONTRACT-COMPLETE / ANDROID BUILD CAVEAT  
**Package coverage:** 12/12 = 100%

- [x] 02.1 Experience data model and deterministic proposal flow — PR #27.
- [x] 02.2 Mood/details Either-Or — PR #31.
- [x] 02.3 Proposal-location image duels — PR #55 plus app routing.
- [x] 02.4 Refreshed ring-image duels — PR #48, integrated by PR #77.
- [x] 02.5 Drag/drop proposal-priority ranking — PR #49, integrated by PR #77.
- [x] 02.6 Partner prediction A → B → Reveal — PR #50, integrated by PR #77.
- [x] 02.7 Concrete proposal scenarios — PR #56.
- [x] 02.8 Open personal prompts — PR #58.
- [x] 02.9 Qualitative `Euer perfekter Antrag` reveal — PR #63.
- [x] 02.10 Reuse strongest existing proposal/ring/wedding content — PR #68.
- [x] 02.11 Entry/navigation + complete fullscreen runner — PR #77, merge `82c951c53e42f4d6a76a61ee3596d4e36ef4dd05`.
- [x] 02.12 Verification contracts — PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`; UI/Robolectric, ring-asset and 35-subround journey contracts added. Green Android/Gradle execution remains unavailable because repository Actions fail before step 1.

### Definition of Done / caveat

Feature implementation and available contract coverage are complete. A genuine Android/Gradle build must still be run once runner infrastructure works; until then Stage 02 must not be described as having a green full build.

---

## Stage 03/08 — Reusable Harmony Experience Engine

**Original plan reference:** Point 2  
**Status:** 🟣 ACTIVE  
**Progress after PR #87:** 3/7

- [x] **03.1 General mixed-step experience definition/state model** — 26.1 / PR #81. Adds generic step kinds, immutable validated experience definitions, generic position/navigation/progress, safe invalid-position handling, content-owned item-count resolution, and a proposal compatibility adapter. Proposal remains nine steps / 35 positions with locked ratio parity.
- [x] **03.2 Reusable `EitherOr` step** — 26.2 / PR #85, merge `4ef6d583144df4f59c59daef301ffe2596b89b7b`. Adds validated generic two-choice rounds, proposal adapter, stateless reusable Compose board, explicit selection semantics and proposal integration while preserving all 11 shipped proposal Either-Or rounds.
- [x] **03.3 Reusable `ImageDuel` step** — 26.3 / PR #87, merge `704fa2a14732ed8141abb7d98da2018d27853aeb`. Adds generic image-duel options/rounds, shared static + animated boards, location/ring adapters, keeps the shipped location timing/tags through compatibility wrappers, and moves proposal ring duels onto the same reusable visual mechanic while preserving saved asset-key answers.
- [ ] **03.4 Reusable `Ranking` step** — 🔵 NEXT.
- [ ] 03.5 Reusable `PartnerPrediction` step
- [ ] 03.6 Reusable `Scenario` + `OpenPrompt` steps
- [ ] 03.7 Reusable `Reveal`/result flow plus legacy compatibility during migration

### 03.1–03.3 verification note

26.1–26.3 follow test-first sequencing. Focused Kotlin RED compilation confirmed each generic API was absent before implementation; subsequent isolated Kotlin harnesses pass the model/navigation/adapter contracts. Existing proposal-location tests remain the compatibility contract for the shipped staged image-duel choreography, and 26.3 adds a Robolectric Compose contract for the generic image-duel surface. The full Android/Gradle/Robolectric suite is not claimed green while GitHub Actions/runners remain unavailable or expose no runnable status checks.

### NEXT EXACT ACTION

Implement **03.4 — Reusable `Ranking` step** as a narrow package. Reuse the existing drag/drop ranking board and `RankingAnswerCodec`, keep proposal priority IDs/labels and persistence semantics stable, and do not pull PartnerPrediction, Scenario, OpenPrompt or Reveal into that slice.

Definition of Done: the proposal implementation becomes the reference for reusable mixed-step experiences without copying one-off runners; legacy content remains usable during migration.

---

## Stage 04/08 — Existing Proposal/Ring Content Consolidation

**Original plan reference:** Point 3  
**Status:** ⬜ PLANNED  
**Progress:** 0/5  
**Dependencies:** Stage 02 implementation plus Stage 03 reusable engine

- [ ] 04.1 Inventory all standalone proposal, ring and relevant wedding content
- [ ] 04.2 Migrate/reuse strong questions and assets in the Experience system
- [ ] 04.3 Remove duplicate presentation from normal navigation/catalog surfaces
- [ ] 04.4 Hide/archive legacy standalone packs without destructive source deletion
- [ ] 04.5 Regression verification for migrated content, images and navigation

---

## Stage 05/08 — Harmony-360 Questions Quality Rework

**Original plan reference:** Point 4  
**Status:** 🟡 PARALLEL ACTIVE  
**Progress:** 1/5 = 20%  
**Next:** 05.2 — Food / travel / leisure / culture

- [x] **05.1 Relationship / communication / everyday-life** — 7/7 = 100%. Sections 01/02/06/12: 72/72 explicit decisions; six archived fillers; curated counts 16/17/16/17; plus Quick Game `Was brauchst du gerade?` with 10×2 choices. Final audit PR #76.
- [ ] **05.2 Food / travel / leisure / culture**
- [ ] **05.3 Future / money / work / family**
- [ ] **05.4 Psychology / feelings / health / intimacy**
- [ ] **05.5 Values / belief / society / humor / fantasy / teamwork**

The separately merged Sex & Intimität rework is preserved but is not retroactively counted as completion of 05.4; Stage-05 progress changes only through explicit audited 25.x slices.

Definition of Done: audited areas are no longer dominated by noun-substitution templates or repeated generic answer quartets; retained questions are concrete, relationship-relevant and worth keeping.

---

## Stage 06/08 — Broken, Duplicate & Low-Quality Content Cleanup

**Original plan reference:** Point 5 — schlechte Inhalte gezielt entfernen und reparieren  
**Status:** 🟡 PARTIAL PARALLEL REPAIRS  
**Progress:** 0/5

- [ ] 06.1 Missing answer options and malformed questions
- [ ] 06.2 English leftovers and language mismatches
- [ ] 06.3 Typos, awkward translations and broken wording
- [ ] 06.4 Semantic duplicates, repeated stems and repeated generic option quartets
- [ ] 06.5 Brand/franchise cleanup where IP-neutral replacements are preferred

---

## Stage 07/08 — Additional Flagship Harmony Experiences

**Original plan reference:** Point 6  
**Status:** ⬜ PLANNED  
**Progress:** 0/12

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

After every meaningful completed package:

1. Verify current `main` before updating percentages.
2. Keep implementation/contract coverage separate from green executable build claims.
3. Update `360_REWORK_CURRENT_STATE.md` with exact next action.
4. Append package evidence to the relevant ledger (`360_REWORK_WORKLOG.md`; Stage 05 additionally uses `360_REWORK_STAGE05_WORKLOG.md`).
5. Record branch, real PR, merge SHA when available, verification evidence and caveats.
6. Never silently rewrite scope or count unrelated parallel work.
7. Reconcile narrow tracker packages with the newest `main` before merge.
