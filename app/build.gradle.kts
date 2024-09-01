plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.github_actions_firebase_mini_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.github_actions_firebase_mini_project"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.github_actions_firebase_mini_project.runner.CustomAndroidTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        dataBinding = true
    }
    testOptions {
        unitTests {
            this.isIncludeAndroidResources = true
        }
    }
}

dependencies {

    // Project modules
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":ui"))

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Instrumentation testing

    // Espresso & android testing
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.3.0")
    androidTestImplementation("androidx.test:runner:1.1.0")

    // Green coffee
    androidTestImplementation(libs.greenCoffee)

    // Google truth
    androidTestImplementation(libs.googleTruth)

    // Rx
//    androidTestImplementation("io.reactivex.rxjava2:rxjava:2.2.19")
//    androidTestImplementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    // Retrofit Mock web server
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:3.9.1")
    androidTestImplementation("com.squareup.retrofit2:converter-gson:2.3.0")
    androidTestImplementation("com.squareup.okhttp3:logging-interceptor:3.9.1")
    androidTestImplementation("com.squareup.retrofit2:retrofit:2.6.2")
    androidTestImplementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    androidTestImplementation("com.squareup.rx.idler:rx2-idler:0.11.0")

    // Hilt
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")

    // Local testing

    // Junit
    testImplementation(libs.junit)

    // Espresso & android testing
    testImplementation(libs.androidx.espresso.core)
    testImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    testImplementation(libs.androidx.junit)
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    // Data binding
    kaptTest("androidx.databinding:databinding-compiler:8.3.2")

    // Robolectric
    testImplementation("org.robolectric:robolectric:4.12")

    // Mockk
    testImplementation(libs.mockk)

    // Google truth
    testImplementation(libs.googleTruth)

    // Hilt
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptTest("com.google.dagger:hilt-android-compiler:2.51.1")

    // Green coffee
    testImplementation(libs.greenCoffee)

    // Rx
    testImplementation("io.reactivex.rxjava2:rxjava:2.2.19")
    testImplementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    // Retrofit Mock web server
    testImplementation("com.squareup.okhttp3:mockwebserver:3.9.1")
    testImplementation("com.squareup.retrofit2:converter-gson:2.3.0")
    testImplementation("com.squareup.okhttp3:logging-interceptor:3.9.1")
    testImplementation("com.squareup.retrofit2:retrofit:2.6.2")
    testImplementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
}

kapt {
    correctErrorTypes = true
}