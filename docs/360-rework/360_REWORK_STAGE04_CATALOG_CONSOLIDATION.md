# 360 Rework — Stage 04.3 Catalogue Consolidation

**Scope:** remove duplicate standalone proposal/ring/wedding presentation from normal user-facing catalogue surfaces.  
**Source definitions:** preserved.  
**Direct/runtime compatibility:** preserved through the complete `HarmonyPacksData.PACKS` set.

## Hidden standalone catalogue entries

Stage 04.3 hides exactly the five sources inventoried in 04.1 and migrated or superseded by 04.2 / Stage 02:

- `antrag` — Der perfekte Heiratsantrag
- `ringe` — Verlobungsringe
- `straeusse` — Hochzeitssträuße
- `traumhochzeit` — Traumhochzeit
- `h500_060_hochzeit_offene_runde` — Hochzeit – Offene Runde

No source pack is deleted in this slice.

## Presentation boundary

`HarmonyPacksData.PACKS` remains the complete runtime/source-compatible set.

`HarmonyPacksData.CATALOG_PACKS` is the filtered user-facing view. It is backed by `ProposalLegacyCatalogueVisibility`, whose hide set must equal both:

1. the complete Stage-04 inventory; and
2. the set already migrated by `ProposalLegacyExperienceMigration` plus the Stage-02-superseded `antrag` pack.

This makes it impossible to hide a new legacy pack merely by adding an arbitrary ID to the presentation filter.

## User-facing surfaces switched to `CATALOG_PACKS`

- Games search results
- Topic progress calculations
- Unanswered-questions dialog
- Topic/category pack lists

The category rail, generated personal games, card visuals and animation behavior are unchanged.

## Compatibility preserved

Because the source/full runtime set is not modified:

- existing saved answers remain addressable by their historical pack IDs;
- direct pack-ID lookups remain possible where intentionally used;
- Dev Studio/source tooling can still inspect the legacy definitions;
- Stage 04.4 can archive the legacy sources non-destructively with explicit compatibility rules.

## Regression contract

`ProposalLegacyCatalogueVisibilityTest` verifies that:

- the hidden set equals the Stage-04 inventory exactly;
- none of the five legacy entries appears in the user-facing catalogue;
- their source definitions still exist in default/generated registries;
- ordinary packs remain visible.

## Next exact action — 04.4

Archive the five standalone legacy sources non-destructively while preserving any compatibility required for historical answers, source tracing and development tooling. Archive behavior must not silently reintroduce the packs into the normal catalogue.