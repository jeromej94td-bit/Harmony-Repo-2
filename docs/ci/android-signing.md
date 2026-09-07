# Harmony Android signing identity

Installable APKs produced from `main` use one permanent debug signing identity so Android updates and Google Sign-In keep the same package/certificate pair.

## Permanent identity

- Package: `com.aistudio.harmony.couples.xqvz`
- Keystore alias: `androiddebugkey`
- Store password: `android`
- Key password: `android`
- SHA-1: `63:9B:57:CF:60:DE:AC:0C:55:21:FB:9E:DD:79:93:44:4F:F1:3C:6F`
- SHA-256: `77:9B:7D:D8:B4:86:CD:FE:01:F6:1D:90:F4:CE:9C:7F:73:63:D5:59:15:9B:8F:26:CB:6B:A3:5E:A2:AA:6D:7B`

## GitHub Actions secret

The private keystore must never be committed. Store its single-line Base64 representation in the repository Actions secret:

`HARMONY_CI_DEBUG_KEYSTORE_B64`

`.github/workflows/android-apk-build.yml` restores the CI key into repository-root `debug.keystore`. Local real-device builds prefer `%USERPROFILE%\.harmony-build-tools\signing\harmony-debug.keystore` and may override that path with `HARMONY_DEBUG_KEYSTORE_PATH`. The workflow checks both the restored keystore SHA-1 and the final APK certificate SHA-1 before uploading an installable artifact.

If the secret is missing or the certificate differs, the main APK build must fail closed. Never generate a replacement key automatically.

## Google OAuth

The Android OAuth client for package `com.aistudio.harmony.couples.xqvz` must contain the permanent SHA-1 above. A signing-key rotation therefore requires updating Google OAuth before distributing APKs signed by the new key.

## Backup

Keep at least one offline owner backup of the keystore. Losing the private key means APKs signed by this identity cannot be recreated from the public fingerprint or from an existing APK.
