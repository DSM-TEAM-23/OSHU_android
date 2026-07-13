import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val baseUrl = localProperties.getProperty("BASE_URL") ?: ""

android {
    namespace = "com.example.oshu_android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.oshu_android"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
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

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0"
    )

    implementation(
        "com.squareup.retrofit2:retrofit:3.0.0"
    )
    implementation(
        "com.squareup.retrofit2:converter-gson:3.0.0"
    )

    implementation(
        "androidx.navigation:navigation-compose:2.9.8"
    )

    implementation(
        "androidx.datastore:datastore-preferences:1.2.1"
    )
    
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )
    androidTestImplementation(
        libs.androidx.ui.test.junit4
    )

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(
        libs.androidx.ui.test.manifest
    )
}