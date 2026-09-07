# Persistent Repo Install Workflow Design

## Goal

Make repository-backed Harmony and Stromruf work durable by definition: an agent-discovered fix is not complete until it is tested, persisted to GitHub, merged into `main`, remotely verified, and—when installation is requested—verified from the durable merged state.

## Scope

- Harmony: `jeromej94td-bit/Harmony-Repo-2`
- Stromruf: `jeromej94td-bit/Stromruf-Google-Ai-studio-`
- Personal trigger skill under `~/.agents/skills/repo-install-persistence/`
- Repository-specific skill and mandatory `AGENTS.md` trigger in both repositories
- Mechanical persistence verifier plus GitHub contract workflow in both repositories

## Completion contract

`Local-only fixes are not completion.` A working APK, successful local test, worktree edit, patch, or local commit is intermediate state.

The required lifecycle is: current `main` → isolated branch/worktree → relevant tests/build → commit intended files → push → verify remote branch SHA → PR → automatic merge after successful agent verification → verify commit is reachable from remote `main` → final merged-main install/launch check when installation is in scope.

The user grants standing authorization to merge agent-created fixes without a second merge prompt once the relevant verification succeeds. Relevant failures stop the merge; unrelated known red-baseline tests are reported and do not erase a verified fix.

## Safety boundaries

Never commit secrets, `.env`, `local.properties`, keystores, signing material, APKs/build outputs, credentials, or unrelated local files. Preserve dirty user work through isolated branches/worktrees. Do not solve signing mismatches by silently uninstalling an app or rotating keys.

## Enforcement

Each repository contains `.agents/skills/repo-install-persistence/SKILL.md`, `scripts/agent_persistence.ps1`, a contract test, and `.github/workflows/agent-persistence-gate.yml`. `AGENTS.md` makes the skill mandatory for install/update/APK/ADB/build/debug/test/fix/GitHub work.

The personal skill uses Harmony, Harmony Repo-2, Stromruf, repository installation, APK, ADB, build/debug/test/fix/main/PR language as discovery triggers so the process is loaded before local-only work can be mistaken for completion.
