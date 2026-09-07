# Persistent Repo Install Workflow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make Harmony and Stromruf agent fixes durable on GitHub `main` before repository/install work can be called complete.

**Architecture:** Use the same small repository contract in both Android repos: mandatory `AGENTS.md` trigger, repo skill, mechanical remote-persistence verifier, static contract test, and GitHub Actions gate. Add a matching personal skill so mentions of either app or repo-install work discover the workflow before editing.

**Tech Stack:** Git, GitHub Pull Requests/Actions, Markdown agent skills, PowerShell, Python 3, Gradle/ADB for task-specific verification.

**Spec:** `docs/superpowers/specs/2026-09-07-persistent-repo-install-workflow-design.md`

## Global Constraints

- Durable source of truth is remote `main`.
- Agent-created verified fixes are merged without a second merge prompt.
- Relevant verification failures block merge; known unrelated baseline failures are reported.
- Never persist secrets, keystores, `.env`, `local.properties`, APK/build outputs, or unrelated local files.
- Final install verification must correspond to merged `main` when installation is in scope.

---

### Task 1: Harmony persistence contract

**Files:** `AGENTS.md`, `.agents/skills/repo-skills/SKILL.md`, `.agents/skills/repo-install-persistence/SKILL.md`, `scripts/agent_persistence.ps1`, `tools/test_agent_persistence_contract.py`, `.github/workflows/agent-persistence-gate.yml`

**Interfaces:** The skill defines the lifecycle; `AGENTS.md` makes it mandatory; the script proves branch/main remote persistence; the Python contract test protects all required pieces; GitHub Actions runs the test.

- [ ] Add the mandatory trigger and standing merge authorization to `AGENTS.md`.
- [ ] Add `repo-install-persistence` to the repo-skill index and create the skill.
- [ ] Add `scripts/agent_persistence.ps1` with `Branch` and `Main` verification modes.
- [ ] Add the contract test and CI gate.
- [ ] Run the contract test and relevant lightweight syntax/diff checks.
- [ ] Open a PR, verify the remote head SHA, and merge after checks pass.

### Task 2: Stromruf persistence contract

**Files:** `AGENTS.md`, `.agents/skills/repo-skills/SKILL.md`, `.agents/skills/repo-install-persistence/SKILL.md`, `scripts/agent_persistence.ps1`, `tools/test_agent_persistence_contract.py`, `.github/workflows/agent-persistence-gate.yml`

**Interfaces:** Same lifecycle as Harmony, with Stromruf repository/package identity and its existing Android debug-build workflow left intact.

- [ ] Create Stromruf agent instructions and repo-skill index.
- [ ] Add the same persistence skill, verifier, contract test, and CI gate with Stromruf identifiers.
- [ ] Verify the existing Android workflow remains unchanged and the new gate is additive.
- [ ] Open a PR, verify the remote head SHA, and merge after checks pass.

### Task 3: Personal trigger skill

**Files:** `~/.agents/skills/repo-install-persistence/SKILL.md`

**Interfaces:** Discovered by Harmony/Stromruf/repo-install/APK/ADB/build/debug/test/fix/main/PR language; delegates repository details to each repo's mandatory skill.

- [ ] Create the global skill with both canonical repository names and the same definition of done.
- [ ] Include the user's standing authorization to merge verified agent fixes without asking again.
- [ ] Verify the skill exists and contains both repo names plus the local-only completion rule.

### Task 4: End-to-end durability verification

- [ ] Confirm the earlier Harmony test-import fix PR #295 is merged into `main` as the first example of the new policy.
- [ ] Confirm both workflow PRs are merged and fetch their resulting `main` SHAs.
- [ ] Confirm each merged `main` contains its persistence skill, AGENTS trigger, script, contract test, and CI workflow.
- [ ] Report the two durable main SHAs and the personal skill path.
