# Harmony Android signing identity

Installable APKs produced from `main` use one permanent debug signing identity so Android updates and Google Sign-In keep the same package/certificate pair.

## Permanent identity

- Package: `com.aistudio.harmony.couples.xqvz`
- Keystore alias: `androiddebugkey`
- Store password: `android`
- Key password: `android`
- SHA-1: `7F:F5:D5:66:BB:0F:6E:AB:BC:B7:03:E8:8F:64:C7:9A:38:3A:BB:89`
- SHA-256: `24:22:E7:AE:95:80:3A:C9:F6:DF:E0:8D:6C:5F:A0:DB:07:04:28:00:D7:7A:EA:0D:ED:67:22:66:48:4E:21:5D`

## GitHub Actions secret

The private keystore must never be committed. Store its single-line Base64 representation in the repository Actions secret:

`HARMONY_CI_DEBUG_KEYSTORE_B64`

`.github/workflows/android-apk-build.yml` restores the key into repository-root `debug.keystore`. `app/build.gradle.kts` explicitly uses that file for the debug signing config. The workflow checks both the restored keystore SHA-1 and the final APK certificate SHA-1 before uploading an installable artifact.

If the secret is missing or the certificate differs, the main APK build must fail closed. Never generate a replacement key automatically.

## Google OAuth

The Android OAuth client for package `com.aistudio.harmony.couples.xqvz` must contain the permanent SHA-1 above. A signing-key rotation therefore requires updating Google OAuth before distributing APKs signed by the new key.

## Backup

Keep at least one offline owner backup of the keystore. Losing the private key means APKs signed by this identity cannot be recreated from the public fingerprint or from an existing APK.
