# 🔥 Firebase KMP SDK

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-blue.svg)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%20|%20iOS%20|%20JS-orange.svg)](https://kotlinlang.org/docs/multiplatform.html)

**Librería Kotlin Multiplatform que replica fielmente la API del Firebase Android SDK** para Android, iOS y JavaScript.

## ✨ Características

- 🎯 **API idéntica al Firebase Android SDK** - Migra tu código existente fácilmente
- 📱 **Multiplataforma** - Android, iOS (Kotlin/Native) y JavaScript
- 🔄 **Coroutines y Flow** - API moderna con soporte completo para async/await
- 🧪 **Testeable** - Compatible con Firebase Emulator para tests de integración
- 📦 **Modular** - Usa solo los módulos que necesitas

## 📦 Módulos Disponibles

| Módulo | Android | iOS | JS | Descripción |
|--------|:-------:|:---:|:--:|-------------|
| `firebase-core` | ✅ | ✅ | ✅ | FirebaseApp, FirebaseOptions |
| `firebase-auth` | ✅ | ✅ | ✅ | Autenticación completa (Email, Phone, OAuth) |
| `firebase-database` | ✅ | ✅ | ✅ | Realtime Database |
| `firebase-firestore` | ✅ | ✅ | ✅ | Cloud Firestore |
| `firebase-storage` | ✅ | ✅ | ✅ | Cloud Storage |
| `firebase-functions` | ✅ | ✅ | ✅ | Cloud Functions client |
| `firebase-messaging` | ✅ | ✅ | ⚠️ | Push Notifications (FCM) |
| `firebase-analytics` | ✅ | ✅ | ✅ | Google Analytics |
| `firebase-crashlytics` | ✅ | ✅ | ❌ | Crashlytics |
| `firebase-remote-config` | ✅ | ✅ | ✅ | Remote Config |
| `firebase-performance` | ✅ | ✅ | ❌ | Performance Monitoring |
| `firebase-appcheck` | ✅ | ✅ | ❌ | App Check |
| `firebase-inappmessaging` | ✅ | ❌ | ❌ | In-App Messaging |

## 🚀 Instalación

### Gradle (Kotlin DSL)

Agrega el repositorio de Maven (cuando esté publicado en Maven Central):

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        // O para usar Maven Local durante desarrollo:
        mavenLocal()
    }
}
```

Agrega las dependencias que necesites:

```kotlin
// build.gradle.kts (módulo compartido)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.iyr.firebase:firebase-core:1.0.0")
            implementation("com.iyr.firebase:firebase-auth:1.0.0")
            implementation("com.iyr.firebase:firebase-database:1.0.0")
            implementation("com.iyr.firebase:firebase-firestore:1.0.0")
            implementation("com.iyr.firebase:firebase-storage:1.0.0")
            implementation("com.iyr.firebase:firebase-functions:1.0.0")
            implementation("com.iyr.firebase:firebase-messaging:1.0.0")
            implementation("com.iyr.firebase:firebase-analytics:1.0.0")
            // ... otros módulos según necesidad
        }
    }
}
```

### Configuración para iOS

Agrega los pods de Firebase en tu `Podfile`:

```ruby
# iosApp/Podfile
target 'iosApp' do
  use_frameworks!
  
  pod 'FirebaseCore', '~> 10.29'
  pod 'FirebaseAuth', '~> 10.29'
  pod 'FirebaseDatabase', '~> 10.29'
  pod 'FirebaseFirestore', '~> 10.29'
  pod 'FirebaseStorage', '~> 10.29'
  # ... otros pods según los módulos que uses
end
```

### Configuración para JavaScript

Las dependencias de Firebase JS se incluyen automáticamente via npm.

## 📖 Uso Básico

### Inicialización

```kotlin
// La inicialización es automática si tienes:
// - Android: google-services.json
// - iOS: GoogleService-Info.plist

val app = FirebaseApp.getInstance()

// O inicialización manual:
val options = FirebaseOptions.Builder()
    .setApiKey("AIzaSy...")
    .setApplicationId("1:123456789:android:abc123")
    .setProjectId("my-project")
    .setDatabaseUrl("https://my-project.firebaseio.com")
    .setStorageBucket("my-project.appspot.com")
    .build()
    
