plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
    
    androidTarget {
        compilations.all { kotlinOptions { jvmTarget = "11" } }
    }
    
    js(IR) {
        browser()
        nodejs()
        binaries.library()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":firebase-core"))
            implementation(libs.kotlinx.coroutines.core)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.firebase.storage.ktx)
        }
        
        jsMain.dependencies {
            implementation(npm("firebase", "10.12.0"))
        }
    }
}

android {
    namespace = "com.iyr.firebase.storage"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
