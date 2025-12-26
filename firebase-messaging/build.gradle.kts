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
    js(IR) { browser(); nodejs(); binaries.library() }
    
    cocoapods {
        summary = "Firebase Messaging KMP"
        homepage = "https://github.com/iyr/firebase-kmp-sdk"
        version = "1.2.0"
        ios.deploymentTarget = "15.0"
        pod("FirebaseMessaging") { 
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
            implementation(libs.firebase.messaging.ktx)
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
        jsMain.dependencies { implementation(npm("firebase", "10.12.0")) }
    }
}

android {
    namespace = "com.iyr.firebase.messaging"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
