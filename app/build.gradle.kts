import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "mk.ukim.finki.expressu"
    compileSdk = 34
    defaultConfig {
        applicationId = "mk.ukim.finki.expressu"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        packagingOptions {
            exclude("META-INF/DEPENDENCIES")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val file = project.rootProject.file("local.properties")

        val properties = Properties()
        properties.load(file.inputStream())
        val apiKey = properties.getProperty("AZURE_TRANSLATOR_KEY")
        buildConfigField(
            "String",
            "AZURE_TRANSLATOR_KEY",
            apiKey
        )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.hbb20:ccp:2.7.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-messaging:24.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.android.volley:volley:1.2.1")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    //    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}