plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.data"
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
}

dependencies {

    // Project modules
    implementation(project(":domain"))

    // Android core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // RxJava
    implementation(libs.rxKotlin)
    implementation(libs.rxJava)

    // Retrofit
    api(libs.retrofit)
    api(libs.retrofitAdapter)
    api(libs.retrofitConverter)
    api("com.squareup.okhttp3:logging-interceptor:3.9.1")

    // Javax annotation
    implementation("javax.inject:javax.inject:1")

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.googleTruth)
    testImplementation(libs.junitParams)
    testImplementation(libs.mockWebServer)
    testImplementation(libs.okhttpLoggin)
    testImplementation(libs.gson)
}