plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.rmit.bookflowapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rmit.bookflowapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Firebase storage image load
    implementation ("com.squareup.picasso:picasso:2.8")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")
    // Firebase Cloud Firestore
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    // Firebase Cloud Messaging (FCM)
    implementation("com.google.firebase:firebase-messaging:23.4.0")

    // For Google Sign in
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Material Design
    implementation("com.google.android.material:material:1.4.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Google Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
}