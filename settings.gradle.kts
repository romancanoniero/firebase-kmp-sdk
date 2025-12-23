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

// Core module - FirebaseApp, FirebaseOptions
include(":firebase-core")

// Authentication - FirebaseAuth, FirebaseUser, AuthCredential
include(":firebase-auth")

// Realtime Database - DatabaseReference, DataSnapshot, Query
include(":firebase-database")

// Cloud Firestore - FirebaseFirestore, DocumentReference, CollectionReference
include(":firebase-firestore")

// Cloud Storage - StorageReference, UploadTask
include(":firebase-storage")

// Cloud Functions - HttpsCallable
include(":firebase-functions")

// Cloud Messaging (FCM) - Push notifications
include(":firebase-messaging")
