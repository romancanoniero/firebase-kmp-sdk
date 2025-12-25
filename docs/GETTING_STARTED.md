# 🚀 Firebase KMP SDK - Guía de Implementación Completa

Esta guía te llevará paso a paso desde cero hasta tener Firebase funcionando en tu proyecto **Compose Multiplatform**.

---

## 🎯 Principio Fundamental: Código Compartido

Esta librería está diseñada para que **TODO el código de Firebase sea compartido en `commonMain`**:

```
┌─────────────────────────────────────────────────────────────┐
│                        commonMain                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  ✅ ViewModels                                       │   │
│  │  ✅ Repositories                                     │   │
│  │  ✅ Services (Auth, Database, Firestore, etc.)       │   │
│  │  ✅ Modelos de datos                                 │   │
│  │  ✅ UI con Compose Multiplatform                     │   │
│  │  ✅ Llamadas a Firebase KMP SDK                      │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│  androidMain  │   │    iosMain    │   │    jsMain     │
│ ┌───────────┐ │   │ ┌───────────┐ │   │ ┌───────────┐ │
│ │ Solo:     │ │   │ │ Solo:     │ │   │ │ Solo:     │ │
│ │ - Init    │ │   │ │ - Init    │ │   │ │ - Init    │ │
│ │ - Context │ │   │ │           │ │   │ │ - Config  │ │
│ └───────────┘ │   │ └───────────┘ │   │ └───────────┘ │
└───────────────┘   └───────────────┘   └───────────────┘
```

**El 99% del código va en `commonMain`**. Solo la inicialización requiere código específico de plataforma.

---

## 📑 Índice

