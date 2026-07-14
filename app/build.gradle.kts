import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties()
val localPropertiesFile =
    rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val baseUrl =
    localProperties.getProperty("BASE_URL") ?: ""

val kakaoNativeAppKey =
    localProperties.getProperty(
        "KAKAO_NATIVE_APP_KEY"
    ) ?: ""

val googleAuthorizationUrl =
    localProperties.getProperty(
        "GOOGLE_AUTHORIZATION_URL"
    ) ?: "https://kangyu.shop/oauth2/authorization/google"

if (kakaoNativeAppKey.isBlank()) {
    throw GradleException(
        "local.properties에 KAKAO_NATIVE_APP_KEY를 설정해주세요."
    )
}

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

        resValue(
            "string",
            "kakao_native_app_key",
            kakaoNativeAppKey
        )

        resValue(
            "string",
            "google_authorization_url",
            googleAuthorizationUrl,
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
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.compose
    )

    implementation(
        libs.androidx.lifecycle.viewmodel.compose
    )

    implementation(
        libs.androidx.lifecycle.viewmodel.ktx
    )

    implementation(
        libs.androidx.activity.compose
    )

    implementation(
        platform(libs.androidx.compose.bom)
    )

    implementation(
        libs.androidx.ui
    )

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
        libs.androidx.foundation
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
        "androidx.security:security-crypto:1.1.0"
    )

    implementation(
        "com.kakao.maps.open:android:2.14.0"
    )

    implementation(
        "io.coil-kt:coil-compose:2.7.0",
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
