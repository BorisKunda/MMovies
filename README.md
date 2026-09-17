# MMovies

Native apps for **Android phones and Android TV**, powered by [The Movie Database (TMDB)](https://www.themoviedb.org/) and built with **Jetpack Compose**, **MVVM**, and **Clean Architecture**. Browse movies, TV series, seasons, episodes and people, with remote-control navigation on TV.

[![minSdk](https://img.shields.io/badge/minSdk-23-blue)](https://developer.android.com/tools/releases/platforms)
[![targetSdk](https://img.shields.io/badge/targetSdk-37-blue)](https://developer.android.com/tools/releases/platforms)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.06.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![100% Free](https://img.shields.io/badge/100%25%20Free-No%20Ads-brightgreen)](#license--attribution)
[![License](https://img.shields.io/badge/license-all%20rights%20reserved-lightgrey)](#license--attribution)

---

## Screenshots

<p align="center">
  <img src="docs/screenshots/03-movies-catalog.png" width="30%" alt="Movies catalog" />
  <img src="docs/screenshots/04-tv-series-catalog.png" width="30%" alt="TV series catalog" />
  <img src="docs/screenshots/09-search.png" width="30%" alt="Multi-search across movies, TV series and people" />
</p>

<p align="center">
  <strong>Movies · TV Series · Multi-search</strong>
</p>

---

## Features

### Movies & TV

- Popular, Now Playing, Upcoming and Top Rated movies
- Popular, Airing Today, On TV, Upcoming and Top Rated TV series
- Infinite-scroll pagination
- Movie and TV-series details
- Seasons and episode information
- Cast, crew and person details
- User scores, genres, runtime and release information
- Full-size poster preview
- In-app YouTube trailer playback with fullscreen support

### Search

- Debounced multi-search across movies, TV series and people
- Media-type filters
- Recent-search history stored locally with Room

### Accounts & Favourites

- Native TMDB username/password authentication
- Guest mode
- Server-side TMDB favourites
- Optimistic favourite updates with automatic rollback on failure
- Local Room cache of favourite IDs
- Account-specific local data cleared on logout

### News — Mobile

- Entertainment news feed powered by [NewsAPI](https://newsapi.org/), accessible from its own bottom-bar tab
- Infinite-scroll pagination
- Keyword-filtered feed (titles unrelated to film/TV production are excluded)
- In-app article reader with a full-screen WebView overlay
- Share articles to any installed app (WhatsApp, Messages, Gmail, etc.)

### Platform

- English, Russian and Hebrew
- Full RTL support for Hebrew
- Runtime locale detection and TMDB content reloading
- Dark Material 3 UI
- Connectivity monitoring and dedicated offline states
- Terms of Service and Privacy Policy screens
- TMDB attribution
- Crash reporting with Firebase Crashlytics

---

## Detailed Browsing

MMovies supports navigation from a catalog item all the way down to seasons, episodes and people.

### Movie Details

<p align="center">
  <img src="docs/screenshots/05-movie-details.jpg" width="45%" alt="Movie details with trailer, cast and user score" />
</p>

Movie details include poster artwork, release information, genres, user score, overview, trailer, director and writers, and cast.

### TV Series Details

<p align="center">
  <img src="docs/screenshots/06-tv-series-details.jpg" width="45%" alt="TV series details with cast, trailer and seasons" />
</p>

TV-series details include status, season count, genres, overview, trailer, creators, cast and the complete season list.

### Seasons & Episodes

<p align="center">
  <img src="docs/screenshots/07-season-details.jpg" width="45%" alt="Season details with episode list" />
  <img src="docs/screenshots/08-episode-details.png" width="45%" alt="Episode details dialog" />
</p>

Each season exposes its episode list, and individual episodes can be opened for additional details.

### Person Details

<p align="center">
  <img src="docs/screenshots/10-person-details.png" width="45%" alt="Person details and biography" />
</p>

Cast members can be opened directly from movie and TV-series details to view person information and biography.

---

## Localization

MMovies is available in **English, Russian and Hebrew**.

| Language | Resources | TMDB locale | Direction |
|---|---|---|---|
| English | `values/` | `en-US` | LTR |
| Russian | `values-ru/` | `ru-RU` | LTR |
| Hebrew | `values-iw/` | `he-IL` | RTL |

The current locale is automatically applied to TMDB requests. Changing the application language causes currently displayed remote content to be fetched again using the new locale.

<p align="center">
  <img src="docs/screenshots/13-hebrew-rtl-movies.png" width="45%" alt="Movies catalog in Hebrew with RTL layout" />
  <img src="docs/screenshots/14-russian-movie-details.png" width="45%" alt="Movie details localized in Russian" />
</p>

<p align="center">
  <strong>Hebrew RTL · Russian localization</strong>
</p>

---

## Categories

Movies and TV series each have their own category selector.

<p align="center">
  <img src="docs/screenshots/11-movie-categories.png" width="45%" alt="Movie category selector" />
  <img src="docs/screenshots/12-tv-series-categories.png" width="45%" alt="TV series category selector" />
</p>

---

## News

<p align="center">
  <img src="docs/screenshots/18-news.png" width="35%" alt="News feed with article cards, read-full-article and share actions" />
</p>

An entertainment news feed sits alongside Movies and TV Series as its own bottom-bar tab, powered by [NewsAPI](https://newsapi.org/). Each card shows the source, publish date and excerpt, with actions to read the full article in-app or share it to any installed app.

A [NewsAPI key](https://newsapi.org/register) is required to load mobile news. For source builds, configure it in a `newsapi.properties` file at the project root (`NEWS_API_KEY=your-key-here`). This file is gitignored and not included in the repository.

> The Android TV app's News tab is powered by [The Guardian's Content API](https://open-platform.theguardian.com/) instead — see [Android TV](#android-tv) below. Its key follows the same pattern, in a gitignored `guardian.properties` file (`GUARDIAN_API_KEY=your-key-here`).

---

## Android TV

MMovies also ships as a separate, installable **Android TV** app (`:tv` module, leanback launcher), built for D-pad navigation on a 10-foot UI.

<p align="center">
  <img src="docs/screenshots/androidTv-catalog.png" width="100%" alt="Android TV Popular TV Series catalog with nav rail and highlighted selection" />
</p>

<p align="center">
  <img src="docs/screenshots/androidTv-search.png" width="45%" alt="Android TV search with on-screen keyboard" />
</p>

<p align="center">
  <img src="docs/screenshots/androidTv-tv-series-details.png" width="45%" alt="Android TV episode details with artwork, runtime, air date, user score and overview" />
  <img src="docs/screenshots/androidTv-news.png" width="45%" alt="Android TV News tab powered by The Guardian" />
</p>

- Nav-rail navigation for Movies, TV Series, Search, Favorites, News and Recommendations, plus About
- Movie and TV-series details with inline seasons/episodes, cast and trailer — all reachable by D-pad scroll (TV series seasons/episodes are shown inline rather than mobile's tap-to-drill-down flow, since it reads better on a leanback layout)
- On-screen keyboard search with debounced results
- Server-side TMDB favourites, shared with the mobile app's account
- **News tab backed by The Guardian's Content API** (`:tv`-only, separate from mobile's NewsAPI-backed feed)
- **AI-powered Recommendations tab**, backed by Google's Gemini API — suggests titles using favourites and recent searches. A [Gemini API key](https://aistudio.google.com/app/apikey) is required to load recommendations; for source builds, configure it in a gitignored `gemini.properties` file (`GEMINI_API_KEY=your-key-here`)
- Shares its data/domain layer with the mobile app via a common `:core` module — see [Project Structure](#project-structure)

---

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin 2.4.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM |
| State | StateFlow / SharedFlow |
| Async | Kotlin Coroutines + Flow |
| Dependency Injection | Hilt |
| Networking | Retrofit 3 + OkHttp 5 |
| Local Database | Room |
| Image Loading | Coil 3 |
| Navigation | Navigation Compose |
| Serialization | kotlinx.serialization |
| Video | android-youtube-player |
| Crash Reporting | Firebase Crashlytics |
| Leak Detection | LeakCanary |
| Testing | JUnit, Espresso, Compose UI Test, MockWebServer |

Release builds use **R8 code shrinking and resource shrinking**.

---

## Architecture

The project follows Clean Architecture with three main layers:

```text
UI
│
├── Screens / Composables
├── ViewModels
│
▼
Domain
│
├── Models
├── Repository interfaces
├── Result types
│
▼
Data
│
├── Repository implementations
├── DTO → domain mappers
├── Retrofit / OkHttp
├── Room
└── SharedPreferences
```

Dependency direction:

```mermaid
flowchart LR
    UI["UI\nCompose + ViewModels"]
    DOMAIN["Domain\nModels + Repository contracts"]
    DATA["Data\nRepositories + Mappers"]
    LOCAL["Room / SharedPreferences"]
    REMOTE["Retrofit / OkHttp"]
    TMDB["TMDB API"]

    UI --> DOMAIN
    DATA --> DOMAIN
    DATA --> LOCAL
    DATA --> REMOTE
    REMOTE --> TMDB
```

Main repositories include:

- `MovieRepository`
- `TvSeriesRepository`
- `SearchRepository`
- `PersonRepository`
- `AuthenticationRepository`

Cross-cutting services include:

- `InternetMonitor`
- `LocaleMonitor`
- `NetworkManager`
- `ApiKeyInterceptor`
- `LanguageInterceptor`

---

## TMDB API Key

The repository does **not** include a TMDB API key. Users or developers supply their own key.

On mobile, the user enters their own **TMDB API v3 key** when the app starts for the first time.

### Get a key

1. Create a TMDB account at [themoviedb.org/signup](https://www.themoviedb.org/signup).
2. Open [themoviedb.org/settings/api](https://www.themoviedb.org/settings/api).
3. Request a Developer API key.
4. Copy the **API Key (v3 auth)** value.

The app validates the key against TMDB before continuing.

On mobile, the key is entered at runtime — you do **not** need to modify:

- `local.properties`
- `gradle.properties`
- `BuildConfig`
- source code

The stored credentials file is excluded from Android cloud backup and device-to-device transfer.

> Never commit a real TMDB API key to the repository.

For Android TV source builds, the documented developer configuration is a gitignored `tmdb.properties` file (`TMDB_API_KEY=your-key-here`). A build configured with this fallback may include the supplied key; the repository itself does not supply one.

<p align="center">
  <img src="docs/screenshots/01-api-key-setup.png" width="35%" alt="TMDB API key setup" />
</p>

---

## Authentication

MMovies uses TMDB's **v3 session authentication**.

### Signed-in user

```text
Request Token
      ↓
Validate username + password
      ↓
Create TMDB session
      ↓
Load account
      ↓
Enable account favourites
```

Authentication uses a native Compose login form rather than an embedded WebView.

### Guest

Users can also continue with a TMDB guest session.

Guests have full catalog browsing access, but account favourites require a signed-in TMDB account.

### Logout

Logging out:

- Deletes the TMDB session
- Clears locally stored session credentials
- Clears account-scoped favourite caches
- Clears recent search history

This prevents data belonging to one account from appearing after another account signs in.

<p align="center">
  <img src="docs/screenshots/02-auth.png" width="35%" alt="TMDB account login and guest access" />
</p>

---

## Getting Started

### Requirements

- Android Studio compatible with AGP 9.2.1
- JDK 17
- Android API 23+
- Free TMDB API key
- Free [NewsAPI](https://newsapi.org/register) key (only needed for `:app`'s News tab — see [News](#news))
- Free [Guardian Content API](https://open-platform.theguardian.com/access/) key (only needed for `:tv`'s News tab — see [Android TV](#android-tv))
- Free [Google AI Studio (Gemini)](https://aistudio.google.com/app/apikey) key (only needed for `:tv`'s Recommendations tab — see [Android TV](#android-tv))

### Clone

```bash
git clone https://github.com/BorisKunda/MMovies.git
cd MMovies
```

Open the project in Android Studio and let Gradle sync.

No API key is required to compile the project.

On first mobile launch:

1. Enter your TMDB API key.
2. The app validates it.
3. Sign in with your TMDB account or continue as guest.

### Command Line

```bash
# Mobile
./gradlew :app:assembleDebug
./gradlew :app:installDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest

# Android TV
./gradlew :tv:assembleDebug
./gradlew :tv:installDebug
```

On Windows:

```bash
gradlew.bat :app:assembleDebug
gradlew.bat :tv:assembleDebug
```

---

## Project Structure

```text
MMovies/
├── app/                          # Mobile app (phone/tablet)
│   └── src/
│       ├── main/
│       │   ├── java/com/bk/mmovies/
│       │   │   ├── app/
│       │   │   ├── connectivity/
│       │   │   ├── di/
│       │   │   ├── ui/
│       │   │   └── util/
│       │   └── res/
│       ├── test/
│       └── androidTest/
├── core/                         # Shared data/domain layer (library module)
│   └── src/main/
│       ├── java/com/bk/mmovies/
│       │   ├── data/              # Repository impls, mappers, Room, Retrofit/OkHttp
│       │   ├── domain/             # Models, repository interfaces, result types
│       │   ├── di/                 # Hilt modules (DB, network, repositories)
│       │   ├── locale/
│       │   └── util/
│       └── res/
├── tv/                            # Android TV app (leanback launcher)
│   └── src/main/
│       ├── java/com/bk/mmovies/tv/
│       │   ├── di/
│       │   └── ui/                 # Navigation, catalog, details, auth, news, splash
│       └── res/
├── docs/
│   └── screenshots/
├── gradle/libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

`:app` and `:tv` are separate installable apps that depend on `:core` for shared data and domain functionality. News uses a shared repository interface with separate providers: NewsAPI on mobile and The Guardian on Android TV. The Guardian networking implementation lives in `:tv`.

Each feature keeps its UI, ViewModel, screen components and previews together while the domain and data layers remain independent of presentation code.

---

## Firebase

The repository contains `app/google-services.json` for Firebase Crashlytics.

If you fork the project and want your own crash reports, replace it with the configuration file from your own Firebase project.

---

## Additional Screens

All screenshots are stored under [`docs/screenshots/`](docs/screenshots/).

<details>
<summary><strong>Guest mode and legal screens</strong></summary>

<br>

<p align="center">
  <img src="docs/screenshots/15-guest-catalog.png" width="30%" alt="Movies catalog in guest mode" />
  <img src="docs/screenshots/16-privacy-policy.png" width="30%" alt="Privacy Policy screen" />
  <img src="docs/screenshots/17-terms-and-conditions.png" width="30%" alt="Terms and Conditions screen" />
</p>

</details>

---

## License & Attribution

### TMDB

> This product uses the TMDB API but is not endorsed or certified by TMDB.

Movie, TV, person, artwork and trailer metadata is provided by [The Movie Database](https://www.themoviedb.org/).

TMDB content remains subject to:

- [TMDB Terms of Use](https://www.themoviedb.org/terms-of-use)
- [TMDB API Terms](https://www.themoviedb.org/api-terms-of-use)

### This project

Copyright © 2026 Boris Kunda. **All rights reserved.**

This repository is published for portfolio and reference purposes.

No license is granted to use, copy, modify or redistribute the application's source code beyond rights provided by GitHub's Terms of Service.

Third-party libraries remain subject to their respective licenses.



