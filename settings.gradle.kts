pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "firebase-kmp-sdk"

// ✅ Solo incluir los módulos que realmente usa ian-tv-kmp
// BOM - Bill of Materials (gestión de versiones)
include(":firebase-bom")

// Core module - FirebaseApp, FirebaseOptions
include(":firebase-core")

// Authentication - FirebaseAuth, FirebaseUser, AuthCredential
include(":firebase-auth")

// Realtime Database - DatabaseReference, DataSnapshot, Query
include(":firebase-database")

// Cloud Storage - StorageReference, UploadTask
include(":firebase-storage")

// Cloud Functions - HttpsCallable
include(":firebase-functions")

// ⚠️ MÓDULOS TEMPORALMENTE DESHABILITADOS (tienen errores de compilación)
// Cloud Firestore - FirebaseFirestore, DocumentReference, CollectionReference
// include(":firebase-firestore") // Probando con Firebase 11.x

// Cloud Functions - HttpsCallable
// include(":firebase-functions")

// Cloud Messaging (FCM) - Push notifications
// include(":firebase-messaging")

// Analytics - Event tracking, user properties
// include(":firebase-analytics")

// Crashlytics - Crash reporting
// include(":firebase-crashlytics")

// Remote Config - Feature flags, A/B testing
// include(":firebase-remote-config")

// Performance Monitoring
// include(":firebase-performance")

// App Check - App attestation
// include(":firebase-appcheck")

// In-App Messaging
// include(":firebase-inappmessaging")
