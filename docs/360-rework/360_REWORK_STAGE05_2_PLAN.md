# 360 Rework — Stage 05.2 Food / Travel / Leisure / Culture

**Stage:** 05.2 — Food / travel / leisure / culture  
**Scope:** Harmony-360 Sections 04, 05, 07 and 14  
**Raw target:** 4 generated section objects, expected 18 packs each / 72 packs total  
**Policy:** generated source remains intact; curation is explicit by stable pack ID in narrow 25.x packages.

## Scope

| Section | Source object | Tag | Theme |
|---|---|---|---|
| 04 | `GeneratedHarmonyAdrenaline360Section04ReisenAbenteuer` | `h360_section_04_reisen_abenteuer` | Reisen & Abenteuer |
| 05 | `GeneratedHarmonyAdrenaline360Section05EssenGenuss` | `h360_section_05_essen_genuss` | Essen & Genuss |
| 07 | `GeneratedHarmonyAdrenaline360Section07FreizeitHobbys` | `h360_section_07_freizeit_hobbys` | Freizeit & Hobbys |
| 14 | `GeneratedHarmonyAdrenaline360Section14KulturMedien` | `h360_section_14_kultur_medien` | Kultur & Medien |

## Audit findings that define the rework

The first source pass confirms the same generator artifacts that Stage 05 is intended to remove:

- **Travel:** otherwise useful topics are repeatedly wrapped in generic scenario, memory, priority and deep-talk templates rather than travel-specific decisions.
- **Food:** several packs reuse generic relationship/value options that do not match the food topic. Examples include broad `Nähe / Freiheit / Sicherheit / Abenteuer` prediction choices and generic ranking quartets around Fine Dining.
- **Leisure:** raw Section 07 still contains an English sentence in the `Serien & Filme` source and multiple noun-substitution templates. Existing runtime cleanup must be preserved rather than duplicated.
- **Culture/media:** `Musikgeschmack` and other packs reuse generic value/planning quartets; the raw `Museen` ranking contains the known English ranking template that is already handled by `GeneratedHarmony360TextCleanup` at runtime.

These findings are inventory evidence, not final Keep/Rewrite/Archive decisions. Every archive decision must be explicit and must retain a stronger canonical alternative.

## Package sequence

### 25.8 / 05.2a — Scope + curation infrastructure

- Add `Harmony360FoodTravelLeisureCultureQualityRework` as the explicit Stage-05.2 boundary.
- Lock the four section tags in a focused unit contract.
- Verify unrelated and not-yet-curated target packs remain value-equivalent.
- Add a raw inventory contract for the four source sections and unique IDs.
- Do not wire behavior-changing overrides yet.

Expected Stage-05.2 progress after merge: **1/6 = 16.7%**.

### 25.9 / 05.2b — Reisen & Abenteuer

- Make an explicit Keep / Rewrite / Archive decision for every Section-04 pack.
- Replace generic scenario/memory/priority wording with real travel choices: pace, planning, budget comfort, accommodation, transport, exploration, rest, risk tolerance and compromise during trips.
- Preserve distinct trip formats instead of collapsing genuinely different travel preferences.

Expected progress after merge: **2/6 = 33.3%**.

### 25.10 / 05.2c — Essen & Genuss

- Make an explicit decision for every Section-05 pack.
- Replace generic relationship quartets with food-specific preferences: taste, sharing, cooking roles, restaurant style, adventurousness, dietary boundaries, comfort food, spice, breakfast/brunch and special-occasion dining.
- Remove or merge filler only where a stronger canonical food pack covers the same decision space.

Expected progress after merge: **3/6 = 50%**.

### 25.11 / 05.2d — Freizeit & Hobbys

- Make an explicit decision for every Section-07 pack.
- Curate around actual leisure compatibility: active/passive time, gaming, sport, books, concerts, festivals, museums, solo-vs-shared hobbies and trying the partner's interests.
- Preserve the existing runtime English-cleanup behavior and eliminate the underlying low-quality wording from curated output without creating a second cleanup system.

Expected progress after merge: **4/6 = 66.7%**.

### 25.12 / 05.2e — Kultur & Medien

- Make an explicit decision for every Section-14 pack.
- Replace generic safety/freedom/adventure/comfort substitutions with concrete media/culture questions covering music, streaming, cinema, books, museums, concerts, social media and cultural discovery.
- Keep strong distinctions from Section 07; merge/archive only true semantic duplicates.

Expected progress after merge: **5/6 = 83.3%**.

### 25.13 / 05.2f — Cross-section audit + tracker

- Assert 72/72 raw target packs have explicit decisions.
- Assert curated IDs are unique, archived IDs are absent and canonical replacements remain.
- Reject known generic filler quartets and English leftovers in curated Stage-05.2 output.
- Verify non-05.2 content remains unchanged by the Stage-05.2 transform.
- Update master roadmap, current state and Stage-05 worklog with verified final counts.

Expected progress after merge: **6/6 = 100%**; Stage 05 overall becomes **2/5 = 40%**.

## Constraints

- Do not edit the four large generated source files destructively.
- Do not absorb Stage 03 Experience-engine work or the active Ranking branch.
- Do not reopen completed Stage 05.1 unless a regression is proven.
- Do not undo current topic-normalization, relationship curation, scenario-journey curation, `NormensLoeschungen`, or runtime text/scenario cleanup.
- No archive by heuristic. Pack IDs and reasons must be explicit and regression-tested.
- Prefer fewer strong, concrete questions over eight repetitive template questions.
