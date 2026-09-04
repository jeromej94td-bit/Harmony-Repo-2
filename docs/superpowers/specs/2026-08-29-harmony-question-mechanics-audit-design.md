# Harmony Question Mechanics Audit & Rework

Date: 2026-08-29
Branch: `rework/question-mechanics-audit`
Status: Design approved in chat; implementation not started yet.

## Goal

Finish the app-wide answer-mechanics cleanup so users no longer encounter questions whose UI or answer options do not fit the actual meaning of the question.

The system must choose the interaction deliberately per question instead of treating most quiz questions as generic multiple choice plus an automatic free-text fallback.

## Current Problems

1. `Question` currently carries only text, options, and `defaultMine`; it has no explicit interaction metadata.
2. `QuizRunnerScreen` currently appends `Schreibe deine eigene Antwort` to most ordinary quiz questions, even when the supplied options are already exhaustive.
3. Special mechanics already exist, but routing is split across explicit pack/index rules, fullscreen mechanic policy, and a small photo-specific policy.
4. `PhotoQuestionPolicy` currently covers only two explicitly approved photo questions. This is intentionally safe, but not yet app-wide.
5. Pack/index-only routing is fragile when questions are reordered, inserted, or removed.
6. Harmony contains a large generated question pool, so manual screen-by-screen checking is too error-prone as the primary quality process.

## Design Principle

Interaction type is content semantics, not presentation decoration.

A question should first resolve to an explicit `QuestionInteractionSpec`; the UI then renders the matching mechanic. Free text, gallery input, ranking, prediction, secret choice, scenario, scale, assignment, or ordinary fixed choice must each be opt-in outcomes of that resolution.

Keyword-only routing is explicitly rejected. A question merely mentioning a photo, ranking, partner, or secret must not automatically switch mechanics.

## Considered Approaches

### A. Keyword heuristics

Detect words such as `Foto`, `rank`, `Partner`, `geheim` and route automatically.

Rejected because false positives are unavoidable. Existing content already contains ordinary questions mentioning photos that should remain normal choices.

### B. Central pack/index registry only

Maintain one large map of `packId + questionIndex -> mechanic`.

This is easy to add but remains fragile whenever content is reordered. It also hides semantics away from the content definition.

### C. Explicit metadata with migration-safe resolver and curated fallback

Recommended.

Add a small interaction model with safe defaults. Resolution order:

1. explicit interaction metadata on the question when present;
2. curated stable-key override for legacy/generated content not yet annotated;
3. existing specialized/fullscreen mechanic policy for already-established mechanics;
4. safe ordinary-choice/open-text fallback.

This lets Harmony improve incrementally without forcing a risky rewrite of every generated file at once.

## Interaction Model

Introduce a pure model similar to:

- `FIXED_CHOICE`
- `CHOICE_WITH_OPTIONAL_TEXT`
- `OPEN_TEXT`
- `PHOTO_ONLY`
- `CHOICE_WITH_OPTIONAL_PHOTO`
- `RANK_ORDER`
- `PERSON_ASSIGNMENT`
- `PARTNER_PREDICTION`
- `SECRET_CHOICE`
- `SCALE_MATCH`
- `WHO_WOULD`
- `SCENARIO`
- `PRIORITY_POKER`
- `MATCH_TOURNAMENT`
- `DEEP_TALK`
- existing visual special mechanics where needed

The model should also expose whether a custom text answer is allowed. The UI must never infer that merely because the question is a quiz.

Existing constructors must keep compiling through default values; no mass edit of all current question definitions is required for the first slice.

## Stable Question Identity

Do not make new mechanics depend only on mutable question indices.

For legacy/generated content, use a deterministic stable key derived from at least:

- `packId`
- normalized raw question text

When explicit question IDs are introduced later, they take precedence. Existing pack/index rules may remain temporarily for already-shipped mechanics but should be migrated when touched.

## Resolver

Create one pure resolver as the single entry point used by the runner:

`QuestionInteractionPolicy.resolve(pack, questionIndex, question)`

Responsibilities:

- return the resolved mechanic;
- return whether custom text is allowed;
- preserve current specialized mechanics;
- use explicit photo rules without keyword inference;
- make ordinary fixed-choice questions truly fixed choice by default;
- treat questions with no supplied fixed options as open text only when their content/type calls for it;
- remain deterministic and unit-testable without Compose.

## Free-Text Rule

The current global behavior of always appending `Schreibe deine eigene Antwort` is removed.

New rule:

- fixed-choice questions: no custom answer unless explicitly enabled;
- `Ich habe noch nie`: keep the established skip behavior;
- deep/open prompts: text input is the primary mechanic;
- questions intentionally designed to allow nuance may explicitly opt into `CHOICE_WITH_OPTIONAL_TEXT`;
- photo mechanics never receive the generic text fallback unless separately designed for it.

This is the highest-leverage correction because it removes an entire class of semantically wrong answer options across the app.

## Photo Rework Expansion

Keep the already merged behavior for the two approved photo questions.

Then run an inventory across shipped question sources and classify candidates into:

