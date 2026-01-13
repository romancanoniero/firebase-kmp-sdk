/**
 * Firebase KMP SDK
 * 
 * Librería multiplataforma que replica la API del Firebase Android SDK
 * para Kotlin Multiplatform (Android, iOS, JS).
 * 
 * Paquete base: io.github.romancanoniero
 * 
 * Módulos:
 * - firebase-core: FirebaseApp, FirebaseOptions
 * - firebase-auth: Autenticación completa
 * - firebase-database: Realtime Database
 * - firebase-firestore: Cloud Firestore
 * - firebase-storage: Cloud Storage
 * - firebase-functions: Cloud Functions client
 * - firebase-messaging: Push notifications (FCM)
 */

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.kotlin.cocoapods) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = "io.github.romancanoniero"
    version = "1.2.6"
}

// La configuración de Maven Central se hace en cada módulo
// Las credenciales se pasan via ORG_GRADLE_PROJECT_ en GitHub Actions