1. [Requisitos Previos](#-requisitos-previos)
2. [Crear Proyecto en Firebase Console](#-paso-1-crear-proyecto-en-firebase-console)
3. [Configurar Proyecto KMP](#-paso-2-configurar-proyecto-kmp)
4. [Agregar Dependencias](#-paso-3-agregar-dependencias)
5. [Configuración por Plataforma](#-paso-4-configuración-por-plataforma)
6. [Inicialización](#-paso-5-inicialización)
7. [Verificar Instalación](#-paso-6-verificar-instalación)
8. [Ejemplos con Compose Multiplatform](#-ejemplos-con-compose-multiplatform)
9. [Testing con Emuladores](#-testing-con-emuladores)
10. [Solución de Problemas](#-solución-de-problemas)

---

## 📋 Requisitos Previos

### Herramientas Requeridas

| Herramienta | Versión Mínima | Verificar |
|-------------|----------------|-----------|
| **Android Studio** | Arctic Fox 2020.3.1+ | `Android Studio > About` |
| **Xcode** | 14.0+ (solo Mac) | `xcode-select --version` |
| **JDK** | 11+ | `java -version` |
| **Kotlin** | 1.9.0+ | En `build.gradle.kts` |
| **Gradle** | 8.0+ | `./gradlew --version` |
| **CocoaPods** | 1.11+ (para iOS) | `pod --version` |

### Instalar CocoaPods (si no lo tienes)

```bash
# macOS
sudo gem install cocoapods

# O con Homebrew
brew install cocoapods
```

---

## 🔥 Paso 1: Crear Proyecto en Firebase Console

### 1.1 Crear Proyecto

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Click en **"Agregar proyecto"**
3. Ingresa nombre del proyecto (ej: `mi-app-kmp`)
4. Habilita/deshabilita Google Analytics según necesites
5. Click **"Crear proyecto"**

### 1.2 Registrar App Android

1. En la página del proyecto, click en el ícono de **Android**
2. Ingresa el **Package Name** (ej: `com.miempresa.miapp`)
   - ⚠️ Debe coincidir EXACTAMENTE con `applicationId` en tu `build.gradle.kts`
3. (Opcional) Nickname y SHA-1 para Google Sign-In
4. Click **"Registrar app"**
5. **Descarga `google-services.json`**
6. Click **"Siguiente"** hasta terminar

### 1.3 Registrar App iOS

1. Click en **"Agregar app"** > ícono de **iOS**
2. Ingresa el **Bundle ID** (ej: `com.miempresa.miapp`)
   - ⚠️ Debe coincidir con tu proyecto Xcode
3. (Opcional) Nickname y App Store ID
4. Click **"Registrar app"**
5. **Descarga `GoogleService-Info.plist`**
6. Click **"Siguiente"** hasta terminar

### 1.4 Registrar App Web (para JS)

1. Click en **"Agregar app"** > ícono de **Web** `</>`
2. Ingresa nickname (ej: `mi-app-web`)
3. Click **"Registrar app"**
4. **Copia la configuración** que aparece:

```javascript
const firebaseConfig = {
  apiKey: "AIzaSy...",
  authDomain: "mi-app-kmp.firebaseapp.com",
  projectId: "mi-app-kmp",
  storageBucket: "mi-app-kmp.appspot.com",
  messagingSenderId: "123456789",
  appId: "1:123456789:web:abc123"
};
```

---

## 🛠️ Paso 2: Configurar Proyecto KMP

### 2.1 Estructura de Proyecto Recomendada

```
mi-proyecto-kmp/
├── build.gradle.kts              # Root build
├── settings.gradle.kts           # Settings
├── gradle.properties             # Propiedades
├── shared/                       # Módulo compartido
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/           # Código compartido
│       ├── androidMain/          # Código Android
│       ├── iosMain/              # Código iOS
│       └── jsMain/               # Código JS
├── androidApp/                   # App Android
│   ├── build.gradle.kts
│   ├── google-services.json      # ⬅️ Aquí
│   └── src/
├── iosApp/                       # App iOS
│   ├── iosApp.xcodeproj
│   ├── GoogleService-Info.plist  # ⬅️ Aquí
│   └── Podfile
└── webApp/                       # App Web (opcional)
    └── src/
```

### 2.2 Configurar settings.gradle.kts

```kotlin
// settings.gradle.kts
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
        // Para desarrollo local (opcional):
        // mavenLocal()
    }
}

rootProject.name = "MiProyectoKMP"
include(":shared")
include(":androidApp")
```

---

## 📦 Paso 3: Agregar Dependencias

### 3.1 Módulo Compartido (shared/build.gradle.kts)

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("native.cocoapods") // Para iOS
}

kotlin {
    // Targets
    androidTarget {
        compilations.all {
            kotlinOptions { jvmTarget = "11" }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    js(IR) {
        browser()
        nodejs()
        binaries.library()
    }
    
    // CocoaPods para iOS
    cocoapods {
        summary = "Shared KMP Module"
        homepage = "https://miempresa.com"
        version = "1.0.0"
        ios.deploymentTarget = "15.0"
        
        // Firebase Pods (agrega solo los que uses)
        pod("FirebaseCore") { version = "~> 10.29" }
        pod("FirebaseAuth") { version = "~> 10.29" }
        pod("FirebaseDatabase") { version = "~> 10.29" }
        pod("FirebaseFirestore") { version = "~> 10.29" }
        pod("FirebaseStorage") { version = "~> 10.29" }
        // ... más pods según necesites
    }
    
    sourceSets {
        // ========== DEPENDENCIAS COMPARTIDAS ==========
        val commonMain by getting {
            dependencies {
                // Core (SIEMPRE requerido)
                implementation("io.github.romancanoniero:firebase-core:1.0.0")
                
                // Módulos opcionales (agrega los que necesites)
                implementation("io.github.romancanoniero:firebase-auth:1.0.0")
                implementation("io.github.romancanoniero:firebase-database:1.0.0")
                implementation("io.github.romancanoniero:firebase-firestore:1.0.0")
                implementation("io.github.romancanoniero:firebase-storage:1.0.0")
                implementation("io.github.romancanoniero:firebase-functions:1.0.0")
                implementation("io.github.romancanoniero:firebase-messaging:1.0.0")
                implementation("io.github.romancanoniero:firebase-analytics:1.0.0")
                implementation("io.github.romancanoniero:firebase-crashlytics:1.0.0")
                implementation("io.github.romancanoniero:firebase-remote-config:1.0.0")
                implementation("io.github.romancanoniero:firebase-performance:1.0.0")
                
                // Coroutines (requerido)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        
        // ========== ANDROID ==========
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
            }
        }
        
        // ========== iOS ==========
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        
        // ========== JS ==========
        val jsMain by getting {
            dependencies {
                // Las dependencias de Firebase JS se incluyen automáticamente
            }
        }
    }
}

android {
    namespace = "com.miempresa.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

### 3.2 App Android (androidApp/build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services") // ⬅️ Plugin de Google Services
}

android {
    namespace = "com.miempresa.miapp"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.miempresa.miapp" // ⚠️ Debe coincidir con Firebase
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":shared"))
    
    // Firebase BOM (gestiona versiones automáticamente)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

### 3.3 Root build.gradle.kts

```kotlin
plugins {
    kotlin("multiplatform") version "2.0.0" apply false
    kotlin("android") version "2.0.0" apply false
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // ⬅️
}
```

---

## ⚙️ Paso 4: Configuración por Plataforma

### 4.1 Android

#### Colocar google-services.json

```bash
# Copiar el archivo descargado a:
cp ~/Downloads/google-services.json androidApp/google-services.json
```

#### Verificar estructura

```
androidApp/
├── build.gradle.kts
├── google-services.json  ← Aquí
└── src/
    └── main/
        ├── AndroidManifest.xml
        └── kotlin/
```

#### AndroidManifest.xml (permisos)

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Permisos básicos -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <!-- Para Cloud Messaging -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    
    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.MyApp">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>
</manifest>
```

### 4.2 iOS

#### Colocar GoogleService-Info.plist

1. Abre **Xcode**
2. Abre tu proyecto iOS (`iosApp/iosApp.xcodeproj`)
3. Arrastra `GoogleService-Info.plist` al proyecto
4. ✅ Marca "Copy items if needed"
5. ✅ Marca "Add to targets: iosApp"

#### Configurar Podfile (iosApp/Podfile)

```ruby
platform :ios, '15.0'
use_frameworks!

target 'iosApp' do
  # Pods de Firebase (los mismos que en build.gradle.kts)
  pod 'FirebaseCore', '~> 10.29'
  pod 'FirebaseAuth', '~> 10.29'
  pod 'FirebaseDatabase', '~> 10.29'
  pod 'FirebaseFirestore', '~> 10.29'
  pod 'FirebaseStorage', '~> 10.29'
  pod 'FirebaseMessaging', '~> 10.29'
  pod 'FirebaseAnalytics', '~> 10.29'
  pod 'FirebaseCrashlytics', '~> 10.29'
  pod 'FirebaseRemoteConfig', '~> 10.29'
  pod 'FirebasePerformance', '~> 10.29'
  
  # Shared KMP framework
  pod 'shared', :path => '../shared'
end

post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '15.0'
    end
  end
end
```

#### Instalar Pods

```bash
cd iosApp
pod install --repo-update
```

#### Configurar AppDelegate (Swift)

```swift
// iosApp/iosApp/AppDelegate.swift
import UIKit
import FirebaseCore

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        
        // ⬅️ Inicializar Firebase
        FirebaseApp.configure()
        
        return true
    }
}
```

### 4.3 JavaScript/Web

#### Configuración en código

```kotlin
// shared/src/jsMain/kotlin/FirebaseInit.kt
package com.miempresa.shared

import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.core.FirebaseOptions

fun initializeFirebaseJS() {
    val options = FirebaseOptions.Builder()
        .setApiKey("AIzaSy...") // De Firebase Console
        .setApplicationId("1:123456789:web:abc123")
        .setProjectId("mi-app-kmp")
        .setDatabaseUrl("https://mi-app-kmp.firebaseio.com")
        .setStorageBucket("mi-app-kmp.appspot.com")
        .setMessagingSenderId("123456789")
        .build()
    
    FirebaseApp.initializeApp(options)
}
```

---

## 🎬 Paso 5: Inicialización

### 5.1 Código Compartido (commonMain)

Crea un archivo de inicialización:

```kotlin
// shared/src/commonMain/kotlin/FirebaseManager.kt
package com.miempresa.shared

import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.auth.FirebaseAuth
import com.iyr.firebase.database.FirebaseDatabase
import com.iyr.firebase.firestore.FirebaseFirestore
import com.iyr.firebase.storage.FirebaseStorage

/**
 * Manager centralizado para acceder a todos los servicios de Firebase.
 * Usar como Singleton.
 */
object FirebaseManager {
    
    // Lazy initialization
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    
    /**
     * Verificar si Firebase está inicializado
     */
    fun isInitialized(): Boolean {
        return try {
            FirebaseApp.getInstance()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Obtener el usuario actual (si está autenticado)
     */
    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
```

### 5.2 Android - Application Class

```kotlin
// androidApp/src/main/kotlin/MyApplication.kt
package com.miempresa.miapp

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Firebase se inicializa automáticamente con google-services.json
        // Pero podemos verificar:
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        
        println("✅ Firebase inicializado: ${FirebaseApp.getInstance().name}")
    }
}
```

### 5.3 iOS - Ya configurado en AppDelegate

La inicialización ya está en `AppDelegate.swift` con `FirebaseApp.configure()`.

### 5.4 JS - Inicialización explícita

```kotlin
// En tu punto de entrada JS
fun main() {
    initializeFirebaseJS()
    // Tu código de app...
}
```

---

## ✅ Paso 6: Verificar Instalación

### 6.1 Test de Verificación

Crea un test simple:

```kotlin
// shared/src/commonMain/kotlin/FirebaseTest.kt
package com.miempresa.shared

import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.auth.FirebaseAuth

object FirebaseTest {
    
    suspend fun runTests(): List<String> {
        val results = mutableListOf<String>()
        
        // Test 1: Firebase App
        try {
            val app = FirebaseApp.getInstance()
            results.add("✅ FirebaseApp: ${app.getName()}")
        } catch (e: Exception) {
            results.add("❌ FirebaseApp: ${e.message}")
        }
        
        // Test 2: Auth
        try {
            val auth = FirebaseAuth.getInstance()
            results.add("✅ FirebaseAuth: Disponible")
            results.add("   - Usuario actual: ${auth.currentUser?.uid ?: "ninguno"}")
        } catch (e: Exception) {
            results.add("❌ FirebaseAuth: ${e.message}")
        }
        
        // Test 3: Auth anónimo
        try {
            val auth = FirebaseAuth.getInstance()
            val result = auth.signInAnonymously()
            results.add("✅ Login anónimo: ${result.user?.uid}")
            auth.signOut()
            results.add("✅ Logout exitoso")
        } catch (e: Exception) {
            results.add("❌ Login anónimo: ${e.message}")
        }
        
        return results
    }
}
```

### 6.2 Ejecutar Tests

```kotlin
// En tu Activity/ViewController
import kotlinx.coroutines.*

// Android
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            val results = FirebaseTest.runTests()
            results.forEach { println(it) }
        }
    }
}
```

---

## 💡 Ejemplos con Compose Multiplatform

### ⚠️ IMPORTANTE: Dónde va cada código

| Ubicación | Qué incluir |
|-----------|-------------|
| **`commonMain`** | ✅ TODO: UI, ViewModels, Repositories, Services, Modelos |
| **`androidMain`** | ⚡ Solo: Inicialización de Firebase (con Context) |
| **`iosMain`** | ⚡ Solo: Nada extra (init en AppDelegate.swift) |
| **`jsMain`** | ⚡ Solo: Configuración de Firebase (FirebaseOptions) |

---

### Ejemplo Completo: App de Notas con Compose Multiplatform

Este ejemplo muestra una app completa con **TODO el código en `commonMain`**.

#### 📁 Estructura de Archivos

```
shared/src/
├── commonMain/kotlin/com/miempresa/app/
│   ├── models/
│   │   └── Note.kt                    ✅ Compartido
│   ├── repository/
│   │   └── NotesRepository.kt         ✅ Compartido
│   ├── viewmodel/
│   │   └── NotesViewModel.kt          ✅ Compartido
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── NotesListScreen.kt     ✅ Compartido (Compose)
│   │   │   ├── NoteDetailScreen.kt    ✅ Compartido (Compose)
│   │   │   └── LoginScreen.kt         ✅ Compartido (Compose)
│   │   └── components/
│   │       └── NoteCard.kt            ✅ Compartido (Compose)
│   └── App.kt                         ✅ Compartido (Entry point Compose)
│
├── androidMain/kotlin/
│   └── FirebaseInit.android.kt        ⚡ Solo init con Context
│
├── iosMain/kotlin/
│   └── (vacío o expect/actual si necesitas)
│
└── jsMain/kotlin/
    └── FirebaseInit.js.kt             ⚡ Solo config con FirebaseOptions
```

---

### 📦 Modelo de Datos (commonMain)

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/models/Note.kt
package com.miempresa.app.models

data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val userId: String = ""
)
```

### 📦 Repositorio (commonMain) - 100% Compartido

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/repository/NotesRepository.kt
package com.miempresa.app.repository

import com.iyr.firebase.auth.FirebaseAuth
import com.iyr.firebase.firestore.FirebaseFirestore
import com.iyr.firebase.firestore.FieldValue
import com.iyr.firebase.storage.FirebaseStorage
import com.miempresa.shared.models.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepository {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    private val notesCollection = firestore.collection("notes")
    
    /**
     * Obtener ID del usuario actual
     */
    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid 
            ?: throw IllegalStateException("Usuario no autenticado")
    }
    
    // ==================== CREATE ====================
    
    /**
     * Crear una nueva nota
     */
    suspend fun createNote(title: String, content: String): Note {
        val userId = getCurrentUserId()
        val now = System.currentTimeMillis()
        
        val noteData = mapOf(
            "title" to title,
            "content" to content,
            "userId" to userId,
            "createdAt" to now,
            "updatedAt" to now,
            "imageUrl" to null
        )
        
        val docRef = notesCollection.add(noteData)
        
        return Note(
            id = docRef.id,
            title = title,
            content = content,
            userId = userId,
            createdAt = now,
            updatedAt = now
        )
    }
    
    /**
     * Crear nota con imagen
     */
    suspend fun createNoteWithImage(
        title: String, 
        content: String, 
        imagePath: String
    ): Note {
        val userId = getCurrentUserId()
        val now = System.currentTimeMillis()
        
        // 1. Subir imagen
        val imageRef = storage.getReference("notes/$userId/${now}.jpg")
        imageRef.putFile(imagePath)
        val imageUrl = imageRef.getDownloadUrl()
        
        // 2. Crear nota con URL de imagen
        val noteData = mapOf(
            "title" to title,
            "content" to content,
            "userId" to userId,
            "createdAt" to now,
            "updatedAt" to now,
            "imageUrl" to imageUrl
        )
        
        val docRef = notesCollection.add(noteData)
        
        return Note(
            id = docRef.id,
            title = title,
            content = content,
            imageUrl = imageUrl,
            userId = userId,
            createdAt = now,
            updatedAt = now
        )
    }
    
    // ==================== READ ====================
    
    /**
     * Obtener todas las notas del usuario actual
     */
    suspend fun getNotes(): List<Note> {
        val userId = getCurrentUserId()
        
        val snapshot = notesCollection
            .whereEqualTo("userId", userId)
            .orderBy("updatedAt", com.iyr.firebase.firestore.Query.Direction.DESCENDING)
            .get()
        
        return snapshot.documents.map { doc ->
            Note(
                id = doc.id,
                title = doc.getString("title") ?: "",
                content = doc.getString("content") ?: "",
                imageUrl = doc.getString("imageUrl"),
                createdAt = doc.getLong("createdAt") ?: 0,
                updatedAt = doc.getLong("updatedAt") ?: 0,
                userId = doc.getString("userId") ?: ""
            )
        }
    }
    
    /**
     * Observar notas en tiempo real
     */
    fun observeNotes(): Flow<List<Note>> {
        val userId = getCurrentUserId()
        
        return notesCollection
            .whereEqualTo("userId", userId)
            .orderBy("updatedAt", com.iyr.firebase.firestore.Query.Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    Note(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        imageUrl = doc.getString("imageUrl"),
                        createdAt = doc.getLong("createdAt") ?: 0,
                        updatedAt = doc.getLong("updatedAt") ?: 0,
                        userId = doc.getString("userId") ?: ""
                    )
                }
            }
    }
    
    /**
     * Obtener una nota por ID
     */
    suspend fun getNote(noteId: String): Note? {
        val doc = notesCollection.document(noteId).get()
        
        if (!doc.exists()) return null
        
        return Note(
            id = doc.id,
            title = doc.getString("title") ?: "",
            content = doc.getString("content") ?: "",
            imageUrl = doc.getString("imageUrl"),
            createdAt = doc.getLong("createdAt") ?: 0,
            updatedAt = doc.getLong("updatedAt") ?: 0,
            userId = doc.getString("userId") ?: ""
        )
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Actualizar una nota
     */
    suspend fun updateNote(noteId: String, title: String, content: String) {
        notesCollection.document(noteId).update(mapOf(
            "title" to title,
            "content" to content,
            "updatedAt" to System.currentTimeMillis()
        ))
    }
    
    /**
     * Actualizar imagen de una nota
     */
    suspend fun updateNoteImage(noteId: String, imagePath: String): String {
        val userId = getCurrentUserId()
        val now = System.currentTimeMillis()
        
        // Subir nueva imagen
        val imageRef = storage.getReference("notes/$userId/${noteId}_${now}.jpg")
        imageRef.putFile(imagePath)
        val imageUrl = imageRef.getDownloadUrl()
        
        // Actualizar nota
        notesCollection.document(noteId).update(mapOf(
            "imageUrl" to imageUrl,
            "updatedAt" to now
        ))
        
        return imageUrl
    }
    
    // ==================== DELETE ====================
    
    /**
     * Eliminar una nota
     */
    suspend fun deleteNote(noteId: String) {
        // Obtener nota para eliminar imagen si existe
        val note = getNote(noteId)
        
        // Eliminar imagen de Storage si existe
        note?.imageUrl?.let { url ->
            try {
                storage.getReferenceFromUrl(url).delete()
            } catch (e: Exception) {
                // Ignorar error si la imagen ya no existe
            }
        }
        
        // Eliminar documento
        notesCollection.document(noteId).delete()
    }
    
    // ==================== SEARCH ====================
    
    /**
     * Buscar notas por título
     */
    suspend fun searchNotes(query: String): List<Note> {
        val userId = getCurrentUserId()
        
        // Firestore no soporta búsqueda full-text nativa
        // Esta es una búsqueda simple por prefijo
        val snapshot = notesCollection
            .whereEqualTo("userId", userId)
            .orderBy("title")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
        
        return snapshot.documents.map { doc ->
            Note(
                id = doc.id,
                title = doc.getString("title") ?: "",
                content = doc.getString("content") ?: "",
                imageUrl = doc.getString("imageUrl"),
                createdAt = doc.getLong("createdAt") ?: 0,
                updatedAt = doc.getLong("updatedAt") ?: 0,
                userId = doc.getString("userId") ?: ""
            )
        }
    }
}
```

### 📦 Servicio de Autenticación (commonMain) - 100% Compartido

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/service/AuthService.kt
package com.miempresa.app.service

import com.iyr.firebase.auth.FirebaseAuth
import com.iyr.firebase.auth.FirebaseUser
import com.iyr.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthService {
    
    private val auth = FirebaseAuth.getInstance()
    
    /**
     * Usuario actual
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    /**
     * Observar estado de autenticación
     */
    val authState: Flow<Boolean>
        get() = auth.authStateChanges.map { it != null }
    
    /**
     * Registrar con email y contraseña
     */
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            result.user?.let { 
                Result.success(it) 
            } ?: Result.failure(Exception("Usuario no creado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Iniciar sesión con email y contraseña
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            result.user?.let { 
                Result.success(it) 
            } ?: Result.failure(Exception("Login fallido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Iniciar sesión anónima
     */
    suspend fun loginAnonymously(): Result<FirebaseUser> {
        return try {
            val result = auth.signInAnonymously()
            result.user?.let { 
                Result.success(it) 
            } ?: Result.failure(Exception("Login anónimo fallido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Cerrar sesión
     */
    fun logout() {
        auth.signOut()
    }
    
    /**
     * Enviar email de recuperación de contraseña
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verificar si el usuario tiene email verificado
     */
    fun isEmailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified ?: false
    }
    
    /**
     * Enviar email de verificación
     */
    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 📦 ViewModel (commonMain) - 100% Compartido

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/viewmodel/NotesViewModel.kt
package com.miempresa.app.viewmodel

import com.miempresa.app.models.Note
import com.miempresa.app.repository.NotesRepository
import com.miempresa.app.service.AuthService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ViewModel COMPARTIDO para todas las plataformas.
 * No usa AndroidX ViewModel, usa coroutines puras.
 */
class NotesViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {
    private val authService = AuthService()
    private val notesRepository = NotesRepository()
    
    // UI State
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        // Observar estado de autenticación
        scope.launch {
            authService.authState.collect { isAuth ->
                _isLoggedIn.value = isAuth
                if (isAuth) {
                    observeNotes()
                } else {
                    _notes.value = emptyList()
                }
            }
        }
    }
    
    private fun observeNotes() {
        scope.launch {
            try {
                notesRepository.observeNotes()
                    .catch { e -> _error.value = e.message }
                    .collect { _notes.value = it }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun createNote(title: String, content: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                notesRepository.createNote(title, content)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear nota"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateNote(noteId: String, title: String, content: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                notesRepository.updateNote(noteId, title, content)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar nota"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteNote(noteId: String) {
        scope.launch {
            try {
                notesRepository.deleteNote(noteId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar nota"
            }
        }
    }
    
    fun login(email: String, password: String) {
        scope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                authService.login(email, password)
            } catch (e: Exception) {
                _error.value = e.message ?: "Error en login"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun logout() {
        authService.logout()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun onCleared() {
        scope.cancel()
    }
}
```

---

### 📱 UI con Compose Multiplatform (commonMain) - 100% Compartido

#### Pantalla de Login

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/ui/screens/LoginScreen.kt
package com.miempresa.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.miempresa.app.viewmodel.NotesViewModel

@Composable
fun LoginScreen(
    viewModel: NotesViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    // Navegar si ya está logueado
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            onLoginSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        
        error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar Sesión")
            }
        }
    }
}
```

#### Pantalla de Lista de Notas

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/ui/screens/NotesListScreen.kt
package com.miempresa.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miempresa.app.models.Note
import com.miempresa.app.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Note) -> Unit,
    onAddClick: () -> Unit,
    onLogout: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas") },
                actions = {
                    IconButton(onClick = { 
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, "Agregar nota")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && notes.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                notes.isEmpty() -> {
                    Text(
                        text = "No hay notas. ¡Crea una!",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notes) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onNoteClick(note) },
                                onDelete = { viewModel.deleteNote(note.id) }
                            )
                        }
                    }
                }
            }
            
            // Mostrar error si existe
            error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar"
                    )
                }
            }
            
            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.content.take(100) + if (note.content.length > 100) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

#### App Principal (Entry Point Compose)

```kotlin
// ✅ shared/src/commonMain/kotlin/com/miempresa/app/App.kt
package com.miempresa.app

import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import com.miempresa.app.ui.screens.LoginScreen
import com.miempresa.app.ui.screens.NotesListScreen
import com.miempresa.app.ui.screens.NoteDetailScreen
import com.miempresa.app.viewmodel.NotesViewModel
import com.miempresa.app.models.Note

@Composable
fun App() {
    // ViewModel compartido
    val viewModel = remember { NotesViewModel() }
    
    // Estado de navegación simple
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    
    // Limpiar ViewModel al salir
    DisposableEffect(Unit) {
        onDispose { viewModel.onCleared() }
    }
    
    MaterialTheme {
        when (val screen = currentScreen) {
            is Screen.Login -> {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { currentScreen = Screen.NotesList }
                )
            }
            is Screen.NotesList -> {
                NotesListScreen(
                    viewModel = viewModel,
                    onNoteClick = { note ->
                        selectedNote = note
                        currentScreen = Screen.NoteDetail
                    },
                    onAddClick = {
                        selectedNote = null
                        currentScreen = Screen.NoteDetail
                    },
                    onLogout = { currentScreen = Screen.Login }
                )
            }
            is Screen.NoteDetail -> {
                NoteDetailScreen(
                    viewModel = viewModel,
                    note = selectedNote,
                    onBack = { currentScreen = Screen.NotesList }
                )
            }
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object NotesList : Screen()
    object NoteDetail : Screen()
}
```

---

### ⚡ Código Específico de Plataforma (MÍNIMO)

#### Android: Solo Inicialización

```kotlin
// ⚡ shared/src/androidMain/kotlin/com/miempresa/app/FirebaseInit.android.kt
package com.miempresa.app

import android.content.Context
import com.google.firebase.FirebaseApp

/**
 * ÚNICA función específica de Android.
 * Se llama UNA VEZ desde Application.onCreate()
 */
fun initializeFirebase(context: Context) {
    if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
    }
}
```

```kotlin
// androidApp/src/main/kotlin/MyApplication.kt
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebase(this) // ⬅️ Única llamada específica
    }
}
```

#### iOS: En AppDelegate (Swift)

```swift
// iosApp/iosApp/AppDelegate.swift
import FirebaseCore

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(...) -> Bool {
        FirebaseApp.configure() // ⬅️ Única línea específica
        return true
    }
}
```

#### JS: Configuración de Options

```kotlin
// ⚡ shared/src/jsMain/kotlin/com/miempresa/app/FirebaseInit.js.kt
package com.miempresa.app

