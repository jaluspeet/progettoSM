plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.common.rpsmodel"
    compileSdk = 35

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":common:game"))
    implementation(project(":common:storage"))

    implementation("org.pytorch:executorch-android:0.6.0-rc1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    //    implementation("org.pytorch:pytorch_android:1.13.1")
    //    implementation("org.pytorch:pytorch_android_torchvision:1.13.1")
    //    implementation ("org.pytorch:pytorch_android:1.6.0-SNAPSHOT")
    //    implementation ("org.pytorch:pytorch_android_torchvision:1.6.0-SNAPSHOT")
}