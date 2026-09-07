---
name: repo-install-persistence
description: Use when Harmony Repo-2 work mentions installation, update, APK, ADB, build, debug, tests, fixes, GitHub, main, branches, commits, pushes, or pull requests.
---

# Repo Install Persistence

## Core rule

**Local-only fixes are not completion.** The durable product state is GitHub `main`, not a working tree, worktree, local commit, APK, patch, or phone installation.

Repository: `jeromej94td-bit/Harmony-Repo-2`
Package: `com.aistudio.harmony.couples.xqvz`

## Mandatory flow

1. Fetch and identify the exact current `origin/main` SHA before work.
2. Protect dirty user work. Use an isolated branch/worktree for agent changes; never overwrite unrelated local edits.
3. Debug/build/test from that branch. Prefer targeted tests and an incremental build; use `clean` only when evidence requires it.
4. When debugging discovers any fix, preserve it in the branch. Do not leave a successful fix only in generated files or an APK.
5. Run the relevant regression test(s), `git diff --check`, and the build needed for the task. Known unrelated red-baseline tests must be reported, not used as a reason to discard a verified fix.
6. Commit only intended files. Never commit `.env`, `local.properties`, keystores/signing material, APK/build outputs, credentials, or unrelated local files.
7. Push the branch and verify the remote branch SHA equals the intended local commit SHA.
8. Create or update a PR against `main` with the verification evidence. Only after local relevant verification is green, add the exact marker `<!-- agent-persist: verified -->` to the PR body.
9. Standing user authorization: when the agent's change is successfully relevant-tested and, when installation is part of the task, successfully install/launch-checked, **merge the PR into `main` without asking again**. `.github/workflows/agent-verified-automerge.yml` waits for `persistence-contract` and `build-debug-apk` before merging marked, same-repo, owner-created PRs. If the auto-merge workflow is not active yet (for example while bootstrapping it), merge through the GitHub API only after those checks succeed.
10. Fetch remote `main` and verify the fix commit is contained in it. Record the resulting `main` SHA. The guarded auto-merge then dispatches `android-main-verify.yml`, a non-publishing compile check of the merged `main`; require it to succeed before reporting persistence work complete.
11. For installation work, the final durable installation should be built from merged `main` (or a properly signed GitHub artifact for that merged SHA), then installed and launch/crash-checked. The compile-only persistence check is not a substitute for installable APK signing. If a branch APK was needed for diagnosis, merge first and perform the final installation verification from `main`.
12. Final response must state: repository, merged PR, durable `main` SHA, test/build result, merged-main verification result, installation result when applicable, and any known unrelated baseline failures.

## Stop conditions

Do not merge when the relevant test/build fails, the intended diff is ambiguous, secrets are present, signing identity unexpectedly changes, or the installation exposes a new relevant regression. Fix or report the blocker instead.

Do not silently uninstall an existing app to solve a signing problem. Preserve user data and signing identity; follow the repository's signing rules. The installable `android-apk-build.yml` must remain fail-closed if its protected stable signing secret is unavailable; never weaken it just to make persistence CI green.

## Mechanical verification

Use `scripts/agent_persistence.ps1` after testing to verify branch/remote persistence and after merge to prove the commit is reachable from `origin/main`.

The GitHub workflow `.github/workflows/agent-persistence-gate.yml` protects the persistence contract. `.github/workflows/agent-verified-automerge.yml` turns the verified marker into a guarded merge after the required checks complete and dispatches `.github/workflows/android-main-verify.yml` for the final non-publishing merged-main compile verification.
