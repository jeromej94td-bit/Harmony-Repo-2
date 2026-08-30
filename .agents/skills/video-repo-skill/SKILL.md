---
name: video-repo-skill
description: Use when adding, replacing, wiring, debugging, or changing an intro video, fullscreen video, in-game video, video asset, or video-triggered flow in the Harmony repository.
---

# Video Repo Skill

## Core rule

Before adding or changing a Harmony video, inspect the current repository and reuse the newest working video path instead of inventing a parallel playback or asset system.

The current reference implementation for a large cinematic intro is the Unterbewusstsein / introspection flow.

## Current source of truth

Inspect these files before editing a video flow:

1. `app/src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt` — reference for a fullscreen intro that gates the game flow and coordinates narrator/background audio.
2. `app/src/main/java/com/example/ui/components/HarmonyRawVideoAnimation.kt` — shared Compose/Android `VideoView` playback wrapper.
3. `app/build.gradle.kts` — build-time reconstruction and integrity verification for chunked video assets.
4. `app/src/main/assets/introspection/` — numbered Base64 chunks for large cinematic assets.
5. `app/src/main/res/raw/` — reconstructed/raw playable media resources.
6. The screen that owns the target experience, plus its state/navigation code, before deciding where the video should trigger.

Do not assume these paths or mechanics are unchanged. Read the current versions first.

## Proven Unterbewusstsein intro pattern

The current introspection intro uses this sequence:

1. The source video is represented by ordered chunks such as `introspection_intro_00.b64` ... `introspection_intro_24.b64` under `app/src/main/assets/introspection/`.
2. `ReconstructIntrospectionIntroTask` in `app/build.gradle.kts` requires the exact chunk-name set, concatenates and Base64-decodes it, checks the decoded byte count, checks SHA-256, then atomically writes `app/src/main/res/raw/introspection_intro.mp4`.
3. `preBuild` depends on that reconstruction task, so a missing, reordered, corrupt, wrong-size, or wrong-hash asset fails before packaging.
4. `IntrospectionExperienceScreen` owns a `showIntroVideo` state. Starting or continuing the experience stops narrator/answer audio, pauses background music, sets the intro visible, and prevents narrator playback while the intro is active.
5. The intro is rendered as a fullscreen overlay with `HarmonyRawVideoAnimation(rawResId = R.raw.introspection_intro, immersive = true, roundedCorners = false, assetPrefix = "introspection_intro_")`.
6. On video completion the overlay is dismissed and background music resumes. The game/narrator flow is allowed to continue only after completion.

This is a reference pattern, not a license to copy its IDs, file size, SHA, chunk count, resource name, or timing into another video.

## Shared player behavior

`HarmonyRawVideoAnimation` currently:

- wraps Android `VideoView` inside Compose `AndroidView`;
- starts playback when prepared;
- mutes the video's own audio with `player.setVolume(0f, 0f)`;
- calls `onCompleted` when playback ends;
- can hide Android system bars in immersive mode and restores them on dispose;
- supports square/fullscreen presentation by disabling its default rounded corners;
- supports direct `res/raw` playback;
- contains an introspection-specific chunk/cache fallback when `assetPrefix` is used.

Important: the current `assetPrefix` reconstruction path inside `HarmonyRawVideoAnimation` is not fully generic. It reads from the `introspection` assets directory and validates the introspection video's known byte size. Do not pass a new arbitrary prefix and assume it will work. Either use direct `res/raw` playback or deliberately generalize the component with tests before using chunk-runtime reconstruction for a different video.

## How to add a new large video

For a new large cinematic video, follow the existing build-safe pattern unless the current repo has since adopted something newer:

- give the video a unique stable snake_case resource prefix;
- store ordered Base64 chunks in the existing appropriate asset location or a clearly dedicated sibling location;
- create a dedicated reconstruction task rather than overwriting the introspection task;
- derive and record the new video's own expected chunk names/count, exact decoded byte size, and SHA-256;
- write to a unique `src/main/res/raw/<video_name>.mp4` output;
- wire that reconstruction task into `preBuild`;
- use `HarmonyRawVideoAnimation` for playback instead of creating another player unless the existing component cannot satisfy a verified requirement.

Never copy `7_297_407`, the introspection SHA-256, `00..24`, or `R.raw.introspection_intro` to a different video.

## Flow integration rules

The screen that owns the experience owns the video state. A video should not be triggered from an unrelated global layer merely because that is convenient.

Before wiring it, verify:

- exactly when it should appear: before first question, once per run, on every restart, on continue, or only for a particular pack;
- what UI must remain hidden until completion;
- whether system back is allowed, blocked, or delegated to the owning flow;
- what narrator, answer audio, music, or recording may already be active;
- what must resume after completion;
- how state behaves after recomposition and when the pack/experience changes.

For introspection specifically, the current contract is: intro first, narrator second. Do not allow both to start at the same time.

## Audio rule

Do not let a newly inserted cinematic accidentally overlap existing Harmony audio.

Inspect the owning media controller. Stop/pause only the streams that the current flow requires, then restore the correct stream from `onCompleted` or the owning lifecycle. Do not globally mute or release unrelated app audio.

The shared video component currently mutes the video's own track. If a future video is intentionally supposed to contain audible dialogue/music, that is a behavior change to the player contract and must be implemented explicitly rather than assumed.

## Existing second example

`QuizRunnerScreen.kt` uses the same shared player for `moral_grey_zones_intro.mp4`: it keys intro state to the active pack, keeps the question UI hidden until `moralIntroFinished`, plays the raw resource fullscreen/immersive, then reveals the normal question flow after `onCompleted`.

Use this as the simpler reference when a video is already available directly in `res/raw` and does not need the introspection audio choreography.

## Verification before completion

For every video change, verify the parts that apply:

- asset/chunks exist at the exact paths referenced by code;
- reconstruction task expects exactly the files actually committed;
- decoded size and SHA-256 match the actual new video;
- `preBuild` depends on the reconstruction task when reconstruction is required;
- target raw resource name matches the Kotlin reference;
- playback starts, completes, and invokes the transition once;
- the underlying question/game UI does not become interactive early;
- immersive system bars are restored when the video leaves composition;
- narrator/music/recording do not overlap incorrectly;
- restart/continue/back behavior matches the experience requirement;
- no unrelated video, game, question, audio, or navigation code is rewritten as a side effect.

Do not claim the video path works merely because the Kotlin compiles. Verify the asset reconstruction/integrity path and the owning state transition too.

## Failure patterns to avoid

Do not:

- invent a new video folder without checking the real asset/build path;
- drop a large MP4 into an arbitrary location and bypass the existing integrity/reconstruction setup;
- create a second player when `HarmonyRawVideoAnimation` already fits;
- copy introspection-specific byte/hash/chunk constants to another video;
- start narrator/music underneath an intro that is supposed to gate them;
- show the first question behind or before a mandatory intro finishes;
- use conversation memory as proof of current implementation — inspect `main` or the requested branch first.
