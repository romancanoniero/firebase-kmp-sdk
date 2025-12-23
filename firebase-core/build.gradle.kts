/**
 * Firebase Core Module
 * 
 * Contiene las clases base de Firebase:
 * - FirebaseApp: Punto de entrada principal
 * - FirebaseOptions: Configuración de la app
 * 
 * Este módulo es dependencia de todos los demás módulos Firebase.
 */
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    // Android target
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
        publishLibraryVariants("release")
    }
    
    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    // JS target (Web)
    js(IR) {
        browser {
            webpackTask {
                mainOutputFileName = "firebase-core.js"
            }
        }
        nodejs()
        binaries.library()
    }
    
    // CocoaPods configuration for iOS Firebase SDK
    cocoapods {
        summary = "Firebase Core KMP - Multiplatform Firebase SDK"
        homepage = "https://github.com/iyr/firebase-kmp-sdk"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        
        // Firebase iOS SDK pods
        pod("FirebaseCore") {
            version = "~> 10.29"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(platform(libs.firebase.bom))
                implementation(libs.firebase.common.ktx)
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
    namespace = "com.iyr.firebase.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    
    coordinates(group.toString(), "firebase-core", version.toString())
    
    pom {
        name = "Firebase Core KMP"
        description = "Multiplatform Firebase Core SDK - FirebaseApp, FirebaseOptions"
        inceptionYear = "2024"
        url = "https://github.com/iyr/firebase-kmp-sdk"
        
        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }
        
        developers {
            developer {
                id = "iyr"
                name = "IYR Team"
                url = "https://github.com/iyr"
            }
        }
        
        scm {
            url = "https://github.com/iyr/firebase-kmp-sdk"
            connection = "scm:git:git://github.com/iyr/firebase-kmp-sdk.git"
            developerConnection = "scm:git:ssh://github.com/iyr/firebase-kmp-sdk.git"
        }
    }
}
