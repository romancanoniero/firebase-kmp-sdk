plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.cocoapods)
}

kotlin {
    androidTarget {
        compilations.all { kotlinOptions { jvmTarget = "11" } }
    }
    iosX64(); iosArm64(); iosSimulatorArm64()
    js(IR) { browser(); nodejs(); binaries.library() }
    
    cocoapods {
        summary = "Firebase Database KMP"
        homepage = "https://github.com/iyr/firebase-kmp-sdk"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        pod("FirebaseDatabase") { version = "~> 10.29" }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(project(":firebase-core"))
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(platform(libs.firebase.bom))
            implementation(libs.firebase.database.ktx)
        }
        jsMain.dependencies { implementation(npm("firebase", "10.12.0")) }
    }
}

android {
    namespace = "com.iyr.firebase.database"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