import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.core.FirebaseOptions

/**
 * ÚNICA función específica de JS.
 * Se llama UNA VEZ al iniciar la app web.
 */
fun initializeFirebaseJS() {
    val options = FirebaseOptions.Builder()
        .setApiKey("AIzaSy...")          // ⬅️ De Firebase Console
        .setApplicationId("1:123...")
        .setProjectId("mi-proyecto")
        .setDatabaseUrl("https://mi-proyecto.firebaseio.com")
        .setStorageBucket("mi-proyecto.appspot.com")
        .build()
    
    FirebaseApp.initializeApp(options)
}
```

---

### 📊 Resumen: ¿Qué va dónde?

| Código | Ubicación | Ejemplo |
|--------|-----------|---------|
| **Modelos** | `commonMain` ✅ | `data class Note(...)` |
| **Repositories** | `commonMain` ✅ | `class NotesRepository` |
| **ViewModels** | `commonMain` ✅ | `class NotesViewModel` |
| **Services** | `commonMain` ✅ | `class AuthService` |
| **UI Compose** | `commonMain` ✅ | `@Composable fun LoginScreen()` |
| **Navegación** | `commonMain` ✅ | `sealed class Screen` |
| **Llamadas Firebase** | `commonMain` ✅ | `FirebaseAuth.getInstance()` |
| **Init Android** | `androidMain` ⚡ | `initializeFirebase(context)` |
| **Init iOS** | Swift ⚡ | `FirebaseApp.configure()` |
| **Config JS** | `jsMain` ⚡ | `FirebaseOptions.Builder()...` |

**Resultado: 95%+ del código es compartido** 🎉
```

