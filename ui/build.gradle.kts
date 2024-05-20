plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(project(":domain"))

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.activity:activity-ktx:1.8.0")

    // Rx
    implementation(libs.rxKotlin)
    implementation(libs.rxJava)
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.11.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Joda Time
    implementation("joda-time:joda-time:2.10.4")

    // Local unit testing

    // Junit
    testImplementation(libs.junit)

    // Espresso & android testing
    testImplementation(libs.androidx.espresso.core)
    testImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    testImplementation(libs.androidx.junit)
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("androidx.test.espresso:espresso-intents:3.3.0")

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
}

kapt {
    correctErrorTypes = true
}