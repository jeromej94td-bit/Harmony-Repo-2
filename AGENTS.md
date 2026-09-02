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

## Google authentication is protected infrastructure

The production Google login is intentionally centralized in:

`app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt`

- **NEVER** replace the Google button in `AuthScreen.kt` with a direct `SupabaseConfig.client.auth.signInWith(Google)` / `auth.signInWith(Google)` call.
- **NEVER** delete or bypass `GoogleAuthCoordinator.kt` as a cleanup/refactor unless the user explicitly asks to replace the complete Google authentication architecture.
- The coordinator's required behavior is: Credential Manager / Google ID token first, clear stale credential state and retry once for `[16] Account reauth failed`, then use Supabase browser OAuth only as the fallback.
- Preserve email/password login, password recovery, and demo-mode callbacks independently; changes to those flows must not rewrite the Google auth path.
- Before merging authentication-related changes, preserve `GoogleAuthRegressionContractTest.kt`. Any deliberate replacement architecture must update that test in the same change.
- Do not introduce anonymous Supabase sign-in as a Google-login fallback.

## Android signing identity is protected infrastructure

Installable Harmony APKs from `main` must keep one stable Android signing identity because Google Sign-In and Android updates depend on the package/signing-certificate pair.

- **NEVER** generate a fresh keystore for an installable `main` APK.
- `.github/workflows/android-apk-build.yml` must restore `HARMONY_CI_DEBUG_KEYSTORE_B64` from GitHub Actions secrets and verify the pinned SHA-1 before publishing an APK.
- The pinned CI signing SHA-1 is `73:85:7C:7D:A2:C1:0A:29:79:14:6C:20:15:0C:AE:4E:7A:77:B3:92`.
- A different SHA-1 is an explicit signing-key rotation, not a cleanup. Before changing it, update the Android OAuth client for package `com.aistudio.harmony.couples.xqvz` and make the migration explicit.
- Pull-request-only compile builds may use an ephemeral debug key, but those APKs must not be published as installable Harmony artifacts.
- If the stable signing secret is unavailable, fail the installable build. Do not silently fall back to a newly generated key.

## Custom UI / Image Choice Routing (Happy Couple, etc.)
When updating questions, translating packs, or importing data via GitHub, you MUST preserve the exact logic for visual questions (like "Happy Couple" / "Liebe im Gleichgewicht").
- **NEVER** use hardcoded question texts (e.g., `HAPPY_COUPLE_PROMPTS`) or text-matching to route UI components in `HarmonyImageChoicePolicy` or `QuizRunnerScreen`.
- **ALWAYS** route visual cards strictly based on `pack.id` and the explicit question index (e.g., `pack.id == LoveBalanceQuestionPolicy.PACK_ID && questionIndex == 0`).
- If you modify the `liebegleichgewicht` pack or similar visual packs, ensure the index-based routing remains intact so visual components (images) do not accidentally bleed into other standard text questions.
