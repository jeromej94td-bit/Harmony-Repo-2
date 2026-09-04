# Harmony Stabilization & Content Expansion Design

## Goal
Make the uploaded Harmony Android app reliable enough for active testing: one working Harmony Brain backend, automatic personalized game generation, durable Moments with photos, correct completed-game behavior, a removable Live Change launcher, a real drawing game, cleaner game organization, and substantially more non-image question content.

## Approved Constraints
- Do not remove or change chat microphone behavior in this pass.
- Keep speech-to-text where own/free answers need it.
- Do not materially change `Tauche ins Selbstbewusstsein`.
- Do not materially change image-based `Das oder Das` games.
- Other existing games may be re-sorted and expanded, but their core meaning should remain recognizable unless explicitly named below.
- `Liebe im Gleichgewicht` must become a normal question game, not a `tot` image/pair game.
- `Zeichne für mich` must become a drawing experience with five fun drawing prompts and a palette/canvas.
- `Unbeliebte Meinungen` and `Zustimmen oder Ablehnen` need at least ten meaningful additions each.
- Automatic personalized games run only while the app is active during test mode.
- Test cadence is one generation attempt per minute, maximum 20 successfully stored games per calendar day.
- Generated games must avoid duplicate questions and duplicate whole-game fingerprints.
- New personalized games must surface visibly under `Für dich` with an unread count/badge.
- A completed static game opened again must show results immediately rather than restart.
- Remove the redundant Harmony Brain coach block from Home.
- Remove the Daily Activity game/banner so `Für dich` is the primary recommendation surface.
- Live Change launcher must be dismissible without turning Live Change mode on.
- Moments must support multiple photos, copied into app-owned storage, and the moment description must influence Harmony Brain personalization.
- The current note preview behavior must remain.

## Backend Architecture
The Android app will use one Supabase project and one primary Edge Function: `harmony-brain-generate`. The connected project is `yepluyipizbbrgoffqdq`. Client-safe requests use the project publishable key and a user-provided `GEMINI_API_KEY` header for Gemini calls; the Gemini key is never written into source control or Supabase SQL.

`harmony-brain-generate` supports `chat`, `search`, `questions`, and `recommendations` modes. Local/place intents use Google Maps grounding first and Google Search as fallback. Search responses include a stable answer, grounding flag, sources and search queries. The function never invents local business names when grounding failed.

The Android gateway no longer performs anonymous Supabase signup just to call the Edge Function. It sends `apikey`, the generated app Gemini key as `x-gemini-key`, and JSON mode/query/context directly to the public function. Missing Gemini key is returned as an explicit configuration error.

## Harmony Brain Routing
Place/current-info detection moves to a focused pure helper with broad German/English terms: restaurants, hotels, cafés, bars, activities, excursions, museums, sights, events, cinema, opening hours, addresses, `near me`, city-specific food queries, and explicit current/today requests. Search-like requests never fall back to fabricated local venues. Non-local relationship chat may still use local offline relationship guidance if the live AI request fails, but that offline fallback must not pretend to have current venue data.

## Personalized Game Generation
Use a single foreground generator path. The daily successful-game limit is 20, with no startup batch. While the lifecycle is STARTED, the manager attempts one generation immediately and then every 60 seconds. Failures do not consume quota.

Each generated game contains 5–7 free/choice-style couple questions, stores as `GAME`, category `Für dich`, and is rejected if too similar to existing generated content. `firstShownAt == null` is the unread/new signal. Opening a generated game marks it seen/played.

## Moments
`MomentEntity` gains `imagePathsJson`, stored as JSON array text for Room compatibility. Database schema migrates 7→8. Image picker supports multiple images; repository copies every selected image to `filesDir/moments/` before the DB row is written, so picker grants cannot make images disappear later.

Moment cards show an image carousel with a subtle animated rotation/scale transition. Generated relationship milestones remain secondary and visually quieter.

Saving a moment also records a high-importance Harmony Brain memory fact and extracts local interest tags from its title/description. These facts and tags flow through the existing Harmony context builder, so later generated questions can reference experiences such as a first Christmas, trips, restaurants, hobbies or family events.

## Game Completion
`startPack` computes total question count and existing answer coverage. If every question has an answer, `ActivePackRun` starts with `isFinished=true` and the stored answers. Otherwise it resumes at the first unanswered index instead of always index 0.

## Drawing Game
`QuestionPack` supports a new `draw` type. `Zeichne für mich` uses five prompts with no multiple-choice options. `QuizRunnerScreen` routes draw packs to a Compose canvas with palette, brush sizes, undo/clear, and a `Fertig` action that stores a lightweight completion answer. Drawing state is local to the current prompt; persistent image export is intentionally out of scope for this pass.

## Content & Organization
Keep existing category IDs where possible for translation stability. Improve topic assignment and add several non-image packs that naturally fit existing categories/topics. Increase thin packs broadly, focusing on `Wer würde eher`, `Ich habe noch nie`, relationship conversations, values/opinions, travel, food, future and everyday-life prompts. Do not add placeholder/generated nonsense.

## Notes
The list-splitting bug must be reproduced against the current `MemoryViewModel`/editor flow before modification. If the current source already stores one multiline list in one entry, no speculative rewrite is allowed; only the proven path causing multiple entries may be changed.

## Verification
Because the uploaded archive does not contain a Gradle wrapper or Android SDK, full APK compilation may be unavailable in this container. Required verification therefore includes:
- Kotlin pure-logic tests compiled/run with `kotlinc` for routing, completion, quota and content policies.
- Static source assertions for Compose wiring and Room migration.
- Existing Python project verification scripts that do not require Android SDK.
- Supabase function deployment plus HTTP diagnostic calls for missing-key behavior and response contract.
- A final source audit ensuring protected games were not materially edited.
