plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.frolic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.frolic"
        minSdk = 24
        targetSdk = 34
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<Test>{
        useJUnitPlatform()
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore")
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Firebase Cloud Messaging (FCM) for push notifications
    implementation("com.google.firebase:firebase-messaging:23.1.2")
    // Firebase Storage for storing event posters
    implementation("com.google.firebase:firebase-storage")
    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.espresso.intents)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.activity:activity:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.junit)

    // Testing libraries
    //testImplementation("junit:junit:4.13.2")
    // Testing libraries
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") // Match Espresso core version
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}