FirebaseApp.initializeApp(context, options) // Android
FirebaseApp.initializeApp(options) // iOS/JS
```

### Authentication

```kotlin
val auth = FirebaseAuth.getInstance()

// Crear usuario
val result = auth.createUserWithEmailAndPassword("email@example.com", "password123")
println("Usuario creado: ${result.user?.uid}")

// Iniciar sesión
val result = auth.signInWithEmailAndPassword("email@example.com", "password123")
val user = result.user

// Cerrar sesión
auth.signOut()

// Observar cambios de autenticación
auth.authStateChanges.collect { user ->
    if (user != null) {
        println("Conectado como: ${user.email}")
    } else {
        println("Desconectado")
    }
}

// Autenticación anónima
val result = auth.signInAnonymously()
println("Usuario anónimo: ${result.user?.uid}")
```

### Realtime Database

```kotlin
val database = FirebaseDatabase.getInstance()
val usersRef = database.getReference("users")

// Escribir datos
usersRef.child(userId).setValue(mapOf(
    "name" to "John Doe",
    "email" to "john@example.com",
    "age" to 30
))

// Leer una vez
val snapshot = usersRef.child(userId).get()
val name = snapshot.child("name").getValue() as? String

// Escuchar cambios en tiempo real
usersRef.child(userId).valueEvents.collect { snapshot ->
    val userData = snapshot.getValue() as? Map<*, *>
    println("Datos actualizados: $userData")
}

// Push (generar ID único)
val newPostRef = database.getReference("posts").push()
newPostRef.setValue(mapOf("title" to "Mi Post"))
println("Post ID: ${newPostRef.key}")
```

### Cloud Firestore

```kotlin
val firestore = FirebaseFirestore.getInstance()

// Agregar documento (ID auto-generado)
val docRef = firestore.collection("users").add(mapOf(
    "name" to "Jane Doe",
    "email" to "jane@example.com"
))
println("Documento creado: ${docRef.id}")

// Establecer documento (ID específico)
firestore.collection("users").document("user123").set(mapOf(
    "name" to "John",
    "active" to true
))

// Leer documento
val snapshot = firestore.document("users/user123").get()
if (snapshot.exists()) {
    val data = snapshot.getData()
    println("Nombre: ${data?.get("name")}")
}

// Query con filtros
val activeUsers = firestore.collection("users")
    .whereEqualTo("active", true)
    .orderBy("name")
    .limit(10)
    .get()

activeUsers.documents.forEach { doc ->
    println("${doc.id}: ${doc.getData()}")
}

// Escuchar cambios en tiempo real
firestore.collection("messages")
    .whereEqualTo("roomId", "room123")
    .snapshots
    .collect { querySnapshot ->
        querySnapshot.documentChanges.forEach { change ->
            when (change.type) {
                DocumentChange.Type.ADDED -> println("Nuevo mensaje")
                DocumentChange.Type.MODIFIED -> println("Mensaje editado")
                DocumentChange.Type.REMOVED -> println("Mensaje eliminado")
            }
        }
    }
```

### Cloud Storage

```kotlin
val storage = FirebaseStorage.getInstance()
val imagesRef = storage.getReference("images")

// Subir archivo
val photoRef = imagesRef.child("photo.jpg")
val uploadTask = photoRef.putFile("/path/to/local/photo.jpg")

// Monitorear progreso
uploadTask.progressFlow.collect { progress ->
    val percent = (100.0 * progress.bytesTransferred / progress.totalByteCount).toInt()
    println("Subida: $percent%")
}

// Obtener URL de descarga
val downloadUrl = photoRef.getDownloadUrl()
println("URL: $downloadUrl")

// Descargar a archivo local
photoRef.getFile("/path/to/download/photo.jpg")

// Metadata
val metadata = photoRef.getMetadata()
println("Tamaño: ${metadata.sizeBytes} bytes")
```

### Cloud Functions

```kotlin
val functions = FirebaseFunctions.getInstance()

// Llamar función HTTPS
val result = functions.getHttpsCallable("myFunction").call(mapOf(
    "param1" to "value1",
    "param2" to 123
))
val data = result.data as Map<*, *>
println("Respuesta: $data")
```

### Remote Config

```kotlin
val remoteConfig = FirebaseRemoteConfig.getInstance()

