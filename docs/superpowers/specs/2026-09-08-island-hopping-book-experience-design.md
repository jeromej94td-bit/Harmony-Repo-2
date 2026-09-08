# Island Hopping Magical Book Experience

## Goal
Transform only `h500_089_inselhopping_prioritaet` ("Inselhopping – Ehrliche Runde") into a magical book experience without changing its curated question content or persistence semantics.

The player opens the game, sees a cinematic Harmony book arrive and open, then answers the existing six curated questions as successive book pages. Every answer triggers a visible page-turn animation before the existing answer callback advances the runner.

## Existing content contract
The canonical runtime questions remain the six curated overrides in `Harmony360FoodTravelLeisureCultureQualityRework.kt`:
1. Was ist beim Inselhopping wichtiger?
2. Was muss auf den Inseln unbedingt passieren?
3. Worauf würdest du bei engem Budget am wenigsten verzichten?
4. Was nervt dich beim Inselwechsel am ehesten?
5. Was soll bei der Route den Ausschlag geben?
6. Was wäre dein perfekter letzter Inselabend?

Do not replace these questions with the older generated eight-question source pack.
## Runtime flow
1. `QuizRunnerScreen` detects the exact pack ID and starts a one-time book intro before question 1 becomes interactive.
2. The intro reuses the existing Filament/SceneView book renderer when its GLB is available, with the current Compose book animation as fallback.
3. After the intro, the priority-poker question renders through an island-hopping-specific book board instead of the generic `DirectPriorityPokerBoard`.
4. A card tap locks input, gives selection feedback, and starts the page-turn animation.
5. Only after the turn reaches its commit point does the board call the existing `onPick(answer)` callback.
6. The ViewModel remains responsible for persistence, paired submission, current-index advancement, finishing, and history.
7. The newly advanced question enters as the next book page. The same transition repeats through question 6.

## Visual direction
The book is the hero object: dark navy/violet cover, fine metallic-gold trim, warm cream pages, pink-magenta heart/gem glow, controlled cyan highlights, and deep purple-black surroundings.

The island theme adds restrained turquoise/ocean light, distant floating-island silhouettes, cloud haze and gold/pink magical dust. Text readability always wins over particles and bloom.

No third-party franchise branding, logos or copyrighted characters are introduced.
## Interaction timing
- Intro target: roughly 2.4–3.6 seconds, skippable only by fallback/unavailability, not by accidental taps.
- Answer confirmation: 120–220 ms of selected-card glow/scale feedback.
- Page turn: roughly 650–850 ms with perspective rotation, moving shadow and page-edge highlight.
- The answer callback fires once, after the page-turn commit point; repeated taps while turning are ignored.
- Question entrance: short 180–280 ms settle/fade after the runner advances.

## Component boundaries
- `IslandHoppingBookPolicy`: exact pack-ID feature gate and constants.
- `IslandHoppingBookIntro`: themed wrapper around the reusable Harmony book cinematic/fallback.
- `IslandHoppingBookBoard`: renders the current question and options inside the book spread.
- `IslandHoppingPageTurn`: owns only page-turn progress and visuals.
- `FullscreenQuestionMechanicBoard`: routes this pack to the special board while all other priority-poker games remain unchanged.
- `QuizRunnerScreen`: owns only the one-time intro visibility for this pack.

The generic `DirectPriorityPokerBoard` must keep its current one-tap behavior for every other priority-poker pack.
## Reliability and fallback
If the Filament model is missing, fails to load, or the device cannot sustain the renderer, the intro falls back to the existing Compose book animation and the questions still use the interactive book-page UI.

The fallback must not change question order, selected answers, paired submission, skip behavior, completion, or answer history.

## Performance
- Use SceneView/Filament only for the short intro; do not keep a heavyweight 3D scene alive behind all six questions.
- Keep the question/page-turn interaction in Compose for reliable text layout and touch targets.
- Avoid 4K textures and large particle counts; prefer procedural gradients, geometry and shared PBR materials.
- Prevent double submits while a page turn is active.

## Protected scope
Do not modify Google/Supabase authentication, package ID, signing/keystore configuration, locale catalogs, unrelated games, or the curated island-hopping question text.

## Verification
Add regression tests proving the exact pack is routed to the book board, other priority-poker packs still use the generic board, the answer callback is delayed until page-turn completion, and the six curated questions remain unchanged. Run focused tests and `assembleDebug --max-workers=1`, then verify the debug signer SHA remains unchanged.