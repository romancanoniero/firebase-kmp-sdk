# 📚 Firebase KMP SDK - Referencia de API Completa

Este documento detalla todas las funciones disponibles en cada módulo de Firebase KMP SDK, organizadas por biblioteca.

---

## 📑 Índice

1. [Firebase Core](#-firebase-core)
2. [Firebase Auth](#-firebase-auth)
3. [Firebase Realtime Database](#-firebase-realtime-database)
4. [Firebase Cloud Firestore](#-firebase-cloud-firestore)
5. [Firebase Cloud Storage](#-firebase-cloud-storage)
6. [Firebase Cloud Functions](#-firebase-cloud-functions)
7. [Firebase Cloud Messaging](#-firebase-cloud-messaging)
8. [Firebase Analytics](#-firebase-analytics)
9. [Firebase Crashlytics](#-firebase-crashlytics)
10. [Firebase Remote Config](#-firebase-remote-config)
11. [Firebase Performance](#-firebase-performance)
12. [Firebase App Check](#-firebase-app-check)
13. [Firebase In-App Messaging](#-firebase-in-app-messaging)

---

## 🔥 Firebase Core

**Dependencia:** `io.github.romancanoniero:firebase-core:1.0.0`

### Clases Principales

#### `FirebaseApp`

Representa una instancia de la aplicación Firebase.

```kotlin
import com.iyr.firebase.core.FirebaseApp
import com.iyr.firebase.core.FirebaseOptions
```

| Método | Descripción | Retorno |
|--------|-------------|---------|
| `getInstance()` | Obtiene la app por defecto | `FirebaseApp` |
| `getInstance(name)` | Obtiene una app por nombre | `FirebaseApp` |
| `getApps()` | Lista todas las apps inicializadas | `List<FirebaseApp>` |
| `initializeApp(context)` | Inicializa con config automática (Android) | `FirebaseApp` |
| `initializeApp(context, options)` | Inicializa con opciones personalizadas | `FirebaseApp` |
| `initializeApp(options)` | Inicializa (iOS/JS) | `FirebaseApp` |
| `initializeApp(options, name)` | Inicializa con nombre personalizado | `FirebaseApp` |
| `getName()` | Obtiene el nombre de la app | `String` |
| `delete()` | Elimina la instancia de la app | `Unit` |

**Ejemplos:**

```kotlin
// Inicialización automática (requiere google-services.json / GoogleService-Info.plist)
val app = FirebaseApp.getInstance()

// Inicialización manual
val options = FirebaseOptions.Builder()
    .setApiKey("AIzaSy...")
    .setApplicationId("1:123456789:android:abc123")
    .setProjectId("my-project")
    .setDatabaseUrl("https://my-project.firebaseio.com")
    .setStorageBucket("my-project.appspot.com")
    .setGcmSenderId("123456789")
    .build()

// Android
FirebaseApp.initializeApp(context, options)

// iOS / JS
FirebaseApp.initializeApp(options)

// Múltiples apps
FirebaseApp.initializeApp(options, "secondary")
val secondaryApp = FirebaseApp.getInstance("secondary")

// Listar todas las apps
val apps = FirebaseApp.getApps()
apps.forEach { println("App: ${it.getName()}") }
```

#### `FirebaseOptions`

Configuración para inicializar Firebase.

| Propiedad | Descripción |
|-----------|-------------|
| `apiKey` | API Key de Firebase |
| `applicationId` | ID de la aplicación (mobilesdk_app_id) |
| `projectId` | ID del proyecto Firebase |
| `databaseUrl` | URL del Realtime Database |
| `storageBucket` | Bucket de Cloud Storage |
| `gcmSenderId` | Sender ID para Cloud Messaging |
| `gaTrackingId` | ID de Google Analytics |

---

## 🔐 Firebase Auth

**Dependencia:** `io.github.romancanoniero:firebase-auth:1.0.0`

### Clases Principales

#### `FirebaseAuth`

Servicio principal de autenticación.

```kotlin
import com.iyr.firebase.auth.FirebaseAuth
import com.iyr.firebase.auth.FirebaseUser
import com.iyr.firebase.auth.AuthResult
```

| Método | Descripción | Retorno |
|--------|-------------|---------|
| `getInstance()` | Obtiene instancia por defecto | `FirebaseAuth` |
| `getInstance(app)` | Obtiene instancia para una app específica | `FirebaseAuth` |
| `currentUser` | Usuario actualmente autenticado | `FirebaseUser?` |
| `authStateChanges` | Flow de cambios de autenticación | `Flow<FirebaseUser?>` |

**Métodos de Autenticación:**

| Método | Descripción |
|--------|-------------|
| `signInAnonymously()` | Inicia sesión anónima |
| `signInWithEmailAndPassword(email, password)` | Inicia sesión con email/password |
| `createUserWithEmailAndPassword(email, password)` | Crea cuenta con email/password |
| `signInWithCredential(credential)` | Inicia sesión con credencial (OAuth) |
| `signInWithCustomToken(token)` | Inicia sesión con token personalizado |
| `signOut()` | Cierra sesión |
| `sendPasswordResetEmail(email)` | Envía email para resetear contraseña |
| `confirmPasswordReset(code, newPassword)` | Confirma reset de contraseña |
| `verifyPasswordResetCode(code)` | Verifica código de reset |
| `applyActionCode(code)` | Aplica código de acción (verificación email) |
| `useEmulator(host, port)` | Conecta al emulador de Auth |

**Ejemplos Completos:**

```kotlin
val auth = FirebaseAuth.getInstance()

// === REGISTRO DE USUARIO ===
suspend fun registerUser(email: String, password: String): FirebaseUser? {
    return try {
        val result = auth.createUserWithEmailAndPassword(email, password)
        println("Usuario registrado: ${result.user?.uid}")
        result.user
    } catch (e: Exception) {
        println("Error en registro: ${e.message}")
        null
    }
}

// === INICIO DE SESIÓN ===
suspend fun login(email: String, password: String): FirebaseUser? {
    return try {
        val result = auth.signInWithEmailAndPassword(email, password)
        println("Login exitoso: ${result.user?.email}")
        result.user
    } catch (e: Exception) {
        println("Error en login: ${e.message}")
        null
    }
}

// === AUTENTICACIÓN ANÓNIMA ===
suspend fun loginAnonymous(): FirebaseUser? {
    val result = auth.signInAnonymously()
    return result.user
}

// === OBSERVAR ESTADO DE AUTENTICACIÓN ===
fun observeAuthState() {
    // Usando Flow (recomendado para UI reactiva)
    auth.authStateChanges.collect { user ->
        if (user != null) {
            println("Usuario conectado: ${user.email ?: user.uid}")
            println("  - UID: ${user.uid}")
            println("  - Email verificado: ${user.isEmailVerified}")
            println("  - Anónimo: ${user.isAnonymous}")
        } else {
            println("Usuario desconectado")
        }
    }
}

// === RECUPERAR CONTRASEÑA ===
suspend fun recoverPassword(email: String) {
    try {
        auth.sendPasswordResetEmail(email)
        println("Email de recuperación enviado a $email")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// === CERRAR SESIÓN ===
fun logout() {
    auth.signOut()
}

// === VERIFICAR EMAIL ===
suspend fun sendEmailVerification() {
    auth.currentUser?.sendEmailVerification()
}
```

#### `FirebaseUser`

Representa un usuario autenticado.

| Propiedad | Tipo | Descripción |
|-----------|------|-------------|
| `uid` | `String` | ID único del usuario |
| `email` | `String?` | Email del usuario |
| `displayName` | `String?` | Nombre para mostrar |
| `phoneNumber` | `String?` | Número de teléfono |
| `photoUrl` | `String?` | URL de la foto de perfil |
| `isAnonymous` | `Boolean` | Si es usuario anónimo |
| `isEmailVerified` | `Boolean` | Si el email está verificado |
| `providerId` | `String` | Proveedor de autenticación |
| `metadata` | `UserMetadata` | Fechas de creación y último login |

| Método | Descripción |
|--------|-------------|
| `getIdToken(forceRefresh)` | Obtiene token JWT |
| `reload()` | Recarga datos del usuario |
| `delete()` | Elimina la cuenta |
| `updateEmail(email)` | Actualiza email |
| `updatePassword(password)` | Actualiza contraseña |
| `updateProfile(request)` | Actualiza displayName y photoUrl |
| `sendEmailVerification()` | Envía email de verificación |
| `linkWithCredential(credential)` | Vincula con otra credencial |
| `unlink(providerId)` | Desvincula un proveedor |
| `reauthenticate(credential)` | Re-autentica al usuario |

```kotlin
// Actualizar perfil de usuario
suspend fun updateUserProfile(name: String, photoUrl: String?) {
    val user = auth.currentUser ?: return
    
    val request = UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .apply { photoUrl?.let { setPhotoUri(it) } }
        .build()
    
    user.updateProfile(request)
    user.reload() // Recargar para ver cambios
    
    println("Perfil actualizado: ${user.displayName}")
}

// Obtener token para llamadas a backend
suspend fun getAuthToken(): String? {
    return auth.currentUser?.getIdToken(forceRefresh = false)
}
```

#### `AuthCredential` y Proveedores

Credenciales para autenticación OAuth.

```kotlin
import com.iyr.firebase.auth.EmailAuthProvider
import com.iyr.firebase.auth.GoogleAuthProvider
import com.iyr.firebase.auth.FacebookAuthProvider
import com.iyr.firebase.auth.PhoneAuthProvider

// Email/Password credential (para re-autenticación)
val credential = EmailAuthProvider.getCredential("email@example.com", "password")

// Google credential (después de Google Sign-In)
val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

// Facebook credential
val credential = FacebookAuthProvider.getCredential(accessToken)

// Phone credential
val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)

// Usar credencial
val result = auth.signInWithCredential(credential)
```

---

## 📊 Firebase Realtime Database

**Dependencia:** `io.github.romancanoniero:firebase-database:1.0.0`

### Clases Principales

#### `FirebaseDatabase`

Punto de entrada al Realtime Database.

```kotlin
import com.iyr.firebase.database.FirebaseDatabase
import com.iyr.firebase.database.DatabaseReference
import com.iyr.firebase.database.DataSnapshot
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia por defecto |
| `getInstance(app)` | Obtiene instancia para una app |
| `getInstance(url)` | Obtiene instancia para una URL específica |
| `getReference()` | Referencia a la raíz |
| `getReference(path)` | Referencia a un path |
| `getReferenceFromUrl(url)` | Referencia desde URL completa |
| `goOnline()` | Reconecta al servidor |
| `goOffline()` | Desconecta del servidor |
| `setPersistenceEnabled(enabled)` | Habilita persistencia local |
| `useEmulator(host, port)` | Conecta al emulador |

#### `DatabaseReference`

Referencia a una ubicación en la base de datos.

| Método | Descripción | Retorno |
|--------|-------------|---------|
| `key` | Nombre del nodo actual | `String?` |
| `parent` | Referencia al nodo padre | `DatabaseReference?` |
| `root` | Referencia a la raíz | `DatabaseReference` |
| `child(path)` | Referencia a un hijo | `DatabaseReference` |
| `push()` | Crea referencia con ID único | `DatabaseReference` |
| `setValue(value)` | Escribe valor (sobreescribe) | `suspend Unit` |
| `updateChildren(map)` | Actualiza múltiples hijos | `suspend Unit` |
| `removeValue()` | Elimina el nodo | `suspend Unit` |
| `get()` | Lee datos una vez | `suspend DataSnapshot` |
| `valueEvents` | Flow de cambios | `Flow<DataSnapshot>` |
| `childEvents` | Flow de eventos de hijos | `Flow<ChildEvent>` |
| `orderByChild(path)` | Query ordenada por hijo | `Query` |
| `orderByKey()` | Query ordenada por key | `Query` |
| `orderByValue()` | Query ordenada por valor | `Query` |
| `limitToFirst(limit)` | Limita primeros N | `Query` |
| `limitToLast(limit)` | Limita últimos N | `Query` |
| `startAt(value)` | Inicia desde valor | `Query` |
| `endAt(value)` | Termina en valor | `Query` |
| `equalTo(value)` | Filtra por valor exacto | `Query` |

**Ejemplos Completos:**

```kotlin
val database = FirebaseDatabase.getInstance()

// === ESTRUCTURA DE DATOS ===
// La base de datos es un árbol JSON:
// {
//   "users": {
//     "user1": { "name": "John", "email": "john@example.com" },
//     "user2": { "name": "Jane", "email": "jane@example.com" }
//   },
//   "posts": {
//     "-NxYZ123": { "title": "Hello", "authorId": "user1" }
//   }
// }

// === ESCRIBIR DATOS ===

// Escribir objeto completo (sobreescribe)
suspend fun createUser(userId: String, name: String, email: String) {
    val userRef = database.getReference("users/$userId")
    userRef.setValue(mapOf(
        "name" to name,
        "email" to email,
        "createdAt" to ServerValue.TIMESTAMP
    ))
}

// Actualizar campos específicos (no sobreescribe otros)
suspend fun updateUserEmail(userId: String, newEmail: String) {
    database.getReference("users/$userId").updateChildren(mapOf(
        "email" to newEmail,
        "updatedAt" to ServerValue.TIMESTAMP
    ))
}

// Crear con ID auto-generado (push)
suspend fun createPost(title: String, content: String, authorId: String): String {
    val postsRef = database.getReference("posts")
    val newPostRef = postsRef.push()
    
    newPostRef.setValue(mapOf(
        "title" to title,
        "content" to content,
        "authorId" to authorId,
        "timestamp" to ServerValue.TIMESTAMP
    ))
    
    return newPostRef.key!! // Retorna el ID generado
}

// Eliminar datos
suspend fun deletePost(postId: String) {
    database.getReference("posts/$postId").removeValue()
}

// === LEER DATOS ===

// Lectura única
suspend fun getUser(userId: String): Map<String, Any?>? {
    val snapshot = database.getReference("users/$userId").get()
    return if (snapshot.exists()) {
        snapshot.getValue() as? Map<String, Any?>
    } else {
        null
    }
}

// Listener en tiempo real (Flow)
fun observeUser(userId: String): Flow<Map<String, Any?>?> {
    return database.getReference("users/$userId")
        .valueEvents
        .map { snapshot ->
            if (snapshot.exists()) {
                snapshot.getValue() as? Map<String, Any?>
            } else {
                null
            }
        }
}

// Observar lista de hijos
fun observeMessages(roomId: String) {
    database.getReference("rooms/$roomId/messages")
        .childEvents
        .collect { event ->
            when (event) {
                is ChildEvent.Added -> println("Nuevo mensaje: ${event.snapshot.key}")
                is ChildEvent.Changed -> println("Mensaje editado: ${event.snapshot.key}")
                is ChildEvent.Removed -> println("Mensaje eliminado: ${event.snapshot.key}")
                is ChildEvent.Moved -> println("Mensaje movido: ${event.snapshot.key}")
            }
        }
}

// === QUERIES ===

// Obtener últimos 10 posts
suspend fun getRecentPosts(): List<Map<String, Any?>> {
    val snapshot = database.getReference("posts")
        .orderByChild("timestamp")
        .limitToLast(10)
        .get()
    
    return snapshot.children.map { it.getValue() as Map<String, Any?> }
}

// Buscar usuarios por nombre
suspend fun searchUsers(name: String): List<Map<String, Any?>> {
    val snapshot = database.getReference("users")
        .orderByChild("name")
        .equalTo(name)
        .get()
    
    return snapshot.children.map { it.getValue() as Map<String, Any?> }
}

// Paginación
suspend fun getPostsPage(startAfter: String?, limit: Int): List<Map<String, Any?>> {
    var query = database.getReference("posts")
        .orderByKey()
        .limitToFirst(limit)
    
    if (startAfter != null) {
        query = query.startAt(startAfter)
    }
    
    return query.get().children.map { it.getValue() as Map<String, Any?> }
}

// === TRANSACCIONES ===
suspend fun incrementLikes(postId: String) {
    val likesRef = database.getReference("posts/$postId/likes")
    likesRef.runTransaction { currentData ->
        val currentLikes = (currentData.getValue() as? Long) ?: 0
        Transaction.success(currentLikes + 1)
    }
}

// === OFFLINE ===
// Habilitar persistencia (llamar antes de cualquier uso)
database.setPersistenceEnabled(true)

// Mantener sincronizado
database.getReference("users").keepSynced(true)
```

#### `DataSnapshot`

Representa un snapshot de datos.

| Propiedad/Método | Descripción |
|------------------|-------------|
| `key` | Nombre del nodo |
| `ref` | Referencia al nodo |
| `exists()` | Si tiene datos |
| `getValue()` | Obtiene el valor como Any? |
| `child(path)` | Snapshot de un hijo |
| `children` | Iterable de hijos |
| `childrenCount` | Número de hijos |
| `hasChild(path)` | Si tiene un hijo específico |
| `hasChildren()` | Si tiene hijos |

#### 🆕 Extensiones Tipadas (getValue<T>)

Para deserializar automáticamente a objetos tipados, usa las extensiones con kotlinx.serialization:

```kotlin
import com.iyr.firebase.database.*
import kotlinx.serialization.Serializable

// 1. Define tu modelo con @Serializable
@Serializable
data class User(
    val name: String,
    val email: String,
    val age: Int = 0,
    val active: Boolean = true
)

@Serializable
data class Post(
    val title: String,
    val content: String,
    val authorId: String,
    val likes: Int = 0
)

// 2. Deserializa directamente desde DataSnapshot
suspend fun getUser(userId: String): User? {
    val snapshot = database.getReference("users/$userId").get()
    return snapshot.getValue<User>()  // ⬅️ Deserialización automática
}

// 3. Deserializa lista de objetos
suspend fun getAllUsers(): List<User> {
    val snapshot = database.getReference("users").get()
    return snapshot.getValueList<User>()  // ⬅️ Lista tipada
}

// 4. Deserializa como Map
suspend fun getUsersMap(): Map<String, User> {
    val snapshot = database.getReference("users").get()
    return snapshot.getValueMap<User>()  // ⬅️ Map de userId -> User
}

// 5. Helpers para campos individuales
suspend fun getUserName(userId: String): String? {
    val snapshot = database.getReference("users/$userId").get()
    return snapshot.getString("name")  // ⬅️ Helper tipado
}
```

**Extensiones disponibles:**

| Extensión | Descripción |
|-----------|-------------|
| `getValue<T>()` | Deserializa a objeto @Serializable |
| `getValueList<T>()` | Deserializa hijos a List<T> |
| `getValueMap<T>()` | Deserializa hijos a Map<String, T> |
| `getString(field)` | Obtiene campo como String |
| `getLong(field)` | Obtiene campo como Long |
| `getInt(field)` | Obtiene campo como Int |
| `getDouble(field)` | Obtiene campo como Double |
| `getBoolean(field)` | Obtiene campo como Boolean |
| `getStringList(field)` | Obtiene campo como List<String> |

---

## 📄 Firebase Cloud Firestore

**Dependencia:** `io.github.romancanoniero:firebase-firestore:1.0.0`

### Clases Principales

#### `FirebaseFirestore`

```kotlin
import com.iyr.firebase.firestore.FirebaseFirestore
import com.iyr.firebase.firestore.DocumentReference
import com.iyr.firebase.firestore.CollectionReference
import com.iyr.firebase.firestore.DocumentSnapshot
import com.iyr.firebase.firestore.QuerySnapshot
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia por defecto |
| `getInstance(app)` | Obtiene instancia para una app |
| `collection(path)` | Referencia a colección |
| `document(path)` | Referencia a documento |
| `collectionGroup(collectionId)` | Query sobre todas las colecciones con ese ID |
| `batch()` | Crea un batch de escrituras |
| `runTransaction(block)` | Ejecuta transacción |
| `clearPersistence()` | Limpia caché local |
| `terminate()` | Termina la instancia |
| `useEmulator(host, port)` | Conecta al emulador |

#### `CollectionReference`

| Método | Descripción | Retorno |
|--------|-------------|---------|
| `id` | ID de la colección | `String` |
| `path` | Path completo | `String` |
| `document()` | Documento con ID auto-generado | `DocumentReference` |
| `document(id)` | Documento con ID específico | `DocumentReference` |
| `add(data)` | Agrega documento | `suspend DocumentReference` |
| `get()` | Obtiene todos los documentos | `suspend QuerySnapshot` |
| `snapshots` | Flow de cambios | `Flow<QuerySnapshot>` |
| Métodos de Query (ver abajo) | | |

#### `DocumentReference`

| Método | Descripción | Retorno |
|--------|-------------|---------|
| `id` | ID del documento | `String` |
| `path` | Path completo | `String` |
| `parent` | Colección padre | `CollectionReference` |
| `set(data)` | Establece datos (sobreescribe) | `suspend Unit` |
| `set(data, merge)` | Establece con merge | `suspend Unit` |
| `update(data)` | Actualiza campos | `suspend Unit` |
| `delete()` | Elimina documento | `suspend Unit` |
| `get()` | Lee documento | `suspend DocumentSnapshot` |
| `snapshots` | Flow de cambios | `Flow<DocumentSnapshot>` |
| `collection(path)` | Subcolección | `CollectionReference` |

#### Query Methods

| Método | Descripción |
|--------|-------------|
| `whereEqualTo(field, value)` | Igual a |
| `whereNotEqualTo(field, value)` | Diferente de |
| `whereLessThan(field, value)` | Menor que |
| `whereLessThanOrEqualTo(field, value)` | Menor o igual |
| `whereGreaterThan(field, value)` | Mayor que |
| `whereGreaterThanOrEqualTo(field, value)` | Mayor o igual |
| `whereArrayContains(field, value)` | Array contiene |
| `whereArrayContainsAny(field, values)` | Array contiene alguno |
| `whereIn(field, values)` | Valor en lista |
| `whereNotIn(field, values)` | Valor no en lista |
| `orderBy(field, direction?)` | Ordenar por campo |
| `limit(n)` | Limitar resultados |
| `limitToLast(n)` | Últimos N resultados |
| `startAt(values)` | Iniciar desde valores |
| `startAfter(values)` | Iniciar después de valores |
| `endAt(values)` | Terminar en valores |
| `endBefore(values)` | Terminar antes de valores |

**Ejemplos Completos:**

```kotlin
val firestore = FirebaseFirestore.getInstance()

// === CREAR/ESCRIBIR ===

// Agregar con ID auto-generado
suspend fun createUser(name: String, email: String): String {
    val docRef = firestore.collection("users").add(mapOf(
        "name" to name,
        "email" to email,
        "createdAt" to FieldValue.serverTimestamp()
    ))
    return docRef.id
}

// Establecer con ID específico
suspend fun createOrUpdateUser(userId: String, data: Map<String, Any>) {
    firestore.collection("users").document(userId).set(data)
}

// Merge (no sobreescribe campos existentes)
suspend fun updateUserPartial(userId: String, updates: Map<String, Any>) {
    firestore.collection("users").document(userId).set(updates, SetOptions.merge())
}

// Update específico
suspend fun incrementPostViews(postId: String) {
    firestore.collection("posts").document(postId).update(mapOf(
        "views" to FieldValue.increment(1),
        "lastViewed" to FieldValue.serverTimestamp()
    ))
}

// === LEER ===

// Leer documento
suspend fun getUser(userId: String): Map<String, Any>? {
    val snapshot = firestore.collection("users").document(userId).get()
    return if (snapshot.exists()) snapshot.getData() else null
}

// Leer colección
suspend fun getAllUsers(): List<Map<String, Any>> {
    val snapshot = firestore.collection("users").get()
    return snapshot.documents.mapNotNull { it.getData() }
}

// === QUERIES ===

// Usuarios activos ordenados por nombre
suspend fun getActiveUsers(): List<Map<String, Any>> {
    val snapshot = firestore.collection("users")
        .whereEqualTo("active", true)
        .orderBy("name")
        .get()
    
    return snapshot.documents.mapNotNull { it.getData() }
}

// Posts de una categoría, últimos 20
suspend fun getPostsByCategory(category: String): List<Map<String, Any>> {
    val snapshot = firestore.collection("posts")
        .whereEqualTo("category", category)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(20)
        .get()
    
    return snapshot.documents.mapNotNull { it.getData() }
}

// Productos en rango de precio
suspend fun getProductsInPriceRange(min: Double, max: Double): List<Map<String, Any>> {
    val snapshot = firestore.collection("products")
        .whereGreaterThanOrEqualTo("price", min)
        .whereLessThanOrEqualTo("price", max)
        .orderBy("price")
        .get()
    
    return snapshot.documents.mapNotNull { it.getData() }
}

// Búsqueda en array
suspend fun getUsersWithSkill(skill: String): List<Map<String, Any>> {
    val snapshot = firestore.collection("users")
        .whereArrayContains("skills", skill)
        .get()
    
    return snapshot.documents.mapNotNull { it.getData() }
}

// === LISTENERS EN TIEMPO REAL ===

fun observeMessages(roomId: String): Flow<List<Message>> {
    return firestore.collection("rooms")
        .document(roomId)
        .collection("messages")
        .orderBy("timestamp")
        .snapshots
        .map { snapshot ->
            snapshot.documents.map { doc ->
                Message(
                    id = doc.id,
                    text = doc.getString("text") ?: "",
                    senderId = doc.getString("senderId") ?: "",
                    timestamp = doc.getTimestamp("timestamp")
                )
            }
        }
}

// Observar cambios específicos
fun observeDocumentChanges(collectionPath: String) {
    firestore.collection(collectionPath)
        .snapshots
        .collect { snapshot ->
            snapshot.documentChanges.forEach { change ->
                val doc = change.document
                when (change.type) {
                    DocumentChange.Type.ADDED -> 
                        println("Agregado: ${doc.id}")
                    DocumentChange.Type.MODIFIED -> 
                        println("Modificado: ${doc.id}")
                    DocumentChange.Type.REMOVED -> 
                        println("Eliminado: ${doc.id}")
                }
            }
        }
}

// === BATCH WRITES ===
suspend fun batchUpdateUserStatus(userIds: List<String>, status: String) {
    val batch = firestore.batch()
    
    userIds.forEach { userId ->
        val docRef = firestore.collection("users").document(userId)
        batch.update(docRef, mapOf("status" to status))
    }
    
    batch.commit() // Atómico: todo o nada
}

// === TRANSACCIONES ===
suspend fun transferCredits(fromUserId: String, toUserId: String, amount: Int): Boolean {
    return firestore.runTransaction { transaction ->
        val fromRef = firestore.collection("users").document(fromUserId)
        val toRef = firestore.collection("users").document(toUserId)
        
        val fromSnapshot = transaction.get(fromRef)
        val currentCredits = fromSnapshot.getLong("credits") ?: 0
        
        if (currentCredits < amount) {
            throw Exception("Créditos insuficientes")
        }
        
        transaction.update(fromRef, mapOf("credits" to FieldValue.increment(-amount.toLong())))
        transaction.update(toRef, mapOf("credits" to FieldValue.increment(amount.toLong())))
        
        true
    }
}

// === SUBCOLECCIONES ===
suspend fun addComment(postId: String, userId: String, text: String): String {
    val docRef = firestore.collection("posts")
        .document(postId)
        .collection("comments")
        .add(mapOf(
            "userId" to userId,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp()
        ))
    return docRef.id
}

// Collection Group Query (buscar en todas las subcolecciones "comments")
suspend fun getAllCommentsByUser(userId: String): List<Map<String, Any>> {
    val snapshot = firestore.collectionGroup("comments")
        .whereEqualTo("userId", userId)
        .get()
    
    return snapshot.documents.mapNotNull { it.getData() }
}

// === PAGINACIÓN ===
class PaginatedQuery(
    private val query: Query,
    private val pageSize: Int
) {
    private var lastDocument: DocumentSnapshot? = null
    
    suspend fun nextPage(): List<DocumentSnapshot> {
        var currentQuery = query.limit(pageSize)
        
        lastDocument?.let {
            currentQuery = currentQuery.startAfter(it)
        }
        
        val snapshot = currentQuery.get()
        lastDocument = snapshot.documents.lastOrNull()
        
        return snapshot.documents
    }
    
    fun hasMore(): Boolean = lastDocument != null
}

// Uso
val paginatedUsers = PaginatedQuery(
    firestore.collection("users").orderBy("name"),
    pageSize = 20
)

val page1 = paginatedUsers.nextPage()
val page2 = paginatedUsers.nextPage()
```

#### FieldValue

Valores especiales para operaciones.

```kotlin
import com.iyr.firebase.firestore.FieldValue

// Timestamp del servidor
FieldValue.serverTimestamp()

// Incrementar número
FieldValue.increment(1)
FieldValue.increment(-5)

// Agregar a array
FieldValue.arrayUnion("element1", "element2")

// Remover de array
FieldValue.arrayRemove("element1")

// Eliminar campo
FieldValue.delete()
```

#### 🆕 Extensiones Tipadas (toObject<T>)

Para deserializar automáticamente a objetos tipados, usa las extensiones con kotlinx.serialization:

```kotlin
import com.iyr.firebase.firestore.*
import kotlinx.serialization.Serializable

// 1. Define tu modelo con @Serializable
@Serializable
data class User(
    val name: String,
    val email: String,
    val age: Int = 0,
    val active: Boolean = true
)

// 2. Deserializa DocumentSnapshot
suspend fun getUser(userId: String): User? {
    val snapshot = firestore.collection("users").document(userId).get()
    return snapshot.toObject<User>()  // ⬅️ Deserialización automática
}

// 3. Deserializa QuerySnapshot completo
suspend fun getAllUsers(): List<User> {
    val snapshot = firestore.collection("users").get()
    return snapshot.toObjects<User>()  // ⬅️ Lista tipada
}

// 4. Deserializa como Map de ID -> Objeto
suspend fun getUsersMap(): Map<String, User> {
    val snapshot = firestore.collection("users").get()
    return snapshot.toObjectsMap<User>()  // ⬅️ Map de docId -> User
}

// 5. Serializa objeto a Map para guardar
suspend fun saveUser(userId: String, user: User) {
    val data = user.toFirestoreMap()  // ⬅️ @Serializable a Map
    firestore.collection("users").document(userId).set(data)
}

// 6. Helpers para campos individuales
suspend fun getUserEmail(userId: String): String? {
    val snapshot = firestore.collection("users").document(userId).get()
    return snapshot.getString("email")  // ⬅️ Helper tipado
}

// 7. Observar con tipo
fun observeUsers(): Flow<List<User>> {
    return firestore.collection("users")
        .snapshots
        .map { it.toObjects<User>() }  // ⬅️ Flow tipado
}
```

**Extensiones disponibles para DocumentSnapshot:**

| Extensión | Descripción |
|-----------|-------------|
| `toObject<T>()` | Deserializa a objeto @Serializable |
| `getString(field)` | Obtiene campo como String |
| `getLong(field)` | Obtiene campo como Long |
| `getInt(field)` | Obtiene campo como Int |
| `getDouble(field)` | Obtiene campo como Double |
| `getBoolean(field)` | Obtiene campo como Boolean |
| `getTimestamp(field)` | Obtiene Timestamp como Long (millis) |
| `getStringList(field)` | Obtiene campo como List<String> |
| `getMap(field)` | Obtiene campo como Map<String, Any?> |

**Extensiones disponibles para QuerySnapshot:**

| Extensión | Descripción |
|-----------|-------------|
| `toObjects<T>()` | Deserializa todos los docs a List<T> |
| `toObjectsMap<T>()` | Deserializa a Map<docId, T> |

**Extensiones para serialización:**

| Extensión | Descripción |
|-----------|-------------|
| `object.toFirestoreMap()` | Convierte @Serializable a Map para set() |

---

## 📁 Firebase Cloud Storage

**Dependencia:** `io.github.romancanoniero:firebase-storage:1.0.0`

### Clases Principales

#### `FirebaseStorage`

```kotlin
import com.iyr.firebase.storage.FirebaseStorage
import com.iyr.firebase.storage.StorageReference
import com.iyr.firebase.storage.UploadTask
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia por defecto |
| `getInstance(app)` | Obtiene instancia para una app |
| `getInstance(url)` | Obtiene instancia para un bucket específico |
| `getReference()` | Referencia a la raíz |
| `getReference(path)` | Referencia a un path |
| `getReferenceFromUrl(url)` | Referencia desde URL de descarga |
| `maxUploadRetryTimeMillis` | Tiempo máximo de reintentos de subida |
| `maxDownloadRetryTimeMillis` | Tiempo máximo de reintentos de descarga |
| `useEmulator(host, port)` | Conecta al emulador |

#### `StorageReference`

| Método | Descripción | Retorno |
|--------|-------------|---------|
| `name` | Nombre del archivo | `String` |
| `path` | Path completo | `String` |
| `bucket` | Nombre del bucket | `String` |
| `parent` | Referencia padre | `StorageReference?` |
| `root` | Referencia a raíz | `StorageReference` |
| `child(path)` | Referencia hija | `StorageReference` |
| `putFile(path)` | Sube archivo desde path local | `UploadTask` |
| `putBytes(bytes)` | Sube bytes | `UploadTask` |
| `getFile(path)` | Descarga a archivo local | `suspend Unit` |
| `getBytes(maxSize)` | Descarga como bytes | `suspend ByteArray` |
| `getDownloadUrl()` | Obtiene URL de descarga | `suspend String` |
| `getMetadata()` | Obtiene metadata | `suspend StorageMetadata` |
| `updateMetadata(metadata)` | Actualiza metadata | `suspend StorageMetadata` |
| `delete()` | Elimina archivo | `suspend Unit` |
| `list(maxResults)` | Lista archivos | `suspend ListResult` |
| `listAll()` | Lista todos los archivos | `suspend ListResult` |

**Ejemplos Completos:**

```kotlin
val storage = FirebaseStorage.getInstance()

// === SUBIR ARCHIVOS ===

// Subir desde path local
suspend fun uploadImage(localPath: String, remotePath: String): String {
    val ref = storage.getReference(remotePath)
    val uploadTask = ref.putFile(localPath)
    
    // Monitorear progreso
    uploadTask.progressFlow.collect { progress ->
        val percent = (100.0 * progress.bytesTransferred / progress.totalByteCount).toInt()
        println("Subiendo: $percent%")
    }
    
    // Obtener URL de descarga
    return ref.getDownloadUrl()
}

// Subir con metadata
suspend fun uploadImageWithMetadata(
    localPath: String,
    remotePath: String,
    contentType: String = "image/jpeg"
): String {
    val ref = storage.getReference(remotePath)
    
    val metadata = StorageMetadata.Builder()
        .setContentType(contentType)
        .setCustomMetadata("uploadedBy", "user123")
        .setCustomMetadata("originalName", "photo.jpg")
        .build()
    
    ref.putFile(localPath, metadata)
    return ref.getDownloadUrl()
}

// Subir bytes (útil para datos en memoria)
suspend fun uploadBytes(data: ByteArray, remotePath: String): String {
    val ref = storage.getReference(remotePath)
    ref.putBytes(data)
    return ref.getDownloadUrl()
}

// === DESCARGAR ARCHIVOS ===

// Descargar a archivo local
suspend fun downloadToFile(remotePath: String, localPath: String) {
    val ref = storage.getReference(remotePath)
    ref.getFile(localPath)
    println("Descargado a: $localPath")
}

// Descargar como bytes (para imágenes pequeñas)
suspend fun downloadAsBytes(remotePath: String): ByteArray {
    val ref = storage.getReference(remotePath)
    return ref.getBytes(10 * 1024 * 1024) // Max 10MB
}

// Obtener URL de descarga (para usar en ImageLoader, etc.)
suspend fun getImageUrl(remotePath: String): String {
    return storage.getReference(remotePath).getDownloadUrl()
}

// === METADATA ===

suspend fun getFileInfo(remotePath: String) {
    val ref = storage.getReference(remotePath)
    val metadata = ref.getMetadata()
    
    println("Nombre: ${metadata.name}")
    println("Tamaño: ${metadata.sizeBytes} bytes")
    println("Tipo: ${metadata.contentType}")
    println("Creado: ${metadata.creationTimeMillis}")
    println("Actualizado: ${metadata.updatedTimeMillis}")
    println("MD5: ${metadata.md5Hash}")
    
    // Custom metadata
    metadata.customMetadataKeys.forEach { key ->
        println("$key: ${metadata.getCustomMetadata(key)}")
    }
}

suspend fun updateFileMetadata(remotePath: String) {
    val ref = storage.getReference(remotePath)
    
    val newMetadata = StorageMetadata.Builder()
        .setContentType("image/png")
        .setCustomMetadata("processed", "true")
        .build()
    
    ref.updateMetadata(newMetadata)
}

// === ELIMINAR ===

suspend fun deleteFile(remotePath: String) {
    storage.getReference(remotePath).delete()
}

// === LISTAR ARCHIVOS ===

suspend fun listUserImages(userId: String): List<String> {
    val ref = storage.getReference("users/$userId/images")
    val result = ref.listAll()
    
    return result.items.map { it.getDownloadUrl() }
}

// Paginación
suspend fun listImagesPaginated(path: String, pageSize: Int = 20): List<StorageReference> {
    val ref = storage.getReference(path)
    val result = ref.list(pageSize)
    
    // result.pageToken se puede usar para la siguiente página
    return result.items
}

// === EJEMPLO COMPLETO: Upload de foto de perfil ===

class ProfileImageUploader(private val storage: FirebaseStorage) {
    
    suspend fun uploadProfileImage(
        userId: String,
        imagePath: String,
        onProgress: (Int) -> Unit
    ): String {
        // 1. Referencia con nombre único
        val filename = "${userId}_${System.currentTimeMillis()}.jpg"
        val ref = storage.getReference("profiles/$userId/$filename")
        
        // 2. Crear metadata
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCacheControl("public, max-age=31536000") // Cache 1 año
            .build()
        
        // 3. Subir con progreso
        val uploadTask = ref.putFile(imagePath, metadata)
        
        uploadTask.progressFlow.collect { progress ->
            val percent = (100.0 * progress.bytesTransferred / progress.totalByteCount).toInt()
            onProgress(percent)
        }
        
        // 4. Retornar URL
        return ref.getDownloadUrl()
    }
    
    suspend fun deleteOldProfileImages(userId: String, keepLatest: Int = 1) {
        val ref = storage.getReference("profiles/$userId")
        val result = ref.listAll()
        
        // Ordenar por nombre (timestamp) y eliminar excepto los últimos
        val toDelete = result.items
            .sortedByDescending { it.name }
            .drop(keepLatest)
        
        toDelete.forEach { it.delete() }
    }
}
```

---

## ⚡ Firebase Cloud Functions

**Dependencia:** `io.github.romancanoniero:firebase-functions:1.0.0`

```kotlin
import com.iyr.firebase.functions.FirebaseFunctions
import com.iyr.firebase.functions.HttpsCallableResult
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia por defecto |
| `getInstance(app)` | Obtiene instancia para una app |
| `getInstance(regionOrCustomDomain)` | Instancia para región específica |
| `getHttpsCallable(name)` | Referencia a función HTTPS |
| `getHttpsCallableFromUrl(url)` | Referencia desde URL |
| `useEmulator(host, port)` | Conecta al emulador |

**Ejemplos:**

```kotlin
val functions = FirebaseFunctions.getInstance()

// Llamar función simple
suspend fun callHelloWorld(): String {
    val result = functions.getHttpsCallable("helloWorld").call()
    return result.data as String
}

// Llamar función con parámetros
suspend fun processOrder(orderId: String, items: List<String>): Map<String, Any> {
    val result = functions.getHttpsCallable("processOrder").call(mapOf(
        "orderId" to orderId,
        "items" to items,
        "timestamp" to System.currentTimeMillis()
    ))
    return result.data as Map<String, Any>
}

// Función en otra región
val euroFunctions = FirebaseFunctions.getInstance("europe-west1")
suspend fun europeanFunction() {
    val result = euroFunctions.getHttpsCallable("myEuropeanFunction").call()
}

// Timeout personalizado
suspend fun longRunningFunction() {
    val callable = functions.getHttpsCallable("longProcess")
    callable.timeout = 120_000L // 2 minutos
    val result = callable.call(mapOf("data" to "value"))
}

// Manejo de errores
suspend fun safeCall() {
    try {
        val result = functions.getHttpsCallable("riskyFunction").call()
    } catch (e: FirebaseFunctionsException) {
        when (e.code) {
            FunctionsErrorCode.INVALID_ARGUMENT -> println("Argumentos inválidos")
            FunctionsErrorCode.UNAUTHENTICATED -> println("No autenticado")
            FunctionsErrorCode.PERMISSION_DENIED -> println("Permiso denegado")
            FunctionsErrorCode.NOT_FOUND -> println("Función no encontrada")
            else -> println("Error: ${e.message}")
        }
    }
}
```

---

## 📨 Firebase Cloud Messaging

**Dependencia:** `io.github.romancanoniero:firebase-messaging:1.0.0`

```kotlin
import com.iyr.firebase.messaging.FirebaseMessaging
import com.iyr.firebase.messaging.RemoteMessage
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `getToken()` | Obtiene token FCM actual |
| `deleteToken()` | Elimina token actual |
| `subscribeToTopic(topic)` | Suscribe a un topic |
| `unsubscribeFromTopic(topic)` | Desuscribe de un topic |
| `isAutoInitEnabled` | Si la auto-inicialización está habilitada |
| `setAutoInitEnabled(enabled)` | Habilita/deshabilita auto-init |

**Ejemplos:**

```kotlin
val messaging = FirebaseMessaging.getInstance()

// Obtener token para enviar notificaciones a este dispositivo
suspend fun getDeviceToken(): String {
    val token = messaging.getToken()
    println("FCM Token: $token")
    
    // Guardar en tu backend
    saveTokenToServer(token)
    
    return token
}

// Suscribirse a topics
suspend fun subscribeToNotifications() {
    messaging.subscribeToTopic("news")
    messaging.subscribeToTopic("promotions")
    println("Suscrito a topics")
}

// Desuscribirse
suspend fun unsubscribeFromPromotions() {
    messaging.unsubscribeFromTopic("promotions")
}

// En Android, implementar servicio para recibir mensajes
// En iOS, configurar delegate en AppDelegate
```

---

## 📊 Firebase Analytics

**Dependencia:** `io.github.romancanoniero:firebase-analytics:1.0.0`

```kotlin
import com.iyr.firebase.analytics.FirebaseAnalytics
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `logEvent(name, params)` | Registra evento |
| `setUserId(id)` | Establece ID de usuario |
| `setUserProperty(name, value)` | Establece propiedad de usuario |
| `setAnalyticsCollectionEnabled(enabled)` | Habilita/deshabilita recolección |
| `resetAnalyticsData()` | Resetea datos de analytics |
| `getAppInstanceId()` | Obtiene ID de instancia de app |

**Ejemplos:**

```kotlin
val analytics = FirebaseAnalytics.getInstance()

// === EVENTOS ESTÁNDAR ===

// Login
fun logLogin(method: String) {
    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, mapOf(
        FirebaseAnalytics.Param.METHOD to method
    ))
}

// Registro
fun logSignUp(method: String) {
    analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, mapOf(
        FirebaseAnalytics.Param.METHOD to method
    ))
}

// Ver pantalla
fun logScreenView(screenName: String, screenClass: String) {
    analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, mapOf(
        FirebaseAnalytics.Param.SCREEN_NAME to screenName,
        FirebaseAnalytics.Param.SCREEN_CLASS to screenClass
    ))
}

// Compra
fun logPurchase(
    transactionId: String,
    value: Double,
    currency: String,
    items: List<Map<String, Any>>
) {
    analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, mapOf(
        FirebaseAnalytics.Param.TRANSACTION_ID to transactionId,
        FirebaseAnalytics.Param.VALUE to value,
        FirebaseAnalytics.Param.CURRENCY to currency,
        FirebaseAnalytics.Param.ITEMS to items
    ))
}

// === EVENTOS PERSONALIZADOS ===

fun logCustomEvent() {
    analytics.logEvent("share_image", mapOf(
        "image_type" to "profile",
        "share_method" to "whatsapp",
        "file_size_kb" to 256
    ))
}

fun logFeatureUsed(featureName: String) {
    analytics.logEvent("feature_used", mapOf(
        "feature_name" to featureName,
        "timestamp" to System.currentTimeMillis()
    ))
}

// === PROPIEDADES DE USUARIO ===

fun setUserProperties(subscriptionType: String, accountAge: Int) {
    analytics.setUserId("user_12345")
    analytics.setUserProperty("subscription_type", subscriptionType)
    analytics.setUserProperty("account_age_days", accountAge.toString())
}

// === CONTROL DE RECOLECCIÓN ===

fun disableAnalytics() {
    analytics.setAnalyticsCollectionEnabled(false)
}

fun enableAnalytics() {
    analytics.setAnalyticsCollectionEnabled(true)
}
```

---

## 💥 Firebase Crashlytics

**Dependencia:** `io.github.romancanoniero:firebase-crashlytics:1.0.0`

```kotlin
import com.iyr.firebase.crashlytics.FirebaseCrashlytics
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `log(message)` | Agrega log al reporte de crash |
| `setUserId(id)` | Establece ID de usuario |
| `setCustomKey(key, value)` | Establece clave personalizada |
| `setCustomKeys(keys)` | Establece múltiples claves |
| `recordException(throwable)` | Registra excepción no fatal |
| `sendUnsentReports()` | Envía reportes pendientes |
| `deleteUnsentReports()` | Elimina reportes pendientes |
| `didCrashOnPreviousExecution()` | Si crasheó en ejecución anterior |
| `setCrashlyticsCollectionEnabled(enabled)` | Habilita/deshabilita |

**Ejemplos:**

```kotlin
val crashlytics = FirebaseCrashlytics.getInstance()

// === CONFIGURAR USUARIO ===
fun setUserContext(userId: String, email: String) {
    crashlytics.setUserId(userId)
    crashlytics.setCustomKey("email", email)
    crashlytics.setCustomKey("subscription", "premium")
    crashlytics.setCustomKey("app_version", "1.2.3")
}

// === LOGS ===
fun logUserAction(action: String) {
    crashlytics.log("User action: $action")
}

// === ERRORES NO FATALES ===
fun logNonFatalError(exception: Exception, context: String) {
    crashlytics.log("Context: $context")
    crashlytics.setCustomKey("last_screen", context)
    crashlytics.recordException(exception)
}

// Ejemplo en try-catch
fun riskyOperation() {
    try {
        // Operación que puede fallar
        performOperation()
    } catch (e: Exception) {
        crashlytics.log("Error en operación: ${e.message}")
        crashlytics.recordException(e)
        // Manejar el error para el usuario
    }
}

// === MÚLTIPLES CLAVES ===
fun setContextKeys(screen: String, userId: String, action: String) {
    crashlytics.setCustomKeys(mapOf(
        "current_screen" to screen,
        "user_id" to userId,
        "last_action" to action,
        "timestamp" to System.currentTimeMillis().toString()
    ))
}

// === CONTROL ===
fun handleUserConsent(accepted: Boolean) {
    crashlytics.setCrashlyticsCollectionEnabled(accepted)
}

// Verificar crash anterior
fun checkPreviousCrash() {
    if (crashlytics.didCrashOnPreviousExecution()) {
        // Mostrar diálogo de disculpas o enviar feedback
        showCrashRecoveryDialog()
    }
}
```

---

## 🎛️ Firebase Remote Config

**Dependencia:** `io.github.romancanoniero:firebase-remote-config:1.0.0`

```kotlin
import com.iyr.firebase.remoteconfig.FirebaseRemoteConfig
import com.iyr.firebase.remoteconfig.FirebaseRemoteConfigSettings
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `fetchAndActivate()` | Fetch y activa en un paso |
| `fetch()` | Solo fetch (requiere activate) |
| `activate()` | Activa valores fetched |
| `setDefaultsAsync(defaults)` | Establece valores por defecto |
| `setConfigSettingsAsync(settings)` | Configura settings |
| `getString(key)` | Obtiene string |
| `getBoolean(key)` | Obtiene boolean |
| `getLong(key)` | Obtiene long |
| `getDouble(key)` | Obtiene double |
| `getValue(key)` | Obtiene valor con metadata |
| `getAll()` | Obtiene todos los valores |
| `reset()` | Resetea a defaults |

**Ejemplos:**

```kotlin
val remoteConfig = FirebaseRemoteConfig.getInstance()

// === CONFIGURACIÓN INICIAL ===
suspend fun initRemoteConfig() {
    // 1. Establecer defaults locales (usados si no hay fetch)
    remoteConfig.setDefaultsAsync(mapOf(
        "welcome_message" to "¡Bienvenido!",
        "feature_new_ui" to false,
        "max_items_per_page" to 20L,
        "discount_percentage" to 0.0,
        "maintenance_mode" to false
    ))
    
    // 2. Configurar intervalo de fetch
    val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(3600) // 1 hora
        .setFetchTimeoutInSeconds(60)
        .build()
    remoteConfig.setConfigSettingsAsync(configSettings)
    
    // 3. Fetch y activar
    try {
        val activated = remoteConfig.fetchAndActivate()
        if (activated) {
            println("Nuevos valores activados")
        } else {
            println("Usando valores cacheados")
        }
    } catch (e: Exception) {
        println("Error en fetch: ${e.message}")
    }
}

// === OBTENER VALORES ===
fun getRemoteValues() {
    val welcomeMessage = remoteConfig.getString("welcome_message")
    val newUiEnabled = remoteConfig.getBoolean("feature_new_ui")
    val maxItems = remoteConfig.getLong("max_items_per_page")
    val discount = remoteConfig.getDouble("discount_percentage")
    
    println("Mensaje: $welcomeMessage")
    println("Nueva UI: $newUiEnabled")
    println("Max items: $maxItems")
    println("Descuento: $discount%")
}

// === FEATURE FLAGS ===
class FeatureFlags(private val remoteConfig: FirebaseRemoteConfig) {
    
    val isNewCheckoutEnabled: Boolean
        get() = remoteConfig.getBoolean("feature_new_checkout")
    
    val isDarkModeDefault: Boolean
        get() = remoteConfig.getBoolean("default_dark_mode")
    
    val maxUploadSizeMB: Long
        get() = remoteConfig.getLong("max_upload_size_mb")
    
    val apiBaseUrl: String
        get() = remoteConfig.getString("api_base_url")
    
    suspend fun refresh(): Boolean {
        return try {
            remoteConfig.fetchAndActivate()
        } catch (e: Exception) {
            false
        }
    }
}

// Uso
val featureFlags = FeatureFlags(remoteConfig)
if (featureFlags.isNewCheckoutEnabled) {
    showNewCheckout()
} else {
    showLegacyCheckout()
}

// === MODO MANTENIMIENTO ===
fun checkMaintenanceMode(): Boolean {
    return remoteConfig.getBoolean("maintenance_mode")
}

// === A/B TESTING ===
fun getExperimentVariant(): String {
    return remoteConfig.getString("experiment_variant") // "control", "variant_a", "variant_b"
}
```

---

## ⏱️ Firebase Performance

**Dependencia:** `io.github.romancanoniero:firebase-performance:1.0.0`

```kotlin
import com.iyr.firebase.performance.FirebasePerformance
import com.iyr.firebase.performance.Trace
import com.iyr.firebase.performance.HttpMetric
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `newTrace(name)` | Crea trace personalizado |
| `newHttpMetric(url, method)` | Crea métrica HTTP |
| `isPerformanceCollectionEnabled()` | Si está habilitada la recolección |
| `setPerformanceCollectionEnabled(enabled)` | Habilita/deshabilita |

**Ejemplos:**

```kotlin
val performance = FirebasePerformance.getInstance()

// === TRACES PERSONALIZADOS ===

// Trace simple
suspend fun measureOperation() {
    val trace = performance.newTrace("my_custom_operation")
    trace.start()
    
    // Operación a medir
    performHeavyComputation()
    
    trace.stop()
}

// Trace con atributos y métricas
suspend fun measureComplexOperation(userId: String) {
    val trace = performance.newTrace("data_processing")
    trace.start()
    
    // Atributos (máximo 5)
    trace.putAttribute("user_id", userId)
    trace.putAttribute("operation_type", "batch_import")
    
    // Procesar items
    var processedCount = 0
    var errorCount = 0
    
    items.forEach { item ->
        try {
            processItem(item)
            processedCount++
        } catch (e: Exception) {
            errorCount++
        }
    }
    
    // Métricas (contadores)
    trace.putMetric("items_processed", processedCount.toLong())
    trace.putMetric("errors", errorCount.toLong())
    trace.putAttribute("result", if (errorCount == 0) "success" else "partial_failure")
    
    trace.stop()
}

// Trace incremental
fun measureWithIncrements() {
    val trace = performance.newTrace("file_uploads")
    trace.start()
    
    files.forEach { file ->
        uploadFile(file)
        trace.incrementMetric("files_uploaded", 1)
        trace.incrementMetric("bytes_uploaded", file.size)
    }
    
    trace.stop()
}

// === HTTP METRICS ===

suspend fun measureApiCall(url: String): Response {
    val metric = performance.newHttpMetric(url, "GET")
    metric.start()
    
    try {
        val response = httpClient.get(url)
        
        metric.setHttpResponseCode(response.statusCode)
        metric.setResponsePayloadSize(response.body.size.toLong())
        metric.setResponseContentType(response.contentType)
        metric.putAttribute("cache_hit", if (response.fromCache) "true" else "false")
        
        return response
    } finally {
        metric.stop()
    }
}

// POST con payload
suspend fun measurePostRequest(url: String, data: ByteArray): Response {
    val metric = performance.newHttpMetric(url, "POST")
    metric.setRequestPayloadSize(data.size.toLong())
    metric.start()
    
    try {
        val response = httpClient.post(url, data)
        metric.setHttpResponseCode(response.statusCode)
        metric.setResponsePayloadSize(response.body.size.toLong())
        return response
    } finally {
        metric.stop()
    }
}

// === INLINE FUNCTION HELPER ===

inline fun <T> FirebasePerformance.trace(name: String, block: Trace.() -> T): T {
    val trace = newTrace(name)
    trace.start()
    return try {
        trace.block()
    } finally {
        trace.stop()
    }
}

// Uso
val result = performance.trace("quick_operation") {
    putAttribute("input_size", inputData.size.toString())
    val output = processData(inputData)
    putMetric("output_size", output.size.toLong())
    output
}
```

---

## ✅ Firebase App Check

**Dependencia:** `io.github.romancanoniero:firebase-appcheck:1.0.0`

```kotlin
import com.iyr.firebase.appcheck.FirebaseAppCheck
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `installAppCheckProviderFactory(factory)` | Instala proveedor |
| `getAppCheckToken(forceRefresh)` | Obtiene token de App Check |
| `addAppCheckListener(listener)` | Agrega listener de tokens |
| `removeAppCheckListener(listener)` | Remueve listener |
| `setTokenAutoRefreshEnabled(enabled)` | Habilita auto-refresh |

**Ejemplos:**

```kotlin
val appCheck = FirebaseAppCheck.getInstance()

// === CONFIGURACIÓN ===

// Android - Configurar en Application.onCreate()
fun initAppCheck() {
    val factory = PlayIntegrityAppCheckProviderFactory.getInstance()
    appCheck.installAppCheckProviderFactory(factory)
}

// === OBTENER TOKEN ===

suspend fun getAppCheckToken(): String? {
    return try {
        val result = appCheck.getAppCheckToken(forceRefresh = false)
        result.token
    } catch (e: Exception) {
        println("Error obteniendo App Check token: ${e.message}")
        null
    }
}

// Usar token en llamadas a tu backend
suspend fun callProtectedApi(endpoint: String): Response {
    val token = getAppCheckToken() ?: throw Exception("No App Check token")
    
    return httpClient.get(endpoint) {
        header("X-Firebase-AppCheck", token)
    }
}

// === LISTENER ===

fun listenToTokenChanges() {
    appCheck.addAppCheckListener { token ->
        println("Nuevo App Check token: ${token.token.take(20)}...")
        println("Expira en: ${token.expireTimeMillis}")
    }
}
```

---

## 💬 Firebase In-App Messaging

**Dependencia:** `io.github.romancanoniero:firebase-inappmessaging:1.0.0`

> ⚠️ **Nota:** Solo disponible en Android.

```kotlin
import com.iyr.firebase.inappmessaging.FirebaseInAppMessaging
```

| Método | Descripción |
|--------|-------------|
| `getInstance()` | Obtiene instancia |
| `isAutomaticDataCollectionEnabled` | Si la recolección automática está habilitada |
| `setAutomaticDataCollectionEnabled(enabled)` | Habilita/deshabilita recolección |
| `setMessagesSuppressed(suppressed)` | Suprime mensajes temporalmente |
| `triggerEvent(eventName)` | Dispara evento para mostrar mensaje |
| `addClickListener(listener)` | Agrega listener de clicks |
| `addDismissListener(listener)` | Agrega listener de dismiss |
| `addImpressionListener(listener)` | Agrega listener de impresiones |
| `addDisplayErrorListener(listener)` | Agrega listener de errores |

**Ejemplos:**

```kotlin
val inAppMessaging = FirebaseInAppMessaging.getInstance()

// === CONFIGURACIÓN ===

fun initInAppMessaging() {
    // Habilitar recolección de datos
    inAppMessaging.setAutomaticDataCollectionEnabled(true)
}

// === TRIGGERS ===

// Disparar eventos personalizados (configurados en Firebase Console)
fun onUserCompletedPurchase() {
    inAppMessaging.triggerEvent("purchase_completed")
}

fun onUserViewedProduct(productId: String) {
    inAppMessaging.triggerEvent("product_viewed")
}

// === CONTROL ===

// Suprimir mensajes durante operaciones críticas
fun performCriticalOperation() {
    inAppMessaging.setMessagesSuppressed(true)
    
    try {
        // Operación crítica
        processCriticalTask()
    } finally {
        inAppMessaging.setMessagesSuppressed(false)
    }
}

// === LISTENERS ===

fun setupListeners() {
    // Click en mensaje
    inAppMessaging.addClickListener { message, action ->
        println("Click en mensaje: ${message.campaignId}")
        println("Acción: ${action.actionUrl}")
    }
    
    // Mensaje mostrado
    inAppMessaging.addImpressionListener { message ->
        println("Mensaje mostrado: ${message.campaignId}")
    }
    
    // Mensaje cerrado
    inAppMessaging.addDismissListener { message ->
        println("Mensaje cerrado: ${message.campaignId}")
    }
    
    // Error
    inAppMessaging.addDisplayErrorListener { error ->
        println("Error mostrando mensaje: ${error.message}")
    }
}
```

---

## 🔗 Recursos Adicionales

- **Repositorio GitHub:** https://github.com/romancanoniero/firebase-kmp-sdk
- **Firebase Documentation:** https://firebase.google.com/docs
- **Kotlin Multiplatform:** https://kotlinlang.org/docs/multiplatform.html

---

## 📧 Soporte

- **Issues:** [GitHub Issues](https://github.com/romancanoniero/firebase-kmp-sdk/issues)
- **Email:** romancanoniero@gmail.com

---

**Desarrollado con ❤️ por Roman Canoniero**

