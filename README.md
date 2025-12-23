# Firebase KMP SDK

A Kotlin Multiplatform library that provides Firebase SDK functionality for Android, iOS, and JavaScript platforms with a unified API matching the Firebase Android SDK.

## Features

- 🔥 **Full Firebase API parity** with Android SDK
- 📱 **Multiplatform support**: Android, iOS, JavaScript
- ⚡ **Coroutines-first**: All async operations use `suspend` functions
- 🔄 **Real-time data**: Flow-based listeners for reactive data
- 🎯 **Type-safe**: Full Kotlin type safety across platforms

## Modules

| Module | Description | Status |
|--------|-------------|--------|
| `firebase-core` | FirebaseApp initialization | ✅ Complete |
| `firebase-auth` | Authentication (Email, Google, Facebook, Phone) | ✅ Complete |
| `firebase-database` | Realtime Database | ✅ Complete |
| `firebase-firestore` | Cloud Firestore | ✅ Complete |
| `firebase-storage` | Cloud Storage | ✅ Complete |
| `firebase-functions` | Cloud Functions client | ✅ Complete |
| `firebase-messaging` | Cloud Messaging (FCM) | ✅ Complete |

## Installation

### Gradle (Android/KMP)

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

// build.gradle.kts
dependencies {
    implementation("com.iyr.firebase:firebase-core:1.0.0")
    implementation("com.iyr.firebase:firebase-auth:1.0.0")
    implementation("com.iyr.firebase:firebase-database:1.0.0")
    implementation("com.iyr.firebase:firebase-firestore:1.0.0")
    implementation("com.iyr.firebase:firebase-storage:1.0.0")
    implementation("com.iyr.firebase:firebase-functions:1.0.0")
    implementation("com.iyr.firebase:firebase-messaging:1.0.0")
}
```

## Usage Examples

### Firebase Core

```kotlin
import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.core.FirebaseOptions

// Initialize (usually done automatically on Android)
val options = FirebaseOptions.Builder()
    .setApiKey("your-api-key")
    .setApplicationId("your-app-id")
    .setProjectId("your-project-id")
    .build()

FirebaseApp.initializeApp(options)
```

### Firebase Auth

```kotlin
import com.iyr.firebase.auth.FirebaseAuth
import com.iyr.firebase.auth.EmailAuthProvider

val auth = FirebaseAuth.getInstance()

// Sign in with email/password
val result = auth.signInWithEmailAndPassword("user@example.com", "password")
println("User: ${result.user?.email}")

// Create new user
val newUser = auth.createUserWithEmailAndPassword("new@example.com", "password")

// Listen to auth state changes (Flow)
auth.authStateChanges.collect { user ->
    if (user != null) {
        println("Signed in: ${user.email}")
    } else {
        println("Signed out")
    }
}

// Sign out
auth.signOut()
```

### Firebase Realtime Database

```kotlin
import com.iyr.firebase.database.FirebaseDatabase

val database = FirebaseDatabase.getInstance()
val ref = database.getReference("users").child("user123")

// Write data
ref.setValue(mapOf("name" to "John", "age" to 30))

// Read data once
val snapshot = ref.get()
println("Name: ${snapshot.child("name").getValue()}")

// Listen to changes (Flow)
ref.valueEvents.collect { snapshot ->
    println("Data changed: ${snapshot.getValue()}")
}

// Query data
val query = database.getReference("users")
    .orderByChild("age")
    .startAt(18.0)
    .limitToFirst(10)
    
val results = query.get()
```

### Firebase Firestore

```kotlin
import com.iyr.firebase.firestore.FirebaseFirestore

val firestore = FirebaseFirestore.getInstance()
val collection = firestore.collection("users")

// Add document
val docRef = collection.add(mapOf("name" to "Jane", "email" to "jane@example.com"))

// Get document
val doc = firestore.document("users/user123").get()
if (doc.exists()) {
    println("Name: ${doc.get("name")}")
}

// Query
val query = collection
    .whereEqualTo("active", true)
    .orderBy("createdAt", Direction.DESCENDING)
    .limit(20)
    
val results = query.get()
results.documents.forEach { doc ->
    println("${doc.id}: ${doc.getData()}")
}

// Real-time updates
collection.snapshots.collect { snapshot ->
    snapshot.documentChanges.forEach { change ->
        when (change.type) {
            DocumentChangeType.ADDED -> println("New: ${change.document.id}")
            DocumentChangeType.MODIFIED -> println("Modified: ${change.document.id}")
            DocumentChangeType.REMOVED -> println("Removed: ${change.document.id}")
        }
    }
}

// Transactions
firestore.runTransaction {
    val doc = get(docRef)
    val currentCount = doc.get("count") as Long
    update(docRef, mapOf("count" to currentCount + 1))
}
```

### Firebase Storage

```kotlin
import com.iyr.firebase.storage.FirebaseStorage

val storage = FirebaseStorage.getInstance()
val ref = storage.getReference("images/photo.jpg")

// Upload bytes
val bytes = byteArrayOf(/* image data */)
val uploadTask = ref.putBytes(bytes)
uploadTask.await()

// Get download URL
val url = ref.getDownloadUrl()
println("URL: $url")

// Download
val data = ref.getBytes(1024 * 1024) // 1MB max

// List files
val listResult = ref.parent?.listAll()
listResult?.items?.forEach { item ->
    println("File: ${item.name}")
}
```

### Firebase Functions

```kotlin
import com.iyr.firebase.functions.FirebaseFunctions

val functions = FirebaseFunctions.getInstance()

// Call a function
val callable = functions.getHttpsCallable("myFunction")
val result = callable.call(mapOf("param1" to "value1"))
println("Result: ${result.data}")

// With timeout
callable.withTimeout(30, TimeUnit.SECONDS)
    .call(data)
```

### Firebase Messaging

```kotlin
import com.iyr.firebase.messaging.FirebaseMessaging

val messaging = FirebaseMessaging.getInstance()

// Get FCM token
val token = messaging.getToken()
println("FCM Token: $token")

// Subscribe to topic
messaging.subscribeToTopic("news")

// Unsubscribe
messaging.unsubscribeFromTopic("news")

// Handle messages (Android - in your service)
class MyMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val notification = remoteMessage.notification
        // Handle message
    }
    
    override fun onNewToken(token: String) {
        // Send token to server
    }
}
```

## Platform-Specific Notes

### Android
- Full implementation using Firebase Android SDK
- Automatic initialization via `google-services.json`

### iOS
- Uses Firebase iOS SDK via CocoaPods cinterop
- Requires `GoogleService-Info.plist`
- Configure in AppDelegate for messaging

### JavaScript
- Stub implementations for Promise-based API
- Requires Firebase JS SDK configuration

## Requirements

- Kotlin 2.0.21+
- Android: minSdk 24, compileSdk 36
- iOS: Deployment target 14.1+
- Xcode 15+ (for iOS builds)

## Building

```bash
# Build Android
./gradlew assembleDebug

# Build all (requires compatible Xcode)
./gradlew build
```

## License

Apache License 2.0

## Contributing

Contributions welcome! Please read our contributing guidelines.
