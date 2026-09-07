# Harmony Agent Instructions

## Repo Skills

Repository-specific reusable workflows are indexed in:

`.agents/skills/repo-skills/SKILL.md`

## Persistent repo/install workflow is mandatory

When a user mentions Harmony, Harmony Repo-2, repository work, `main`, an APK, ADB, installation/update, debugging, testing, building, fixing, pushing, or a PR in connection with this app, you MUST read and follow:

`.agents/skills/repo-install-persistence/SKILL.md`

**Local-only fixes are not completion.** A code or test fix discovered while debugging/building/installing is not finished while it exists only in a working tree, worktree, APK, patch, or local commit.

The user has granted standing authorization for agent-created fixes that were successfully relevant-tested and, when applicable, successfully installed/launch-checked: create/update the PR, merge it into `main` without asking again, verify the merge on remote `main`, and use the merged `main` as the durable source of truth.

Never report repository/install work as complete until every intended agent change is either on `main` or explicitly reported as blocked. Never commit secrets, `.env`, signing keys/keystores, local SDK configuration, generated APKs, or unrelated local files.

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

## Google authentication is protected infrastructure

The production Google login is intentionally centralized in:

`app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt`

- **NEVER** replace the Google button in `AuthScreen.kt` with a direct `SupabaseConfig.client.auth.signInWith(Google)` / `auth.signInWith(Google)` call.
- **NEVER** delete or bypass `GoogleAuthCoordinator.kt` as a cleanup/refactor unless the user explicitly asks to replace the complete Google authentication architecture.
- The coordinator's required behavior is native-only: Android Credential Manager with `GetGoogleIdOption`, Google ID token, SHA-256(raw nonce) sent to Google, and the original raw nonce sent to Supabase. Do not add a browser/WebView OAuth fallback for the Google button.
- Preserve email/password login, password recovery, and demo-mode callbacks independently; changes to those flows must not rewrite the Google auth path.
- Before merging authentication-related changes, preserve `GoogleAuthRegressionContractTest.kt`. Any deliberate replacement architecture must update that test in the same change.
- Do not introduce anonymous Supabase sign-in as a Google-login fallback.
- Post-login account loading is part of the authentication contract: `get_app_session()` must remain callable for an authenticated Google user.
- In the `get_app_session()` PL/pgSQL body, use `ON CONFLICT ON CONSTRAINT harmony_profiles_pkey` rather than `ON CONFLICT(user_id)` to avoid output-column/variable ambiguity, and cast `auth.users.email` to `text` in `RETURN QUERY`.
- Preserve `AppSessionRpcMigrationContractTest.kt` when touching session RPC migrations.

## Android signing identity is protected infrastructure

Installable Harmony APKs from `main` must keep one stable Android signing identity because Google Sign-In and Android updates depend on the package/signing-certificate pair.

- **NEVER** generate a fresh keystore for an installable `main` APK.
- `.github/workflows/android-apk-build.yml` must restore `HARMONY_CI_DEBUG_KEYSTORE_B64` from GitHub Actions secrets and verify the pinned SHA-1 before publishing an APK.
- The permanent install/update signing SHA-1 is `63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F`.
- The permanent certificate SHA-256 is `77:9B:7D:D8:B4:86:CD:FE:01:F6:1D:90:F4:CE:9C:7F:73:63:D5:59:15:9B:8F:26:CB:6B:A3:5E:A2:AA:6D:7B`.
- Before every real-device installation, verify the built APK SHA-1 against the currently installed Harmony package. If it differs, abort; never uninstall Harmony to work around a signing mismatch.
- A different fingerprint is an explicit signing-key rotation, not a cleanup. Before changing it, update the Android OAuth client for package `com.aistudio.harmony.couples.xqvz` and make the migration explicit.
- Pull-request-only compile builds may use an ephemeral debug key, but those APKs must not be published as installable Harmony artifacts.
- If the stable signing secret is unavailable, fail the installable build. Do not silently fall back to a newly generated key.
- Never commit `debug.keystore`, its Base64 encoding, or any private signing material to Git. Keep the private key only in protected secret storage and an offline owner backup.

## Custom UI / Image Choice Routing (Happy Couple, etc.)
When updating questions, translating packs, or importing data via GitHub, you MUST preserve the exact logic for visual questions (like "Happy Couple" / "Liebe im Gleichgewicht").
- **NEVER** use hardcoded question texts (e.g., `HAPPY_COUPLE_PROMPTS`) or text-matching to route UI components in `HarmonyImageChoicePolicy` or `QuizRunnerScreen`.
- **ALWAYS** route visual cards strictly based on `pack.id` and the explicit question index (e.g., `pack.id == LoveBalanceQuestionPolicy.PACK_ID && questionIndex == 0`).
- If you modify the `liebegleichgewicht` pack or similar visual packs, ensure the index-based routing remains intact so visual components (images) do not accidentally bleed into other standard text questions.
