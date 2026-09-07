# Harmony Google Login + Local Update Signing Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make Harmony's physical-device Google login fully native and nonce-verified while preserving the installed app's local update signing identity.

**Architecture:** Keep Supabase as the session authority, but remove browser OAuth from the Google button. Android Credential Manager obtains a Google ID token using the existing Harmony App web client ID and a SHA-256 nonce; Supabase receives the ID token plus raw nonce. Local device builds use a stable private keystore outside Git, while CI remains fail-safe when that local file is absent.

**Tech Stack:** Android/Kotlin, Jetpack Credential Manager, Google ID, Supabase Kotlin Auth, Gradle Kotlin DSL, JUnit, ADB/apksigner.

**Spec:** User-provided Harmony Google-login/signing requirements in this chat.

## Global Constraints

- Repository: `jeromej94td-bit/Harmony-Repo-2`.
- Package: `com.aistudio.harmony.couples.xqvz`.
- Installed-device local update SHA-1: `63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F`.
- Installed-device local update SHA-256: `77:9B:7D:D8:B4:86:CD:FE:01:F6:1D:90:F4:CE:9C:7F:73:63:D5:59:15:9B:8F:26:CB:6B:A3:5E:A2:AA:6D:7B`.
- Google Cloud project: existing `Harmony App`; do not create a replacement project.
- Supabase project: `rspgnonlpkxdudbjxnrl`; nonce checks stay enabled.
- Never commit secrets, keystores, local.properties, or APK outputs.
- Existing CI production signing identity remains untouched unless explicitly migrated separately.

---
### Task 1: Lock the native Google-auth contract with RED tests

**Files:**
- Modify: `app/src/test/java/com/example/GoogleAuthRegressionContractTest.kt`
- Create: `app/src/test/java/com/example/ui/auth/GoogleNativeAuthConfigTest.kt`

- [ ] Replace fallback expectations with `GetGoogleIdOption`, hashed nonce, raw Supabase nonce, and no browser OAuth fallback.
- [ ] Add a pure unit test asserting the exact Harmony web client ID and SHA-256 nonce transform.
- [ ] Run only these tests and confirm they fail because the new architecture is not implemented yet.

### Task 2: Implement native Credential Manager + Supabase ID-token flow

**Files:**
- Create: `app/src/main/java/com/example/ui/auth/GoogleNativeAuthConfig.kt`
- Modify: `app/src/main/java/com/example/ui/auth/GoogleAuthCoordinator.kt`

- [ ] Generate a cryptographically random raw nonce.
- [ ] Hash the raw nonce with SHA-256 for Google Credential Manager.
- [ ] Request Google credentials with `GetGoogleIdOption` and the existing Web client ID.
- [ ] Exchange the ID token through Supabase `signInWith(IDToken)` with provider Google and the original raw nonce.
- [ ] Keep cancellation/error handling native; do not start browser OAuth from the Google button.
- [ ] Re-run auth tests until GREEN.

### Task 3: Stabilize local update signing without weakening CI

**Files:**
- Modify: `app/build.gradle.kts`
- Create: local-only `%USERPROFILE%/.harmony-build-tools/signing/harmony-debug.keystore`

- [ ] Copy the already matching installed-app key to the stable local path; never generate a new key.
- [ ] Resolve local debug signing from `HARMONY_DEBUG_KEYSTORE_PATH` / Gradle property first, then stable home path, then safe CI-compatible fallback.
- [ ] Keep existing CI production signing workflow and its protected CI key unchanged.
- [ ] Run `signingReport` and `apksigner --print-certs` before any device update.
### Task 4: Document protected infrastructure

**Files:**
- Create: `docs/android/harmony-google-signing.md`
- Modify: `AGENTS.md`

- [ ] Document package, local stable keystore path, local update fingerprints, Google Cloud project/client IDs, Supabase project/callback, nonce flow, verification commands, and abort-on-mismatch rule.
- [ ] Update AGENTS so future local device updates must verify the local installed-app SHA before `adb install -r` and must never uninstall to bypass a mismatch.
- [ ] Explicitly distinguish the local physical-device debug identity from the repository's existing protected CI production signing identity.

### Task 5: Physical-device verification and persistence

**Files:** repository changes above only.

- [ ] Run targeted tests, relevant unit tests, `git diff --check`, `assembleDebug`, `signingReport`, and `apksigner`.
- [ ] Install only when the APK SHA-1 equals the currently installed Harmony SHA-1; use `adb install -r` and preserve app data.
- [ ] Launch Harmony and exercise the Google button through Credential Manager to a real Supabase session.
- [ ] Build a second APK from the same setup, verify the same signature, update again, and confirm the saved Supabase session survives.
- [ ] Commit intended files, push branch, create PR with verification evidence and `<!-- agent-persist: verified -->`, wait for required checks, merge, verify `origin/main`, then perform final merged-main install/launch verification.