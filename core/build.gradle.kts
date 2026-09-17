import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// NewsAPI key: kept out of source in a gitignored properties file (see
// newsapi.properties, parallel to local.properties) rather than committed as
// a literal - falls back to an empty string so a fresh clone without that
// file still compiles, just without a working News feature until one is added.
val newsApiKey: String = run {
    val properties = Properties()
    val propertiesFile = rootProject.file("newsapi.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { properties.load(it) }
    }
    properties.getProperty("NEWS_API_KEY", "")
}

// Gemini API key (Google AI Studio): same gitignored-properties pattern as
// newsApiKey above - see gemini.properties, parallel to newsapi.properties.
val geminiApiKey: String = run {
    val properties = Properties()
    val propertiesFile = rootProject.file("gemini.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { properties.load(it) }
    }
    properties.getProperty("GEMINI_API_KEY", "")
}

// Guardian Content API key (open-platform.theguardian.com): same
// gitignored-properties pattern as newsApiKey/geminiApiKey above - see
// guardian.properties. Backs :tv's News feature (see TvNewsModule); :app
// still uses NewsAPI (see newsApiKey above).
val guardianApiKey: String = run {
    val properties = Properties()
    val propertiesFile = rootProject.file("guardian.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { properties.load(it) }
    }
    properties.getProperty("GUARDIAN_API_KEY", "")
}

android {
    namespace = "com.bk.mmovies.core"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "NEWS_API_KEY", "\"$newsApiKey\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "GUARDIAN_API_KEY", "\"$guardianApiKey\"")
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)

    api(platform(libs.firebase.bom))
    api(libs.firebase.crashlytics)

    hilt()
    room()
    retrofit()
    okHttp()
    compose()
    coil()

    implementation(libs.lottie)

    testImplementation(libs.junit)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

fun DependencyHandler.compose() {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)
}

fun DependencyHandler.hilt() {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

fun DependencyHandler.room() {
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.androidx.room.testing)
}

fun DependencyHandler.retrofit() {
    api(libs.retrofit.core)
    api(libs.retrofit.gson)
}

fun DependencyHandler.okHttp() {
    api(libs.okhttp)
    implementation(libs.okhttp.logging)

    androidTestImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.okhttp.tls)
}

fun DependencyHandler.coil() {
    api(libs.coil)
    implementation(libs.coil.network.okhttp)
}
