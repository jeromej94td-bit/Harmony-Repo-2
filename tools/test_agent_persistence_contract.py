from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    target = ROOT / path
    assert target.is_file(), f"missing required persistence file: {path}"
    return target.read_text(encoding="utf-8")


def require(text: str, needle: str, path: str) -> None:
    assert needle in text, f"{path} must contain: {needle}"


def main() -> None:
    agents = read("AGENTS.md")
    skill = read(".agents/skills/repo-install-persistence/SKILL.md")
    index = read(".agents/skills/repo-skills/SKILL.md")
    script = read("scripts/agent_persistence.ps1")
    read(".github/workflows/agent-persistence-gate.yml")
    automerge = read(".github/workflows/agent-verified-automerge.yml")
    main_verify = read(".github/workflows/android-main-verify.yml")

    require(agents, ".agents/skills/repo-install-persistence/SKILL.md", "AGENTS.md")
    require(agents, "Local-only fixes are not completion.", "AGENTS.md")
    require(agents, "merge it into `main` without asking again", "AGENTS.md")
    require(index, "repo-install-persistence", ".agents/skills/repo-skills/SKILL.md")

    require(skill, "jeromej94td-bit/Harmony-Repo-2", "repo-install-persistence/SKILL.md")
    require(skill, "merge the PR into `main` without asking again", "repo-install-persistence/SKILL.md")
    require(skill, "built from merged `main`", "repo-install-persistence/SKILL.md")
    require(skill, "<!-- agent-persist: verified -->", "repo-install-persistence/SKILL.md")

    for command in ("git diff --check", "fetch", "merge-base", "--is-ancestor"):
        require(script, command, "scripts/agent_persistence.ps1")
    require(script, "refs/heads/${branch}:refs/remotes/${Remote}/${branch}", "scripts/agent_persistence.ps1")
    require(script, "refs/heads/${BaseBranch}:refs/remotes/${Remote}/${BaseBranch}", "scripts/agent_persistence.ps1")

    for needle in (
        "pull_request_target",
        "github.event.pull_request.head.repo.full_name == github.repository",
        "github.event.pull_request.user.login == github.repository_owner",
        "<!-- agent-persist: verified -->",
        "persistence-contract",
        "build-debug-apk",
        "merge_method: 'squash'",
        "actions: write",
        "createWorkflowDispatch",
        "workflow_id: 'android-main-verify.yml'",
        "ref: 'main'",
    ):
        require(automerge, needle, ".github/workflows/agent-verified-automerge.yml")

    for needle in (
        "workflow_dispatch",
        "keytool -genkeypair",
        "gradle :app:assembleDebug",
    ):
        require(main_verify, needle, ".github/workflows/android-main-verify.yml")
    assert "secrets." not in main_verify, "main verification must not require signing secrets"
    assert "upload-artifact" not in main_verify, "main verification must not publish an installable APK"

    print("Agent persistence contract: OK")


if __name__ == "__main__":
    main()
