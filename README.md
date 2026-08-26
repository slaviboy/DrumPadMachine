# DrumPadMachine

A native Android drum pad / sampler app built with Jetpack Compose. Browse sound packs,
play a low-latency multi-touch drum pad grid, and learn presets through interactive,
tap-along lessons that score your timing.

## Features

- **Drum pad grid** — multi-touch pad grid with per-pad pan/gain, choke groups (tapping one
  pad stops others sharing the same choke id), and paging across large presets.
- **Presets & sound packs** — browse and load presets (color, pad layout, filenames per pad),
  synced from a remote source and cached locally.
- **Lessons** — guided, tap-along tutorials per preset:
  - **Listen** phase plays back the pattern on a fixed tempo-driven clock while the relevant
    pads flash in sequence.
  - **Play** phase asks the user to tap the highlighted pad; taps are scored against the
    beat (Perfect / Good / Late / Missed), and the pad only advances once tapped — it never
    auto-skips ahead of the user.
  - Pass/fail results screen with score, stars, best score, and quick actions to retry
    (drops straight into Play, skipping Listen) or continue into the next unlocked lesson.
- **Low-latency audio** — native C++ playback via Google's Oboe library, bridged to Kotlin
  over JNI.

## Tech stack

- **Kotlin**, multi-module Gradle (Kotlin DSL), no version catalog — dependency
  coordinates/versions live in `buildSrc/`.
- **UI**: Jetpack Compose + Material3 (no XML layouts for screens).
- **DI**: Dagger Hilt.
- **Persistence**: Room DB + DataStore preferences.
- **Networking**: Retrofit + OkHttp + Gson.
- **Images**: Coil, with disk + memory caching enabled.
- **Navigation**: [compose-destinations](https://github.com/raamcosta/compose-destinations).
- **Crash/analytics**: Firebase Crashlytics + Analytics.
- **Audio engine**: [Oboe](https://github.com/google/oboe) (native C++) via a Kotlin/JNI bridge.

## Module structure

- `:app` — feature-first screens (`drumpad`, `home`, `lessonslist`, `lessonplayer`,
  `presetslist`), Room entities/DAOs, API layer, Hilt modules.
- `:lib:audio` — Kotlin/JNI bridge (`DrumPadPlayer`): trigger/stop, pan/gain, WAV loading.
- `:lib:oboe`, `:lib:iolib`, `:lib:parselib` — native C++ (Oboe itself, the native player,
  WAV/stream parsing).

Resources (presets, pads, lessons) are loaded through a DB → extracted ZIP → API-fetched ZIP
cascade, synced into Room via a `CoroutineWorker`.

## Building

```bash
./gradlew assembleDevDebug     # or assembleBetaDebug / assembleProdDebug
./gradlew lint
```

Requires the Android NDK + CMake 3.22.1 for the native audio modules.

----

#DrumPadMachine

----

TODO:
1) [x] Implement crashlitics for app
2) [ ] Implement Tutorials functionality
3) [ ] Allow users to load custom sounds (23 audio files, pick color and position)
4) [ ] Implement recording songs
5) [ ] Implement settings
      - pan
      - reverb

----

Loading Resources:
1) If has DB -> loads resources from it
2) If no DB -> If has extracted ZIP -> load resources from it
3) If no DB -> If no extracted ZIP -> make API call (get ZIP archive) -> extract ZIP -> load resources from it
4) Use `CoroutineWorker` to save the extracted ZIP in to DB (as it takes 15-20s)