1. real photo answer required;
2. ordinary choice about photo preferences;
3. ordinary question that merely mentions a photo.

Only categories 1 and deliberately chosen category-2 questions get photo mechanics. Category 3 remains untouched.

Selected gallery files remain local unless a separate sync/privacy design is approved later. The answer stored in Harmony Brain remains semantic text, not a local filesystem path.

## App-Wide Audit

Add a pure audit utility that can enumerate loaded `QuestionPack` content and emit findings such as:

- fixed choices currently receiving unnecessary free text;
- empty-option quiz questions;
- duplicate/near-duplicate answer options;
- suspicious generic answers such as `Beide`, `Niemand`, or `Eigene Antwort` where the prompt semantics do not support them;
- questions already covered by a special mechanic;
- questions whose wording asks for ordering, prediction, secret choice, scale, assignment, photo, or scenario behavior but which are not explicitly mapped;
- invalid or unstable legacy mappings.

Heuristics in the audit are advisory only. They may flag candidates, but they must not silently change runtime behavior.

The audit becomes the worklist for explicit curation.

## Runtime Flow

For each question:

1. load the raw question and options;
2. resolve `QuestionInteractionSpec`;
3. render the matching existing or new mechanic;
4. store one semantic answer through the existing answer persistence path;
5. keep local media attachment metadata separate from `answerText`;
6. advance only when the chosen mechanic reports completion.

This avoids special mechanics accidentally falling through to the generic quiz buttons.

## Compatibility

- No destructive Room migration is required for the initial interaction-policy slice.
- Existing saved text answers remain readable.
- Existing special mechanics must retain their current routing and answer behavior.
- Existing photo answer files must continue to restore correctly.
- Dynamic/generated packs that do not yet carry explicit metadata use resolver defaults and curated overrides.

## Implementation Slices

### Slice 1 — Interaction policy foundation

- add interaction model/resolver;
- route `QuizRunnerScreen` through it;
- remove automatic custom-answer injection;
- preserve `Ich habe noch nie` skip behavior;
- regression-test all currently wired fullscreen mechanics and Photo Rework.

### Slice 2 — Full content audit

- add pure audit utility;
- run it against shipped/default/generated Harmony packs;
- check every flagged result;
- create an explicit curated mapping list rather than runtime keyword heuristics.

### Slice 3 — Question mechanics corrections

Apply reviewed mappings in small groups:

- photo/media;
- ranking/priorities;
- partner prediction/secret choice/person assignment;
- scales/scenarios;
- free-text/deep-talk vs fixed choice.

Each group gets focused tests and an independent PR if the diff is substantial.

### Slice 4 — Stable IDs and cleanup

- migrate touched index-based special cases to stable identity;
- remove obsolete duplicated routing rules;
- document any intentionally retained exceptions.

### Slice 5 — Release verification

- full Android compile when the repository runner/build environment is available;
- smoke test main game entry paths;
- answer persistence and reopen checks;
- photo restore/change/remove checks;
- representative checks for every mechanic;
- no question-screen scroll requirement regression on compact displays where the fullscreen compact layout applies.

## Testing Strategy

TDD applies to each implementation slice.

Minimum pure tests:

- fixed-choice question does not get a custom-answer fallback;
- explicitly optional text still allows text;
- `nie` retains skip;
- both current Photo Rework questions resolve correctly;
- an ordinary question containing `Foto` remains ordinary;
- all existing special mechanic routes remain unchanged;
- stable question key remains unchanged for insignificant whitespace normalization and differs for distinct prompts;
- audit flags known deliberately seeded mismatch fixtures;
- audit never changes runtime mechanics by itself.

Compose tests should verify at least one representative screen for fixed choice, optional text, photo-only, choice+photo, and existing fullscreen mechanics.

## Initial Release Priorities

P0 before calling the question system finished:

- remove global meaningless custom-answer fallback;
- establish one resolver;
- inventory every shipped question and curate all mechanic mismatches;
- keep existing special mechanics working;
- complete a trustworthy build/smoke pass.

P1 before polished public release:

- migrate fragile touched index routes to stable IDs/keys;
- resolve remaining content-quality duplicates and generic answers;
- finish outstanding 360 experience wiring that affects the normal user flow.

Later/non-blocking:

- cloud sync for user-selected photo attachments;
- large UI redesigns unrelated to interaction correctness;
- automatic AI mechanic assignment at runtime.

## Explicit Non-Goals

- no keyword-driven automatic mechanic switching;
- no silent rewrite of all generated question text;
- no unrelated visual redesign;
- no new cloud media upload in this rework;
- no claim that GitHub Actions/Android build is green until an executable build actually runs.

## Definition of Done

This rework is complete when:

1. every shipped question resolves deterministically to a meaningful interaction;
2. ordinary fixed-choice questions no longer show an irrelevant custom-answer option;
3. all audit findings are either corrected or explicitly accepted with rationale;
4. special mechanics and saved answers regressions are covered by tests;
5. a full Android build and representative end-to-end smoke pass have been recorded successfully.