---

## 🧪 Testing con Emuladores

### Instalar Firebase CLI

```bash
npm install -g firebase-tools
firebase login
```

### Inicializar Emuladores

```bash
cd mi-proyecto
firebase init emulators

# Seleccionar:
# - Authentication
# - Firestore
# - Realtime Database
# - Storage
# - Functions (opcional)
```

### Configurar firebase.json

```json
{
  "emulators": {
    "auth": {
      "port": 9099
    },
    "firestore": {
      "port": 8080
    },
    "database": {
      "port": 9000
    },
    "storage": {
      "port": 9199
    },
    "functions": {
      "port": 5001
    },
    "ui": {
      "enabled": true,
      "port": 4000
    }
  }
}
```

### Iniciar Emuladores

```bash
firebase emulators:start
```

### Conectar desde Código

```kotlin
// Solo en debug/testing
fun connectToEmulators() {
    // Para Android: usar 10.0.2.2 (emulador Android)
    // Para iOS Simulator: usar localhost
    // Para dispositivo físico: usar IP de tu computadora
    
    val host = "10.0.2.2" // Android Emulator
    // val host = "localhost" // iOS Simulator
    // val host = "192.168.1.100" // Dispositivo físico
    
    FirebaseAuth.getInstance().useEmulator(host, 9099)
    FirebaseFirestore.getInstance().useEmulator(host, 8080)
    FirebaseDatabase.getInstance().useEmulator(host, 9000)
    FirebaseStorage.getInstance().useEmulator(host, 9199)
    FirebaseFunctions.getInstance().useEmulator(host, 5001)
}
```

