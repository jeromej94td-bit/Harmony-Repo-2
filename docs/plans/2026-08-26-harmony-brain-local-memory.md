# Harmony Brain — Local Memory Implementation Plan

**Goal:** Add a durable, local-first personal memory for Harmony using Android Room/SQLite, plus a developer-only Brain Control Center. Room stays the source of truth. No cloud database is required for memory.

## Architecture

`Harmony gameplay -> Room brain event log -> local preference/fact aggregation -> HarmonyContextBuilder -> optional secure AI gateway -> generated content -> Room`

The full history remains local. External AI receives only a bounded, relevant and sanitized context snapshot.

## Tasks

1. **Room schema v5**
   - Add `brain_answers`, `brain_preferences`, `brain_interactions`, `brain_generated_content`, `brain_memories`, and `brain_prompt_versions`.
   - Add `BrainDao` queries for full-history aggregation, category/relevance candidates, generated-content duplicate checks, statistics, and prompt versioning.
   - Add non-destructive `MIGRATION_4_5`.

2. **Local Brain domain**
   - `HarmonyPreferenceEngine`: update durable interest scores locally from answers, choices, skips, likes/dislikes and categories.
   - `HarmonyRelevanceEngine`: local keyword/category relevance ranking.
   - `HarmonyDuplicateDetector`: normalized exact + token/Jaccard similarity checks for generated content.
   - `HarmonyContextBuilder`: build three context layers — durable profile, important memories, relevant/current answers — from all local history.
   - `RoomHarmonyBrainRepository`: single API for recording interactions, generating context and prompt version management.

3. **Gameplay integration**
   - Mirror saved answers into Brain memory without replacing the existing `answers` table.
   - Resolve question text/category/type from the existing pack definition.
   - Record played/answered/choice interactions and update local preference signals.
   - Keep existing static content and translations untouched.

4. **Developer-only Brain Control Center**
   - Add `🧠 AI Brain` to the existing Entwickler Studio.
   - Show memory stats and strongest/weakest interests.
   - Provide local context preview for a test task/category.
   - Prompt workflow: draft -> test/preview -> publish -> rollback to a previous published version.
   - Keep prompts/version history local in Room.

5. **Offline + AI boundary**
   - Local memory, profile, generated content and context building work fully offline.
   - Define a gateway boundary for future/present secure Gemini calls; never store a Gemini secret in the APK.
   - New generation can be deferred when no network exists; already generated content remains local.

6. **Tests**
   - DAO persistence and migration 4->5.
   - Preference scoring uses old + new history and negative signals.
   - Relevance ranking selects matching older memories.
   - Context builder is bounded and does not emit names/raw unrelated history.
   - Duplicate detector rejects exact and near-duplicate questions.
   - Prompt draft/publish/rollback behavior.

## Verification

Run:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

No existing questions, categories, translations, games or Memory feature tables are removed or replaced.