# 360 Rework — Current State

**Last updated:** 2026-08-29  
**Project:** 360 Rework  
**Completed core stages:** 1/8  
**Current stage:** Stage 02/08 — 💍 Unser perfekter Antrag  
**Current substage:** 02.3 — Proposal-location image duels  
**Current stage progress:** 0/12 complete (0%)  
**Current status:** 🧪 VERIFY  
**Next Stage-02 work package:** 24.8 — Stage 02.3 Proposal-location image duels  
**Prepared follow-up:** 24.9 — Stage 02.4 Refreshed ring-image duels (PR #46)

## Source-of-truth rule

PR #24 is an **umbrella/reference PR only** and must never be merged wholesale.

`24.x` IDs are logical **360 Rework work-package IDs**. Every real change lands through its own small GitHub PR so it remains independently traceable and revertible.

Parallel Stage-06 work must not reuse logical IDs reserved by Stage 02. In particular:

- **24.7** = Stage-02 proposal merge-status sync via PR #37.
- **24.8** = reserved/next Stage-02 package for proposal-location image duels.
- Parallel Stage-06 continuation uses the non-conflicting **24.6.x** range.

## Stage 02 — active functional path

### 24.2 — Stage 02.1 Experience data model / deterministic proposal flow

**Status:** 🧪 MERGED, BUILD VERIFICATION OPEN  
**PR:** #27  
**Scope:** UI-independent proposal-flow model only.  
**Verification gap:** no Gradle-capable environment has recorded the focused Android build result.

### 24.3 — Stage 02.2 Proposal mood/details rounds

**Status:** 🧪 MERGED, BUILD VERIFICATION OPEN  
**PR:** #31  
**Merge commit:** `3012f1bbcb0c4406e4fb8b34258ddb2a4d88bd5f`  
**Scope:** proposal mood/details Either-Or data only.  
**Verification gap:** no Gradle-capable environment has recorded the focused Android build result.

### NEXT EXACT ACTION — Stage 02

**24.8 / Stage 02.3:** inspect the current image-duel runner and image-resource pipeline, then implement only proposal-location image duels.

Do **not** bundle refreshed-ring duels, ranking, partner prediction, scenarios, reveal, broad navigation changes or legacy cleanup into 24.8.

## Parallel Stage 06 cleanup already merged

Stage 06 is allowed to progress in parallel only where its files/scope do not collide with active Stage-02 work.

- **24.4 — 06.1 partial:** PR #35, merge `817d0a0c002aeac7a560f31513092957d242880a` — repairs missing options in `ichhabenochnie` content.
- **24.5 — 06.2 partial:** PR #33, merge `4e49989504824987933ebcf4587b58a7a8f8d0c1` — removes the confirmed accidental English ranking-template prompt.
- **24.6 — 06.3 partial:** PR #34, merge `cf8b18a319613919d2f50d5853ac25a4ce065016` — repairs confirmed wording/typo defects.
- **24.6.2 — 06.4 audit helper:** PR #39, merge `d4dc969bf5a47d4be5b5470157fe3750cace21d1` — repetition audit helper; focused Python tests 2/2 passed.
- **24.6.3 — 06.4 targeted cleanup:** PR #40, merge `ee2ca1b8315be2a8a9db45e78328d49ba3706e39` — de-templates the repeated opener in Morgenroutine, Sportliche Ziele and Bücher; focused Kotlin verification passed.
- **24.6.4 — project-control correction:** PR #41, merge `963fb04aee6b9b9899c61819e764f96b67eb5aa1` — resolves the concurrent 24.7/24.8 numbering collision without changing app code.
- **24.6.5 — 06.5 partial:** PR #43, merge `785e93c94f93cb43903b2247e677920839f9f2af` — turns the user-facing `cj_hogwarts_quiz` content into the IP-neutral `Magische Akademie` presentation while retaining the internal pack id; focused Kotlin red/green verification passed.
- **24.6.6 — 06.5 partial:** PR #44, merge `6a160ff1a5724ddf4789ad7419e095dadac277f2` — neutralizes the user-facing franchise references in `cj_disney_quiz`; all 15 actual questions passed the focused Kotlin neutralization check.

### Stage 06 accounting

**Stage 06 remains 0/5 complete (0%).**

The packages above are confirmed partial repairs. Do not mark 06.1–06.5 complete until the corresponding wider audit shows that the defect class is exhausted or remaining exceptions are explicitly documented.

For 06.5 specifically, two known high-franchise legacy packs are now neutralized, but the rest of the generated/legacy catalogue still needs a brand/franchise audit before 06.5 can be checked off.

## Verification caveat

The repo-wide GitHub Actions repeatedly fail before executable workflow step 1 (`steps: null`, no job logs). This remains an infrastructure verification caveat:

- it is **not** a green full-suite result;
- it is **not** evidence of an application regression by itself;
- focused local tests/verifications are recorded separately per small package.

## DO NOT REPEAT

- Do not merge umbrella PR #24 wholesale.
- Do not combine unrelated 360 Rework substages into one PR.
- Do not reuse logical IDs 24.7 or 24.8 for Stage-06 work.
- Do not rebuild/remerge completed cleanup packages 24.4–24.6.6 unless a regression is proven.
- Do not replace the refreshed ring assets again unless a regression is proven.
- Do not delete proposal/ring/wedding source content yet; consolidation belongs to Stage 04.
- Do not add Daily back into this scope.
- Do not mark Stage 02 or Stage 06 progress complete from implementation alone when required verification remains open.

## Handover

Read `docs/360-rework/360_REWORK_MASTER_ROADMAP.md` for the full stage plan and Definitions of Done.

Read `docs/360-rework/360_REWORK_WORKLOG.md` for historical detail. PRs #43 and #44 are the authoritative merge records for the latest 06.5 partial cleanup until their full append-only worklog entries are added in a later documentation-safe sync.
