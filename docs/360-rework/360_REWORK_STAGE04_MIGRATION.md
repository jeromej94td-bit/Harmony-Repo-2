# 360 Rework — Stage 04.2 Legacy Content → Experience Migration

**Scope:** migrate/reuse strong proposal/ring/wedding material in reusable Experience models.  
**Navigation:** unchanged in this slice.  
**Source deletion:** none.

## Migrated sources

### `ringe` — Verlobungsringe

The ten ring concepts already selected in Stage 02.10 are now explicitly traced to the ten refreshed ring asset keys used by `ProposalRingImageDuels`.

The migration validates all three sides of the contract:

1. the source concept belongs to the Stage-02 reuse manifest;
2. the concept still exists in the effective generated `ringe` runtime pack;
3. the mapped asset key is one of the ten actual Proposal Experience ring assets.

No image is copied or renamed.

### `straeusse` — Hochzeitssträuße

All four existing legacy pairs are adapted into reusable `ExperienceEitherOrRound`s. Choice strings are read directly from `HarmonyPacksData.DEFAULT_PACKS`; they are not duplicated in the migration layer.

### `traumhochzeit` — Traumhochzeit

All four existing wedding-style pairs are adapted into reusable `ExperienceEitherOrRound`s, again retaining the source pack ID and pair index.

### `h500_060_hochzeit_offene_runde`

All six Stage-05-curated wedding prompts are adapted directly from `GeneratedContentRegistry.PACKS` into reusable `ExperienceOpenPromptRound`s. The migration therefore follows the current curated runtime source instead of keeping a second hardcoded copy of the questions.

### `antrag`

No duplicate migration is created. Stage 02 already supersedes its two broad questions with the richer Proposal Experience location/detail mechanics.

## Traceability

Each migrated choice/open-prompt round stores:

- source pack ID;
- source index;
- stable Experience round ID;
- generic Experience model.

Ring reuse additionally stores source label → refreshed asset key.

## Regression contract

`ProposalLegacyExperienceMigrationTest` verifies:

- all legacy bouquet and wedding-style choices remain byte-for-byte sourced from their original pairs;
- all six curated wedding open prompts come from the current generated runtime source;
- ring reuse covers exactly the Stage-02 reuse labels and exactly the Proposal Experience ring assets;
- all migrated items retain source traceability and unique stable IDs;
- superseded `antrag` content is not duplicated.

## Next exact action — 04.3

Remove duplicate presentation of the migrated/superseded standalone packs from normal catalogue/navigation surfaces while preserving their source definitions for Stage 04.4 archival compatibility.
