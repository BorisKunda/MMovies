import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// TEMPORARY: :tv has no API-key entry flow yet (unlike :app's
// ApiKeySetupView), so this lets TvCatalogViewModel authenticate using a key
// from a gitignored properties file (see tmdb.properties, parallel to
// newsapi.properties) instead of a literal committed to source - falls back
// to an empty string so a fresh clone without that file still compiles, just
// without a working catalog until one is added.
val tmdbApiKey: String = run {
    val properties = Properties()
    val propertiesFile = rootProject.file("tmdb.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { properties.load(it) }
    }
    properties.getProperty("TMDB_API_KEY", "")
}

android {
    namespace = "com.bk.mmovies.tv"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.bk.mmovies.tv"
        // 24, not 23 like :app - androidx.webkit (used for cross-origin
        // CSS injection into the catalog trailer preview's embedded
        // YouTube iframe) requires it, and Android TV/Google TV devices on
        // API 23 are effectively nonexistent.
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                         )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(project(":core"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.webkit)
    // For TvNewsModule's debug-only Guardian request logging - :core's own
    // okhttp-logging dependency is `implementation`, not `api`, so it isn't
    // visible here without declaring it directly.
    implementation(libs.okhttp.logging)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
