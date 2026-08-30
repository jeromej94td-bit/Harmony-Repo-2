# 360 Rework — Current State

**Last updated:** 2026-08-30  
**Project:** 360 Rework  
**Completed green core stages:** 1/8  
**Core Stage 02 status:** 🧪 IMPLEMENTATION + CONTRACT COVERAGE COMPLETE; GREEN ANDROID BUILD PENDING INFRASTRUCTURE  
**Stage-02 package coverage:** 12/12 = 100%  
**Latest Stage-02 package:** 24.17 / PR #79, merge `571ec3b4b29f3bb8982837a8bd1d12914b1f1fca`  
**Current functional core stage:** Stage 03/08 — Reusable Harmony Experience Engine  
**Stage-03 progress after PR #126:** 6/7  
**Current Stage-03 substage after PR #126:** 03.7 — Reusable `Reveal`/result flow  
**Stage-05 progress:** **5/5 = 100%**  
**Stage 05.1:** ✅ 7/7 = 100%  
**Stage 05.2:** ✅ 6/6 = 100%  
**Stage 05.3:** ✅ complete  
**Stage 05.4:** ✅ complete  
**Stage 05.5:** ✅ complete — audit baseline PR #169; authoritative post-audit correction PR #178, merge `5e309627ec495bae4031173938b6ef8fea45bc8b`  
**Next Stage-05 action:** none; reopen only for a demonstrated regression

## Source-of-truth rule

PR #24 remains an **umbrella/reference PR only** and must never be merged wholesale. `24.x`, `25.x` and `26.x` are logical 360 Rework work-package IDs; real GitHub PR numbers remain integers.

Parallel branches count only after they reach `main`. Narrow packages must preserve all newer main changes and may not bundle unrelated runtime work.

## Stage 02 — latest authoritative state

All twelve Stage-02 implementation/contract slices are on `main`:

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

PR #79 adds UI/Robolectric, ring-asset and 35-subround end-to-end contracts and records the deterministic path to `ProposalReveal`. It explicitly does **not** provide a green Android/Gradle build because repository Actions still fail before executable step 1. A later real Android build remains an infrastructure verification check, not another feature slice.

## Stage 03 — Reusable Harmony Experience Engine

### 03.1 Generic mixed-step experience foundation

**Status:** ✅ DONE  
**Package:** 26.1 / PR #81  

PR #81 introduced:

- generic `ExperienceStepKind`, `ExperienceStep` and `ExperienceDefinition`;
- generic `ExperiencePosition` and deterministic `ExperienceNavigator`;
- fail-fast flow validation and safe invalid-position handling;
- feature-owned item-count resolution with non-positive counts normalized to one;
- `ProposalExperienceAdapter` compatibility layer;
- proposal navigation/progress delegation without moving proposal content or UI state;
- contract/parity coverage locking the existing nine proposal steps, 35 navigable positions and shipped progress ratios.

### 03.2 Reusable Either-Or step

**Status:** ✅ DONE  
**Package:** 26.2 / PR #85  
**Merge:** `4ef6d583144df4f59c59daef301ffe2596b89b7b`  

PR #85 introduced:

- reusable `ExperienceEitherOrRound` with fail-fast validation;
- a narrow proposal-to-generic adapter so proposal content stays proposal-owned;
- stateless `ExperienceEitherOrBoard` with caller-owned selection state;
- explicit first/second choice tags and selection semantics;
- proposal integration by replacing the old private Either-Or pane with the generic board;
- model and Compose contracts while preserving the existing 11 proposal Either-Or rounds, text, order and direct advance behavior.

### 03.3 Reusable ImageDuel step

**Status:** ✅ DONE  
**Package:** 26.3 / PR #87  
**Merge:** `704fa2a14732ed8141abb7d98da2018d27853aeb`  

PR #87 introduced:

- reusable `ExperienceImageDuelOption` and `ExperienceImageDuelRound` with fail-fast validation;
- narrow proposal adapters for both location duels and ring duels;
- stateless `ExperienceImageDuelBoard` plus `AnimatedExperienceImageDuelBoard`;
- the shipped proposal-location choreography moved into the generic board: cards first, question after 620 ms, selection lock, then 420 ms hold + 760 ms 3D transition;
- `ProposalLocationDuelBoard` retained as a compatibility wrapper with the existing `proposal_location_*` tags and callbacks;
- proposal ring rounds now use the same generic board while preserving existing drawable keys and saved asset-key answers;
- the old private proposal ring-card renderer was removed.

