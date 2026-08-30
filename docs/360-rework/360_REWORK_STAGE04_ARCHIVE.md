# 360 Rework — Stage 04.4 Non-destructive Legacy Archive

**Scope:** archive the five consolidated proposal/ring/wedding standalone packs without deleting their shipped source definitions.  
**Catalogue:** already consolidated by Stage 04.3.  
**Source deletion:** none.

## Archived stable IDs

- `antrag`
- `ringe`
- `straeusse`
- `traumhochzeit`
- `h500_060_hochzeit_offene_runde`

`ProposalLegacyArchiveRegistry` is the authoritative Stage-04 archive manifest for these IDs.

## Archive semantics

An archived legacy pack:

1. is no longer a normal user-facing catalogue destination;
2. has an explicit archive reason and replacement Experience path;
3. retains its stable source ID;
4. retains its original source definition in the appropriate registry;
5. remains available for historic answer references, migration traceability, direct-id compatibility and developer tooling.

Archive therefore does **not** mean source deletion.

## Source compatibility

The registry preserves the source classification already established by Stage 04.1:

- `DEFAULT`: source must remain in `HarmonyPacksData.DEFAULT_PACKS`;
- `DEFAULT_WITH_GENERATED_OVERRIDE`: the default source and generated override both remain resolvable;
- `GENERATED_360`: source remains resolvable through `GeneratedContentRegistry.PACKS`.

The archive layer does not move or rewrite any of these definitions.

## Safety contracts

`ProposalLegacyArchiveRegistryTest` verifies:

- exactly the five Stage-04 inventory IDs are archived;
- the archive set stays identical to the Stage-04.3 hidden catalogue set;
- title, area and source metadata stay aligned with the Stage-04.1 inventory;
- every entry has a concrete archive reason and replacement path;
- original source definitions remain resolvable according to their source type;
- archived IDs remain absent from `CATALOG_PACKS` while ordinary packs remain visible.

## Why sources stay shipped

Hard deletion would risk breaking historic saved answers, direct pack-id references, Dev Studio workflows and migration/audit traceability. Stage 04 therefore treats the source definitions as compatibility assets while removing their duplicate user-facing presentation.

## Next exact action — 04.5

Run the Stage-04 regression verification across inventory, migration, catalogue visibility and archive compatibility. The final gate must prove that:

- migrated bouquet/wedding/open-prompt content still matches its source;
- proposal ring assets remain fully covered;
- all five legacy IDs remain hidden from normal catalogue/navigation surfaces;
- all five original sources remain resolvable;
- ordinary unrelated packs remain unaffected;
- Proposal Experience routing/navigation remains the surviving user path.
