# Firebase KMP SDK

Librería multiplataforma que replica la API del Firebase Android SDK para Kotlin Multiplatform.

## Plataformas soportadas

- Android
- iOS (via Kotlin/Native + CocoaPods)
- JavaScript (Web)

## Módulos

| Módulo | Descripción | Estado |
|--------|-------------|--------|
| firebase-core | FirebaseApp, FirebaseOptions | ✅ |
| firebase-auth | Autenticación | 🚧 |
| firebase-database | Realtime Database | 📋 |
| firebase-firestore | Cloud Firestore | 📋 |
| firebase-storage | Cloud Storage | 📋 |
| firebase-functions | Cloud Functions | 📋 |
| firebase-messaging | Push Notifications | 📋 |

## Uso

```kotlin
// Inicializar Firebase
val app = FirebaseApp.getInstance()

// Autenticación
val auth = FirebaseAuth.getInstance()
val result = auth.signInWithEmailAndPassword(email, password)
val user = result.user

// Realtime Database
val database = FirebaseDatabase.getInstance()
val ref = database.getReference("users")
ref.child(userId).setValue(userData)

// Observar cambios (Flow)
auth.authStateChanges.collect { user ->
    println("User: ${user?.uid}")
}
```

## Instalación

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.iyr.firebase:firebase-core:1.0.0")
    implementation("com.iyr.firebase:firebase-auth:1.0.0")
    implementation("com.iyr.firebase:firebase-database:1.0.0")
}
```

## Licencia

Apache 2.0
