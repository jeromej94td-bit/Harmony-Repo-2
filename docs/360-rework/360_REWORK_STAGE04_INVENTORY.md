# 360 Rework — Stage 04.1 Proposal/Ring/Wedding Inventory

**Status:** Stage 04.1 implementation complete on its narrow work branch  
**Scope:** inventory only; no source pack is hidden, deleted or removed from navigation in this slice

## Runtime sources found

| Pack ID | Title | Area | Runtime/source note | Stage-04 direction |
|---|---|---|---|---|
| `antrag` | Der perfekte Heiratsantrag | Proposal | Default legacy pack | Stage 02 already supersedes its two broad questions; remove duplicate catalogue surface later and archive non-destructively. |
| `ringe` | Verlobungsringe | Rings | Exists in `Models.kt` and as a same-ID `GeneratedHarmonyContent` override | Strong ring concepts are already reused by Stage 02.4; verify remaining useful labels/assets in 04.2, then consolidate the duplicate standalone surface. |
| `straeusse` | Hochzeitssträuße | Wedding | Default legacy pack | Review bouquet preferences for reuse in a richer experience before archiving the standalone pack. |
| `traumhochzeit` | Traumhochzeit | Wedding | Default legacy pack | Migrate the strongest wedding-day preferences in 04.2, then remove duplicate presentation. |
| `h500_060_hochzeit_offene_runde` | Hochzeit – Offene Runde | Wedding | Harmony-360 generated pack, retained and rewritten by the Stage-05 Future curation | Reuse the six curated open wedding prompts in the Experience system before catalogue consolidation. |

## Important findings

1. Stage 02.10 correctly tracked four legacy sources, but Stage 04 must additionally account for `h500_060_hochzeit_offene_runde`; otherwise a separate wedding path would remain after consolidating the legacy packs.
2. `ringe` is layered: the stable ID exists in the default model and is also supplied by generated content. Stage 04 must preserve the effective runtime content/assets rather than assuming the default definition is the only source.
3. Stage 04.1 is deliberately non-destructive. Navigation/catalog removal belongs to 04.3 and source archiving to 04.4, after strong material is migrated in 04.2.

## Contract

`ProposalLegacyContentInventoryTest` scans current default/expansion and generated registries using stable ID/title/tag metadata. Every matching standalone proposal/ring/wedding pack must be represented exactly once in `ProposalLegacyContentInventory`.

This means a newly shipped pack with explicit proposal/wedding/ring metadata cannot silently bypass Stage-04 consolidation without updating the inventory contract.

## Next exact action — 04.2

Migrate/reuse strong content in the reusable Experience system:

- verify the ring reuse manifest against the effective runtime `ringe` content/assets;
- select any bouquet/wedding-day preferences worth retaining from `straeusse` and `traumhochzeit`;
- adapt the six curated `h500_060_hochzeit_offene_runde` prompts into a reusable Experience-owned wedding/open-prompt source without duplicating text;
- preserve stable source IDs until 04.3/04.4 remove duplicate presentation and archive the legacy standalone packs.
