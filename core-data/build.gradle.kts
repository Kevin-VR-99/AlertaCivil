plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "mx.utselva.alertacivil.data"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    api(project(":core-model"))

    implementation("androidx.core:core-ktx:1.13.1")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    api("com.google.firebase:firebase-firestore-ktx:25.1.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}