# DrumPadMachine — Architecture & AI Assistant Guide

Read this first. It exists so you don't have to re-explore the repo each
session — trust it over globbing/searching for the facts listed below.

## Stack
- Kotlin, multi-module Gradle (Kotlin DSL). **No version catalog** — all
  dependency coordinates/versions live in `buildSrc/src/main/kotlin/`:
  `ApplicationDependencies.kt`, `ApplicationDependencyVersions.kt`,
  `ApplicationConfiguration.kt` (compileSdk/minSdk/targetSdk, namespaces).
  Add/bump deps there, not inline in a module's `build.gradle.kts`.
- UI: Jetpack Compose + Material3. No XML layouts for screens.
- DI: Dagger Hilt (`@HiltAndroidApp` on `Application.kt`, `@AndroidEntryPoint`
  on activities, `@HiltViewModel` on view models).
- Persistence: Room DB + DataStore preferences.
- Networking: Retrofit + OkHttp + Gson.
- Navigation: `compose-destinations` (raamcosta) — generated `NavGraphs` /
  `*Destination` classes, don't hand-write nav graphs.
- Firebase: Crashlytics + Analytics.
- Audio: native C++ via Google's **Oboe** low-latency audio lib, bridged to
  Kotlin over JNI.
- Build variants: flavor dimension `environment` × `dev` / `beta` / `prod`
  (distinct app id/name/icon per flavor).

## Module map
- `:app` (`com.slaviboy.drumpadmachine`) — feature-first layout, see below.
- `:lib:audio` — Kotlin/JNI bridge. Core class:
  `lib/audio/src/main/java/com/slaviboy/audio/DrumPadPlayer.kt`
  (`trigger`, `stopTrigger`, `setPan`/`getPan`, `setGain`/`getGain`,
  `loadWavFile`, `loadWavAssets`).
- `:lib:oboe`, `:lib:iolib`, `:lib:parselib` — native C++ (Oboe itself,
  the native player, WAV/stream parsing). **Native code — edit with care**;
  requires NDK + CMake 3.22.1 to build.

### `:app` internal layout (feature-first, not layer-first)
- `screens/<feature>/{composables,viewmodels,helpers,usecases}` — one
  package per feature: `drumpad`, `home`, `lessonslist`, `presetslist`.
  Put new screen code in the matching feature package, following this shape.
- `data/room/<entity>/` — one folder per table, each with `*Entity.kt` +
  `*Dao.kt` (e.g. `preset/`, `pad/`, `category/`, `file/`, `filter/`,
  `lesson/`, `config/`); `data/room/relations/` for joined query models;
  `data/room/Database.kt` is the Room DB root.
- `data/entities/` — plain domain models (`Preset`, `File`, ...).
- `data/workers/` — `CoroutineWorker`s that sync DB from API/ZIP.
- `api/` — `entities/`, `repositories/` (e.g. `ApiRepository.kt`),
  `results/`, `services/` (Retrofit interfaces).
- `modules/` — Hilt modules: `DataModule.kt`, `NetworkModule.kt`,
  `ViewModelModule.kt`.
- `composables/`, `ui/`, `core/entities/`, `enums/`, `events/`,
  `extensions/`, `dispatchers/`, `network/`, `global/` — shared/cross-cutting
  code.

## Architecture pattern
MVVM + Repository. `@HiltViewModel` classes inject repositories (e.g.
`ApiRepository`) rather than talking to Room/Retrofit directly. Namespace
convention is `com.slaviboy.<module>`.

## Domain core (start here for pad/audio work)
- `DrumPadPlayer` (`:lib:audio`) — native audio engine wrapper.
- `DrumPadViewModel`
  (`app/src/main/java/com/slaviboy/drumpadmachine/screens/drumpad/viewmodels/`)
  — multi-touch hit-testing over the pad grid, choke-group stopping
  (`playSoundAtIndex` stops other pads sharing a `choke` id), pad paging.
- `Preset` / `File` entities carry pad config: filename, color, choke,
  stopOnRelease.
- `PadColor` enum (`enums/PadColor.kt`) maps preset color names to pad UI.
- **Resource loading cascade**: DB → extracted ZIP → API-fetched ZIP, synced
  via a `CoroutineWorker` (`data/workers/`) into Room (~15–20s operation).

## Build & test facts (don't re-derive these)
- `./gradlew assembleDevDebug` / `assembleBetaDebug` / `assembleProdDebug`
  (flavor × build type combos). `./gradlew lint`.
- **No tests exist** — `app/src/test` and `app/src/androidTest` are absent.
  Don't search for test coverage or assume a testing convention; if asked to
  add tests, you're creating the first ones.
- **No CI** — there is no `.github/` directory or other CI config.

## Working efficiently here
- Use the module map above instead of globbing the repo to find where
  something lives.
- Don't grep every `build.gradle.kts` for dependency versions — they're only
  in `buildSrc/src/main/kotlin/ApplicationDependencies*.kt`.
- Follow the existing feature-first / per-entity-folder shape for new code
  rather than inventing a new structure.
- Native modules (`lib/oboe`, `lib/iolib`, `lib/parselib`) are vendored/
  low-level — prefer changing the Kotlin/JNI bridge (`lib/audio`) unless the
  task specifically requires touching native code.
