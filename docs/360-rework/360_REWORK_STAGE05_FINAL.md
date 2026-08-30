# 360 Rework — Stage 05 Final State

**Date:** 2026-08-30  
**Stage:** 05 — Harmony-360 Questions Quality Rework  
**Progress:** **5/5 = 100%**  
**Status:** ✅ content curation and deterministic regression coverage complete  
**Final audit baseline:** 25.26 / PR #169 / merge `aab3acb80b245e2d4520a63ee36f91461a421dcf`  
**Post-audit correction:** 25.28 / PR #178 / merge `5e309627ec495bae4031173938b6ef8fea45bc8b`  
**Build caveat:** no full Android/Gradle green is claimed without a fresh executable full-suite runner result.

## Completion map

### 05.1 — Relationship core

Completed explicit curation for relationship closeness, communication, everyday/home, conflict/reconnection and the `Was brauchst du gerade?` Quick Game. This established the stable-ID curation/archive pattern used by the later slices.

### 05.2 — Travel / food / leisure / culture

Completed Sections 04, 05, 07 and 14. Subject-specific questions replace generic noun-substitution and relationship/value quartets.

### 05.3 — Future / family / money / work

Completed Future, Friends & Family, Money & Finance, and Work & Career. The final 05.3 contract protects 72 raw packs, 59 runtime survivors and 13 archives. Child/parent prompts are conditional, money content avoids investment instructions, and topic moves are regression-protected.

### 05.4 — Health / psychology / intimacy

Completed Health & Fitness and Psychology & Feelings, plus reconciliation of the existing `naehe` and `intimleben` runtime overrides. The final 05.4 audit protects 36 raw Health/Psych packs, 17 survivors and 19 archives; the two intimacy overrides retain 12 and 18 curated questions.

### 05.5 — Values / faith / society / humor / fantasy / teamwork

Six 18-pack raw sections = **108 raw packs**:

- Persönlichkeit & Werte — 8 visible survivors
- Glaube & Religion — 4 visible survivors
- Politik & Gesellschaft — 4 visible survivors
- Humor & Lachen — 10 visible survivors
- Fantasie / Was wäre wenn — 13 visible survivors
- Teamwork & Challenge — **12 visible survivors + 1 merged source**

Final corrected 05.5 accounting after 25.28: **51 visible curated packs / 56 genuine archives / 1 merged source**.

The merged source is `h500_422_mutprobe_wer_eher`. It is not classified as low-value archive content. Its useful ideas are redistributed into existing real destination packs before the mixed source pack is removed from the Teamwork runtime.

## Teamwork final decision ledger

**12 REWRITE:**
`h500_411_zusammenhalt_entweder_oder`, `h500_412_krisenmodus_wer_eher`, `h500_413_teamgeist_skala`, `h500_414_rollenverteilung_ranking`, `h500_416_escape_room_szenario`, `h500_417_geheimes_ziel_geheime_wahl`, `h500_418_groesster_triumph_memory`, `h500_419_paarchallenge_prioritaet`, `h500_421_wettbewerb_entweder_oder`, `h500_425_gemeinsamer_sieg_prognose`, `h500_426_notfallplan_szenario`, `h500_430_team_zukunft_offene_runde`.

**5 ARCHIVE:**
`h500_415_blindes_vertrauen_prognose`, `h500_420_unschlagbar_offene_runde`, `h500_423_durchhaltevermoegen_skala`, `h500_424_staerken_ranking`, `h500_427_mutiger_traum_geheime_wahl`.

**1 MERGE:**
`h500_422_mutprobe_wer_eher`.

## Mutprobe redistribution

Because Harmony routes at pack level rather than question level, the mixed `Mutprobe` source must not simply be mislabeled as one category. 25.28 explicitly re-homes its useful ideas:

- Bungee/Fallschirmspringen and cold-water courage → `h500_085_abenteuerurlaub_prognose` / Reisen.
- Wrong restaurant order / speaking up → `h500_119_restaurantwahl_prioritaet` / Essen.
- Unknown dishes while travelling → `h500_103_streetfood_skala` / Essen.
- Intervening when someone is treated unfairly → `h500_340_gerechtigkeit_offene_runde` / Moral.
- Speaking spontaneously before many people and everyday courage → `h500_272_charaktereigenschaften_wer_eher` / Kennenlernen.
- Encouraging a self-chosen comfort-zone step without pressure → `h500_413_teamgeist_skala` / Teamwork.

The source-to-destination mapping lives in `Harmony360MutprobeRedistributionCuration` and is regression-protected. The repository sorting skill now permanently codifies the rule **re-home useful content before archive**; a mixed source removed after redistribution is classified as `MERGE`, not `ARCHIVE`.

## Final Stage-05.5 quality gate

`Harmony360Stage055FinalAudit` protects against:

- archived IDs becoming visible again;
- merged source IDs remaining visible as duplicates;
- legacy/non-visible topic IDs;
- known generic copy-paste quartets;
- English `Rank` / `Rankt` / generator residue;
- obsolete literal `User/Partner` answer text in curated Teamwork content;
- the removed named reality-TV reference;
- ordinary identical four-option sets reused across three or more different packs, except the intentional `{user}/{partner}/Beide/Niemand` mechanic set;
- scenario packs ending with anything other than eight decisions.

A negative regression fixture deliberately injects `Alpha/Beta/Gamma/Delta` into three final packs and requires the audit to report the copied set.

## Final 05.5 packages

- 25.23 — Values / Faith / Society / Fantasy regression audit: same final blobs landed on `main`; redundant PR #162 was closed.
- 25.24 — Humor & Lachen: PR #164, merge `ac501f022c46211541e6d61a4a75be1d231e313b`.
- 25.25 — Teamwork & Challenge initial pass: PR #166, merge `721f33a9e11a010d674b5e39c67710ca64c4ca9b`; later corrected by 25.28 after the approved re-home-before-archive decision.
- 25.26 — original final 05.5 audit: PR #169, merge `aab3acb80b245e2d4520a63ee36f91461a421dcf`; counts superseded by 25.28.
- 25.27 — tracker sync based on the pre-correction 25.26 counts; detailed 05.5 figures superseded by 25.28/25.29.
- **25.28 — authoritative Teamwork/Mutprobe correction: PR #178, merge `5e309627ec495bae4031173938b6ef8fea45bc8b`.**

## Runtime rules preserved

- No new visible topic categories were introduced.
- Raw section files remain available for traceability; curation is applied in the runtime pipeline.
- `Harmony360MutprobeRedistributionCuration` runs after the existing destination content curations and before `Harmony360TeamworkSectionCuration`, so approved ideas are re-homed before the mixed source is merged out.
- `Harmony360ScenarioJourneyCuration` runs after content rewrites and before final topic sorting, preserving the eight-step fullscreen scenario contract.
- Generated packs are rebuilt from `GeneratedContentRegistry` during app initialization; the persisted generated-version value in `DeveloperDataManager` controls generated image-file refresh and is not required merely to expose changed question copy.
- `GeneratedHarmonySexIntimacyRework.PACKS` remains the last stable-ID content override in `GeneratedContentRegistry`.

## Next

Stage 05 stays closed at 5/5 unless a concrete regression is demonstrated. The corrected 05.5 accounting is 51 visible / 56 archived / 1 merged source. Future cleanup/sorting must follow the repository rule: **reuse or re-home valuable content before considering archive**.
