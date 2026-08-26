# MMovies

MMovies is an Android app for browsing movies from [The Movie Database (TMDB)](https://www.themoviedb.org/) — built with **Jetpack Compose** and **Clean Architecture (MVVM)**.

## Features

- Browse movies by category: **Popular**, **Upcoming**, **Now Playing**, **Top Rated**, and **Favorites**
- View detailed movie info: poster, backdrop, release date, runtime, genres, user score, and overview
- Long-press-to-zoom poster preview
- Offline/connectivity detection with dedicated UI states
- User-supplied TMDB API key, validated on first launch and persisted locally

## Architecture

The app follows **Clean Architecture**, split into three layers, wired together with Hilt dependency injection:

```
com.bk.mmovies
├── data/            # Data layer — remote/local sources, repository implementations, mappers
├── domain/           # Domain layer — models & repository interfaces, no Android dependencies
├── ui/                # Presentation layer — Compose screens, ViewModels, navigation
├── di/                # Hilt modules
├── connectivity/      # Network/internet monitoring
└── util/              # Shared utilities
```

- **Domain layer** (`domain/`) defines plain Kotlin models (`MovieModel`, `MovieDetailsModel`, `CatalogItem`, `MovieCategory`) and repository contracts (`MovieRepository`, `TvSeriesRepository`, `SearchRepository`, `AuthenticationRepository`), independent of any framework.
- **Data layer** (`data/`) implements those contracts. Remote calls go through a Retrofit `TmdbApi`, wrapped by a `NetworkManager` that returns a sealed `ApiCallResult` (Success/Failure); `ApiKeyInterceptor` and `LanguageInterceptor` attach the user's API key and the current app locale to every request. Room (`MovieDb`) backs three caches — favorite movie ids, favorite TV ids, and recent searches — reached through their DAOs; the TMDB API remains the source of truth and `LocalSessionDataCleaner` wipes all three on logout. The user's API key and session id live in `AuthCredentialsSharedPrefs` (excluded from cloud backup and device transfer). Mappers (`MovieMapper`, `MovieDetailsMapper`, `CatalogItemMapper`, …) convert DTOs to domain models, and the repository impls return sealed results (`MoviesResult`, `MovieDetailsResult`).
- **Presentation layer** (`ui/`) uses Jetpack Compose screens paired with `ViewModel`s that expose UI state via `StateFlow`/`SharedFlow`. Navigation is type-safe Compose Navigation (`AppNavigation.kt`) with sealed `AppDestination` routes.

## Screens

| Screen | Description |
|---|---|
| **Splash** | Checks internet connectivity and validates/prompts for a TMDB API key before proceeding |
| **Movies** | Category-filterable movie list with a popup category selector |
| **Details** | Full movie details — poster, backdrop, release date, runtime, genres, user score, overview |

## Tech Stack

- **UI**: Jetpack Compose, Material 3, Navigation Compose, ConstraintLayout Compose
- **DI**: Hilt (with KSP)
- **Networking**: Retrofit, OkHttp, Gson, Kotlinx Serialization
- **Persistence**: Room, SharedPreferences (API key storage)
- **Images**: Coil3
- **Other**: Lottie (animations), Firebase Crashlytics, LeakCanary (debug)
- **Testing**: JUnit, Espresso, Compose UI Test, MockWebServer, Hilt Testing

## Requirements

- `minSdk`: 23
- `targetSdk` / `compileSdk`: 37
- Java 17

## Setup

The app requires a personal TMDB API key, which is entered and validated on first launch (persisted locally afterward). Get a free key at [themoviedb.org](https://www.themoviedb.org/settings/api).

## Known Issues / Roadmap

The **Favorites** category is currently a UI stub — the local Room database exists but favorites persistence is not yet wired into the repository.