// Configurar defaults
remoteConfig.setDefaultsAsync(mapOf(
    "welcome_message" to "Bienvenido!",
    "feature_enabled" to false
))

// Fetch y activar
val success = remoteConfig.fetchAndActivate()
if (success) {
    val welcomeMsg = remoteConfig.getString("welcome_message")
    val featureEnabled = remoteConfig.getBoolean("feature_enabled")
    println("Mensaje: $welcomeMsg, Feature: $featureEnabled")
}
```

### Analytics

```kotlin
val analytics = FirebaseAnalytics.getInstance()

// Log evento
analytics.logEvent("purchase", mapOf(
    "item_id" to "SKU_123",
    "item_name" to "Premium Plan",
    "price" to 9.99
))

// Establecer propiedades de usuario
analytics.setUserProperty("subscription_type", "premium")
analytics.setUserId("user_12345")
```

### Performance Monitoring

```kotlin
val performance = FirebasePerformance.getInstance()

// Trace personalizado
val trace = performance.newTrace("my_operation")
trace.start()
// ... operación a medir ...
trace.putAttribute("result", "success")
trace.putMetric("items_processed", 42)
trace.stop()

// HTTP Metric
val httpMetric = performance.newHttpMetric("https://api.example.com/data", "GET")
httpMetric.start()
// ... hacer request HTTP ...
httpMetric.setHttpResponseCode(200)
httpMetric.setResponsePayloadSize(1024)
httpMetric.stop()
```

## 🧪 Testing

### Unit Tests

```bash
# Android
./gradlew testDebugUnitTest

# iOS
./gradlew iosSimulatorArm64Test

# JavaScript
./gradlew jsNodeTest
```

### Integration Tests con Firebase Emulator

1. Instala Firebase CLI:
```bash
npm install -g firebase-tools
```

2. Inicia los emuladores:
```bash
firebase emulators:start --only auth,database,firestore,storage,functions
```

3. Ejecuta tests de integración:
```bash
# Android
./gradlew connectedAndroidTest

# O el script incluido:
./run_integration_tests.sh
```

## 🏗️ Arquitectura

```
firebase-kmp-sdk/
├── firebase-core/          # FirebaseApp, FirebaseOptions
├── firebase-auth/          # Authentication
├── firebase-database/      # Realtime Database
├── firebase-firestore/     # Cloud Firestore
├── firebase-storage/       # Cloud Storage
├── firebase-functions/     # Cloud Functions
├── firebase-messaging/     # Push Notifications
├── firebase-analytics/     # Analytics
├── firebase-crashlytics/   # Crashlytics
├── firebase-remote-config/ # Remote Config
├── firebase-performance/   # Performance Monitoring
├── firebase-appcheck/      # App Check
└── firebase-inappmessaging/# In-App Messaging
```

### Implementación por Plataforma

| Plataforma | Tecnología |
|------------|------------|
| **Android** | Wrapper sobre Firebase Android SDK oficial |
| **iOS** | Kotlin/Native cinterop → Firebase iOS SDK (Objective-C) |
| **JavaScript** | Interoperabilidad → Firebase JS SDK (npm) |

## 📝 Publicar en Maven Local

Para desarrollo local:

```bash
./gradlew publishToMavenLocal
```

Los artefactos se publican en `~/.m2/repository/com/iyr/firebase/`.

## 🤝 Créditos

### Autor Principal
- **Roman Canoniero** - Arquitectura, diseño e implementación inicial

### Desarrollado con ❤️ por
- **IYR Team** - [https://iyr.com](https://iyr.com)

### Agradecimientos
- Google Firebase Team por los SDKs oficiales
- JetBrains por Kotlin Multiplatform
- Comunidad de Kotlin por las herramientas de cinterop

## 📄 Licencia

```
Copyright 2024 Roman Canoniero / IYR

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🙏 Contribuciones

¡Contribuciones son bienvenidas! Por favor:

1. Fork el repositorio
2. Crea una branch para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la branch (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📬 Contacto

- **Issues**: [GitHub Issues](https://github.com/iyr/firebase-kmp-sdk/issues)
- **Email**: romancanoniero@gmail.com

---

**⭐ Si este proyecto te es útil, considera darle una estrella en GitHub!**
