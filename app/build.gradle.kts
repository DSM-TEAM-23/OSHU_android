import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

configurations.configureEach {
    resolutionStrategy.force(
        "androidx.core:core:1.16.0",
        "androidx.core:core-ktx:1.16.0",
        "androidx.activity:activity:1.10.1",
        "androidx.activity:activity-ktx:1.10.1",
        "androidx.activity:activity-compose:1.10.1",
        "androidx.lifecycle:lifecycle-runtime:2.9.4",
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4",
        "androidx.lifecycle:lifecycle-runtime-compose:2.9.4",
        "androidx.lifecycle:lifecycle-viewmodel:2.9.4",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4",
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4",
    )
}

val localProperties = Properties()
val localPropertiesFile =
    rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val secretsProperties = Properties()
val secretsPropertiesFile =
    rootProject.file("secrets.properties")

if (secretsPropertiesFile.exists()) {
    secretsPropertiesFile.inputStream().use {
        secretsProperties.load(it)
    }
}

val baseUrl =
    localProperties.getProperty("BASE_URL") ?: ""

val mapsApiKey =
    secretsProperties.getProperty(
        "MAPS_API_KEY"
    ) ?: ""

android {
    namespace = "com.example.oshu_android"
    compileSdk = 35

    defaultConfig {
        applicationId =
            "com.example.oshu_android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

        resValue(
            "string",
            "oshu_api_base_url",
            baseUrl
        )

        manifestPlaceholders[
            "MAPS_API_KEY"
        ] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility =
            JavaVersion.VERSION_11
        targetCompatibility =
            JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(
        "androidx.core:core-ktx:1.16.0"
    )
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4"
    )
    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.9.4"
    )
    implementation(
        "androidx.activity:activity-compose:1.10.1"
    )

    implementation(
        platform(libs.androidx.compose.bom)
    )
    implementation(libs.androidx.ui)
    implementation(
        libs.androidx.ui.graphics
    )
    implementation(
        libs.androidx.ui.tooling.preview
    )
    implementation(
        libs.androidx.material3
    )
    implementation(
        "androidx.compose.foundation:foundation"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4"
    )

    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )
    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )

    implementation(
        "androidx.navigation:navigation-compose:2.8.9"
    )

    implementation(
        "androidx.datastore:datastore-preferences:1.1.7"
    )

    implementation(
        "com.google.maps.android:maps-compose:8.3.0"
    )
    implementation(
        "com.google.android.gms:play-services-location:21.4.0"
    )

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        libs.androidx.junit
    )
    androidTestImplementation(
        libs.androidx.espresso.core
    )
    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )
    androidTestImplementation(
        libs.androidx.ui.test.junit4
    )

    debugImplementation(
        libs.androidx.ui.tooling
    )
    debugImplementation(
        libs.androidx.ui.test.manifest
    )
}