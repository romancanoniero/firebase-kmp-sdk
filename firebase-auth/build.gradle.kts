plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions { jvmTarget = "11" }
        }
        publishLibraryVariants("release")
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    js(IR) {
        browser()
        nodejs()
        binaries.library()
    }
    
    cocoapods {
        summary = "Firebase Auth KMP"
        homepage = "https://github.com/iyr/firebase-kmp-sdk"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        
        pod("FirebaseAuth") { version = "~> 10.29" }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":firebase-core"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(platform(libs.firebase.bom))
                implementation(libs.firebase.auth.ktx)
            }
        }
        
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        
        val jsMain by getting {
            dependencies {
                implementation(npm("firebase", "10.12.0"))
            }
        }
    }
}

android {
    namespace = "com.iyr.firebase.auth"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

