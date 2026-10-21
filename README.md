# MMovies

<p align="left">
  <img src="docs/icons/mmovies-logo-aligned.png" alt="MMovies logo" height="200">
</p>

#### MMovies is a movie, TV series, and entertainment news discovery app for Android phones and Android TV, built with Kotlin and Jetpack Compose

[![Android](https://img.shields.io/badge/Android-555?logo=android&logoColor=3DDC84)](https://developer.android.com/) [![minSdk](https://img.shields.io/badge/minSdk-23-blue)](https://developer.android.com/about/versions/marshmallow/android-6.0) [![targetSdk](https://img.shields.io/badge/targetSdk-37-blue)](https://developer.android.com/about/versions/17/features)
<br>
[![Android TV](https://img.shields.io/badge/AndroidTV-555?logo=android&logoColor=3DDC84)](https://developer.android.com/tv) [![minSdk](https://img.shields.io/badge/minSdk-24-blue)](https://developer.android.com/about/versions/nougat/android-7.0) [![targetSdk](https://img.shields.io/badge/targetSdk-37-blue)](https://developer.android.com/about/versions/17/features)
<br>
[![Kotlin](https://img.shields.io/badge/Kotlin-555?logo=kotlin&logoColor=7F52FF)](https://kotlinlang.org/) [![Coroutines & Flow](https://img.shields.io/badge/Coroutines_%26_Flow-555?logo=kotlin&logoColor=7F52FF)](https://kotlinlang.org/docs/coroutines-overview.html)
<br>
[![Jetpack Compose](docs/badges/jetpack-compose.svg)](https://developer.android.com/compose) [![Room](docs/badges/room.svg)](https://developer.android.com/training/data-storage/room) [![Hilt](docs/badges/hilt.svg)](https://developer.android.com/training/dependency-injection/hilt-android) [![Retrofit](https://img.shields.io/badge/Retrofit-555)](https://lysine.dev/retrofit/) [![OkHttp](https://img.shields.io/badge/OkHttp-555)](https://lysine.dev/okhttp/)
<br>
[![TMDB](docs/badges/tmdb.svg)](https://developer.themoviedb.org/reference/getting-started) [![NewsAPI](docs/badges/newsapi.svg)](https://newsapi.org/) [![The Guardian](docs/badges/theguardianbadge.svg)](https://open-platform.theguardian.com/)

<table>
  <tr>
    <th>Android Mobile</th>
    <th>Android TV</th>
  </tr>
  <tr>
    <td align="center">
      <a href="docs/demos/MMoviesMobileDemoOptimized.gif">
        <img src="docs/demos/MobileDemoThumbnail.png" alt="Watch the Android Mobile GIF demo" height="250">
      </a>
    </td>
    <td align="center">
      <a href="docs/demos/MMoviesTvDemoOptimized.gif">
        <img src="docs/demos/MMoviesTvDemoThumbnail.png" alt="Watch the Android TV GIF demo" height="250">
      </a>
    </td>
  </tr>
</table>

<p>▶️ Click a preview thumbnail to watch the GIF demo</p>
<p>
  View full-size GIFs:
  <a href="docs/demos/MMoviesMobileDemo.gif">Android Mobile</a>
  · <a href="docs/demos/MMoviesTvDemo.gif">Android TV</a>
</p>

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Screenshots](#screenshots)
- [Limitations](#limitations)
- [License and Attribution](#license-and-attribution)

## Features

### Browse Movies and TV Series · Powered by TMDB

- Browse movies through paginated catalogs by category: Popular, Now Playing, Upcoming, and Top Rated
- Browse TV series through paginated catalogs by category: Popular, Airing Today, On TV, Upcoming, and Top Rated
- Explore movie and TV series details, including overviews, ratings, genres, release information, cast, trailers, seasons, episodes, and biographies

### Search · Powered by TMDB

- Search for movies and TV series
- Search for people (Mobile)
- Filter results by media type (Mobile)
- Revisit recent searches (Mobile)
- Search by voice using the microphone button, with microphone permission required (Android TV)

### Favorites · Powered by TMDB

- Add movies and TV series to your favorites and access them on mobile and TV using the same TMDB account

### Entertainment News · Powered by NewsAPI and The Guardian

- Browse entertainment news from NewsAPI (Mobile) and The Guardian (Android TV)

### Account

- Sign in with your TMDB account to manage favorites, or continue as a guest to browse movies and TV series
- Signing out clears the local session and account-specific cached data

### AI Recommendations · Powered by Google Gemini

- Discover personalized movie and TV series recommendations based on your favorites (Android TV)

### Localization

- The mobile interface supports **English, Russian, and Hebrew** and follows your device’s language settings
- TMDB content is displayed in the selected language when translations are available

### Upcoming

- Wear OS support
- Full Hebrew and Russian localization on Android TV
- AI recommendations on mobile
- Voice search on mobile

## Architecture

MMovies uses MVVM with a layered architecture. Compose screens observe UI state exposed by ViewModels through StateFlow. ViewModels call repository interfaces defined in the domain layer, while the data layer implements those interfaces using Retrofit, Room, and SharedPreferences. Hilt provides dependency injection.

The domain and data layers live in the shared `:core` module, allowing the mobile (`:app`) and Android TV (`:tv`) applications to share repository contracts and common data functionality while providing platform-specific screens and navigation.

The diagram shows component dependencies, with a separate connection for external API requests.

```mermaid
flowchart LR
  UI["UI<br/>Compose + ViewModels<br/>(:app / :tv)"]
  DOMAIN["Domain<br/>Models + Repository contracts<br/>(:core)"]
  DATA["Data<br/>Repositories + Mappers<br/>(:core)"]
  LOCAL["Room / SharedPreferences"]
  REMOTE["Retrofit / OkHttp"]
  APIS["TMDB · NewsAPI · Guardian · Gemini"]

  UI --> DOMAIN
  DATA --> DOMAIN
  DATA --> LOCAL
  DATA --> REMOTE
  REMOTE --> APIS
```

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin 2.4.0 |
| Java configuration | JDK 21 for the Gradle daemon; Java 17 source/target compatibility |
| Build | Gradle 9.4.1, Android Gradle Plugin 9.2.1, KSP |
| Android SDK | compileSdk 37, targetSdk 37, minSdk 23 (Mobile) / 24 (Android TV) |
| UI | Jetpack Compose + Material 3 (Mobile and Android TV) |
| Architecture | MVVM with presentation, domain, and data layers |
| State | StateFlow / SharedFlow |
| Async | Kotlin Coroutines + Flow |
| Dependency injection | Hilt with KSP |
| Networking | Retrofit 3 + OkHttp 5 |
| API JSON parsing | Gson |
| Navigation | Navigation Compose with kotlinx.serialization for type-safe routes |
| Local storage | Room, SharedPreferences |
| Image loading | Coil 3 |
| Animation | Lottie |
| Mobile video playback | android-youtube-player (WebView-based YouTube player library) |
| Crash reporting | Firebase Crashlytics (Mobile) |
| Leak detection | LeakCanary (debug builds) |
| Test libraries | JUnit, Espresso, Compose UI Test, MockWebServer |

Release builds enable R8 code shrinking and resource shrinking.

## Getting Started

### Build Requirements

- Android Studio compatible with Android Gradle Plugin 9.2.1
- JDK 21 for the Gradle daemon; Java 17 source/target compatibility
- Android SDK Platform 37
- For mobile builds, a Firebase configuration file at `app/google-services.json`, registered for the application ID `com.bk.mmovies`

### Build and Run

1. Clone or download this repository
2. Open the project in Android Studio
3. Configure JDK 21 as the Gradle JDK and install Android SDK Platform 37
4. For mobile builds, add your Firebase configuration file at `app/google-services.json`
5. Sync the project with Gradle
6. Select the `app` module for mobile or the `tv` module for Android TV
7. Choose a matching device or emulator and click **Run**

### TMDB Setup

1. Obtain your own [TMDB API key](https://developer.themoviedb.org/docs/getting-started)
2. Open the app and enter the **API Key (v3 auth)** value when prompted
3. Sign in with your TMDB account or continue as a guest

A TMDB API key is required for both signed-in and guest access.

### Optional Feature Keys

Additional keys are required for the features you want to use:

| Service | Feature | Platform |
|---|---|---|
| [NewsAPI](https://newsapi.org/docs/get-started) | Entertainment news | Mobile |
| [The Guardian](https://open-platform.theguardian.com/access/) | Entertainment news | Android TV |
| [Google AI Studio](https://ai.google.dev/gemini-api/docs/get-started) | Gemini-powered AI recommendations | Android TV |

Enter each key in the app when prompted by the corresponding feature.

The repository does not supply API keys. Local API-key configuration files are excluded from version control through `.gitignore`, so you must provide your own keys.

## Screenshots

<details>
<summary><strong>Phone — Authentication</strong></summary>

<table>
  <tr>
    <th>TMDB API Key Setup</th>
    <th>TMDB Log In</th>
    <th>NewsAPI Key Setup</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/tmdb-api-key-setup.png" width="300" alt="Mobile TMDB API key setup">
    </td>
    <td valign="top">
      <img src="docs/screenshots/tmdb-log-in.png" width="300" alt="Mobile TMDB login">
    </td>
    <td valign="top">
      <img src="docs/screenshots/newsapi-api-key-setup.jpg" width="300" alt="Mobile NewsAPI key setup">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>Phone — Discovery and Search</strong></summary>

<table>
  <tr>
    <th>Movie Catalog</th>
    <th>TV Series Catalog</th>
    <th>Search</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/movies-catalog.png" width="300" alt="Mobile movie catalog">
    </td>
    <td valign="top">
      <img src="docs/screenshots/tv-series-catalog.png" width="300" alt="Mobile TV series catalog">
    </td>
    <td valign="top">
      <img src="docs/screenshots/search.png" width="300" alt="Mobile search">
    </td>
  </tr>
  <tr>
    <th>Movie Categories</th>
    <th>TV Series Categories</th>
    <th>Favorites</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/movie-categories.png" width="300" alt="Mobile movie categories">
    </td>
    <td valign="top">
      <img src="docs/screenshots/tv-series-categories.png" width="300" alt="Mobile TV series categories">
    </td>
    <td valign="top">
      <img src="docs/screenshots/catalog_favorites_en.png" width="300" alt="Mobile favorites">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>Phone — Movie and Series Details</strong></summary>

<table>
  <tr>
    <th>Movie Details</th>
    <th>Person Biography</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/movie-details.jpg" width="300" alt="Mobile movie details">
    </td>
    <td valign="top">
      <img src="docs/screenshots/person-details.png" width="300" alt="Mobile person biography">
    </td>
  </tr>
</table>

<table>
  <tr>
    <th>TV Series Details</th>
    <th>Season Details</th>
    <th>Episode Details</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/tv-series-details.jpg" width="300" alt="Mobile TV series details">
    </td>
    <td valign="top">
      <img src="docs/screenshots/season-details.jpg" width="300" alt="Mobile season details">
    </td>
    <td valign="top">
      <img src="docs/screenshots/episode-details.png" width="300" alt="Mobile episode details">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>Phone — News</strong></summary>

<table>
  <tr>
    <th>Articles</th>
    <th>Full Article</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/news-articles.png" width="300" alt="Mobile news articles">
    </td>
    <td valign="top">
      <img src="docs/screenshots/full-article-webview.jpg" width="300" alt="Full news article on mobile">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>Phone — Localization</strong></summary>

<table>
  <tr>
    <th>Favorites — English</th>
    <th>Favorites — Hebrew</th>
    <th>Favorites — Russian</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/catalog_favorites_en.png" width="300" alt="Mobile favorites in English">
    </td>
    <td valign="top">
      <img src="docs/screenshots/catalog_favorites_he.png" width="300" alt="Mobile favorites in Hebrew">
    </td>
    <td valign="top">
      <img src="docs/screenshots/catalog_favorites_ru.png" width="300" alt="Mobile favorites in Russian">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>Phone — Legal</strong></summary>

<table>
  <tr>
    <th>Terms and Conditions</th>
    <th>Privacy Policy</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/terms-and-conditions.jpg" width="300" alt="Mobile terms and conditions">
    </td>
    <td valign="top">
      <img src="docs/screenshots/privacy-policy.jpg" width="300" alt="Mobile privacy policy">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>TV — Authentication</strong></summary>

<table>
  <tr>
    <th>TMDB API Key Setup</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-tmdb-api-key-setup.png" width="900" alt="Android TV TMDB API key setup">
    </td>
  </tr>
  <tr>
    <th>TMDB Log In</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-tmdb-log-in.png" width="900" alt="Android TV TMDB login">
    </td>
  </tr>
  <tr>
    <th>The Guardian API Key Setup</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-theguardian-api-key-setup.png" width="900" alt="Android TV Guardian API key setup">
    </td>
  </tr>
  <tr>
    <th>Gemini API Key Setup</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-gemini-api-key-setup.png" width="900" alt="Android TV Gemini API key setup">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>TV — Discovery and Search</strong></summary>

<table>
  <tr>
    <th>Movie Catalog</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-movies-catalog.png" width="900" alt="Android TV movie catalog">
    </td>
  </tr>
  <tr>
    <th>TV Series Catalog</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-tvseries-catalog.png" width="900" alt="Android TV series catalog">
    </td>
  </tr>
  <tr>
    <th>Search</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-search.png" width="900" alt="Android TV search">
    </td>
  </tr>
  <tr>
    <th>Favorites</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-favorites.png" width="900" alt="Android TV favorites">
    </td>
  </tr>
  <tr>
    <th>Hero Banner</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-hero-banner.png" width="900" alt="Android TV hero banner">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>TV — Movie and Series Details</strong></summary>

<table>
  <tr>
    <th>Movie Details</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-movie-details-1.png" width="900" alt="Android TV movie details, part 1">
    </td>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-movie-details-2.png" width="900" alt="Android TV movie details, part 2">
    </td>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-movie-details-3.png" width="900" alt="Android TV movie details, part 3">
    </td>
  </tr>
  <tr>
    <th>Person Biography</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-person-biography.png" width="900" alt="Android TV person biography">
    </td>
  </tr>
  <tr>
    <th>TV Series Details</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-series-details-1.png" width="900" alt="Android TV series details, part 1">
    </td>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-series-details-2.png" width="900" alt="Android TV series details, part 2">
    </td>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-series-details-3.png" width="900" alt="Android TV series details, part 3">
    </td>
  </tr>
  <tr>
    <th>Episode Details</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-episode-details.png" width="900" alt="Android TV episode details">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>TV — News</strong></summary>

<table>
  <tr>
    <th>Articles</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-news-articles.png" width="900" alt="Android TV news articles">
    </td>
  </tr>
  <tr>
    <th>Full Article</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-full-in-app-article.png" width="900" alt="Full news article on Android TV">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>TV — AI Recommendations</strong></summary>

<table>
  <tr>
    <th>AI Recommendations</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-ai-recommendations.png" width="900" alt="Android TV AI recommendations">
    </td>
  </tr>
</table>
</details>

<details>
<summary><strong>TV — Legal</strong></summary>

<table>
  <tr>
    <th>Legal</th>
  </tr>
  <tr>
    <td valign="top">
      <img src="docs/screenshots/android-tv-legal.png" width="900" alt="Android TV legal information">
    </td>
  </tr>
</table>
</details>

## Limitations

- **AI recommendations:** Availability depends on the quota and rate limits of your Gemini API project; recommendations may be temporarily unavailable when these limits are reached
- **Partial translations:** TMDB does not provide Russian and Hebrew translations for all content, so some details may appear in English or their original language

## License and Attribution

MMovies is a free, non-commercial personal project with no advertising.

### License

Copyright © 2026 Boris Kunda. All rights reserved.

No license is granted to use, modify, or redistribute the source code except as permitted by applicable law or with the copyright holder’s permission.

Third-party libraries and content remain subject to their respective licenses and terms.

### Attribution

- **TMDB:** Movie and TV metadata and images are provided by [The Movie Database](https://www.themoviedb.org)

  > This product uses the TMDB API but is not endorsed or certified by TMDB.

- **NewsAPI · Mobile:** News headlines are provided through [NewsAPI](https://newsapi.org), with content belonging to the respective publishers and rights holders
- **The Guardian · Android TV:** News content is provided through [The Guardian Open Platform](https://open-platform.theguardian.com)
- **Google Gemini · Android TV:** Optional AI recommendations are generated using Google's Gemini API with a user-provided API key

Third-party content, images, and trademarks belong to their respective rights holders. MMovies claims no ownership of them. Use of third-party services is subject to their applicable terms and privacy policies.

### Privacy

TMDB credentials are used to authenticate directly with TMDB. A session identifier and account ID are stored locally to keep users signed in. Guest access is also available.

MMovies does not use advertising or collect usage analytics. Requests needed for enabled features are sent to the relevant service providers, including TMDB, news providers, and Google Gemini.

The mobile app uses Firebase Crashlytics for crash reporting. Crash reports and diagnostic information may be sent to Google Firebase to help identify and resolve stability issues.

Optional voice search on Android TV uses the device's speech recognition service, which may process audio on its provider's servers. Microphone access requires permission and can be revoked in device settings.

See the in-app Terms and Conditions and Privacy Policy for further details.

### Disclaimer

MMovies is provided “as is” and “as available,” without warranties to the extent permitted by applicable law. Third-party information and AI-generated recommendations may be incomplete, inaccurate, or outdated. Feature availability depends on external services and their usage limits.