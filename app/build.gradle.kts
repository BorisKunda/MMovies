import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bk.mmovies"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.bk.mmovies"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.bk.mmovies.di.HiltTestRunner"

        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${getRequiredLocalProperty("TMDB_API_KEY")}\""
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    compose()
    firebase()
    hilt()
    room()
    retrofit()
    okHttp()
    lottie()
    coil()

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

fun DependencyHandler.compose() {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
}

fun DependencyHandler.firebase() {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}

fun DependencyHandler.hilt() {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.hilt.navigation.compose)

    androidTestImplementation(libs.hilt.test)
    kspAndroidTest(libs.hilt.compiler)
}

fun DependencyHandler.room() {
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.androidx.room.testing)
}

fun DependencyHandler.retrofit() {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
}

fun DependencyHandler.okHttp() {
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    androidTestImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.okhttp.tls)
}

fun DependencyHandler.lottie() {
    implementation(libs.lottie)
}

fun DependencyHandler.coil() {
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
}

fun getRequiredLocalProperty(key: String): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }

    return properties.getProperty(key)
           ?: throw GradleException("Missing $key in local.properties")
}