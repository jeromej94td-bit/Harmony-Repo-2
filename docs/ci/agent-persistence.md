# Agent persistence workflow

Harmony repository-backed fixes are only complete when the verified change is durable on GitHub `main`.

The required flow is:

1. start from the current remote `main` in an isolated branch/worktree;
2. run the relevant regression checks and Android build;
3. commit and push every successful agent-discovered fix;
4. verify the remote branch SHA and open a PR with `<!-- agent-persist: verified -->` only after relevant local verification succeeds;
5. let the persistence gate and PR Android build complete successfully;
6. let the guarded GitHub workflow squash-merge the verified owner-created same-repo PR;
7. explicitly dispatch `android-main-verify.yml` on `main` after that token-driven merge;
8. require the dispatched merged-main compile verification to succeed and verify the merge SHA is contained in remote `main`.

`android-main-verify.yml` is deliberately non-publishing: it uses an ephemeral verification keystore, compiles the merged `main`, requires no signing secret, and uploads no APK. It proves persistence and buildability only.

Installable Harmony APKs remain governed separately by `android-apk-build.yml`, the stable signing identity, and `HARMONY_CI_DEBUG_KEYSTORE_B64`. If that protected signing secret is unavailable, the installable artifact workflow must continue to fail closed rather than generate a replacement identity.

Local-only changes, APK-only fixes, unpushed commits, and unmerged PRs are not completion. Never commit secrets, `.env`, local SDK configuration, keystores/signing material, or generated APKs.
