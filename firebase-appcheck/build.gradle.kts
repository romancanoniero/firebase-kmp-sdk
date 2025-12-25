plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.cocoapods)
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
    
    iosX64(); iosArm64(); iosSimulatorArm64()
    
    cocoapods {
        summary = "Firebase App Check KMP"
        homepage = "https://github.com/iyr/firebase-kmp-sdk"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        
        pod("FirebaseAppCheck") {
            version = "~> 10.29"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(project(":firebase-core"))
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation("com.google.firebase:firebase-appcheck-ktx:18.0.0")
            implementation("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")
            implementation("com.google.firebase:firebase-appcheck-debug:18.0.0")
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain.get())
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.iyr.firebase.appcheck"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

