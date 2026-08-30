# Harmony Agent Instructions

## Repo Skills

Repository-specific reusable workflows are indexed in:

`.agents/skills/repo-skills/SKILL.md`

## Sorting and reclassification

When a user asks to **verschieben**, **sortieren**, **umsortieren**, **einsortieren**, move a game/question/pack to another category, or reorganize visible Harmony areas, you MUST read and follow:

`.agents/skills/repo-sorting/SKILL.md`

Do this before proposing destinations and before editing code.

The repository's current section/curration structure is the source of truth. Never invent topic IDs, pack IDs, categories, folders, or destinations from memory. A pure sorting request must not silently delete, archive, rewrite, or replace content.

## Video work

When a user asks to add, replace, integrate, wire, debug, or change an **intro video**, **fullscreen video**, **in-game video**, video asset, video playback, or a video-triggered game/experience flow, you MUST read and follow:

`.agents/skills/video-repo-skill/SKILL.md`

Do this before choosing asset paths, playback code, build tasks, or trigger/state logic.

The current repository implementation is the source of truth. Reuse the newest working Harmony video path where appropriate; do not invent folders, duplicate the player stack, copy another video's integrity constants, or rely on conversation memory instead of inspecting the current branch.

## Custom UI / Image Choice Routing (Happy Couple, etc.)
When updating questions, translating packs, or importing data via GitHub, you MUST preserve the exact logic for visual questions (like "Happy Couple" / "Liebe im Gleichgewicht").
- **NEVER** use hardcoded question texts (e.g., `HAPPY_COUPLE_PROMPTS`) or text-matching to route UI components in `HarmonyImageChoicePolicy` or `QuizRunnerScreen`.
- **ALWAYS** route visual cards strictly based on `pack.id` and the explicit question index (e.g., `pack.id == LoveBalanceQuestionPolicy.PACK_ID && questionIndex == 0`).
- If you modify the `liebegleichgewicht` pack or similar visual packs, ensure the index-based routing remains intact so visual components (images) do not accidentally bleed into other standard text questions.