TDD/verification evidence for 26.3: generic model contracts landed before production code; focused Kotlin RED compilation failed on the absent generic image-duel types, followed by a passing isolated Kotlin model/adapter harness. Existing proposal-location tests continue to specify legacy tags, timing, selection-lock and the three-round sequence. A new Robolectric Compose contract covers generic rendering, stable tags and the pick callback. GitHub reported the final PR conflict-free and 0 commits behind `main`; no green full Android/Gradle/Robolectric run is claimed because repository CI has no runnable status checks/credits.

### 03.4 Reusable Ranking step

**Status:** ✅ DONE  
**Package:** 26.4 / PR #99  
**Merge:** `958ba77670958d34ff2eecb581ab159114e310bd`  
**Stage-03 progress:** **4/7**

PR #99 introduced:

- reusable `ExperienceRankingItem` and validated `ExperienceRankingRound` with stable item IDs;
- `ExperienceRankingSelectionCodec` as the compatibility bridge to the existing label-based `RankingAnswerCodec`;
- the existing persisted ranking payload format remains unchanged and decodable;
- stateless `ExperienceRankingBoard` wrapping the shipped drag/drop `RankingSlotBoard` instead of copying its gestures or layout;
- proposal priorities now use the generic ranking board while preserving the same five priority IDs, labels, order semantics and reveal input;
- a dedicated wrapper test tag plus existing `ranking_slot_*` tags remain available for UI contracts.

TDD/verification evidence for 26.4: RED isolated Kotlin compilation confirmed the generic ranking API was absent; GREEN isolated Kotlin model/adapter verification produced `EXPERIENCE_RANKING_MODEL_PASS`. A Robolectric/Compose contract for the wrapper is present but was not executed here because repository GitHub Actions/credits are unavailable. No green full Android/Gradle build is claimed.

### 26.5 — Reusable `PartnerPrediction` step

**Status:** ✅ DONE  
**Package:** 26.5 / PR #120  
**Merge:** `8100488082eacf1313d2aa88891c049335b5e6d1`

Adds the typed reusable prediction round/selection model and adapter, routes the proposal screen through the reusable board, and preserves the existing A → B → Reveal flow and encoded persistence format. Static compatibility review passed; no full Android build is claimed.

### 26.6 — Reusable `Scenario` + `OpenPrompt` steps

**Status:** ✅ DONE  
**Package:** 26.6 / PR #126  
**Merge:** `9470b8c84d15463a2076320c967c970d6be3854d`

Adds validated generic Scenario/OpenPrompt rounds, proposal adapters and reusable boards. Existing scenario result choreography, open-prompt copy, answer gating and test tags remain unchanged. Static source review passed; no long-running GitHub test was run.

### NEXT EXACT ACTION — core

Begin **03.7 — Reusable `Reveal`/result flow plus legacy compatibility during migration** as the next narrow 26.x package. Keep the current reveal output stable, preserve legacy compatibility and avoid navigation redesign or deletions.

## Parallel Stage 05 — Questions Quality Rework

**Status:** ✅ COMPLETE  
**Overall progress:** **5/5 = 100%**  
**Audit baseline:** 25.26 / PR #169 / merge `aab3acb80b245e2d4520a63ee36f91461a421dcf`  
**Authoritative 05.5 correction:** 25.28 / PR #178 / merge `5e309627ec495bae4031173938b6ef8fea45bc8b`  
**Detailed final record:** `docs/360-rework/360_REWORK_STAGE05_FINAL.md`

### 05.1 — Relationship / communication / everyday-life

**Status:** ✅ DONE — 7/7  
Sections 01, 02, 06 and 12 were explicitly curated by stable ID, six confirmed filler/duplicate packs were archived, canonical relationship packs were retained, and `h360_need_now_quick` added 10 concrete two-choice situations.

Package trail: 25.0 PR #59, 25.1 PR #60, 25.2 PR #62, 25.3 PR #64, 25.4 PR #69, 25.5 PR #70, 25.6 PR #71, 25.7 PR #76.

### 05.2 — Food / travel / leisure / culture