---

## ❓ Solución de Problemas

### Error: "Default FirebaseApp is not initialized"

**Causa:** Firebase no se inicializó antes de usarlo.

**Solución:**
- Android: Verifica que `google-services.json` esté en `androidApp/`
- iOS: Verifica que `GoogleService-Info.plist` esté en el target correcto
- JS: Llama a `initializeFirebaseJS()` antes de usar cualquier servicio

### Error: "No matching client found for package name"

**Causa:** El package name en `google-services.json` no coincide con tu app.

**Solución:**
1. Ve a Firebase Console
2. Verifica el package name registrado
3. Actualiza `applicationId` en `build.gradle.kts` para que coincida

### Error: CocoaPods "Unable to find a specification"

**Causa:** Pods no están actualizados.

**Solución:**
```bash
cd iosApp
pod repo update
pod install --repo-update
```

### Error: "Missing google_app_id" en iOS

**Causa:** `GoogleService-Info.plist` no está en el bundle.

**Solución:**
1. En Xcode, verifica que el archivo esté en "Copy Bundle Resources"
2. Build Phases > Copy Bundle Resources > verificar que está listado

### Error: Gradle Sync Failed

**Causa:** Versiones incompatibles.

**Solución:**
1. Limpia el cache:
```bash
./gradlew clean
rm -rf ~/.gradle/caches
./gradlew build --refresh-dependencies
```

2. Verifica versiones compatibles en `libs.versions.toml`

### Error: "PERMISSION_DENIED" en Firestore/Database

**Causa:** Reglas de seguridad restrictivas.

**Solución temporal (solo desarrollo):**

```javascript
// Firestore Rules (firebase console)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

```javascript
// Realtime Database Rules
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

---

## 📚 Recursos Adicionales

- **API Reference:** [docs/API_REFERENCE.md](API_REFERENCE.md)
- **GitHub:** https://github.com/romancanoniero/firebase-kmp-sdk
- **Firebase Docs:** https://firebase.google.com/docs
- **KMP Docs:** https://kotlinlang.org/docs/multiplatform.html

---

## 📧 Soporte

¿Tienes problemas? 

1. Revisa [Issues en GitHub](https://github.com/romancanoniero/firebase-kmp-sdk/issues)
2. Crea un nuevo issue con:
   - Descripción del problema
   - Pasos para reproducir
   - Logs de error
   - Versiones (Kotlin, Gradle, Firebase SDK)
3. Email: romancanoniero@gmail.com

---

**¡Feliz coding! 🚀**

*Desarrollado con ❤️ por Roman Canoniero*

