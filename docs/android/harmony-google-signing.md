# Harmony Google login and Android signing

## Fixed Android identity

- Package: `com.aistudio.harmony.couples.xqvz`
- Local keystore: `%USERPROFILE%\.harmony-build-tools\signing\harmony-debug.keystore`
- Gradle override: `HARMONY_DEBUG_KEYSTORE_PATH` as Gradle property or environment variable
- Alias: `androiddebugkey`
- SHA-1: `63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F`
- SHA-256: `77:9B:7D:D8:B4:86:CD:FE:01:F6:1D:90:F4:CE:9C:7F:73:63:D5:59:15:9B:8F:26:CB:6B:A3:5E:A2:AA:6D:7B`

Never generate a replacement key for a real-device Harmony update. The keystore, Base64 encoding, passwords and OAuth client secret must never be committed.

## Google Cloud

Use only the existing Google Cloud project **Harmony App**.

- Web OAuth client: `Harmony App Supabase`
- Web client ID: `1038373974684-lh5o0nhstljubgf76gfg62ifp302ulm6.apps.googleusercontent.com`
- Authorized callback: `https://rspgnonlpkxdudbjxnrl.supabase.co/auth/v1/callback`
- Android OAuth client: `Harmony Android App`
- Android client ID: `1038373974684-igvmqtg5hgk4ttf49ko8m7j717u5vta0.apps.googleusercontent.com`
- Android binding: package above + permanent SHA-1 above
- Audience status: external / testing; `jeromej9465@gmail.com` is a test user.

## Supabase native ID-token flow

Project: `rspgnonlpkxdudbjxnrl`. Google provider stays enabled with nonce checks enabled. The provider client-ID list must include the Web and Android client IDs above; the Web OAuth secret lives only in Supabase.

Harmony Google sign-in is native-only:

1. Generate a cryptographically random raw nonce.
2. Compute lowercase hexadecimal SHA-256 of the raw nonce.
3. Send the SHA-256 nonce to `GetGoogleIdOption` together with the Web client ID.
4. Receive `GoogleIdTokenCredential` from Android Credential Manager.
5. Send the Google ID token plus the original raw nonce to Supabase `signInWith(IDToken)` with provider Google.
6. Let Supabase persist/refresh the authenticated session through the installed Auth client.

Do not add WebView or browser OAuth as a Google-button fallback and do not disable Supabase nonce validation.

## Build and installation guard

Run `./gradlew :app:testDebugUnitTest :app:assembleDebug :app:signingReport`. Then run `apksigner verify --print-certs app/build/outputs/apk/debug/app-debug.apk`.

Before `adb install -r`, pull or inspect the currently installed `com.aistudio.harmony.couples.xqvz` APK and compare SHA-1 fingerprints. Install only when both equal the permanent SHA-1 above. On mismatch: abort, fix signing, and never uninstall Harmony as a workaround.

GitHub installable builds use repository secret `HARMONY_CI_DEBUG_KEYSTORE_B64`; the workflow must fail closed when the secret is absent or its certificate differs.

## Post-login account session

A successful Google credential exchange is not enough by itself: Harmony immediately calls `get_app_session()` after Supabase Auth creates the session.

The canonical RPC migration is `supabase/migrations/20260907222425_fix_get_app_session_email_type.sql`.
It must use `ON CONFLICT ON CONSTRAINT harmony_profiles_pkey` instead of `ON CONFLICT(user_id)` and cast `auth.users.email` to `text` in the return query. These details prevent PL/pgSQL name ambiguity and exact return-type failures that otherwise surface as the generic account-loading error screen.