**Status:** ✅ DONE — 6/6  
Sections 04, 05, 07 and 14 were converted from generator filler to concrete travel, food, hobby and media questions. The cross-section gate rejects known generic quartets, English generator fragments, source typos and repeated ordinary four-option sets.

Package trail: 25.8 PR #97, 25.9 PR #108, 25.10 PR #114, 25.11 PR #117, 25.12 PR #132, 25.13 PR #136.

### 05.3 — Future / family / money / work

**Status:** ✅ DONE  
The final 05.3 contract covers 72 raw packs, 59 visible survivors and 13 archives. Child/parent prompts are conditional, finance content avoids investment instructions, and existing topic-routing moves are regression-protected.

Key finish packages: 25.15 Friends & Family / PR #145, 25.16 Money & Finance / PR #150, 25.17 Work & Career / PR #151, 25.18 cross-section audit / PR #152.

### 05.4 — Health / psychology / intimacy

**Status:** ✅ DONE  
Health & Fitness and Psychology & Feelings were curated down to 17 survivors from 36 raw packs with 19 archives. The existing `naehe` and `intimleben` runtime overrides were reconciled and regression-protected at 12 and 18 curated questions. The central scenario finalizer was moved after content rewrites so scenario packs cannot shrink below eight decisions.

Key finish packages: 25.19 Health / PR #153, scenario-contract fix PR #156, 25.20 Psychology / PR #157, 25.21 intimacy reconciliation / PR #158, 25.22 cross-section audit / PR #159.

### 05.5 — Values / faith / society / humor / fantasy / teamwork

**Status:** ✅ DONE  
Six 18-pack raw sections = 108 raw packs. **Authoritative corrected accounting after 25.28: 51 visible curated packs / 56 genuine archives / 1 merged source.**

- Persönlichkeit & Werte — 8 visible survivors
- Glaube & Religion — 4 visible survivors
- Politik & Gesellschaft — 4 visible survivors
- Humor & Lachen — 10 visible survivors
- Fantasie / Was wäre wenn — 13 visible survivors
- Teamwork & Challenge — **12 visible survivors + 1 merged source**

25.23 protects the already-curated Values/Faith/Society/Fantasy survivor set. 25.24 / PR #164 curates Humor. 25.25 / PR #166 was the initial Teamwork pass. 25.26 / PR #169 created the original final audit. **25.28 / PR #178 supersedes the detailed Teamwork/05.5 counts with the approved 12 REWRITE / 5 ARCHIVE / 1 MERGE model.** `h500_422_mutprobe_wer_eher` is the merged source: its useful travel, food, moral, personality and Teamwork ideas are redistributed into existing destination packs before the mixed source disappears from runtime. The final audit now distinguishes archive IDs from merged-source IDs and protects both against accidental runtime resurrection.

The repository sorting skill permanently requires **re-home before archive** for mixed but useful content. A source removed only after its usable ideas were redistributed must be recorded as `MERGE`, not `ARCHIVE`.

### NEXT EXACT ACTION — Stage 05

None. Stage 05 is closed at **5/5 = 100%**. Reopen only for a demonstrated content regression; new feature or runner work belongs to another stage.

## Newer parallel main changes preserved

The separately developed **Sex & Intimität** rework remains preserved and was explicitly reconciled into Stage 05.4 without rewriting its stable-ID runtime overrides.

Parallel UI/navigation/answer-persistence work remains outside Stage-05 content progress and is preserved by narrow Stage-05 branches.

## Stage 06

Stage 06 remains 0/5 complete. Existing targeted cleanup packages are partial repairs, not completed defect classes.

## Verification caveat

Repository-wide GitHub Actions continue to terminate before executable workflow step 1 or expose no runnable status checks in affected runs, so no green full Android/Gradle result is claimed from those failures. Focused local/static/contract evidence is recorded per narrow package and completed implementation is not left unmerged solely because CI credits/runners are unavailable.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not combine unrelated substages in one PR.
- Do not overwrite newer main changes while reconciling tracker work.
- Do not count unrelated content reworks toward Stage-03 engine progress.
- Do not reopen Stage 02 feature implementation unless a regression is demonstrated.
- Do not reopen completed Stage 05 unless a concrete regression is demonstrated.
- **Do not archive mixed useful content before checking whether it belongs in existing real destination packs; re-home/merge first.**
- Do not claim a green Android build until one actually executes.