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
