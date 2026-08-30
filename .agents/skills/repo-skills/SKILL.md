---
name: repo-skills
description: Use when working in the Harmony repository and a repo-specific workflow, convention, or reusable project rule may apply.
---

# Repo Skills

This is the umbrella/index for Harmony repository-specific skills.

Repo-specific workflows must be kept as separate skills so new rules can be added without mixing unrelated behavior into one large instruction file.

## Available Repo Skills

- `repo-sorting` — use for verschieben, sortieren, umsortieren, einsortieren, Kategorie ändern, or moving games/questions/packs between visible Harmony areas.

## Rule for future additions

When a new reusable repository workflow is introduced:

1. create it as its own skill under `.agents/skills/<repo-skill-name>/SKILL.md`;
2. use a clear `repo-...` name;
3. add it to this index;
4. reference it from `AGENTS.md` when the trigger should be mandatory.

Do not turn this index into the implementation instructions for every repo workflow. Detailed rules belong in the individual Repo Skill.
