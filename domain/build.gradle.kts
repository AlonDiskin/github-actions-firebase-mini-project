plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Rx
    implementation(libs.rxKotlin)
    implementation(libs.rxJava)

    // Javax annotation
    implementation("javax.inject:javax.inject:1")

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.googleTruth)
}