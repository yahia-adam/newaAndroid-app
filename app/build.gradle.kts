import java.io.FileInputStream
import java.util.Properties

// for local dev
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}
// for local dev
fun getApiKey() = localProperties.getProperty("TICKETMASTER_API_KEY")
    ?: System.getenv("TICKETMASTER_API_KEY")
    ?: throw GradleException("TICKETMASTER_API_KEY not found, add it in local properities")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    // Dependency injection with Hilt
    alias(libs.plugins.dagger.hilt.android)
    id("kotlin-kapt")

    // kotlin 2
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.bythewayapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bytheway"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "TICKETMASTER_API_KEY", "\"${getApiKey()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true

        // for local dev
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Dependency injection with Hilt
    kapt {
        correctErrorTypes = true
    }

    buildToolsVersion = "35.0.0"

    hilt {
        enableAggregatingTask = false
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.i18n)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // viewModel lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Dependency injection with Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // privy
    implementation(libs.privy.core)

    // Navigation with jet pack compose
    implementation(libs.androidx.navigation.compose)

    // Add mapbox
    implementation("com.mapbox.maps:android:11.10.2")
    implementation("androidx.webkit:webkit:1.13.0")
    implementation("com.mapbox.extension:maps-compose:11.10.2")

    // location
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    // geohash
    implementation ("ch.hsr:geohash:1.4.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

}