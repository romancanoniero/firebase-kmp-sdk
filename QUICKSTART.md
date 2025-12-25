# 🚀 Firebase KMP SDK - Guía Rápida de Implementación

## Paso 1: Agregar Repositorio

### Para desarrollo (Maven Local):
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenLocal()  // ← Agregar esto primero
        mavenCentral()
        google()
    }
}
```

### Para producción (cuando se publique en Maven Central):
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
```

## Paso 2: Agregar Dependencias

### Proyecto Kotlin Multiplatform

```kotlin
// build.gradle.kts (módulo shared/commonMain)
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core (obligatorio)
            implementation("com.iyr.firebase:firebase-core:1.0.0-SNAPSHOT")
            
            // Módulos opcionales (agrega los que necesites)
            implementation("com.iyr.firebase:firebase-auth:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-database:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-firestore:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-storage:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-functions:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-messaging:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-analytics:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-remote-config:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-performance:1.0.0-SNAPSHOT")
            implementation("com.iyr.firebase:firebase-crashlytics:1.0.0-SNAPSHOT")
        }
    }
}
```

## Paso 3: Configuración por Plataforma

### Android

1. Agrega `google-services.json` en `androidApp/` o `composeApp/`
2. Agrega el plugin de Google Services:

```kotlin
// build.gradle.kts (nivel proyecto)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}

// build.gradle.kts (nivel app)
plugins {
    id("com.google.gms.google-services")
}
```

### iOS

1. Agrega `GoogleService-Info.plist` en `iosApp/iosApp/`
2. Configura los pods en `iosApp/Podfile`:

```ruby
target 'iosApp' do
  use_frameworks!
  
  # Pods de Firebase (agrega los que uses)
  pod 'FirebaseCore', '~> 10.29'
  pod 'FirebaseAuth', '~> 10.29'
  pod 'FirebaseDatabase', '~> 10.29'
  pod 'FirebaseFirestore', '~> 10.29'
  pod 'FirebaseStorage', '~> 10.29'
  pod 'FirebaseFunctions', '~> 10.29'
  pod 'FirebaseMessaging', '~> 10.29'
  pod 'FirebaseAnalytics', '~> 10.29'
  pod 'FirebaseRemoteConfig', '~> 10.29'
  pod 'FirebasePerformance', '~> 10.29'
  pod 'FirebaseCrashlytics', '~> 10.29'
end
```

3. Ejecuta `pod install` en el directorio `iosApp/`

### JavaScript (Web)

Las dependencias de Firebase JS se incluyen automáticamente. Solo necesitas configurar Firebase:

```javascript
// Configuración manual si no usas auto-init
const firebaseConfig = {
  apiKey: "...",
  authDomain: "...",
  projectId: "...",
  storageBucket: "...",
  messagingSenderId: "...",
  appId: "..."
};
```

## Paso 4: Uso Básico

```kotlin
// En commonMain
import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.auth.FirebaseAuth
import com.iyr.firebase.database.FirebaseDatabase

// Obtener instancias
val auth = FirebaseAuth.getInstance()
val database = FirebaseDatabase.getInstance()

// Autenticación
suspend fun login(email: String, password: String) {
    val result = auth.signInWithEmailAndPassword(email, password)
    println("Logged in: ${result.user?.uid}")
}

// Base de datos
suspend fun saveData(userId: String, data: Map<String, Any>) {
    database.getReference("users/$userId").setValue(data)
}

suspend fun readData(userId: String): Map<*, *>? {
    val snapshot = database.getReference("users/$userId").get()
    return snapshot.getValue() as? Map<*, *>
}
```

## ✅ Verificar Instalación

Ejecuta este código para verificar que todo funcione:

```kotlin
fun testFirebaseSetup() {
    try {
        val app = FirebaseApp.getInstance()
        println("✅ Firebase inicializado: ${app.getName()}")
        
        val auth = FirebaseAuth.getInstance()
        println("✅ Auth disponible")
        
        val database = FirebaseDatabase.getInstance()
        println("✅ Database disponible")
        
        println("🎉 Firebase KMP SDK configurado correctamente!")
    } catch (e: Exception) {
        println("❌ Error: ${e.message}")
    }
}
```

## 📚 Documentación Completa

Ver [README.md](README.md) para documentación completa con ejemplos de cada módulo.

## 🆘 Solución de Problemas

### Error: "Default FirebaseApp is not initialized"
- Verifica que `google-services.json` (Android) o `GoogleService-Info.plist` (iOS) esté en la ubicación correcta
- En iOS, asegúrate de llamar `FirebaseApp.configure()` en AppDelegate

### Error: "Could not resolve com.iyr.firebase:..."
- Verifica que `mavenLocal()` esté en los repositorios
- Ejecuta `./gradlew publishToMavenLocal` en el proyecto firebase-kmp-sdk

### iOS: Pod not found
- Ejecuta `pod repo update` y luego `pod install`
- Verifica la versión de CocoaPods (`pod --version`)

---

**Firebase KMP SDK** - Creado por Roman Canoniero (romancanoniero@gmail.com)

