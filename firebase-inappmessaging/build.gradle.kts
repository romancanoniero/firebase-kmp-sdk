plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    // alias(libs.plugins.kotlin.cocoapods) // Deshabilitado - InAppMessaging no disponible como pod
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
                freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }
    
    androidTarget {
        compilations.all { kotlinOptions { jvmTarget = "11" } }
    }
    
    // iOS deshabilitado - FirebaseInAppMessaging no disponible como pod público
    // iosX64(); iosArm64(); iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            api(project(":firebase-core"))
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation("com.google.firebase:firebase-inappmessaging-ktx:21.0.1")
            implementation("com.google.firebase:firebase-inappmessaging-display-ktx:21.0.1")
        }
        // iOS deshabilitado - FirebaseInAppMessaging no disponible como pod público
    }
}

android {
    namespace = "com.iyr.firebase.inappmessaging"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

